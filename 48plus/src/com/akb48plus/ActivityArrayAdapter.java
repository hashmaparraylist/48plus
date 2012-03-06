/**
 * 
 */
package com.akb48plus;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akb48plus.common.Const;
import com.akb48plus.common.Utils;
import com.akb48plus.common.image.ImageLoader;
import com.google.api.client.util.DateTime;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityObject;
import com.google.api.services.plus.model.ActivityObjectAttachments;

/**
 * @author QuSheng
 * 
 */
public class ActivityArrayAdapter extends ArrayAdapter<Activity> {
    
    public ImageLoader imageLoader;
    private static String TAG = ActivityArrayAdapter.class.getName();
    
    public ActivityArrayAdapter(Context context, List<Activity> activities) {
        super(context, android.R.layout.simple_spinner_item, activities);
        setDropDownViewResource(R.layout.activities_list);
        imageLoader = new ImageLoader(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        Log.d(TAG, "Making List");
        if (view == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activities_list, parent, false);
        }

        final Activity activity = getItem(position);
        if (null == activity) {
            return view;
        }
        
        Log.d(TAG, "Now Activity ID: " + activity.getId());
        
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
        String localDateTime = Utils.parseRfc3339ToLoacle(updDateTime.toStringRfc3339(), view.getContext());
        
        Log.d(TAG, "GMT = " + updDateTime.toStringRfc3339());
        Log.d(TAG, "Local = " + localDateTime);
        txtUpdDateTime.setText(localDateTime);
        RelativeLayout shared = (RelativeLayout) view.findViewById(R.id.shared);
        shared.setVisibility(View.GONE);
        // Google Plus 详细发表内容的对象
        ActivityObject object = activity.getPlusObject();
        
        if (Const.ACTIVITY_VERB_POST.equals(activity.getVerb())) {
            handlePost(view, object);
        } else {
            TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
            txtContent.setText(Html.fromHtml(activity.getAnnotation()));
            handleShare(view, object);
        }
        
        return view;
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
                content += attachments.getContent();
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
}
