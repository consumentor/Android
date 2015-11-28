package consumentor.shopgun.aidroid.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;

import consumentor.shopgun.aidroid.view.LoginActivity;
import consumentor.shopgun.aidroid.model.LoginResult;
import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 2013-11-05.
 */
public class ShopgunAccountAuthenticator extends AbstractAccountAuthenticator {

    public static final String ACCOUNT_TYPE = "org.consumentor.shopgun";
    Context mContext;

    public ShopgunAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountAuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountAuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountAuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (Util.isNullOrEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                authToken = userSignIn(account.name, password, authTokenType);
            }
        }

        // If we get an authToken - we return it
        if (!Util.isNullOrEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.

        return addAccount(response, account.type, authTokenType, new String[0], options);
    }


    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }

    private String userSignIn(String name, String password, String authTokenType) {

        RESTLoader restLoader = new RESTLoader(mContext, RESTLoader.HTTPVerb.GET, Uri.parse(RESTLoader.SERVER_URL +"/membership/ValidateUser/" +name +"/" +password));
        RESTLoader.RESTResponse restResponse = restLoader.loadInBackground();

        if (restResponse.getCode() == 200){
            Gson gson = new Gson();
            LoginResult loginResult = gson.fromJson(restResponse.getData(), LoginResult.class);
            return loginResult.token;
        }

        return null;
    }
}
