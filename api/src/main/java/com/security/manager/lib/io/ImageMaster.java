package com.security.manager.lib.io;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.security.manager.lib.Utils;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class ImageMaster {
    private static final String TAG = "Privacy::ImageMaster";

    public static LruCache<String, Bitmap> imageCache;

    public static final LruCache<String, Bitmap> imageCacheOld = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() * .25)){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (oldValue != newValue){
                oldValue.recycle();
                Utils.LOGE(TAG, key + " is recycled ");
            }
        }
    };

    public static boolean hasImage(String key){
        return imageCache.get(key) != null;
    }

    public static Bitmap getImage(String key){
        return imageCache.get(key);
    }

    public static void addImage(String key, Bitmap bmp){
        if (bmp == null || bmp.isRecycled()) {
            Utils.LOGE(TAG, "bmp can't be null or recycled for " + key);
            return;
        }
        imageCache.put(key, bmp);
    }

    public static void remove(String key) {
        imageCache.remove(key);
    }

    public static void evictAll(){
        imageCache.evictAll();
    }
}
