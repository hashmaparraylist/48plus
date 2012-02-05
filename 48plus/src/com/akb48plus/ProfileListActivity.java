/**
 * 
 */
package com.akb48plus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.akb48plus.common.Const;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;


/**
 * @author QuSheng
 *
 */
public class ProfileListActivity extends android.app.Activity {
    
    private ListView mListView;
    public final static String TAG = ProfileListActivity.class.getName();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        
        Intent intent = getIntent();
        
        if (intent == null || !intent.hasExtra(MainActivity.INTENT_MEMBER_SELECTED)) {
            return;
        }
        
        String profileCatalog = intent.getExtras().getString(MainActivity.INTENT_MEMBER_SELECTED);
        SharedPreferences preferences = getSharedPreferences(Const.PREF_AKB_LIST_NAME, MODE_PRIVATE);
        String members = preferences.getString(profileCatalog, "");
        String memberList[] = new Gson().fromJson(members, String[].class);
          
        setContentView(R.layout.profile_list);
        mListView = (ListView) findViewById(R.id.profileList);
        ((ImageView) findViewById(R.id.imgProfilePhoto)).setVisibility(ImageView.INVISIBLE);
        AsyncTask<String, Void, List<Person>> task = new AsyncTask<String, Void, List<Person>>() {
            @Override
            protected List<Person> doInBackground(String... params) {
                try {
                    Plus plus = new PlusWrap(ProfileListActivity.this).get();
                    List<Person> profileList = new ArrayList<Person>();
                    
                    for (String member : params) {
                        Log.d(TAG, "Profile id=" + member + " get.");
                        profileList.add(plus.people().get(member).execute());
                    }

                    return profileList;

                } catch (IOException e) {
                    Log.e(TAG, "Unable to list recommended people for user: "
                            + params[0], e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Person> feed) {
                if (feed != null) {
                    mListView.setAdapter(new MemberListAdapter(getApplicationContext(), feed));
                }
            }
        };
        
        task.execute(memberList);
    }
}
