/**
 * 
 */
package com.akb48plus.common.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;

/**
 * @author QuSheng
 * 
 */
public class AuthUtils {

    private static final String TAG = AuthUtils.class.getName();

    public static final String PREF_NAME = "48plus";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String PREF_TOKEN = "accessToken";
    public static final String PLUS_ME_SCOPE = "View your Google+ id\nView your Public Google+ data";

    public static void refreshAuthToken(final Context context) {
        final SharedPreferences settings = context.getSharedPreferences(PREF_NAME, 0);
        final String accessToken = settings.getString(PREF_TOKEN, "");
        final String accountName = settings.getString(PREF_ACCOUNT_NAME, "");
        final GoogleAccountManager manager = new GoogleAccountManager(context);
        final Account account = manager.getAccountByName(accountName);
        final Intent callback = new Intent(context, context.getClass());

        if (null == account) {
            // Google Account is not exist
            Intent accountListIntent = new Intent(context, AccountListActivity.class);
            accountListIntent.putExtra("callback", callback);
            context.startActivity(accountListIntent);
        } else {
            final AccountManagerCallback<Bundle> cb = new AccountManagerCallback<Bundle>() {
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        final Bundle result = future.getResult();
                        final String accountName = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                        final String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                        final Intent authIntent = result.getParcelable(AccountManager.KEY_INTENT);

                        if (accountName != null && authToken != null) {
                            final SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PREF_TOKEN, authToken);
                            editor.commit();
                            callback.putExtra("token", authToken);
                            context.startActivity(callback);
                        } else if (authIntent != null) {
                            context.startActivity(authIntent);
                        } else {
                            Log.e(TAG, "AccountManager was unable to obtain an authToken.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Auth Error", e);
                    }
                }
            };
            manager.invalidateAuthToken(accessToken);
            AccountManager.get(context).getAuthToken(account, PLUS_ME_SCOPE, true, cb, null);
        }
    }

}
