/**
 * 
 */
package com.akb48plus.common.cache;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akb48plus.R;
import com.akb48plus.common.Const;
import com.akb48plus.common.Utils;
import com.akb48plus.common.image.ImageLoader;
import com.akb48plus.common.model.Model;
import com.akb48plus.common.model.Post;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityObject;
import com.google.api.services.plus.model.ActivityObjectAttachments;

/**
 * @author Thinkpad
 *
 */
public class PostWrapper extends ModelWrapper {
    
    public PostWrapper(Context ctx) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        super(ctx);
    }
    
    public List<Model> getPostByPeople(String id) {
        Post model = (Post) getModelInstance();
        model.set(getAdditionalKey(), id);
        return getByKey(model);
    }

    /* (non-Javadoc)
     * @see com.akb48plus.common.cache.ModelWrapper#getTableName()
     */
    @Override
    public String getTableName() {
        return "post_cache";
    }

    /* (non-Javadoc)
     * @see com.akb48plus.common.cache.ModelWrapper#getModelInstance()
     */
    @Override
    protected Model getModelInstance() {
        return new Post();
    }

    @Override
    protected String getAdditionalKey() {
        return "people_id";
    }
    
    public void adapter(View view, Model model, ImageLoader imageLoader) {
        Post post = (Post) model;
        // Activity ID
        TextView txtActivityId = (TextView) view.findViewById(R.id.txtActivityId);
        txtActivityId.setText(post.getId());
        
        // 发表头像
        ImageView imgProfilePhoto = (ImageView) view.findViewById(R.id.imgProfilePhoto);
        imgProfilePhoto.setVisibility(View.VISIBLE);
        imageLoader.displayImage(
                Utils.changePhotoSizeInUrl(post.get("profileUrl"), Const.PHOTO_SIZE),
                imgProfilePhoto);
        
        // 发表者
        TextView txtDisplayName = (TextView) view.findViewById(R.id.txtDisplayName);
        txtDisplayName.setText(post.get("displayName"));
        
        // 发表时间
        TextView txtUpdDateTime = (TextView) view.findViewById(R.id.txtUpdDateTime);
        txtUpdDateTime.setText(post.get("updated"));
        RelativeLayout shared = (RelativeLayout) view.findViewById(R.id.shared);
        shared.setVisibility(View.GONE);
        // Google Plus 详细发表内容的对象
        TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
        ImageView imageAttach = (ImageView) view.findViewById(R.id.imgAttach);
        imageAttach.setVisibility(View.GONE);

        txtContent.setText(Html.fromHtml(post.get("object_content")));
        
        String verb = post.get("verb");
        
        if (Const.ACTIVITY_VERB_POST.equals(verb)) {
            handlePost(view, post, imageLoader);
        } else {
            handleShare(view, (Post)post, imageLoader);
        }
    }
    
    /**
     * 
     * @param view
     * @param object
     */
    private void handleShare(View view, Post post, ImageLoader imageLoader) {
        RelativeLayout shared = (RelativeLayout) view.findViewById(R.id.shared);
        ImageView imgSharedProfile = (ImageView) shared.findViewById(R.id.imgSharedProfile);
        TextView txtSharedDisplayName = (TextView) shared.findViewById(R.id.txtSharedDisplayName);
        TextView txtSharedContent = (TextView) shared.findViewById(R.id.txtSharedContent);
        ImageView imgSharedAttach = (ImageView) shared.findViewById(R.id.imgSharedAttach);
        
        shared.setVisibility(View.VISIBLE);
        imgSharedProfile.setVisibility(View.VISIBLE);
        imageLoader.displayImage(
                Utils.changePhotoSizeInUrl(post.get("shared_profileUrl"), Const.PHOTO_SIZE),
                imgSharedProfile);
        txtSharedDisplayName.setText(post.get("shared_displayName"));
        txtSharedContent.setText(post.get("shared_content"));
        // 附件是图片的时候
        if (!"".equals(post.get("shared_attach_url"))) {
            String imageUrl = post.get("shared_attach_url");
            imgSharedAttach.setVisibility(View.VISIBLE);
            imageLoader.displayImage(imageUrl, imgSharedAttach);
        }
    }

    /**
     * 
     * @param view
     * @param object
     */
    private void handlePost(View view, Post post, ImageLoader imageLoader) {
        ImageView imageAttach = (ImageView) view.findViewById(R.id.imgAttach);
        imageAttach.setVisibility(View.GONE);
        //String type = post.get("attachments_type");
        String imageUrl = post.get("attachments_url");
        // 附件是图片的时候
        if (!"".equals(imageUrl)) {
            imageAttach.setVisibility(View.VISIBLE);
            imageLoader.displayImage(imageUrl, imageAttach);
        }
    }
    
    public Post parse(Activity activity) {
        Post post = (Post) getModelInstance();
        post.setId(activity.getId());
        post.set("people_id", activity.getActor().getId());
        post.set("displayName", activity.getActor().getDisplayName());
        post.set("profileUrl", activity.getActor().getImage().getUrl());
        post.set("updated", Utils.parseRfc3339ToLoacle(activity.getUpdated().toStringRfc3339(), this.context));
        post.set("verb", activity.getVerb());
        ActivityObject object = activity.getPlusObject();
        if (Const.ACTIVITY_VERB_POST.equals(activity.getVerb())) { 
            String content = object.getContent();
            List<ActivityObjectAttachments> attachments = object.getAttachments();
            if ((null != attachments) && (0 < attachments.size())) {
                ActivityObjectAttachments attachment = attachments.get(0);
                String type = attachment.getObjectType();
                post.set("attachments_type", type);
                if (Const.ATTACH_TYPE_ARTICLE.equals(type)) {
                    content += "<br />";
                    content += "<a herf=\"" + attachment.getUrl() + "\">";
                    content += attachment.getDisplayName() + "</a><br />";
                    if ((null != attachment.getContent()) || (!"".equals(attachment.getContent()))) {
                        content += attachment.getContent();
                    }
                } else if (Const.ATTACH_TYPE_PHOTO.equals(type)) {
                    post.set("attachments_url", attachment.getFullImage().getUrl());
                }
            }
            post.set("object_content", content);
        }
        if (Const.ACTIVITY_VERB_SHARE.equals(activity.getVerb())) {
            post.set("object_content", activity.getAnnotation());
            post.set("shared_displayName", object.getActor().getDisplayName());
            post.set("shared_profileUrl", object.getActor().getImage().getUrl());

            String content = object.getContent();
            List<ActivityObjectAttachments> attachments = object.getAttachments();
            if ((null != attachments) && (0 < attachments.size())) {
                ActivityObjectAttachments attachment = attachments.get(0);
                String type = attachment.getObjectType();
                if (Const.ATTACH_TYPE_ARTICLE.equals(type)) {
                    content += "<br />";
                    content += "<a herf=\"" + attachment.getUrl() + "\">";
                    content += attachment.getDisplayName() + "</a><br />"; 
                    if ((null != attachment.getContent()) || (!"".equals(attachment.getContent()))) {
                        content += attachment.getContent();
                    }
                } else if (Const.ATTACH_TYPE_PHOTO.equals(type)) {
                    post.set("shared_attach_url", attachment.getFullImage().getUrl());
                }
                post.set("shared_attach_content", content);
            }
        }
        
        return post;
    }


}
