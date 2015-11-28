package consumentor.shopgun.aidroid.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.TitlePageIndicator;

import consumentor.shopgun.aidroid.fragment.AdviceListFragment;
import consumentor.shopgun.aidroid.fragment.ProductOverviewFragment;
import consumentor.shopgun.aidroid.fragment.ProductFactsFragment;
import consumentor.shopgun.aidroid.fragment.ProductInfoFragment;
import consumentor.shopgun.aidroid.fragment.ProductListFragment;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.util.Util;

import java.util.Date;

public class ProductActivity extends PagerActivity implements
        LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>,
        ProductOverviewFragment.ProductOverviewFragmentListener {

    private static final int LOADER_GET_PRODUCT = 0x1;
    private static final int NUM_PAGES = 4;
    public static final String EXTRA_PRODUCT = "org.consumentor.shopgun.extra.PRODUCT";
    public static final String EXTRA_PRODUCT_ID = "org.consumentor.shopgun.extra.PRODUCT_ID";
    private static final String TAG = ProductActivity.class.getName();
    private static final int PRODUCT_OVERVIEW_SLIDE = 0;
    private static final int PRODUCT_ADVICES_SLIDE = 1;
    private static final int PRODUCT_FACTS_SLIDE = 2;
    private static final int PRODUCT_INFO_SLIDE = 3;
    private static final int CERTIFIED_ALTERNATIVES_SLIDE = 4;
    private String mBarcode;
    private Product mProduct;

    private ImageLoader mImageLoader;
    private ScreenSlidePagerAdapter mPagerAdapter;

    private Context mContext;
    private View mContentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mContentView = getLayoutInflater().inflate(R.layout.activity_product, null);

        mPager = (ViewPager) mContentView.findViewById(R.id.pager);
        mPagerIndicator = (TitlePageIndicator)mContentView.findViewById(R.id.pagerIndicator);
        mImageLoader = ImageLoader.getInstance();

        Uri uri;
        if (getIntent().getExtras().containsKey(ScanActivity.EXTRA_BARCODE)){
            mBarcode = getIntent().getExtras().getString(ScanActivity.EXTRA_BARCODE);
            uri = Uri.parse(RESTLoader.BASE_URL +"/productsbygtin/" +mBarcode);
            loadProduct(uri);
        }else if(getIntent().getExtras().containsKey(EXTRA_PRODUCT_ID)){
            int productId = getIntent().getExtras().getInt(EXTRA_PRODUCT_ID);
            uri = Uri.parse(RESTLoader.BASE_URL +"/products/" +productId);
            loadProduct(uri);
        }else if (getIntent().getExtras().containsKey(EXTRA_PRODUCT)){
            mProduct = (Product) getIntent().getExtras().getSerializable(EXTRA_PRODUCT);
            setupView();
        }
    }

    private void loadProduct(Uri uri) {
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        Loader<RESTLoader.RESTResponse> restResponseLoader = getSupportLoaderManager().initLoader(LOADER_GET_PRODUCT, args, this);
        showLoaderIndicator(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
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
        int    code = data.getCode();
        String json = data.getData();

        switch (loader.getId()){

            case LOADER_GET_PRODUCT:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mProduct = gson.fromJson(json, Product.class);
                    setupView();
                }
                else {
                    Toast.makeText(this, "Failed to load product!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

        getSupportLoaderManager().destroyLoader(loader.getId());
        updateLoaderIndicator();
    }

    private void setupView() {
        View errorInfo = mContentView.findViewById(R.id.errorInfo);
        getActionBar().setSubtitle(mProduct.gtin);
        getActionBar().setIcon(R.drawable.ic_product);
        if (Util.isNullOrEmpty(mProduct.name)){
            setTitle(getResources().getString(R.string.productNotFound));
            mPager.setVisibility(View.GONE);
            if (mProduct.producerId != 0){
                Toast.makeText(this, "Du länkas vidare till producenten...", Toast.LENGTH_LONG).show();
                Intent companyIntent = new Intent(this, CompanyActivity.class);
                companyIntent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, mProduct.producerId);
                startActivity(companyIntent);
            }
        }else{
            errorInfo.setVisibility(View.GONE);
            String productTitle = mProduct.name;
            if (!Util.isNullOrEmpty(mProduct.quantity) && !Util.isNullOrEmpty(mProduct.quantityUnit)){
                productTitle += " "+ mProduct.quantity +" " +mProduct.quantityUnit;
            }
            setTitle(productTitle);
    //        mImageLoader.loadImage(mProduct.imageSmall, new SimpleImageLoadingListener(){
    //            @Override
    //            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
    //                Drawable icon = new BitmapDrawable(getResources(), loadedImage);
    //                getActionBar().setIcon(icon);
    //                ImageView homeIconImageView = (ImageView)findViewById(android.R.id.home);
    //                homeIconImageView.setPadding(10, 0, 10, 0);
    //            }
    //        });
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            mPagerIndicator.setViewPager(mPager);

        }
        setContentView(mContentView);
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
        Log.d(TAG, String.format("Loader reset: {0}", loader.getId()));
    }

    @Override
    public void onShowCertifiedAlternatives(View view) {
        mPager.setCurrentItem(CERTIFIED_ALTERNATIVES_SLIDE, true);
    }

    @Override
    public void onShowAdvices(View view) {
        mPager.setCurrentItem(PRODUCT_ADVICES_SLIDE, true);
    }

    @Override
    public void onShowProductFacts(View view) {
        mPager.setCurrentItem(PRODUCT_FACTS_SLIDE, true);
    }

    @Override
    public void onShowProductInfo(View view) {
        mPager.setCurrentItem(PRODUCT_INFO_SLIDE, true);
    }

    @Override
    public void onStartCompanyActivity(View view) {
        Intent intent = new Intent(this, CompanyActivity.class);
        intent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, mProduct.brand.company.id);
        startActivity(intent);
    }

    @Override
    public void onStartBrandActivity(View view) {
        Intent intent = new Intent(this, BrandActivity.class);
        intent.putExtra(BrandActivity.EXTRA_BRAND_ID, mProduct.brand.id);
        startActivity(intent);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putSerializable(EXTRA_PRODUCT, mProduct);
            Fragment fragment = null;
            switch (position){
                case PRODUCT_OVERVIEW_SLIDE:{
                    fragment = Fragment.instantiate(mContext, ProductOverviewFragment.class.getName(), args);
                    break;
                }
                case PRODUCT_ADVICES_SLIDE:{
                    Bundle adviceListArgs = new Bundle();
                    adviceListArgs.putSerializable(AdviceListFragment.EXTRA_ADVICEABLE, mProduct);
                    fragment = Fragment.instantiate(mContext, AdviceListFragment.class.getName(), adviceListArgs);
                    break;
                }
                case PRODUCT_INFO_SLIDE:{
                    fragment = Fragment.instantiate(mContext, ProductInfoFragment.class.getName(), args);
                    break;
                }
                case PRODUCT_FACTS_SLIDE:{
                    fragment = Fragment.instantiate(mContext, ProductFactsFragment.class.getName(), args);
                    break;
                }
                case CERTIFIED_ALTERNATIVES_SLIDE:{
                    args = new Bundle();
                    args.putBoolean(ProductListFragment.EXTRA_ONLY_CERTIFIED_PRODUCTS, true);
                    args.putSerializable(ProductListFragment.EXTRA_PRODUCT_CATEGORY, mProduct.productCategory);
                    fragment = Fragment.instantiate(mContext, ProductListFragment.class.getName(), args);
                    break;
                }
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case PRODUCT_OVERVIEW_SLIDE:{
                    return getString(R.string.title_overview);
                }
                case PRODUCT_ADVICES_SLIDE:{
                    return getString(R.string.title_advice_list);
                }
                case PRODUCT_INFO_SLIDE:{
                    return getString(R.string.title_product_info);
                }
                case PRODUCT_FACTS_SLIDE:{
                    return getString(R.string.title_product_facts);
                }
                case CERTIFIED_ALTERNATIVES_SLIDE:{
                    return getString(R.string.title_product_list_certified_alternatives);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}