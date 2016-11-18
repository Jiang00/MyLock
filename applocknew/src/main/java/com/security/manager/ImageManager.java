package com.security.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by song on 15/9/22.
 */
public class ImageManager {
    private static final int TILE_SIZE_DENSITY_HIGH = 256;

    private static final int TILE_SIZE_DEFAULT = 128;

    private static BitmapLruCache cache;

    private static final class BitmapLruCache extends LruCache<String, Bitmap> {

        private BitmapLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (newValue != oldValue) {
                oldValue.recycle();
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

    private static void getDisplayMetrics(Context context, DisplayMetrics outMetrics) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(outMetrics);
        } else {
            display.getMetrics(outMetrics);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                try {
                    outMetrics.widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                    outMetrics.heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                } catch (Exception ignore) {
                }
            }
        }
    }

    public static void initialize(Context context) {
        final DisplayMetrics metrics = new DisplayMetrics();
        getDisplayMetrics(context, metrics);

        int tileSize = metrics.densityDpi >= DisplayMetrics.DENSITY_HIGH ? TILE_SIZE_DENSITY_HIGH : TILE_SIZE_DEFAULT;
        // The Tile can be reduced up to half of its size until the next level of tiles is displayed
        final int maxHorizontalTiles = (int) Math.ceil(2 * metrics.widthPixels / (float) tileSize);
        final int maxVerticalTiles = (int) Math.ceil(2 * metrics.heightPixels / (float) tileSize);

        // The shared cache will have the minimum required size to display all visible tiles
        // Here, we multiply by 4 because in ARGB_8888 config, each pixel is stored on 4 bytes
        final int cacheSize = 4 * maxHorizontalTiles * maxVerticalTiles * tileSize * tileSize;

        final int memory = (int) (Runtime.getRuntime().maxMemory() / 4L);

        cache = new BitmapLruCache(cacheSize > memory ? cacheSize : memory);
    }

    public static boolean has(String key) {
        return cache.get(key) != null;
    }

    public static Bitmap get(String key) {
        return cache.get(key);
    }

    public static void put(String key, Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            cache.put(key, bmp);
        }
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static void evictAll() {
        cache.evictAll();
    }



    public static void saveImage(String filePath, Bitmap bmp) {
        try {
            File f = new File(filePath);
            FileOutputStream stream = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean removeImage(String filePath) {
        return new File(filePath).delete();
    }
}
