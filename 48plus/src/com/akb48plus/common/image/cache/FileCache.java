/**
 * 
 */
package com.akb48plus.common.image.cache;

import java.io.File;
import java.net.URLEncoder;

import android.content.Context;

/**
 * @author QuSheng
 * 
 */
public class FileCache {
    private File cacheDir;
    private final static String JPEG_EXTE = ".jpg";

    public FileCache(Context context) {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            // have SDCard
            cacheDir = context.getExternalCacheDir();
        else
            // haven't SDCard
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {

        // String filename = String.valueOf(url.hashCode());
        // Another possible solution (thanks to grantland)
        String filename = URLEncoder.encode(url) + JPEG_EXTE;
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }
}
