package consumentor.shopgun.aidroid.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.model.CertificationMark;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.Date;

public class CertificationMarkActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>{

    public static final String EXTRA_CERTIFICATIONMARK_ID = "org.consumentor.shopgun.extra.EXTRA_CERTIFICATIONMARK_ID";

    private CertificationMark mCertificationMark;

    private ImageView mCertificationMarkLogo;
    private TextView mCertificationMarkDescription;
    private TextView mCertificationMarkHomepage;
    private TextView mCertificationMarkNameTextView;
    private static final int LOADER_GET_CERTIFICATIONMARK = 0x1;
    private ImageLoader mImageLoader;
    private View mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = getLayoutInflater().inflate(R.layout.activity_certificationmark, null);
        // Show the Up button in the action bar.
        setupActionBar();

        mCertificationMarkLogo = (ImageView) mView.findViewById(R.id.certificationMarkLogo);
        mCertificationMarkDescription = (TextView) mView.findViewById(R.id.certificationMarkDescription);
        mCertificationMarkHomepage = (TextView) mView.findViewById(R.id.certificationMarkHomepage);
        mCertificationMarkNameTextView = (TextView) mView.findViewById(R.id.certificationMarkName);
        mImageLoader = ImageLoader.getInstance();

        int certificationMarkId = getIntent().getIntExtra(EXTRA_CERTIFICATIONMARK_ID, -1);
        if (certificationMarkId != -1){
            Uri uri = Uri.parse(RESTLoader.BASE_URL +"/certificationMarks/" +certificationMarkId);

            Bundle args = new Bundle();
            Bundle params = new Bundle();
            args.putParcelable(RESTLoader.ARGS_URI, uri);
            args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
            args.putParcelable(RESTLoader.ARGS_PARAMS, params);
            // Initialize the Loader.
            getSupportLoaderManager().initLoader(LOADER_GET_CERTIFICATIONMARK, args, this);
            showLoaderIndicator(true);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public android.support.v4.content.Loader<RESTLoader.RESTResponse> onCreateLoader(int i, Bundle args) {
        if (args != null && args.containsKey(RESTLoader.ARGS_URI) && args.containsKey(RESTLoader.ARGS_PARAMS)) {
            Uri action = args.getParcelable(RESTLoader.ARGS_URI);
            RESTLoader.HTTPVerb verb = args.containsKey(RESTLoader.ARGS_VERB) ? (RESTLoader.HTTPVerb) args.getSerializable(RESTLoader.ARGS_VERB) : RESTLoader.HTTPVerb.GET;
            Bundle params = args.getParcelable(RESTLoader.ARGS_PARAMS);

            return new RESTLoader(this, verb, action, params);
        }

        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {

        int    code = data.getCode();
        String json = data.getData();

        switch (loader.getId()){

            case LOADER_GET_CERTIFICATIONMARK:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mCertificationMark = gson.fromJson(json, CertificationMark.class);
                    mCertificationMarkDescription.setMovementMethod(LinkMovementMethod.getInstance());
                    if(mCertificationMark.description != null && !mCertificationMark.description.isEmpty()){
                        mCertificationMarkDescription.setTypeface(null, Typeface.NORMAL);
                        mCertificationMarkDescription.setText(Html.fromHtml(mCertificationMark.description));
                    }
                    if(mCertificationMark.homepage != null && !mCertificationMark.homepage.isEmpty()){
                        mCertificationMarkHomepage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri uri = Uri.parse(mCertificationMark.homepage);
                                Intent intent = new Intent (Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });

                    }else{
                        mCertificationMarkHomepage.setVisibility(View.GONE);
                    }

                    mCertificationMarkNameTextView.setText(mCertificationMark.certificationName);
                    if(mCertificationMark.logotypeUrl != null && !mCertificationMark.logotypeUrl.isEmpty()){
                        mImageLoader.loadImage(mCertificationMark.logotypeUrl, new SimpleImageLoadingListener(){
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                mCertificationMarkNameTextView.setVisibility(View.GONE);
                                mCertificationMarkLogo.setImageBitmap(loadedImage);
                            }
                        });
                    }
                    else{
                        mCertificationMarkLogo.setVisibility(View.GONE);
                    }
                    setContentView(mView);
                }
                else {
                    Toast.makeText(this, "Failed to load certification mark info!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

        getSupportLoaderManager().destroyLoader(loader.getId());
        updateLoaderIndicator();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<RESTLoader.RESTResponse> restResponseLoader) {

    }
}
