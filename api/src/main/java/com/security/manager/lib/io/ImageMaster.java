package com.security.manager.lib.io;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import com.security.manager.lib.Utils;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class ImageMaster {
    private static final String TAG = "Privacy::ImageMaster";

    public static android.support.v4.util.LruCache<String, Bitmap> imageCache;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private static final class LruCache extends android.util.LruCache<String, Bitmap> {

        private LruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (newValue != oldValue) {
//                oldValue.recycle();
            }
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return getBitmapSize(value);
        }

        private static int getBitmapSize(Bitmap bitmap) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return bitmap.getAllocationByteCount();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                return bitmap.getByteCount();
            } else {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static boolean hasImage(String key){
        return imageCache.get(key) != null;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static Bitmap getImage(String key){
        return imageCache.get(key);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void addImage(String key, Bitmap bmp){
        if (bmp == null || bmp.isRecycled()) {
            Utils.LOGE(TAG, "bmp can't be null or recycled for " + key);
            return;
        }
        imageCache.put(key, bmp);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void remove(String key) {
        imageCache.remove(key);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void evictAll(){
        imageCache.evictAll();
    }
}
