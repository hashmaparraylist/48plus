/**
 * 
 */
package com.akb48plus;

import java.io.IOException;
import java.util.ArrayList;
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
import android.widget.Toast;

import com.akb48plus.common.cache.PostWrapper;
import com.akb48plus.common.model.Model;
import com.akb48plus.common.model.Post;
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
    private PostWrapper wrapper;
    
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
        
        try {
            wrapper = new PostWrapper(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.init_table_error),
                    Toast.LENGTH_SHORT).show();
            this.finish();
        }
        List<Model> activies = wrapper.getPostByPeople(memberId);
        
        mListView = (ListView) findViewById(R.id.activityList);
        mListView.setAdapter(new ActivityArrayAdapter(getApplicationContext(), activies));
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
        
        AsyncTask<String, Void, List<Post>> task = new AsyncTask<String, Void, List<Post>>() {
            @Override
            protected List<Post> doInBackground(String... params) {
                List<Post> target = new ArrayList<Post>();
                try {
                    Plus plus = new PlusWrap(ActivitiesListActivity.this).get();
                    Log.d(TAG, "Now Loading Plus.activity [Profile Id=" + params[0]);
                    Plus.Activities.List listActivities = plus.activities().list(params[0], "public");
                    
                    ActivityFeed feed = listActivities.execute();
                    List<Activity> list = feed.getItems();
                    Log.d(TAG, "Now Loaded Plus.activity [Profile Id=" + params[0]);
                    
                    for(Activity activity : list) {
                        if (wrapper.containsKey(activity.getId())) {
                            continue;
                        }
                        Post post = (Post) wrapper.parse(activity);
                        try {
                            wrapper.add(post);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            continue;
                        }
                        target.add(post);
                        
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to list recommended people for user: " + params[0], e);
                }
                return target;
            }
            
            @Override
            protected void onPostExecute(List<Post> feed) {
                if (feed == null) return;
                if (feed.size() < 1) return;
                
                ActivityArrayAdapter adapter = (ActivityArrayAdapter)  mListView.getAdapter();
                
                for (Post post : feed) {
                    adapter.add(post);
                }
            }
            
        };
        task.execute(memberId);
    }

}
