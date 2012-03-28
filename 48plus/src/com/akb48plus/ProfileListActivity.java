/**
 *
 */
package com.akb48plus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akb48plus.common.Const;
import com.akb48plus.common.Utils;
import com.akb48plus.common.cache.PeopleWrapper;
import com.akb48plus.common.image.ImageLoader;
import com.akb48plus.common.image.cache.FileCache;
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

    private static final int DIALOG_MEMBER_LONG_CLICK = 0;


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
        mListView.setSelected(true);
        AsyncTask<String, Void, List<People>> task = new AsyncTask<String, Void, List<People>>() {
            @Override
            protected List<People> doInBackground(String... params) {

                Plus plus = new PlusWrap(ProfileListActivity.this).get();
                List<People> profileList = new ArrayList<People>();

                for (String member : params) {
                    Log.d(TAG, "Profile id=" + member + " get.");
                    List<Model> cacheList = wrapper.get(member);
                    People people = new People();
                    if (cacheList.size() > 0 ) {
                        people = (People) cacheList.get(0);
                    }
                    if (!"".equals(people.getProfileUrl())) {
                        continue;
                    }
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

        // ListView Event (Item Click)
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "ListView.onItemClick");
                String pid = ((TextView)view.findViewById(R.id.txtProfileId)).getText().toString();
                String displayName = ((TextView)view.findViewById(R.id.txtProfileName)).getText().toString();
                Intent activites = new Intent(ProfileListActivity.this, ActivitiesListActivity.class);
                activites.putExtra(INTENT_SELECTED_MEMBER_ID, pid);
                activites.putExtra(INTENT_SELECTED_MEMBER_NAME, displayName);
                startActivity(activites);
            }
        });

        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "ListView.onItemLongClick");
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putLong("id", id);
                showDialog(DIALOG_MEMBER_LONG_CLICK, bundle);
                return true;
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id, final Bundle bundle) {
        switch (id) {
        case DIALOG_MEMBER_LONG_CLICK:
            Log.d(TAG, "onCreateDialog");

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileListActivity.this);
            builder.setTitle(R.string.dialog_title);
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int postion = bundle.getInt("position");
                    //long id = bundle.getLong("id");
                    
                    switch (which) {
                    case 0:
                        int pos_offset = postion - mListView.getFirstVisiblePosition() + mListView.getHeaderViewsCount(); 
                        View view = mListView.getChildAt(pos_offset);
                        //mListView.get
                        
                        ImageView imageView = (ImageView) view.findViewById(R.id.imgProfilePhoto);
                        imageView.setImageResource(R.drawable.dummy);
                        TextView textProfileId = (TextView) view.findViewById(R.id.txtProfileId);
                        //TextView textDisplayName = (TextView) view.findViewById(R.id.txtProfileName);
                        String peopleId = textProfileId.getText().toString();
                        List<Model> model = wrapper.get(peopleId);
                        People people = (People) model.get(0);
                        FileCache fileCahe = new FileCache(getApplicationContext());
                        fileCahe.remove(Utils.changePhotoSizeInUrl(people.getProfileUrl(), Const.PHOTO_SIZE));
                        ImageLoader loader = new ImageLoader(getApplicationContext());
                        loader.displayImage(Utils.changePhotoSizeInUrl(people.getProfileUrl(), Const.PHOTO_SIZE), imageView);
                        break;
                    default:
                        break;
                    }
                }
            };
            
            builder.setItems(R.array.profile_memmber_long_click, listener);
            
            return builder.create();
        default:
            break;
        }

        return null;

    }

}
