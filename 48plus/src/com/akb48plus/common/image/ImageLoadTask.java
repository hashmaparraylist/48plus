/**
 * 
 */
package com.akb48plus.common.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.akb48plus.common.image.Utils.CacheBean;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author QuSheng
 *
 */
public class ImageLoadTask extends AsyncTask<String, Void, Drawable> {
    
    private static final String TAG = ImageLoadTask.class.getName();

    private ImageView content;
    
    /**
     * Default Construct
     * 
     * @param content ImageView
     */
    public ImageLoadTask(ImageView content) {
        this.content = content;
    }
    
    /**
     * 
     */
    @Override
    protected Drawable doInBackground(String... params) {
        String url = params[0];
        try {
            Log.d(TAG, "Now cache image:" + url);
            InputStream is = (new URL(url)).openStream();
            if (null == is) {
                return null;
            }
            Drawable image = Drawable.createFromStream(is, "imgsrc.jpg");
            CacheBean imageCache = Utils.imageCache.get(url);
            if (imageCache == null) {
                imageCache = Utils.buildCacheBean();
            }
            imageCache.setImage(image);
            imageCache.setStatus(CacheBean.STATUS_OVER);
            Utils.imageCache.put(url, imageCache);
            return image;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(Drawable image) {
        Log.d(TAG, "cache over");
        //this.
        
        if (image != null) {
            content.setImageDrawable(image);
        }
    }

}
