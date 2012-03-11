/**
 * 
 */
package com.akb48plus;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;

/**
 * 
 * @author QuSheng
 */
public class ActivitiesListActivity extends android.app.Activity {
    
    private static String TAG = ActivitiesListActivity.class.getName();
    public static final String INTENT_SELECTED_ACTIVITY = "INTENT_SELECTED_ACTIVITY";
    private ListView mListView;
    
    public void onCreate(Bundle savedState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedState);
        requestWindowFeature(android.view.Window.FEATURE_CUSTOM_TITLE);

        Intent intent = getIntent();
        
        if (intent == null || !intent.hasExtra(ProfileListActivity.INTENT_SELECTED_MEMBER_ID)) {
            return;
        }
        
        String memberId = intent.getExtras().getString(ProfileListActivity.INTENT_SELECTED_MEMBER_ID);
        String memberName = intent.getExtras().getString(ProfileListActivity.INTENT_SELECTED_MEMBER_NAME);
        
        setContentView(R.layout.activities_list);
        getWindow().setFeatureInt(android.view.Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(memberName);
        
        mListView = (ListView) findViewById(R.id.activityList);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                String value = ((TextView)view.findViewById(R.id.txtActivityId)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.txtDisplayName)).getText().toString();
                Log.d(TAG, "clieck ActivityId's id:" + value);
                Intent intent = new Intent(ActivitiesListActivity.this, CommentsListActivity.class);
                intent.putExtra(INTENT_SELECTED_ACTIVITY, value);
                intent.putExtra(ProfileListActivity.INTENT_SELECTED_MEMBER_NAME, name);
                startActivity(intent);
            }
        });
        
        AsyncTask<String, Void, List<Activity>> task = new AsyncTask<String, Void, List<Activity>>() {
            @Override
            protected List<Activity> doInBackground(String... params) {
                try {
                    Plus plus = new PlusWrap(ActivitiesListActivity.this).get();
                    Log.d(TAG, "Now Loading Plus.activity [Profile Id=" + params[0]);
                    Plus.Activities.List listActivities = plus.activities().list(params[0], "public");
                    //listActivities.setMaxResults(Const.MAX_ACTIVITIES);
                    
                    ActivityFeed feed = listActivities.execute();
                    List<Activity> list = feed.getItems();
                    Log.d(TAG, "Now Loaded Plus.activity [Profile Id=" + params[0]);
                    return list;

                } catch (IOException e) {
                    Log.e(TAG, "Unable to list recommended people for user: " + params[0], e);
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(List<Activity> feed) {
                if (feed != null) {
                    mListView.setAdapter(new ActivityArrayAdapter(getApplicationContext(), feed));
                }
            }
            
        };
        task.execute(memberId);
    }

}
