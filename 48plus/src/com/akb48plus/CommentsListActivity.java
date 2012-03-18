/**
 * 
 */
package com.akb48plus;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akb48plus.common.Const;
import com.akb48plus.common.cache.CommentWrapper;
import com.akb48plus.common.cache.PeopleWrapper;
import com.akb48plus.common.cache.PostWrapper;
import com.akb48plus.common.image.ImageLoader;
import com.akb48plus.common.model.Comment;
import com.akb48plus.common.model.Model;
import com.akb48plus.common.model.Post;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.Comments;
import com.google.api.services.plus.model.CommentFeed;

/**
 * @author QuSheng
 *
 */
public class CommentsListActivity extends android.app.Activity {
    
    private static final String TAG = CommentsListActivity.class.getName();
    private ImageLoader imageLoader;
    private PostWrapper postWrapper;
    private CommentWrapper commentWrapper;
    private ListView mListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        Log.d(TAG, "onCreate");
        
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_CUSTOM_TITLE);

        Intent intent = getIntent();        
        if (intent == null || !intent.hasExtra(ActivitiesListActivity.INTENT_SELECTED_ACTIVITY)) {
            return;
        }
        imageLoader  = new ImageLoader(this);
        String activityId = intent.getExtras().getString(ActivitiesListActivity.INTENT_SELECTED_ACTIVITY);
        String memberName = intent.getExtras().getString(ProfileListActivity.INTENT_SELECTED_MEMBER_NAME);
        
        setContentView(R.layout.comments_list);
        getWindow().setFeatureInt(android.view.Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(memberName);
        
        try {
            postWrapper = new PostWrapper(getApplicationContext());
            commentWrapper = new CommentWrapper(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.init_table_error),
                    Toast.LENGTH_SHORT).show();
            this.finish();
        }
        
        
        mListView = (ListView) findViewById(R.id.activityList);
        
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.activities_list, null);
        
        List<Model> posts = postWrapper.get(activityId);
        Post post = (Post) posts.get(0);
        postWrapper.adapter(view, post, imageLoader);

        mListView.addHeaderView(view);
        inflater = LayoutInflater.from(getApplicationContext());
        View footer = inflater.inflate(R.layout.list_headfooter, null);
        mListView.addFooterView(footer);
        
        List<Model> comments = commentWrapper.get(activityId);
        mListView.setAdapter(new CommentsArrayAdapter(getApplicationContext(), comments));
        
        AsyncTask<String, Void, List<Model>> task = new AsyncTask<String, Void, List<Model>>() {
            
            @Override
            protected List<Model> doInBackground(String... params) {
                try {
                    PeopleWrapper wrapper = new PeopleWrapper(getApplicationContext());
                    Plus plus = new PlusWrap(CommentsListActivity.this).get();
                    
                    Log.d(TAG, "Now loading the Plus.Comments for ID[" + params[0]);
                    Comments.List listComments  = plus.comments().list(params[0]);
                    listComments .setMaxResults(Const.MAX_ACTIVITIES);
                    CommentFeed commentFeed  = listComments.execute();
                    
                    List<com.google.api.services.plus.model.Comment> comments = commentFeed.getItems();
                    List<Model> memberComments = new ArrayList<Model>();
                    
                    // Loop through until we arrive at an empty page
                    while (comments != null) {
                        Log.d(TAG, "Handle one page for comments [all count=" + String.valueOf(comments.size()));
                        for (com.google.api.services.plus.model.Comment comment : comments) {
                           String actorId = comment.getActor().getId();
                           if(!wrapper.containsKey(actorId)) {
                               continue;
                           }
                           Comment commentModel = new Comment();
                           commentModel.setId(params[0]);
                           commentModel.setCommentId(comment.getId());
                           if(commentWrapper.exist(commentModel)) {
                              continue; 
                           }
                           commentModel = (Comment) commentWrapper.parse(comment);
                           commentModel.setId(params[0]);
                           memberComments.add(commentModel);
                        }

                        if (commentFeed.getNextPageToken() == null) {
                            Log.d(TAG, "Have not next page");
                            break;
                        }
                        Log.d(TAG,"Now loading next page comments");
                        // Prepare the next page of results
                        listComments.setPageToken(commentFeed.getNextPageToken());

                        // Execute and process the next page request
                        commentFeed = listComments.execute();
                        comments = commentFeed.getItems();
                    }
                    Log.d(TAG, "Member's comments count = " + String.valueOf(memberComments.size()));
                    return memberComments;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(List<Model> feed) {
                if ((feed != null) && (0 < feed.size())) {
                    CommentsArrayAdapter adapter = (CommentsArrayAdapter) ((HeaderViewListAdapter) mListView
                            .getAdapter()).getWrappedAdapter();
                    for (Model model : feed) {
                        adapter.add(model);
                    }
                }
            }
        };
        
        task.execute(activityId);
    }
}
