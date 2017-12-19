package com.security.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Parcelable;

import com.android.client.AndroidSdk;
import com.auroras.module.charge.saver.aurorasprotectservice.ServiceBattery;
import com.auroras.module.charge.saver.aurorasutils.AurorasUtils;
import com.auroras.module.charge.saver.aurorasutils.BatteryConstants;
import com.ivy.ivyshop.ShopMaster;
import com.ivy.kpa.DaemonClient;
import com.ivy.kpa.DaemonConfigurations;
import com.ivymobi.applock.free.R;
import com.security.gallery.view.TileBitmapDrawable;
import com.security.manager.asyncmanager.SecurityImgManager;
import com.security.manager.db.PreData;
import com.security.manager.db.SecurityPreference;
import com.security.manager.lib.BaseApp;
import com.security.manager.lib.datatype.SDataType;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.meta.MApps;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by SongHualin on 5/6/2015.
 */
public class App extends BaseApp {
    private int video_lock;
    private int show_notice;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        if (requireEarlyReturn()) {
            return;
        }

        watcher = LeakCanary.install(this);
        SDataType.init(this);

        if (SecurityMyPref.isEnglish()) {
            if (getResources().getConfiguration().locale != Locale.ENGLISH) {
                Configuration cfg = getResources().getConfiguration();
                cfg.locale = Locale.ENGLISH;
                getResources().updateConfiguration(cfg, getResources().getDisplayMetrics());
            }
        }
        AndroidSdk.onCreate(this);
        ShopMaster.onCreate(this);
        AppsCore.init(this, SecurityImgManager.ROOT);
        ImageManager.initialize(this);
        ImageMaster.imageCache = TileBitmapDrawable.initCache(this);
        SecurityImgManager.cache = ImageMaster.imageCache;
        SecurityPreference.initialize(this);
//        Start.start(this);
        startService(new Intent(this, SecurityService.class));

        MApps.init();
        SecuritProfiles.init();

        //charging
        try {
            String flurryString = AndroidSdk.getExtraData();
            JSONObject baseJson = new JSONObject(flurryString);
            video_lock = baseJson.getInt("show_charging");
            show_notice = baseJson.getInt("show_notification");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startService(new Intent(this, ServiceBattery.class));
        AurorasUtils.writeData(this, BatteryConstants.CHARGE_SAVER_TITLE, getString(R.string.app_name));
        AurorasUtils.writeData(this, BatteryConstants.CHARGE_SAVER_ICON, R.drawable.ic_launcher);
        if (video_lock == 1) {
            AurorasUtils.writeData(App.this, BatteryConstants.CHARGE_SAVER_SWITCH, true);
        } else {
            AurorasUtils.writeData(App.this, BatteryConstants.CHARGE_SAVER_SWITCH, false);
        }

        if (show_notice == 1) {
            SecurityMyPref.setVisitor(true);
            SecurityMyPref.setNotification(true);
            startService(new Intent(this, NotificationService.class));
        }
        //创建快捷方式
//        if (!isInstallShortcut()) {
        if (PreData.getDB(this, "isInstallShortcut", false)) {
            PreData.putDB(this, "isInstallShortcut", true);
            createShortCut();
        }
    }

    public void createShortCut() {
//创建快捷方式的Intent
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        //需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        //点击快捷图片，运行的程序主入口
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), SecurityPatternActivity.class));
        //发送广播。OK
        sendBroadcast(shortcutintent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DaemonClient mDaemonClient = new DaemonClient(base, new DaemonConfigurations.DaemonListener() {
            @Override
            public void onPersistentStart(Context context) {

            }

            @Override
            public void onDaemonAssistantStart(Context context) {

            }

            @Override
            public void onWatchDaemonDead() {

            }
        });

        mDaemonClient.onAttachBaseContext(base);
    }

    static RefWatcher watcher;

    public static RefWatcher getWatcher() {
        return watcher;
    }

    public static SharedPreferences getSharedPreferences() {
        return App.getContext().getSharedPreferences("cf", MODE_MULTI_PROCESS);
    }

    public static Context getContrext() {
        return context;
    }
}
