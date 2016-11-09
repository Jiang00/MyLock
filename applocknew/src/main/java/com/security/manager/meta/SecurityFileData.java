package com.security.manager.meta;

import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by superjoy on 2014/9/19.
 */
public class SecurityFileData {
    public String url;
    public long id;
    public String name;
    public String bucketUrl;
    public boolean isDir;
    public File[] sons;

    public static final byte TYPE_UNKNOWN = -1;
    public static final byte TYPE_PIC = 0;
    public static final byte TYPE_VIDEO = 1;
    public static final byte TYPE_COMMON = 2;
    public static final byte TYPE_AUDIO = 3;

    public static Uri getUri(int fileType){
        switch (fileType){
            case SecurityFileData.TYPE_PIC:
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            case SecurityFileData.TYPE_VIDEO:
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            case SecurityFileData.TYPE_AUDIO:
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            default:
                return null;
        }
    }
}
