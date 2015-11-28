package consumentor.shopgun.aidroid.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.authentication.ShopgunAccount;
import consumentor.shopgun.aidroid.model.Advice;
import consumentor.shopgun.aidroid.model.UserAdviceRating;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.util.Util;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class AdviceActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>{

    public static final String EXTRA_ADVICE_ID = "org.consumentor.shopgun.extra.ADVICE_ID";
    private static final int LOADER_GET_ADVICE = 0x1;
    private static final int LOADER_GET_ADVICE_RATING = 0x2;
    private static final int LOADER_SEND_RATING = 0x3;

    private TextView mAdviceHeadline;
    private View mDelimiter;
    private TextView mAdviceScore;

    private Button mRateUpButton;
    private Button mRateDownButton;

    private Advice mAdvice;
    private Context mContext;
    private View mView;
    private int mAdviceId;
    private String TAG = getClass().getSimpleName().toString();
    private String mAuthToken;
    private AccountManager mAccountManager;
    private WebView mAdviceTextWebView;

    private SeekBar mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mView = getLayoutInflater().inflate(R.layout.activity_advice, null);

        mAdviceHeadline = (TextView)mView.findViewById(R.id.adviceHeadline);
        mDelimiter = mView.findViewById(R.id.delimiter);
        mAdviceTextWebView = (WebView)mView.findViewById(R.id.adviceTextWebView);

        mAdviceScore = (TextView)mView.findViewById(R.id.adviceScore);

        mRateUpButton = (Button) mView.findViewById(R.id.rateUpButton);
        mRateDownButton = (Button)mView.findViewById(R.id.rateDownButton);
        mSlider = (SeekBar) mView.findViewById(R.id.slider);
        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() <= 50){
                    mSlider.setProgress(0);
                    onRateDownPressed(seekBar);
                }else{
                    mSlider.setProgress(100);
                    onRateUpPressed(seekBar);
                }

            }
        });

        // Check if intent is url
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null){
            List<String> params = data.getPathSegments();
            String first = params.get(0); // "advices"
            mAdviceId = Integer.parseInt(params.get(1)); // "1234"
        }
        else{
            mAdviceId = getIntent().getIntExtra(EXTRA_ADVICE_ID, -1);
        }

        Bundle args = new Bundle();
        Uri uri = Uri.parse(RESTLoader.BASE_URL +"/advices/" +mAdviceId);
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getSupportLoaderManager().initLoader(LOADER_GET_ADVICE, args, this);
        showLoaderIndicator(true);

        getUsersAdviceRating();
    }

    //Todo Needs refactoring!!!
    private void rateAdvice(final boolean rateUp) {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(ShopgunAccount.ACCOUNT_TYPE);
        Account shopgunAccount;
        if(accounts.length > 0){
            shopgunAccount = accounts[0];
            mAccountManager.getAuthToken(shopgunAccount, ShopgunAccount.AUTHTOKEN_TYPE_USER, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> bundleAccountManagerFuture) {
                    try {
                        Bundle bnd = bundleAccountManagerFuture.getResult();
                        mAuthToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        // start request here
                        sendAdviceRating(rateUp);
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }
            }, null);


        }else {
            mAccountManager.addAccount(ShopgunAccount.ACCOUNT_TYPE, ShopgunAccount.AUTHTOKEN_TYPE_USER, null, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {

                    Bundle bnd = null;
                    try {
                        // Todo For some reason bnd doesn't contain AccountManager.KEY_AUTHTOKEN although set in LoginActivity - seems to be removed somewhere...
                        // there fore sendAdviceRating can't be called here...
                        bnd = future.getResult();
                        Log.d("Moep", "AddNewAccount Bundle is " + bnd);
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                    }



                }
            }, null);
        }
    }

    private void getUsersAdviceRating() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(ShopgunAccount.ACCOUNT_TYPE);
        Account shopgunAccount;
        if(accounts.length > 0){
            shopgunAccount = accounts[0];
            mAccountManager.getAuthToken(shopgunAccount, ShopgunAccount.AUTHTOKEN_TYPE_USER, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> bundleAccountManagerFuture) {
                    try {
                        Bundle bnd = bundleAccountManagerFuture.getResult();
                        mAuthToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        // start request here

                        sendGetUserAdviceRatingRequest();
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }
    }

    private void sendGetUserAdviceRatingRequest(){
        Bundle args = new Bundle();
        Uri uri = Uri.parse(RESTLoader.SERVER_URL +"/ShopgunApp/Advices/" +mAdviceId +"/UserAdviceRatings?authtoken=" +(mAuthToken) );
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getSupportLoaderManager().initLoader(LOADER_GET_ADVICE_RATING, args, this);
        showLoaderIndicator(true);
    }

    private void sendAdviceRating(boolean rateUp) {

        UserAdviceRating rating = new UserAdviceRating();
        rating.adviceId = mAdviceId;
        rating.rating = rateUp ? 1 : -1;

        Bundle args = new Bundle();
        Uri uri = Uri.parse(RESTLoader.SERVER_URL +"/ShopgunApp/UserAdviceRatings?authtoken=" +(mAuthToken) );
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.PUT);
        Bundle params = new Bundle();
        params.putSerializable("userAdviceRating", rating);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getSupportLoaderManager().initLoader(LOADER_SEND_RATING, args, this);
        showLoaderIndicator(true);
    }

    public void onAdvisorButtonPressed(View view){
        Intent advisorIntent = new Intent(mContext, AdvisorActivity.class);
        advisorIntent.putExtra(AdvisorActivity.EXTRA_ADVISOR_ID, mAdvice.mentorInfo.id);
        startActivity(advisorIntent);
    }

    public void onRateUpPressed(View view){
        rateAdvice(true);
    }

    public void onRateDownPressed(View view){
        rateAdvice(false);
    }

    private void initAdvisorButton() {
        View advisorButton = mView.findViewById(R.id.advisorButton);
        final TextView advisorName = (TextView)advisorButton.findViewById(R.id.advisorName);
        advisorName.setText(mAdvice.mentorInfo.name);
        final ImageView advisorLogo = (ImageView) advisorButton.findViewById(R.id.advisorLogo);
        if (!Util.isNullOrEmpty(mAdvice.mentorInfo.logotypeUrl)){
            ImageLoader.getInstance().loadImage(mAdvice.mentorInfo.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    advisorLogo.setImageBitmap(loadedImage);
                    advisorName.setVisibility(View.GONE);
                }
            });
        }else {
            advisorLogo.setVisibility(View.GONE);
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

        int    code = data.getCode();
        String json = data.getData();

        switch (loader.getId()){

            case LOADER_GET_ADVICE:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mAdvice = gson.fromJson(json, Advice.class);

                    mAdviceHeadline.setText(mAdvice.label);
                    mAdviceTextWebView.loadData(mAdvice.adviceText, "text/html; charset=UTF-8", null);
                    int color = 0;
                    switch (mAdvice.semaphoreValue){
                        case Advice.SEMAPHORE_GREEN: {
                            color= getResources().getColor(R.color.shopgun_green);
                            break;
                        }
                        case Advice.SEMAPHORE_YELLOW:{
                            color = getResources().getColor(R.color.shopgun_yellow_darker);
                            break;
                        }
                        case Advice.SEMAPHORE_RED:{
                            color = getResources().getColor(R.color.shopgun_red);
                            break;
                        }
                        case Advice.SEMAPHORE_TRANSPARENT:{
                            color = getResources().getColor(R.color.shopgun_gray_lighter);
                            break;
                        }
                        default:{
                            color = getResources().getColor(R.color.shopgun_gray_lighter);
                        }
                    }
                    mAdviceHeadline.setTextColor(color);
                    mDelimiter.setBackgroundColor(color);
                    mAdviceScore.setText("" +mAdvice.adviceScore);

                    initAdvisorButton();
//                    Todo: Api must be updated with advisor label url
//                    ImageLoader.getInstance().loadImage(mAdvice.mentorInfo.label, new SimpleImageLoadingListener(){
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            mAdvisorButton.setCompoundDrawables(new BitmapDrawable(getResources(), loadedImage), null, null, null);
//                        }
//                    });

                    initItemButton();

                    showLoaderIndicator(false);
                    setContentView(mView);
                }
                else {
                    Toast.makeText(this, "Failed to load advice!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case LOADER_SEND_RATING:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    UserAdviceRating rating = gson.fromJson(json, UserAdviceRating.class);
                    mRateUpButton.setEnabled(rating.rating == -1);
                    mRateDownButton.setEnabled(rating.rating == 1);
                    int score = Integer.parseInt(mAdviceScore.getText().toString()) +rating.rating;
                    mAdviceScore.setText("" +score);
                }else if (code == 401){
                    mAccountManager.invalidateAuthToken(ShopgunAccount.ACCOUNT_TYPE, mAuthToken);
                    rateAdvice(true);
                }
                break;
            }
            case LOADER_GET_ADVICE_RATING:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    List<UserAdviceRating> ratings = gson.fromJson(json, new TypeToken<List<UserAdviceRating>>(){}.getType());
                    if(!ratings.isEmpty()){
                        UserAdviceRating rating = ratings.get(0);
                        mSlider.setProgress(rating.rating < 0 ? 0 : mSlider.getMax());

                    }
                }else if (code == 401){
                    mAccountManager.invalidateAuthToken(ShopgunAccount.ACCOUNT_TYPE, mAuthToken);
                    getUsersAdviceRating();
                }
                break;
            }
        }
        getSupportLoaderManager().destroyLoader(loader.getId());
        updateLoaderIndicator();
    }

    public void onItemButtonPressed(View view){
        Intent intent = null;
        switch (mAdvice.getItemType()){
            case PRODUCT:
                intent = new Intent(this, ProductActivity.class);
                intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, mAdvice.getItemId());
                break;
            case INGREDIENT:
                intent = new Intent(this, IngredientActivity.class);
                intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_ID, mAdvice.getItemId());
                break;
            case COMPANY:
                intent = new Intent(this, CompanyActivity.class);
                intent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, mAdvice.getItemId());
                break;
            case BRAND:
                intent = new Intent(this, BrandActivity.class);
                intent.putExtra(BrandActivity.EXTRA_BRAND_ID, mAdvice.getItemId());
                break;
        }

        startActivity(intent);
    }

    private void initItemButton() {
        TextView itemButton = (TextView) mView.findViewById(R.id.itemButton);
        itemButton.setText(mAdvice.itemName);
        int itemIcon = R.drawable.ic_product;
        switch (mAdvice.getItemType()){
            case PRODUCT:itemIcon = R.drawable.ic_product;break;
            case INGREDIENT:itemIcon= R.drawable.ic_ingredient;break;
            case COMPANY:itemIcon = R.drawable.ic_company;break;
            case BRAND:itemIcon=R.drawable.ic_brand;
        }
        itemButton.setCompoundDrawables(getResources().getDrawable(itemIcon), null, null, null);

        //Todo: Set correct icon on button depending on advice type (ingredient or product etc...)
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> restResponseLoader) {

    }
}
