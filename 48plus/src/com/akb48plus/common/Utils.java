/**
 * 
 */
package com.akb48plus.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.text.format.DateFormat;

/**
 * @author QuSheng
 * 
 */
public class Utils {

    /**
     * 
     * @param is
     * @param os
     */
    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024 * 100;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
    
    /**
     * 
     * @param url
     * @param size
     * @return
     */
    public static String changePhotoSizeInUrl(String url, int size) {
        return url.replaceAll("\\?sz\\=\\d+", "?sz=" + String.valueOf(size));
    }
    
    /**
     * 
     * @param rfd3339
     * @param context
     * @return
     */
    public static String parseRfc3339ToLoacle(String rfc3339, Context context) {
        String localDateTime = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date updDateTimeStand = format.parse(rfc3339);
            localDateTime = DateFormat.getLongDateFormat(context).format(updDateTimeStand);
            localDateTime += " ";
            localDateTime += DateFormat.getTimeFormat(context).format(updDateTimeStand);
        } catch (ParseException e) {
        }
        return localDateTime;
    }
}
