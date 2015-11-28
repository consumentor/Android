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
import consumentor.shopgun.aidroid.fragment.IngredientInfoFragment;
import consumentor.shopgun.aidroid.fragment.IngredientOverviewFragment;
import consumentor.shopgun.aidroid.model.Ingredient;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.Date;

public class IngredientActivity extends PagerActivity implements
        IngredientOverviewFragment.IngredientOverviewFragmentListener,
    LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>{

        private static final int LOADER_GET_INGREDIENT = 0x1;
        private static final int NUM_PAGES = 3;
        public static final String EXTRA_INGREDIENT = "org.consumentor.shopgun.extra.INGREDIENT";
        private static final int INGREDIENT_OVERVIEW_SLIDE = 0;
        private static final int INGREDIENT_ADVICES_SLIDE = 1;
        private static final int INGREDIENT_WIKIPEDIA_SLIDE = 2;
        private int mIngredientId;
        private Ingredient mIngredient;
        private ImageLoader mImageLoader;
        private ScreenSlidePagerAdapter mPagerAdapter;

        private Context mContext;
        public static final String EXTRA_INGREDIENT_ID = "org.consumentor.shopgun.extra.EXTRA_BRAND_ID";
    private View mContentView;

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mContext = this;
            getActionBar().setDisplayHomeAsUpEnabled(true);
            mContentView = getLayoutInflater().inflate(R.layout.activity_ingredient, null);

            mPager = (ViewPager) mContentView.findViewById(R.id.pager);
            mPagerIndicator = (TitlePageIndicator) mContentView.findViewById(R.id.pagerIndicator);
            mImageLoader = ImageLoader.getInstance();

            if (getIntent().getExtras().containsKey(EXTRA_INGREDIENT_ID)){
                mIngredientId = getIntent().getExtras().getInt(EXTRA_INGREDIENT_ID);

                Uri uri = Uri.parse(RESTLoader.BASE_URL +"/ingredients/" + mIngredientId);

                Bundle args = new Bundle();
                Bundle params = new Bundle();
                args.putParcelable(RESTLoader.ARGS_URI, uri);
                args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
                args.putParcelable(RESTLoader.ARGS_PARAMS, params);
                // Initialize the Loader.
                getSupportLoaderManager().initLoader(LOADER_GET_INGREDIENT, args, this);
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

                case LOADER_GET_INGREDIENT:{
                    if (code == 200 && !json.equals("")) {
                        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                        mIngredient = gson.fromJson(json, Ingredient.class);
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



        setTitle(mIngredient.name);
        if (mIngredient.alternativeNames != null && mIngredient.alternativeNames.size() > 0){
            String alternativeNames = "";
            StringBuilder stringBuilder = new StringBuilder();
            for (String alternativeName : mIngredient.alternativeNames){
                stringBuilder.append(alternativeName);
                stringBuilder.append(" - ");
            }
            getActionBar().setSubtitle(stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length()).toString());
            getActionBar().setIcon(R.drawable.ic_ingredient);
        }
        //Todo: ingredient images
//                        mImageLoader.loadImage(mIngredient.imageMedium, new SimpleImageLoadingListener(){
//                            @Override
//                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                mImageProgressBar.setVisibility(View.GONE);
//                                mIngredientImage.setImageBitmap(loadedImage);
//                            }
//                        });
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
        mPager.setCurrentItem(1, true);
    }

    @Override
    public void onShowWikipedia(View view) {
        mPager.setCurrentItem(INGREDIENT_WIKIPEDIA_SLIDE, true);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
            public ScreenSlidePagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                Bundle args = new Bundle();
                args.putSerializable(EXTRA_INGREDIENT, mIngredient);
                Fragment fragment = null;
                switch (position){
                    case INGREDIENT_OVERVIEW_SLIDE:{
                        fragment = Fragment.instantiate(mContext, IngredientOverviewFragment.class.getName(), args);
                        break;
                    }

                    case INGREDIENT_ADVICES_SLIDE:{
                        Bundle adviceListArgs = new Bundle();
                        adviceListArgs.putSerializable(AdviceListFragment.EXTRA_ADVICEABLE, mIngredient);
                        fragment = Fragment.instantiate(mContext, AdviceListFragment.class.getName(), adviceListArgs);
                        break;
                    }
                    case INGREDIENT_WIKIPEDIA_SLIDE:{
                        fragment = Fragment.instantiate(mContext, IngredientInfoFragment.class.getName(), args);
                        break;
                    }
                }
                return fragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case INGREDIENT_OVERVIEW_SLIDE:{
                        return getString(R.string.title_overview);
                    }

                    case INGREDIENT_ADVICES_SLIDE:{
                        return getString(R.string.title_advice_list);
                        
                    }
                    case INGREDIENT_WIKIPEDIA_SLIDE:{
                        return getString(R.string.title_ingredient_info);
                        
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
