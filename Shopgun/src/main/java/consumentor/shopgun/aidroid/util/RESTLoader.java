package consumentor.shopgun.aidroid.util;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 08.08.13.
 */
public class RESTLoader extends AsyncTaskLoader<RESTLoader.RESTResponse> {

    //public static String SERVER_URL = "http://Simons-MacBook:8081";
    public static String SERVER_URL = "http://consumentor.publicvm.com:53200/beta";
    //public static final String SERVER_URL = "http://webservice.shopgun.se/side_by_side_release";

    public static String BASE_URL = SERVER_URL +"/ShopgunApp";
    //public static final String BASE_URL = "http://consumentor.publicvm.com:53200/beta/ShopgunApp";
    //public static final String BASE_URL = "http://webservice.shopgun.se/side_by_side_release/ShopgunApp";

    public static final String ARGS_URI    = "org.consumentor.shopgun.util.ARGS_URI";
    public static final String ARGS_PARAMS = "org.consumentor.shopgun.util.ARGS_PARAMS";
    public static final String ARGS_VERB = "org.consumentor.shopgun.util.ARGS_VERB";

    private static final String TAG = RESTLoader.class.getName();

    public static void setServerUrlToDebug(boolean setToDebug){
        if (setToDebug){
            SERVER_URL = "http://consumentor.publicvm.com:53200/beta";
        }
        else{
            SERVER_URL = "http://webservice.shopgun.se/side_by_side_release";
        }
        BASE_URL = SERVER_URL +"/ShopgunApp";
    }

    // We use this delta to determine if our cached data is
    // old or not. The success we have here is 10 minutes;
    private static final long STALE_DELTA = 600000;

    public enum HTTPVerb {
        GET,
        POST,
        PUT,
        DELETE
    }

    public static class RESTResponse {
        private String mData;
        private int    mCode;

        public RESTResponse() {
        }

        public RESTResponse(String data, int code) {
            mData = data;
            mCode = code;
        }

        public String getData() {
            return mData;
        }

        public int getCode() {
            return mCode;
        }
    }

    private HTTPVerb     mVerb;
    private Uri mAction;
    private Bundle mParams;
    private RESTResponse mRestResponse;

    private long mLastLoad;

    public RESTLoader(Context context) {
        super(context);
    }

    public RESTLoader(Context context, HTTPVerb verb, Uri action) {
        super(context);

        mVerb   = verb;
        mAction = action;
    }

    public RESTLoader(Context context, HTTPVerb verb, Uri action, Bundle params) {
        super(context);

        mVerb   = verb;
        mAction = action;
        mParams = params;
    }
    @Override
    public RESTResponse loadInBackground() {
        try {
            // At the very least we always need an action.
            if (mAction == null) {
                Log.e(TAG, "You did not define an action. REST call canceled.");
                return new RESTResponse(); // We send an empty response back. The LoaderCallbacks<RESTResponse>
                // implementation will always need to check the RESTResponse
                // and handle error cases like this.
            }

            // Here we define our base request object which we will
            // send to our REST service via HttpClient.
            HttpRequestBase request = null;

            // Let's build our request based on the HTTP verb we were
            // given.
            switch (mVerb) {
                case GET: {
                    request = new HttpGet();
                    attachUriWithQuery(request, mAction, mParams);
                }
                break;

                case DELETE: {
                    request = new HttpDelete();
                    attachUriWithQuery(request, mAction, mParams);
                }
                break;

                case POST: {
                    request = new HttpPost();
                    request.setURI(new URI(mAction.toString()));

                    // Attach form entity if necessary. Note: some REST APIs
                    // require you to POST JSON. This is easy to do, simply use
                    // postRequest.setHeader('Content-Type', 'application/json')
                    // and StringEntity instead. Same thing for the PUT case
                    // below.
                    HttpPost postRequest = (HttpPost) request;
                    request.addHeader("Content-Type", "application/json");
                    if (mParams != null) {
//                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(mParams));
//                        postRequest.setEntity(formEntity);
                        //MultipartEntity formEntity = createMultipartEntity(mParams);
                        try {
                            JSONObject holder = getJsonObjectFromMap(mParams);
                            StringEntity se = new StringEntity(holder.toString());
                            postRequest.setEntity(se);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //postRequest.setEntity(formEntity);
                    }
                }
                break;

                case PUT: {
                    request = new HttpPut();
                    request.setURI(new URI(mAction.toString()));

                    // Attach form entity if necessary.
                    HttpPut putRequest = (HttpPut) request;
                    request.addHeader("Content-Type", "application/json");

                    if (mParams != null) {
                        try {
                            JSONObject holder = getJsonObjectFromMap(mParams);
                            StringEntity se = new StringEntity(holder.toString());
                            putRequest.setEntity(se);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(mParams));
                        //putRequest.setEntity(formEntity);
                    }
                }
                break;
            }

            if (request != null) {

                request.addHeader("OS_VERSION", "" + android.os.Build.VERSION.RELEASE);
                request.addHeader("MODEL", android.os.Build.MODEL);
                request.addHeader("PHONE_IMEI", Installation.id(getContext()));
                HttpClient client = new DefaultHttpClient();

                // Let's send some useful debug information so we can monitor things
                // in LogCat.
                Log.d(TAG, "Executing request: "+ verbToString(mVerb) +": "+ mAction.toString());

                // Finally, we send our request using HTTP. This is the synchronous
                // long operation that we need to run on this Loader's thread.
                HttpResponse response = client.execute(request);

                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();
                int        statusCode     = responseStatus != null ? responseStatus.getStatusCode() : 0;

                // Here we create our response and send it back to the LoaderCallbacks<RESTResponse> implementation.
                RESTResponse restResponse = new RESTResponse(responseEntity != null ? EntityUtils.toString(responseEntity) : null, statusCode);
                return restResponse;
            }

            // Request was null if we get here, so let's just send our empty RESTResponse like usual.
            return new RESTResponse();
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect. "+ verbToString(mVerb) +": "+ mAction.toString(), e);
            return new RESTResponse();
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, "A UrlEncodedFormEntity was created with an unsupported encoding.", e);
            return new RESTResponse();
        }
        catch (ClientProtocolException e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            return new RESTResponse();
        }
        catch (IOException e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            return new RESTResponse();
        }
    }

    private MultipartEntity createMultipartEntity(Bundle params) {
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (String key : params.keySet()) {
            Object value = params.get(key);

            // We can only put Strings in a form entity, so we call the toString()
            // method to enforce. We also probably don't need to check for null here
            // but we do anyway because Bundle.get() can return null.
            if (value != null) {
                if (value instanceof String)
                try {
                    entity.addPart(key, new StringBody(value.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (value instanceof File){
                File file = (File) value;
                String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                entity.addPart("file", new FileBody(file, "upload.jpg", mimeType, "utf-8"));
            }

        }
        return entity;
    }

    private static JSONObject getJsonObjectFromMap(Bundle params) throws JSONException {

        //Stores JSON
        JSONObject holder = new JSONObject();


        //using the earlier example your first entry would get email
        //and the inner while would get the success which would be 'foo@bar.com'
        //{ fan: { email : 'foo@bar.com' } }

        //While there is another entry
        for (String key : params.keySet()) {
            Object value = params.get(key);

            //puts email and 'foo@bar.com'  together in map
            try {
                Gson gson = new Gson();
                String json = gson.toJson(value);
                JSONObject jsonObject = new JSONObject(json);
                holder.put(key, jsonObject);
            }catch (JSONException e){
                holder.put(key, value);
            }
        }
        return holder;
    }

    @Override
    public void deliverResult(RESTResponse data) {
        // Here we cache our response.
        mRestResponse = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mRestResponse != null) {
            // We have a cached result, so we can just
            // return right away.
            super.deliverResult(mRestResponse);
        }

        // If our response is null or we have hung onto it for a long time,
        // then we perform a force load.
        if (mRestResponse == null || System.currentTimeMillis() - mLastLoad >= STALE_DELTA) forceLoad();
        mLastLoad = System.currentTimeMillis();
    }

    @Override
    protected void onStopLoading() {
        // This prevents the AsyncTask backing this
        // loader from completing if it is currently running.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Stop the Loader if it is currently running.
        onStopLoading();

        // Get rid of our cache if it exists.
        mRestResponse = null;

        // Reset our stale timer.
        mLastLoad = 0;
    }

    private static void attachUriWithQuery(HttpRequestBase request, Uri uri, Bundle params) {
        try {
            if (params == null) {
                // No params were given or they have already been
                // attached to the Uri.
                request.setURI(new URI(uri.toString()));
            }
            else {
                Uri.Builder uriBuilder = uri.buildUpon();

                // Loop through our params and append them to the Uri.
                for (BasicNameValuePair param : paramsToList(params)) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }

                uri = uriBuilder.build();
                request.setURI(new URI(uri.toString()));
            }
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect: "+ uri.toString());
        }
    }

    private static String verbToString(HTTPVerb verb) {
        switch (verb) {
            case GET:
                return "GET";

            case POST:
                return "POST";

            case PUT:
                return "PUT";

            case DELETE:
                return "DELETE";
        }

        return "";
    }

    private static List<BasicNameValuePair> paramsToList(Bundle params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());

        for (String key : params.keySet()) {
            Object value = params.get(key);

            // We can only put Strings in a form entity, so we call the toString()
            // method to enforce. We also probably don't need to check for null here
            // but we do anyway because Bundle.get() can return null.
            if (value != null) formList.add(new BasicNameValuePair(key, value.toString()));
        }

        return formList;
    }
}
