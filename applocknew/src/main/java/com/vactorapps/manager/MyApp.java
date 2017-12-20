package com.vactorapps.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Parcelable;

import com.android.client.AndroidSdk;
import com.android.kpa.DaemonClient;
import com.android.kpa.KeepLiveManager;
import com.android.kpa.PersistService;
import com.batteryvactorapps.module.charge.saver.protectservicevac.ServiceBattery;
import com.batteryvactorapps.module.charge.saver.utilsvac.BatteryConstants;
import com.batteryvactorapps.module.charge.saver.utilsvac.MyUtils;
import com.ivymobi.applock.free.R;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.themesvactor.eshop.ShopMaster;
import com.vactorapps.manager.meta.MApps;
import com.vactorapps.manager.meta.SacProfiles;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.mydb.PreData;
import com.vactorapps.manager.mydb.VacPreference;
import com.vactorapps.manager.syncmanager.VacImgManager;
import com.vactorappsapi.manager.AppsCore;
import com.vactorappsapi.manager.lib.BaseApp;
import com.vactorappsapi.manager.lib.datatype.SDataType;
import com.vactorappsapi.manager.lib.io.ImageMaster;
import com.vactorappsb.gallery.view.TileBitmapDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by SongHualin on 5/6/2015.
 */
public class MyApp extends BaseApp {
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

        if (VacPref.isEnglish()) {
            if (getResources().getConfiguration().locale != Locale.ENGLISH) {
                Configuration cfg = getResources().getConfiguration();
                cfg.locale = Locale.ENGLISH;
                getResources().updateConfiguration(cfg, getResources().getDisplayMetrics());
            }
        }
        AndroidSdk.onCreate(this);
        ShopMaster.onCreate(this);
        AppsCore.init(this, VacImgManager.ROOT);
        ImageManager.initialize(this);
        ImageMaster.imageCache = TileBitmapDrawable.initCache(this);
        VacImgManager.cache = ImageMaster.imageCache;
        VacPreference.initialize(this);
//        Start.start(this);
        startService(new Intent(this, WorksService.class));

        MApps.init();
        SacProfiles.init();

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
        MyUtils.writeData(this, BatteryConstants.CHARGE_SAVER_TITLE, getString(R.string.app_name));
        MyUtils.writeData(this, BatteryConstants.CHARGE_SAVER_ICON, R.drawable.ic_launcher);
        if (video_lock == 1) {
            MyUtils.writeData(MyApp.this, BatteryConstants.CHARGE_SAVER_SWITCH, true);
        } else {
            MyUtils.writeData(MyApp.this, BatteryConstants.CHARGE_SAVER_SWITCH, false);
        }

        if (show_notice == 1) {
            VacPref.setVisitor(true);
            VacPref.setNotification(true);
            startService(new Intent(this, VacNotificationService.class));
        }
        //创建快捷方式
//        if (!isInstallShortcut()) {
        if (!PreData.getDB(this, "isInstallShortcut", false)) {
            PreData.putDB(this, "isInstallShortcut", true);
            createShortCut();
        }

        startService(new Intent(this, PersistService.class));
        KeepLiveManager.startJobScheduler(this, 1000);
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
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), FristActivity.class));
        //发送广播。OK
        sendBroadcast(shortcutintent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DaemonClient mDaemonClient = new DaemonClient(base,null);

        mDaemonClient.onAttachBaseContext(base);
    }

    static RefWatcher watcher;

    public static RefWatcher getWatcher() {
        return watcher;
    }

    public static SharedPreferences getSharedPreferences() {
        return MyApp.getContext().getSharedPreferences("cf", MODE_MULTI_PROCESS);
    }

    public static Context getContrext() {
        return context;
    }
}
