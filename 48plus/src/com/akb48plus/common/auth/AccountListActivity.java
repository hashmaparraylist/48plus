/**
 * 
 */
package com.akb48plus.common.auth;

import com.akb48plus.R;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;

import android.accounts.Account;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author QuSheng
 *
 */
public class AccountListActivity extends ListActivity {
    private Intent callback;
    
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        requestWindowFeature(android.view.Window.FEATURE_CUSTOM_TITLE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra("callback")) {
            callback = (Intent) intent.getExtras().get("callback");
        }

        final Account[] accounts = new GoogleAccountManager(getApplicationContext()).getAccounts();
        this.setListAdapter(new AccountListAdapter(this, R.layout.account_list, accounts));
        getWindow().setFeatureInt(android.view.Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(R.string.account_list_sub_title);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Account account = (Account) getListView().getItemAtPosition(position);
        final SharedPreferences settings = getSharedPreferences(AuthUtils.PREF_NAME, 0);
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(AuthUtils.PREF_ACCOUNT_NAME, account.name);
        editor.commit();
        startActivity(callback);
        finish();
    }

    private static class AccountListAdapter extends ArrayAdapter<Account> {
        private final Account[] accounts;

        public AccountListAdapter(Context ctx, int resourceId, Account[] accounts) {
            super(ctx, resourceId, accounts);
            this.accounts = accounts;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                final LayoutInflater layoutInflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.account_list, null);
            }

            final Account account = accounts[position];
            if (account != null) {
                final TextView textView = (TextView) view.findViewById(R.id.account);
                if (textView != null) {
                    textView.setText(account.name);
                }
            }
            return view;
        }
    }
}
