package consumentor.shopgun.aidroid.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.fragment.ProductListFragment;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.ArrayList;
import java.util.Date;

public class BatchScanActivity extends BaseActivity implements ScanditSDKListener, LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse> {

    public static final int RESULT_SCAN_FAILED = 0x0;
    public static final int RESULT_SCAN_SUCCEEDED = 0x1;
    private static String SCANDIT_SDK_KEY = "e5Y91A/JEeORlTrQtqMWUp0IkkKgCdohxzSdtejmNog";
    private ScanditSDKAutoAdjustingBarcodePicker mPicker;

    private Button mDoneScanningButton;

    private ArrayList<Product> mProducts;

    private boolean mReadyToCompare;

    private int mNumberScans;
    private int mHighestFinishedLoaderId;
    private ProductListFragment mProductListFragment;
    private CheckedTextView mScanMultipleChkBox;
    private Button mFreeTextSearchButton;
    private View mProductListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_scan);

        mScanMultipleChkBox = (CheckedTextView) findViewById(R.id.scanMultipleCheckBox);
        mScanMultipleChkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });

        // Show the Up button in the action bar.
        setupActionBar();

        updateLoaderIndicator();

        mProducts = new ArrayList<Product>();

        Bundle args = new Bundle();
        args.putSerializable(ProductListFragment.EXTRA_PRODUCTS, new ArrayList<Product>());
        mProductListFragment = (ProductListFragment) Fragment.instantiate(this, ProductListFragment.class.getName(), args);
        mProductListContainer = findViewById(R.id.productListContainer);
        mProductListContainer.setVisibility(View.INVISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.productListContainer, mProductListFragment)
                .commit();

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.scannerContainer);
        mPicker = new ScanditSDKAutoAdjustingBarcodePicker(this, SCANDIT_SDK_KEY, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);
        mPicker.set1DScanningEnabled(true);
        mPicker.setEan13AndUpc12Enabled(true);
        mPicker.setEan8Enabled(true);
        mPicker.getOverlayView().addListener(this);
        frameLayout.addView(mPicker);

        mDoneScanningButton = (Button) findViewById(R.id.doneScanningButton);
        mDoneScanningButton.setVisibility(View.GONE);
        mDoneScanningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumberScans == mHighestFinishedLoaderId){
                    startProductComparison();
                }
                else{
                    mReadyToCompare = true;
                }
            }
        });

        mFreeTextSearchButton = (Button) findViewById(R.id.gtinInputButton);
        mFreeTextSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });
    }

    private void startProductComparison() {
        mPicker.stopScanning();
        Intent intent = new Intent(this, ProductComparisonActivity.class);
        intent.putExtra(ProductComparisonActivity.EXTRA_PRODUCTS, mProducts);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPicker != null ){
            mPicker.startScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPicker != null ){
            mPicker.stopScanning();
        }
    }

    @Override
    public void didCancel() {
        setResult(RESULT_SCAN_FAILED);
        finish();
    }

    @Override
    public void didScanBarcode(String barcode, String symbology) {

        if (!mScanMultipleChkBox.isChecked()){
            Intent productIntent = new Intent(this, ProductActivity.class);
            productIntent.putExtra(ScanActivity.EXTRA_BARCODE, barcode);
            startActivity(productIntent);
        }else{
            Uri uri = Uri.parse(RESTLoader.BASE_URL +"/productsbygtin/" +barcode);

            Bundle args = new Bundle();
            Bundle params = new Bundle();
            args.putParcelable(RESTLoader.ARGS_URI, uri);
            args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
            args.putParcelable(RESTLoader.ARGS_PARAMS, params);
            // Initialize the Loader.
            getSupportLoaderManager().initLoader(++mNumberScans, args, this);

            updateLoaderIndicator();
        }
    }

    @Override
    public void didManualSearch(String s) {

    }
    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.batch_scan, menu);
        return true;
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
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int i, Bundle args) {
        if (args != null && args.containsKey(RESTLoader.ARGS_URI) && args.containsKey(RESTLoader.ARGS_PARAMS)) {
            Uri action = args.getParcelable(RESTLoader.ARGS_URI);
            RESTLoader.HTTPVerb verb = args.containsKey(RESTLoader.ARGS_VERB) ? (RESTLoader.HTTPVerb) args.getSerializable(RESTLoader.ARGS_VERB) : RESTLoader.HTTPVerb.GET;
            Bundle params = args.getParcelable(RESTLoader.ARGS_PARAMS);

            return new RESTLoader(this, verb, action, params);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {

        int loaderId = loader.getId();
        if (loaderId > mHighestFinishedLoaderId){
            mHighestFinishedLoaderId = loaderId;
        }
        int    code = data.getCode();
        String json = data.getData();

        if (code == 200 && !json.equals("")) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
            Product product = gson.fromJson(json, Product.class);

            mProducts.add(product);
            mProductListFragment.addProduct(product);

            mScanMultipleChkBox.setVisibility(View.GONE);
            mDoneScanningButton.setVisibility(View.VISIBLE);
            mFreeTextSearchButton.setVisibility(View.GONE);
            mProductListContainer.setVisibility(View.VISIBLE);
        }
        else {
            Toast.makeText(this, "Failed to load product!", Toast.LENGTH_SHORT).show();
        }
        getSupportLoaderManager().destroyLoader(loader.getId());

        updateLoaderIndicator();

        if (mReadyToCompare && mNumberScans == loaderId){
            startProductComparison();
        }
    }

//    private void updateLoaderIndicator() {
//
//        if (getSupportLoaderManager().hasRunningLoaders()) {
//            setProgressBarIndeterminateVisibility(Boolean.TRUE);
//        } else {
//            setProgressBarIndeterminateVisibility(Boolean.FALSE);
//        }
//    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> restResponseLoader) {

    }
}
