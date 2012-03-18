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
import android.widget.Toast;

import com.akb48plus.common.Const;
import com.akb48plus.common.cache.PeopleWrapper;
import com.akb48plus.common.model.Model;
import com.akb48plus.common.model.People;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.gson.Gson;


/**
 * @author QuSheng
 *
 */
public class ProfileListActivity extends android.app.Activity {
    public static final String INTENT_SELECTED_MEMBER_ID = "INTENT_SELECTED_MEMBER_ID";
    public static final String INTENT_SELECTED_MEMBER_NAME = "INTENT_SELECTED_MEMBER_NAME";
    
    private ListView mListView;
    private PeopleWrapper wrapper;
    private final static String TAG = ProfileListActivity.class.getName();
    

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
        try {
            wrapper = new PeopleWrapper(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.init_table_error),
                    Toast.LENGTH_SHORT).show();
            this.finish();
        }

        List<Model> list = new ArrayList<Model>();
        
        for (int i = 0; i < memberList.length; i++) {
            String string = memberList[i];
            List<Model> target = wrapper.get(string);
            list.add(target.get(0));
        }
        
        mListView.setAdapter(new MemberListAdapter(getApplicationContext(), list));
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                Log.d(TAG, "clieck member's name:" + ((TextView)view.findViewById(R.id.txtProfileName)).getText().toString());
                Log.d(TAG, "clieck member's id:" + ((TextView)view.findViewById(R.id.txtProfileId)).getText().toString());
                String pid = ((TextView)view.findViewById(R.id.txtProfileId)).getText().toString();
                String displayName = ((TextView)view.findViewById(R.id.txtProfileName)).getText().toString();
                Intent activites = new Intent(ProfileListActivity.this, ActivitiesListActivity.class);
                activites.putExtra(INTENT_SELECTED_MEMBER_ID, pid);
                activites.putExtra(INTENT_SELECTED_MEMBER_NAME, displayName);
                //activites.setFlags(Intent.Fla)
                startActivity(activites);
            }
        });
        
        AsyncTask<String, Void, List<People>> task = new AsyncTask<String, Void, List<People>>() {
            @Override
            protected List<People> doInBackground(String... params) {
               
                Plus plus = new PlusWrap(ProfileListActivity.this).get();
                List<People> profileList = new ArrayList<People>();
                
                for (String member : params) {
                    Log.d(TAG, "Profile id=" + member + " get.");
                    People people = new People();
                    Person person;
                    try {
                        person = plus.people().get(member).execute();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        continue;
                    }
                    people.setId(person.getId());
                    people.setDisplayName(person.getDisplayName());
                    people.setProfileUrl(person.getImage().getUrl());
                    
                    try {
                        if (!wrapper.exist(people)) {
                            wrapper.add(people);
                            profileList.add(people);
                        } else {
                            wrapper.update(people);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        continue;
                    } 
                }

                return profileList;
            }

            @Override
            protected void onPostExecute(List<People> feed) {
                if (feed == null) return;
                if (feed.size() < 1) return;
                
                MemberListAdapter listAdapter = (MemberListAdapter) mListView.getAdapter();
                for (People people : feed) {
                    listAdapter.add(people);
                }
            }
        };
        
        task.execute(memberList);
    }
}
