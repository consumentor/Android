package consumentor.shopgun.aidroid.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.*;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.plus.PlusClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.fragment.LoginFragment;
import consumentor.shopgun.aidroid.model.LoginResult;
import consumentor.shopgun.aidroid.authentication.AccountAuthenticatorActivity;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.authentication.ShopgunAccountAuthenticator;

import java.io.IOException;
import java.util.Date;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements
        LoginFragment.OnUserLoggedInListener,
        LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        View.OnClickListener{
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello",
            "bar@example.com:world"
    };

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "org.consumentor.shopgun.extra.EMAIL";
    public static final String EXTRA_PASSWORD = "org.consumentor.shopgun.extra.PASSWORD";
    public static final String LOGIN_EMAIL = "org.consumentor.shopgun.extra.LOGIN_EMAIL";
    public static final String LOGIN_PASSWORD = "org.consumentor.shopgun.extra.LOGIN_PASSWORD";
    public static final String LOGIN_PREFS = "org.consumentor.shopgun.prefs.LOGIN";
    public static final String PREFS_SKIP_LOGIN = "org.consumentor.shopgun.prefs.SKIP_LOGIN";
    public static final String PREFS_GOOGLE_SIGN_IN = "org.consumentor.shopgun.prefs.GOOGLE_SIGN_IN";
    private static final int LOADER_SIGN_IN = 0x1;
    private static final int LOADER_REGISTER = 0x2;
    private static final int LOADER_GOOGLE_SIGN_IN = 0x3;
    private static final int REQUEST_CODE_RESOLVE_ERR = 0x0;


    private ProgressDialog mConnectionProgressDialog;
    private PlusClient mPlusClient;
    private ConnectionResult mConnectionResult;
    private final String[] AUTH_SCOPES = {
            //Scopes.PLUS_LOGIN,
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
            //"https://www.googleapis.com/auth/developerssite"
    };

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private LoginFragment facebookLoginFragment;
    private SharedPreferences mAppPreferences;

    private Context mContext;

    private CheckBox mDontShowAgainCheckBox;
    private Button mSkipLoginButton;
    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mPlusClient = new PlusClient.Builder(this, this, this)
                //.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
                .setScopes(AUTH_SCOPES)  // Space separated list of scopes
                .build();
        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        mAccountManager = AccountManager.get(this);
        mAuthTokenType = "org.consumentor.SHOPGUN_USER";

        if (getIntent().hasExtra(EXTRA_EMAIL) && getIntent().hasExtra(EXTRA_PASSWORD)){
            mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
            mPassword = getIntent().getStringExtra(EXTRA_PASSWORD);
            attemptLogin(mEmail, mPassword);
        }
        else{
            setContentView(R.layout.activity_login);
            findViewById(R.id.google_sign_in_button).setOnClickListener(this);

            initViewElements();

            // Set up the login form.
            mEmailView = (EditText) findViewById(R.id.email);
            mEmailView.setText(mEmail);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
                        return true;
                    }
                    return false;
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mLoginStatusView = findViewById(R.id.login_status);
            mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

            findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
                }
            });

            //addFacebookLoginFragment(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            if (mAppPreferences.getBoolean(LoginActivity.PREFS_GOOGLE_SIGN_IN, false)){
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            }
    }

    private void initViewElements() {
        mDontShowAgainCheckBox = (CheckBox) findViewById(R.id.dontShowAgain);
        mSkipLoginButton = (Button) findViewById(R.id.skipButton);
        mSkipLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDontShowAgainCheckBox.isChecked()){
                    SharedPreferences.Editor editor = mAppPreferences.edit();
                    editor.putBoolean(PREFS_SKIP_LOGIN, true);
                    editor.commit();
                }
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(PREFS_SKIP_LOGIN, true);
                startActivity(intent);
            }
        });
    }

    private void addFacebookLoginFragment(Bundle savedInstanceState) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookLoginFragment = new LoginFragment();

            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, facebookLoginFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            facebookLoginFragment = (LoginFragment) supportFragmentManager
                    .findFragmentById(android.R.id.content);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    private void googleSignIn(String googleAccessToken){
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        params.putString("authToken", googleAccessToken);
        Uri uri = Uri.parse(RESTLoader.SERVER_URL +"/membership/tokens/google");
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getSupportLoaderManager().initLoader(LOADER_GOOGLE_SIGN_IN, args, this);

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * @param email
     * @param password
     */
    private void attemptLogin(String email, String password) {
        mEmail = email;
        mPassword = password;
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        Uri uri = Uri.parse(RESTLoader.SERVER_URL +"/membership/validateUser/" +mEmail +"/" +mPassword);
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        Loader<RESTLoader.RESTResponse> restResponseLoader = getSupportLoaderManager().initLoader(LOADER_SIGN_IN, args, this);
    }

    private void attemptRegister(String email, String password){
        mEmail = email;
        mPassword = password;
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        params.putString("username", mEmail);
        params.putString("email", mEmail);
        params.putString("password", mPassword);
        Uri uri = Uri.parse(RESTLoader.SERVER_URL +"/membership/users");
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.POST);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getSupportLoaderManager().initLoader(LOADER_REGISTER, args, this);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onFacebookLoginSuccessful(String authToken) {

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

            case LOADER_GOOGLE_SIGN_IN:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    LoginResult result = gson.fromJson(json, LoginResult.class);
                    if (result.success){
                        onLoginResult(result, result.message.equals("User created!"));
                        SharedPreferences.Editor editor = mAppPreferences.edit();
                        editor.putBoolean(PREFS_GOOGLE_SIGN_IN, true);
                        editor.commit();
                        break;
                    }
                    else {
                        attemptRegister(mEmail, mPassword);
                    }
                    Log.d("",gson.toString());
                }
            }
            case LOADER_SIGN_IN:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    LoginResult result = gson.fromJson(json, LoginResult.class);
                    if (result.success){
                        onLoginResult(result, false);
                        break;
                    }
                    else {
                        attemptRegister(mEmail, mPassword);
                    }
                    Log.d("",gson.toString());
                }
                else {
                    Toast.makeText(this, "Failed to sign in !", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case LOADER_REGISTER:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    LoginResult loginResult = gson.fromJson(json, LoginResult.class);
                    if (loginResult.success){
                        onLoginResult(loginResult, true);
                        finish();
                        break;
                    }
                    else{
                        attemptLogin(mEmail, mPassword);
                    }
                    Log.d("",gson.toString());
                }
                else {
                    Toast.makeText(this, "Failed to register!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

        getSupportLoaderManager().destroyLoader(loader.getId());
    }

    private void onLoginResult(LoginResult result, boolean newAccount) {
        if (result.success){

            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mEmail);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ShopgunAccountAuthenticator.ACCOUNT_TYPE);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, result.token);
            intent.putExtra(EXTRA_PASSWORD, mPassword);


            String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            String accountPassword = intent.getStringExtra(EXTRA_PASSWORD);
            final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, newAccount)) {
                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                String authtokenType = mAuthTokenType;
                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate the user)
                mAccountManager.addAccountExplicitly(account, accountPassword, null);
                mAccountManager.setAuthToken(account, authtokenType, authtoken);
            } else {
                mAccountManager.setPassword(account, accountPassword);
            }
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {

    }

    @Override
    public void onConnected(Bundle bundle) {
// We've resolved any connection errors.

        AsyncTask<Context, Integer, Void> getGoogleTokenTask = new AsyncTask<Context, Integer, Void>(){
            @Override
            protected Void doInBackground(Context... contexts) {
                mAccessToken = null;
                try {
                    Looper.prepare();
                    mEmail = mPlusClient.getAccountName();
                    mPassword = "";
                    mAccessToken = GoogleAuthUtil.getToken(contexts[0],
                            mPlusClient.getAccountName(),
                            "oauth2:" + TextUtils.join(" ", AUTH_SCOPES));

                    googleSignIn(mAccessToken);
                    Looper.loop();
                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.

                    transientEx.printStackTrace();
                } catch (UserRecoverableAuthException e) {
                    // Recover
                    mAccessToken = null;
                    e.printStackTrace();
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                    authEx.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
        getGoogleTokenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mConnectionProgressDialog.isShowing()) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }

        // Save the intent so that we can start an activity when the user clicks
        // the sign-in button.
        mConnectionResult = result;
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            if (responseCode == RESULT_OK){
                mConnectionResult = null;
                mPlusClient.connect();
            }
            else {
                mConnectionProgressDialog.dismiss();
            }

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.google_sign_in_button && !mPlusClient.isConnected()) {
            if (mConnectionResult == null) {
                mConnectionProgressDialog.show();
                mPlusClient.connect();
            } else {
                try {
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    // Try connecting again.
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }
    }
}
