package com.security.manager.meta;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

/**
 * Created by song on 15/11/12.
 */
public class File {
    public static final int TYPE_PIC = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_COMMON = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_TXT = 4;
    public static final int TYPE_OFFICE = 5;
    public static final int TYPE_ZIP = 6;
    public static final int TYPE_APK = 7;
    public static final int TYPE_OTHER = 8;
    public static final int TYPE_LAST = 9;
    public static final int TYPE_PIC_NATIVE = 127;

    public String filePath;
    public long idInDB;
    public String fileName;
    public int fileType;
    public int dateModified;
    public boolean safe;

    public Bitmap requestNormalThumb(ContentResolver resolver) {
        switch (fileType) {
            case TYPE_PIC:
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opt);
                opt.inSampleSize = calcSampleSize(opt.outWidth, opt.outHeight, 192, 192);
                opt.inJustDecodeBounds = false;
                return BitmapFactory.decodeFile(filePath, opt);

            case TYPE_VIDEO:
                return MediaStore.Video.Thumbnails.getThumbnail(resolver, idInDB, MediaStore.Video.Thumbnails.MICRO_KIND, null);

            default:
                return null;
        }
    }


    public static int calcSampleSize(int width, int height, int requireWidth, int requireHeight) {
        if (width <= requireWidth && height <= requireHeight) return 1;
        int inSampleSize = 1;

        if (height > requireHeight || width > requireWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) requireHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) requireWidth);
            }
        }
        return inSampleSize;
    }

    public boolean isVideo() {
        return fileType == TYPE_VIDEO;
    }
}
