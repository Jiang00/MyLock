package com.security.manager.lib.io;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.security.manager.AppsCore;

/**
 * Created by SongHualin on 6/29/2015.
 */
public class LoadSafeThumbnail extends LoadIconFromApp {
    private static LoadSafeThumbnail instance = new LoadSafeThumbnail();
    public static LoadSafeThumbnail Instance() {
        return instance;
    }

    @Override
    protected Bitmap getBitmap(String url, LoadingNotifiable notifiable) throws PackageManager.NameNotFoundException {
        return BitmapFactory.decodeFile(AppsCore.s(url, true));
    }
}
