package com.security.manager.asyncmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


import com.privacy.lock.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by huale on 2015/2/6.
 */
public class SecurityImgManager {
    public static final String ROOT = Environment.getExternalStorageDirectory() + BuildConfig.ROOT_DIR_NAME;
    public static final String CACHE_ROOT = ROOT + "h/";
    static {
        new File(CACHE_ROOT).mkdirs();
    }

    public static LruCache<String, Bitmap> cache;

    public static final LruCache<String, Bitmap> cacheOld = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() * .25f)){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (evicted || (oldValue != newValue && newValue != null && !oldValue.isRecycled())){
//                oldValue.recycle();
            }
        }
    };

    public static boolean isImageExists(String fileName){
        if (cache.get(fileName) != null) return true;
        File file = new File(CACHE_ROOT + fileName);
        return file.exists() && file.length() > 0;
    }

    public static void saveImageToSDCard(String fileName, Bitmap bmp){
        try {
            File f = new File(CACHE_ROOT + fileName);
            FileOutputStream stream = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cacheImage(String fileName, ImageView v){
        Drawable bd = v.getDrawable();
        if (bd instanceof BitmapDrawable){
            Bitmap bmp = ((BitmapDrawable) bd).getBitmap();
            if (bmp != null && !bmp.isRecycled()){
                cache.put(fileName, bmp);
            }
        }
    }

    public static void setImageView(ImageView v, String url, Runnable runnable){
        if (confirmedImageIsValid(v, url)) return;
        executor.execute(runnable);
    }

    public static void setImageView(final ImageView v, final String url, final boolean local){
        if (confirmedImageIsValid(v, url)) return;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (local){
                    if (cache.get(url) == null){
                        final Bitmap bmp = BitmapFactory.decodeFile(CACHE_ROOT + url);
                        try{
                            new Handler(v.getContext().getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    v.setImageBitmap(bmp);
                                }
                            });
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static boolean confirmedImageIsValid(ImageView v, String url) {
        Bitmap bitmap = cache.get(url);
        if (bitmap != null){
            if (!bitmap.isRecycled()){
                v.setImageBitmap(bitmap);
                cache.remove(url);
                return true;
            } else {
                cache.remove(url);
            }
        }
        return false;
    }

    static Executor executor = new ThreadPoolExecutor(5, 8, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(64));
}
