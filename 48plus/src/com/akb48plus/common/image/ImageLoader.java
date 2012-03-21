/**
 * 
 */
package com.akb48plus.common.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.akb48plus.R;
import com.akb48plus.common.Utils;
import com.akb48plus.common.image.cache.FileCache;
import com.akb48plus.common.image.cache.MemoryCache;

/**
 * @author QuSheng
 * 
 */
public class ImageLoader {
    private static String TAG = ImageLoader.class.getName();
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    private Handler handler;
    
    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id = R.drawable.dummy;

    public void displayImage(String url, ImageView imageView) {
        if (null == handler) 
            handler = new Handler();
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            Log.d(TAG, "Hit cache in the MemoryCache");
            imageView.setImageBitmap(bitmap);
        } else {
            Log.d(TAG, "Didn't hit cache in the MemoryCache. Ready to queue the image in the FileCache or download");
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // from SD cache
        Bitmap b = decodeFile(f);
        if (b != null) {
            Log.d(TAG, "Hit cache in the FileCache");
            return b;
        }
        
        Log.d(TAG, "Didn't hit cache in the FileCache. Ready to download the image");
        
        // from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            Log.d(TAG, "Downloading the image");
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.copyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            Log.d(TAG, "Downloaded the image");
            return bitmap;
        } catch (Exception ex) {
            Log.w(TAG, ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Context a = photoToLoad.imageView.getContext();
            Log.d(TAG, "Ready to start refresh ui");
            Log.d(TAG, "Imageview'context is " + a.getClass().getName() );
            handler.post(bd);
            Log.d(TAG, "start ui thread for refresh");
        }
    }

    /**
     * 判读请求的URL，在Memory里面是否存在
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            Log.d(TAG, "now begin refresh imageview in the ui thread");
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                Log.d(TAG, "Refresh ImageView By the Profile image");
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                Log.d(TAG, "Refresh ImageView By the Dummy image");
                photoToLoad.imageView.setImageResource(stub_id);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}
