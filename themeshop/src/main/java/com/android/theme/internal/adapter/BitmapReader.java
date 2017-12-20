package com.android.theme.internal.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.common.SdkCache;

/**
 * Created by song on 2017/3/9.
 */

public class BitmapReader {
    public static Bitmap read(String fileName, Bitmap reusable, boolean external) {
        return SdkCache.cache().readBitmap(fileName, reusable, external);
    }

    public static Bitmap readBitmap(String fileName, int reqWidth, int reqHeight, boolean external) {
        if (reqHeight == 0 || reqWidth == 0) {
            throw new RuntimeException("invalid width: " + reqWidth + " or height: " + reqHeight);
        }
        try {
            final String fn = SdkCache.cache().makeName(fileName, external);
            BitmapFactory.Options e = new BitmapFactory.Options();
            e.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fn, e);

            final int height = e.outHeight;
            final int width = e.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    Log.e("readBitmap", "size " + inSampleSize + " rh " + reqHeight + " rw " + reqWidth);
                    inSampleSize *= 2;
                }
            }
            e.inSampleSize = inSampleSize;
            e.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(fn, e);
        } catch (Error | Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
