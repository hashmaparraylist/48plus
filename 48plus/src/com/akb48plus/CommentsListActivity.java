/**
 * 
 */
package com.akb48plus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akb48plus.common.Const;
import com.akb48plus.common.Utils;
import com.akb48plus.common.image.ImageLoader;
import com.google.api.client.util.DateTime;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.Comments;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityObject;
import com.google.api.services.plus.model.ActivityObjectAttachments;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author QuSheng
 *
 */
public class CommentsListActivity extends android.app.Activity {
    
    private static final String TAG = CommentsListActivity.class.getName();
    public ImageLoader imageLoader;
    private ListView mListView;
    private View view;
    
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
//        view = findViewById(R.id.activity);
        getWindow().setFeatureInt(android.view.Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(memberName);
        
        mListView = (ListView) findViewById(R.id.activityList);
        
        AsyncTask<String, Void, List<Comment>> task = new AsyncTask<String, Void, List<Comment>>() {
            
            private Activity activity;

            @Override
            protected List<Comment> doInBackground(String... params) {
                try {
                    Plus plus = new PlusWrap(CommentsListActivity.this).get();
                    Log.d(TAG, "Now loading the Plus.Activity for ID[" + params[0]);
                    activity = plus.activities().get(params[0]).execute();
                    Log.d(TAG, "Now loading the Plus.Comments for ID[" + params[0]);
                    Comments.List listComments  = plus.comments().list(params[0]);
                    listComments .setMaxResults(Const.MAX_ACTIVITIES);
                    CommentFeed commentFeed  = listComments.execute();
                    
                    List<Comment> comments = commentFeed.getItems();
                    List<Comment> memberComments = new ArrayList<Comment>();
                    
                    SharedPreferences preferences = getSharedPreferences(Const.PREF_AKB_LIST_NAME, MODE_PRIVATE);
                    Type collectionsType = new TypeToken<ArrayList<String>>(){}.getType();
                    ArrayList<String> akb48List = new Gson()
                            .fromJson(preferences.getString(
                                    Const.PREF_AKB_LIST_NAME, ""),
                                    collectionsType);
                    ArrayList<String> ske48List = new Gson()
                            .fromJson(preferences.getString(
                                    Const.PREF_SKE_LIST_NAME, ""),
                                    collectionsType);
                    ArrayList<String> nmb48List = new Gson()
                            .fromJson(preferences.getString(
                                    Const.PREF_NMB_LIST_NAME, ""),
                                    collectionsType);
                    ArrayList<String> hkt48List = new Gson()
                            .fromJson(preferences.getString(
                                    Const.PREF_HKT_LIST_NAME, ""),
                                    collectionsType);
                    
                    // Loop through until we arrive at an empty page
                    while (comments != null) {
                        Log.d(TAG, "Handle one page for comments [all count=" + String.valueOf(comments.size()));
                        for (Comment comment : comments) {
                           String actorId = comment.getActor().getId();
                           if (akb48List.contains(actorId)) {
                               memberComments.add(comment);
                               continue;
                           }
                           
                           if (ske48List.contains(actorId)) {
                               memberComments.add(comment);
                               continue;
                           }
                           
                           if (nmb48List.contains(actorId)) {
                               memberComments.add(comment);
                               continue;
                           }
                           
                           if (hkt48List.contains(actorId)) {
                               memberComments.add(comment);
                               continue;
                           }
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
                } catch (IOException e) {
                    Log.e(TAG, "Unable to list recommended people for user: " + params[0], e);
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(List<Comment> feed) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.activities_list, null);
                
                // Activity ID
                TextView txtActivityId = (TextView) view.findViewById(R.id.txtActivityId);
                txtActivityId.setText(activity.getId());
                
                // 发表头像
                ImageView imgProfilePhoto = (ImageView) view.findViewById(R.id.imgProfilePhoto);
                imgProfilePhoto.setVisibility(View.VISIBLE);
                imageLoader.displayImage(
                        Utils.changePhotoSizeInUrl(activity.getActor().getImage().getUrl(), Const.PHOTO_SIZE),
                        imgProfilePhoto);
                
                // 发表者
                TextView txtDisplayName = (TextView) view.findViewById(R.id.txtDisplayName);
                txtDisplayName.setText(activity.getActor().getDisplayName());
                
                // 发表时间
                TextView txtUpdDateTime = (TextView) view.findViewById(R.id.txtUpdDateTime);
                DateTime updDateTime = activity.getUpdated();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                String localDateTime = Utils.parseRfc3339ToLoacle(updDateTime.toStringRfc3339(), getApplicationContext());
                
                Log.d(TAG, "GMT = " + updDateTime.toStringRfc3339());
                Log.d(TAG, "Local = " + localDateTime);
                txtUpdDateTime.setText(localDateTime);
                
                // Google Plus 详细发表内容的对象
                ActivityObject object = activity.getPlusObject();
                RelativeLayout shared = (RelativeLayout) view.findViewById(R.id.shared);
                shared.setVisibility(View.GONE);
                if (Const.ACTIVITY_VERB_POST.equals(activity.getVerb())) {
                    handlePost(view, object);
                } else {
                    TextView txtContent = (TextView) findViewById(R.id.txtContent);
                    txtContent.setText(Html.fromHtml(activity.getAnnotation()));
                    handleShare(view, object);
                }

                if (feed != null) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
//                    if (Const.ACTIVITY_VERB_POST.equals(activity.getVerb())) {
//                        params.addRule(RelativeLayout.BELOW, R.id.imgAttach);
//                    } else {
//                    params.addRule(RelativeLayout.BELOW, R.id.shared);
//                    }
//                    mListView.setLayoutParams(params);
                    mListView.addHeaderView(view);
                    inflater = LayoutInflater.from(getApplicationContext());
                    View view2 = inflater.inflate(R.layout.list_headfooter, null);
                    mListView.addFooterView(view2);
                    mListView.setAdapter(new CommentsArrayAdapter(getApplicationContext(), feed));

//                    ListAdapter listAdapter = mListView.getAdapter();
//                    if (listAdapter == null) {
//                        return;
//                    }
//
//                    int totalHeight = 0;
//                    //listAdapter.getCount()返回数据项的数目
//                    for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
//                           View listItem = listAdapter.getView(i, null, mListView);
//                           listItem.measure(0, 0);  //计算子项View 的宽高
//                           totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
//                    }

                    
//                    params.height = totalHeight + (mListView.getDividerHeight() * (listAdapter.getCount() - 1));
                    
                }
            }
            
            /**
             * 
             * @param view
             * @param object
             */
            private void handleShare(View view, ActivityObject object) {
                RelativeLayout shared = (RelativeLayout) view.findViewById(R.id.shared);
                ImageView imgSharedProfile = (ImageView) shared.findViewById(R.id.imgSharedProfile);
                TextView txtSharedDisplayName = (TextView) shared.findViewById(R.id.txtSharedDisplayName);
                TextView txtSharedContent = (TextView) shared.findViewById(R.id.txtSharedContent);
                ImageView imgSharedAttach = (ImageView) shared.findViewById(R.id.imgSharedAttach);
                
                shared.setVisibility(View.VISIBLE);
                imgSharedProfile.setVisibility(View.VISIBLE);
                imageLoader.displayImage(
                        Utils.changePhotoSizeInUrl(object.getActor().getImage().getUrl(), Const.PHOTO_SIZE),
                        imgSharedProfile);
                txtSharedDisplayName.setText(object.getActor().getDisplayName());
                
                makeContent(object, txtSharedContent, imgSharedAttach);
            }

            /**
             * 
             * @param view
             * @param object
             */
            private void handlePost(View view, ActivityObject object) {
                TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
                ImageView imageAttach = (ImageView) view.findViewById(R.id.imgAttach);
                imageAttach.setVisibility(View.GONE);
                makeContent(object, txtContent, imageAttach);
            }

            /**
             * 
             * @param object
             * @param txtContent
             * @param imageAttach
             */
            public void makeContent(ActivityObject object, TextView txtContent,
                    ImageView imageAttach) {
                // 发表的正文
                String content = object.getContent();
                imageAttach.setVisibility(View.GONE);
                // 附件
                List<ActivityObjectAttachments> list = object.getAttachments();
                if ((null != list) && (0 < list.size())) {
                    ActivityObjectAttachments attachments = list.get(0);
                    String type = attachments.getObjectType();
                    
                    // 附件是网页的时候(需要和正文整合)
                    if (Const.ATTACH_TYPE_ARTICLE.equals(type)) {
                        content += "<br />";
                        content += "<a herf=\"" + attachments.getUrl() + "\">";
                        content += attachments.getDisplayName() + "</a><br />";
                        if ((null != attachments.getContent()) || (!"".equals(attachments.getContent()))) {
                            content += attachments.getContent();
                        }
                        
                    }
                    
                    // 附件是图片的时候
                    if (Const.ATTACH_TYPE_PHOTO.equals(type)) {
                        String imageUrl = attachments.getFullImage().getUrl();
                        
                        imageAttach.setVisibility(View.VISIBLE);
                        imageLoader.displayImage(imageUrl, imageAttach);
                    }
                }
                
                // 发表内容显示
                txtContent.setText(Html.fromHtml(content));
            }
        };
        
        task.execute(activityId);
    }
}
