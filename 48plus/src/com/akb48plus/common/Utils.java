/**
 * 
 */
package com.akb48plus.common;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author QuSheng
 * 
 */
public class Utils {

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
}
