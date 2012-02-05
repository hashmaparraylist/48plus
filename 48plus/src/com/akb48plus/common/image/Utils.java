/**
 * 
 */
package com.akb48plus.common.image;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

import android.graphics.drawable.Drawable;



/**
 * @author QuSheng
 *
 */
public class Utils {
    
    /**
     * image cache
     */
    public static final ConcurrentMap<String, CacheBean> imageCache = new MapMaker().makeMap();
    
    public static CacheBean buildCacheBean() {
        return new CacheBean();
    }
    
    public static class CacheBean {
        public static final int STATUS_UNDO = 0;
        public static final int STATUS_OVER = 2;
        
        private Drawable image = null;
        private int status = STATUS_UNDO;
        
        public Drawable getImage() {
            return image;
        }
        public void setImage(Drawable image) {
            this.image = image;
        }
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
        
        public boolean isNotCaching() {
            return ((this.status == STATUS_UNDO) ? true: false);
        }
    }
}
