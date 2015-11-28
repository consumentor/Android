package consumentor.shopgun.aidroid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;

import consumentor.shopgun.aidroid.authentication.ShopgunAccountAuthenticator;

/**
 * Created by Simon on 2013-10-31.
 */
public class Global {

    public static String getAuthToken(Activity context){

        AccountManager accountManager = AccountManager.get(context);
        //Account[] accounts = accountManager.getAccountsByType(ShopgunAccountAuthenticator.ACCOUNT_TYPE);
        Account shopgunAccount = new Account(ShopgunAccountAuthenticator.ACCOUNT_TYPE, "org.consumentor.SHOPGUN_USER");
        AccountManagerFuture<Bundle> authToken = null;
        //if (accounts.length > 0){
            //shopgunAccount = accounts[0];
        authToken = accountManager.getAuthToken(shopgunAccount, "org.consumentor.SHOPGUN_USER", null, context, null, null);
        //}

        return "";
    }
}
