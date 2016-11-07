package com.security.mymodule;

/**
 * Created by song on 15/8/5.
 */
public class FileType {
    public static final int TYPE_PIC = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_COMMON = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_PDF = 4;
    public static final int TYPE_DOC = 5;
    public static final int TYPE_EXCEL = 6;
    public static final int TYPE_POWER_POINT = 7;
    public static final int TYPE_TXT = 8;
    public static final int TYPE_LAST = 9;

    public static final int TYPE_PIC_NATIVE = 127;

    public String filePath;
    public long idInDB;
    public String fileName;
    public int fileType;
    public int dateModified;
    public boolean safe;




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
