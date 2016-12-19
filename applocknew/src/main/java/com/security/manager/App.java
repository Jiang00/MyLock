package com.security.manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.android.client.AndroidSdk;
import com.security.gallery.view.TileBitmapDrawable;
import com.security.manager.db.SecurityPreference;
import com.security.manager.lib.BaseApp;
import com.security.manager.lib.datatype.SDataType;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.asyncmanager.SecurityImgManager;
import com.security.manager.meta.MApps;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.Locale;

/**
 * Created by SongHualin on 5/6/2015.
 */
public class App extends BaseApp{

    @Override
    public void onCreate() {
        super.onCreate();

        if (requireEarlyReturn()) {
            return;
        }

        watcher = LeakCanary.install(this);
        SDataType.init(this);

        if (SecurityMyPref.isEnglish()){
            if (getResources().getConfiguration().locale != Locale.ENGLISH){
                Configuration cfg = getResources().getConfiguration();
                    cfg.locale = Locale.ENGLISH;
                getResources().updateConfiguration(cfg, getResources().getDisplayMetrics());
            }
        }
        AppsCore.init(this, SecurityImgManager.ROOT);
        ImageManager.initialize(this);
        ImageMaster.imageCache = TileBitmapDrawable.initCache(this);
        SecurityImgManager.cache = ImageMaster.imageCache;
        SecurityPreference.initialize(this);
//        Start.start(this);
        startService(new Intent(this, SecurityService.class));
        if(SecurityMyPref.getNotification()){
            startService(new Intent(this,NotificationService.class));
        }
        MApps.init();
        SecuritProfiles.init();
    }

    static RefWatcher watcher;

    public static RefWatcher getWatcher() {
        return watcher;
    }

    public static SharedPreferences getSharedPreferences() {
        return App.getContext().getSharedPreferences("cf", MODE_MULTI_PROCESS);
    }
}
