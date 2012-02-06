/**
 * 
 */
package com.akb48plus.common.image.cache;

import java.io.File;
import java.net.URLEncoder;

import com.akb48plus.R;

import android.content.Context;

/**
 * @author QuSheng
 * 
 */
public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
                    context.getString(R.string.app_name));
        else
            cacheDir = context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {

        //String filename = String.valueOf(url.hashCode());
        // Another possible solution (thanks to grantland)
         String filename = URLEncoder.encode(url);
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
