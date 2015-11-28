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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewpagerindicator.TitlePageIndicator;

import consumentor.shopgun.aidroid.fragment.AdviceListFragment;
import consumentor.shopgun.aidroid.fragment.BrandFactsFragment;
import consumentor.shopgun.aidroid.fragment.BrandInfoFragment;
import consumentor.shopgun.aidroid.fragment.BrandOverviewFragment;
import consumentor.shopgun.aidroid.fragment.ProductListFragment;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.Date;

public class BrandActivity extends PagerActivity implements
        BrandOverviewFragment.BrandOverviewFragmentListener,
        BrandFactsFragment.BrandFactsFragmentListener,
        LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>{

    private static final int LOADER_GET_BRAND = 0x1;
    private static final int NUM_PAGES = 5;
    public static final String EXTRA_BRAND = "org.consumentor.shopgun.extra.EXTRA_BRAND";
    private int mBrandId;
    private Brand mBrand;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private Context mContext;
    public static final String EXTRA_BRAND_ID = "org.consumentor.shopgun.extra.EXTRA_BRAND_ID";
    private View mContentView;

    private static final int BRAND_OVERVIEW_SLIDE = 0;
    private static final int BRAND_ADVICES_SLIDE = 1;
    private static final int BRAND_FACTS_SLIDE = 2;
    private static final int BRAND_INFO_SLIDE = 3;
    private static final int BRAND_PRODUCTS_SLIDE = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mContentView = getLayoutInflater().inflate(R.layout.activity_brand, null);

        mPager = (ViewPager) mContentView.findViewById(R.id.pager);
        mPagerIndicator = (TitlePageIndicator)mContentView.findViewById(R.id.pagerIndicator);

        if (getIntent().getExtras().containsKey(EXTRA_BRAND_ID)){
            mBrandId = getIntent().getExtras().getInt(EXTRA_BRAND_ID);
            Uri uri = Uri.parse(RESTLoader.BASE_URL +"/brands/" + mBrandId);
            Bundle args = new Bundle();
            Bundle params = new Bundle();
            args.putParcelable(RESTLoader.ARGS_URI, uri);
            args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
            args.putParcelable(RESTLoader.ARGS_PARAMS, params);
            // Initialize the Loader.
            getSupportLoaderManager().initLoader(LOADER_GET_BRAND, args, this);
            showLoaderIndicator(true);
        }
    }

    private void setupView() {
        setTitle(mBrand.name);
        getActionBar().setIcon(R.drawable.ic_brand);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPagerIndicator.setViewPager(mPager);
        setContentView(mContentView);
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

            case LOADER_GET_BRAND:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mBrand = gson.fromJson(json, Brand.class);
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

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {

    }

    @Override
    public void onShowAdvices(View view) {
        mPager.setCurrentItem(BRAND_ADVICES_SLIDE,true);
    }


    @Override
    public void onBrandInfoButtonPressed(View view) {
        mPager.setCurrentItem(BRAND_INFO_SLIDE, true);
    }

    @Override
    public void onStartCompanyActivity(View view) {
        Intent intent = new Intent(this, CompanyActivity.class);
        intent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, mBrand.company.id);
        startActivity(intent);
    }

    @Override
    public void onShowProducts(View view) {
        mPager.setCurrentItem(BRAND_PRODUCTS_SLIDE, true);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putSerializable(EXTRA_BRAND, mBrand);
            Fragment fragment = null;
            switch (position){
                case BRAND_OVERVIEW_SLIDE:{
                    fragment = Fragment.instantiate(mContext, BrandOverviewFragment.class.getName(), args);
                    break;
                }
                case BRAND_ADVICES_SLIDE:{
                    Bundle adviceListArgs = new Bundle();
                    adviceListArgs.putSerializable(AdviceListFragment.EXTRA_ADVICEABLE, mBrand);
                    fragment = Fragment.instantiate(mContext, AdviceListFragment.class.getName(), adviceListArgs);
                    break;
                }
                case BRAND_INFO_SLIDE:{
                    fragment = Fragment.instantiate(mContext, BrandInfoFragment.class.getName(), args);
                    break;
                }
                case BRAND_FACTS_SLIDE:{
                    fragment = Fragment.instantiate(mContext, BrandFactsFragment.class.getName(), args);
                    break;
                }
                case BRAND_PRODUCTS_SLIDE:{
                    fragment = Fragment.instantiate(mContext, ProductListFragment.class.getName(), args);
                    break;
                }
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case BRAND_OVERVIEW_SLIDE:{
                    return getString(R.string.title_overview);
                }

                case BRAND_ADVICES_SLIDE:{
                    return getString(R.string.title_advice_list);
                    
                }
                case BRAND_INFO_SLIDE:{
                    return getString(R.string.title_brand_info);
                    
                }
                case BRAND_FACTS_SLIDE:{
                    return getString(R.string.title_brand_facts);
                    
                }
                case BRAND_PRODUCTS_SLIDE:{
                    return getString(R.string.brand_products);
                    
                }
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
