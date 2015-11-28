package consumentor.shopgun.aidroid.view;

import android.content.Context;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.TitlePageIndicator;

import consumentor.shopgun.aidroid.fragment.AdviceListFragment;
import consumentor.shopgun.aidroid.fragment.BrandListFragment;
import consumentor.shopgun.aidroid.fragment.CompanyCSRFragment;
import consumentor.shopgun.aidroid.fragment.CompanyFactsFragment;
import consumentor.shopgun.aidroid.fragment.CompanyInfoFragment;
import consumentor.shopgun.aidroid.fragment.CompanyOverviewFragment;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.Date;

public class CompanyActivity extends PagerActivity
        implements CompanyOverviewFragment.CompanyOverviewFragmentListener,
        LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>
{

    private static final int LOADER_GET_COMPANY = 0x1;
    private static final int NUM_PAGES = 6;
    public static final String EXTRA_COMPANY = "org.consumentor.shopgun.extra.EXTRA_BRAND";
    private int mCompanyId;
    private Company mCompany;

    private ImageLoader mImageLoader;
    private ScreenSlidePagerAdapter mPagerAdapter;

    private Context mContext;
    public static final String EXTRA_COMPANY_ID = "org.consumentor.shopgun.extra.EXTRA_BRAND_ID";
    private View mContentView;

    private static final int COMPANY_OVERVIEW_SLIDE = 0;
    private static final int COMPANY_ADVICES_SLIDE = 1;
    private static final int COMPANY_FACTS_SLIDE = 2;
    private static final int COMPANY_INFO_SLIDE = 3;
    private static final int COMPANY_CSR_SLIDE = 4;
    private static final int COMPANY_BRANDS_SLIDE = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mContentView = getLayoutInflater().inflate(R.layout.activity_company, null);

        mPager = (ViewPager) mContentView.findViewById(R.id.pager);
        mPagerIndicator = (TitlePageIndicator)mContentView.findViewById(R.id.pagerIndicator);
        mImageLoader = ImageLoader.getInstance();

        if (getIntent().getExtras().containsKey(EXTRA_COMPANY_ID)){
            mCompanyId = getIntent().getExtras().getInt(EXTRA_COMPANY_ID);

            Uri uri = Uri.parse(RESTLoader.BASE_URL +"/companies/" + mCompanyId);

            Bundle args = new Bundle();
            Bundle params = new Bundle();
            args.putParcelable(RESTLoader.ARGS_URI, uri);
            args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
            args.putParcelable(RESTLoader.ARGS_PARAMS, params);
            // Initialize the Loader.
            getSupportLoaderManager().initLoader(LOADER_GET_COMPANY, args, this);
            showLoaderIndicator(true);
        }
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

            case LOADER_GET_COMPANY:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mCompany = gson.fromJson(json, Company.class);
                    setupView();

                }
                else {
                    Toast.makeText(this, "Failed to load company!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        getSupportLoaderManager().destroyLoader(loader.getId());
        updateLoaderIndicator();
    }

    private void setupView() {
        setTitle(mCompany.name);
        getActionBar().setIcon(R.drawable.ic_company);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPagerIndicator.setViewPager(mPager);
        setContentView(mContentView);
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {

    }

    @Override
    public void onShowAdvices(View view) {
        mPager.setCurrentItem(COMPANY_ADVICES_SLIDE, true);
    }

    @Override
    public void onCompanyInfoButtonPressed(View view) {
        mPager.setCurrentItem(COMPANY_INFO_SLIDE, true);
    }

    @Override
    public void onShowCompanyBrands(View view) {
        mPager.setCurrentItem(COMPANY_BRANDS_SLIDE, true);
    }

    @Override
    public void onShowCSR(View view) {
        mPager.setCurrentItem(COMPANY_CSR_SLIDE, true);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putSerializable(EXTRA_COMPANY, mCompany);
            Fragment fragment = null;
            switch (position){
                case COMPANY_OVERVIEW_SLIDE:{
                    fragment = Fragment.instantiate(mContext, CompanyOverviewFragment.class.getName(), args);
                    break;
                }

                case COMPANY_ADVICES_SLIDE:{
                    Bundle adviceListArgs = new Bundle();
                    adviceListArgs.putSerializable(AdviceListFragment.EXTRA_ADVICEABLE, mCompany);
                    fragment = Fragment.instantiate(mContext, AdviceListFragment.class.getName(), adviceListArgs);
                    break;
                }
                case COMPANY_INFO_SLIDE:{
                    fragment = Fragment.instantiate(mContext, CompanyInfoFragment.class.getName(), args);
                    break;
                }
                case COMPANY_CSR_SLIDE:{
                    fragment = Fragment.instantiate(mContext, CompanyCSRFragment.class.getName(), args);
                    break;
                }
                case COMPANY_FACTS_SLIDE:{
                    fragment = Fragment.instantiate(mContext, CompanyFactsFragment.class.getName(), args);
                    break;
                }
                case COMPANY_BRANDS_SLIDE:{
                    fragment = Fragment.instantiate(mContext, BrandListFragment.class.getName(), args);
                    break;
                }
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case COMPANY_OVERVIEW_SLIDE:{
                    return getString(R.string.title_overview);
                    
                }
                case COMPANY_ADVICES_SLIDE:{
                    return getString(R.string.title_advice_list);
                    
                }
                case COMPANY_INFO_SLIDE:{
                    return getString(R.string.title_company_info);
                    
                }
                case COMPANY_CSR_SLIDE:{
                    return getString(R.string.title_company_csr);
                    
                }
                case COMPANY_FACTS_SLIDE:{
                    return getString(R.string.title_company_facts);
                    
                }
                case COMPANY_BRANDS_SLIDE:{
                    return getString(R.string.title_company_brands);
                    
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
