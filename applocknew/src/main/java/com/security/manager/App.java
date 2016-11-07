package com.security.manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.security.gallery.view.TileBitmapDrawable;
import com.security.manager.db.Preference;
import com.security.manager.lib.BaseApp;
import com.security.manager.lib.datatype.SDataType;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.asyncmanager.ImageManager;
import com.security.manager.meta.MApps;
import com.security.manager.meta.MProfiles;
import com.security.manager.meta.Pref;
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

        if (Pref.isEnglish()){
            if (getResources().getConfiguration().locale != Locale.ENGLISH){
                Configuration cfg = getResources().getConfiguration();
                    cfg.locale = Locale.ENGLISH;
                getResources().updateConfiguration(cfg, getResources().getDisplayMetrics());
            }
        }

        AppsCore.init(this, ImageManager.ROOT);
//        SafeDB.initialize(this, getHandler());


        ImageMaster.imageCache = TileBitmapDrawable.initCache(this);
        ImageManager.cache = ImageMaster.imageCache;
//        Start.start(this);
        Preference.initialize(this);
        startService(new Intent(this, Worker.class));


        MApps.init();
        MProfiles.init();
    }

    static RefWatcher watcher;

    public static RefWatcher getWatcher() {
        return watcher;
    }

    public static SharedPreferences getSharedPreferences() {
        return App.getContext().getSharedPreferences("cf", MODE_MULTI_PROCESS);
    }
}
