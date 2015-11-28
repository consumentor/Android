package consumentor.shopgun.aidroid.util;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.model.ItemInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Simon on 11.09.13.
 */

public class SuggestionContentProvider extends ContentProvider {

    private JSONObject mJsonObject;

    private String[] mColumnNames = {
            BaseColumns._ID
            , SearchManager.SUGGEST_COLUMN_TEXT_1
            , SearchManager.SUGGEST_COLUMN_TEXT_2
            , SearchManager.SUGGEST_COLUMN_ICON_1
            , SearchManager.SUGGEST_COLUMN_ICON_2
            , SearchManager.SUGGEST_COLUMN_INTENT_DATA
            , SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
            , SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
            , SearchManager.SUGGEST_COLUMN_INTENT_ACTION
    };

    private MatrixCursor mCursor;// = new MatrixCursor(mColumnNames);


    public final Uri CONTENT_URI =
            Uri.parse("content://consumentor.shopgun.aidroid.view.SearchSuggestionProvider");

    public SuggestionContentProvider(){
        this.getContext();
        mCursor = new MatrixCursor(mColumnNames);
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * We use selectionArgs to pass the searchString
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {


        String jsonString;
        try {
            String query = uri.getLastPathSegment().toLowerCase();
            jsonString = getSearchResultAsJsonString(query);
            try {

                mJsonObject = new JSONObject(jsonString);
                parseResult();

                return mCursor;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String getSearchResultAsJsonString(String searchPhrase) {

        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(RESTLoader.BASE_URL +"/iteminfo/" +searchPhrase.replaceAll(" ", "%20"));
        // Finally, we send our request using HTTP. This is the synchronous
        // long operation that we need to run on this Loader's thread.
        HttpResponse response = null;
        try {
            response = client.execute(request);
            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int        statusCode     = responseStatus != null ? responseStatus.getStatusCode() : 0;

            return responseEntity != null ? EntityUtils.toString(responseEntity) : null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseResult() throws JSONException {
        String indicateAdvicesIcon = Integer.valueOf(R.drawable.ic_arrow_right).toString();
        mCursor = new MatrixCursor(mColumnNames);
        if(!mJsonObject.get("Ingredients").equals(JSONObject.NULL)) {
            JSONArray jsonIngredients = mJsonObject.getJSONArray("Ingredients");
            for(int i = 0; i < jsonIngredients.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonIngredients.get(i);
                int numAdvices = jsonObject.getInt("NumberAdvices");
                String[] values = {Integer.toString(i)
                        , jsonObject.getString("IngredientName")
                        , null
                        , Integer.valueOf(R.drawable.ic_ingredient).toString()//getContext().getString(R.string.itemType_ingredient)
                        , numAdvices > 0 ? indicateAdvicesIcon : null
                        , "ingredients"
                        , jsonObject.getString("IngredientId")
                        , String.valueOf(ItemInfo.ItemType.INGREDIENT)
                        , "org.consumentor.shopgun.intent.action.INGREDIENT"
                };
                mCursor.addRow(values);
            }
        }

        if(!mJsonObject.get("Companies").equals(JSONObject.NULL)) {
            JSONArray jsonCompanies = mJsonObject.getJSONArray("Companies");
            for(int i = 0; i < jsonCompanies.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonCompanies.get(i);
                int numAdvices = jsonObject.getInt("NumberAdvices");
                String[] values = {Integer.toString(i + mCursor.getCount())
                        , jsonObject.getString("CompanyName")
                        , null
                        , Integer.valueOf(R.drawable.ic_company).toString()
                        , numAdvices > 0 ? indicateAdvicesIcon : null
                        , "companies"
                        , jsonObject.getString("CompanyId")
                        , String.valueOf(ItemInfo.ItemType.COMPANY)
                        , "org.consumentor.shopgun.intent.action.COMPANY"
                };
                mCursor.addRow(values);

            }
        }

        if(!mJsonObject.get("Brands").equals(JSONObject.NULL)) {
            JSONArray jsonBrands = mJsonObject.getJSONArray("Brands");
            for(int i = 0; i < jsonBrands.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonBrands.get(i);
                int numAdvices = jsonObject.getInt("NumberAdvices");
                String[] values = {Integer.toString(i + mCursor.getCount())
                        , jsonObject.getString("BrandName")
                        , null
                        , Integer.valueOf(R.drawable.ic_brand).toString()
                        , numAdvices > 0 ? indicateAdvicesIcon : null
                        , "brands"
                        , jsonObject.getString("BrandId")
                        , String.valueOf(ItemInfo.ItemType.BRAND)
                        , "org.consumentor.shopgun.intent.action.BRAND"
                };
                mCursor.addRow(values);
            }
        }

        if(!mJsonObject.get("Products").equals(JSONObject.NULL)) {
            JSONArray jsonProducts = mJsonObject.getJSONArray("Products");
            for(int i = 0; i < jsonProducts.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonProducts.get(i);
                int numAdvices = jsonObject.getInt("NumberAdvices");
                String[] values = {Integer.toString(i + mCursor.getCount())
                        , jsonObject.getString("ProductName")
                        , jsonObject.getString("BrandName") +" - " +jsonObject.getString("GTIN")
                        , Integer.valueOf(R.drawable.ic_product).toString()
                        , numAdvices > 0 ? indicateAdvicesIcon : null
                        , "products"
                        , jsonObject.getString("ProductId")
                        , String.valueOf(ItemInfo.ItemType.PRODUCT)
                        , "org.consumentor.shopgun.intent.action.PRODUCT"
                };
                mCursor.addRow(values);
            }
        }
    }
}
