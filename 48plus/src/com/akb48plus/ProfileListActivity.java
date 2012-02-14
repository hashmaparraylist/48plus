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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    public static final String INTENT_MEMBER_SELECTED= "INTENT_MEMBER_SELECTED";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        
        Intent intent = getIntent();
        
        if (intent == null || !intent.hasExtra(MainActivity.INTENT_GROUP_SELECTED)) {
            return;
        }
        
        String profileCatalog = intent.getExtras().getString(MainActivity.INTENT_GROUP_SELECTED);
        SharedPreferences preferences = getSharedPreferences(Const.PREF_AKB_LIST_NAME, MODE_PRIVATE);
        String members = preferences.getString(profileCatalog, "");
        String memberList[] = new Gson().fromJson(members, String[].class);
          
        setContentView(R.layout.profile_list);
        mListView = (ListView) findViewById(R.id.profileList);
        ((ImageView) findViewById(R.id.imgProfilePhoto)).setVisibility(ImageView.INVISIBLE);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                Log.d(TAG, "clieck member's name:" + ((TextView)view.findViewById(R.id.txtProfileName)).getText().toString());
                Log.d(TAG, "clieck member's id:" + ((TextView)view.findViewById(R.id.txtProfileId)).getText().toString());
                String str = ((TextView)view.findViewById(R.id.txtProfileId)).getText().toString();
                Intent activites = new Intent(getApplicationContext(), ActivitiesListActivity.class);
                activites.putExtra(INTENT_MEMBER_SELECTED, str);
                getApplication().startActivity(activites);
            }
        });
        
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
