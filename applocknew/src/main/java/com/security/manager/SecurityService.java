package com.security.manager;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.security.lib.customview.SecurityWidget;
import com.security.manager.db.SecurityProfileHelper;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.MyFrameLayout;
import com.security.manager.page.PasswordFragmentSecurity;
import com.security.manager.lib.Utils;
import com.security.manager.lib.io.SafeDB;
import com.privacy.lock.aidl.IWorker;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.page.PretentPresenter;
import com.security.manager.page.OverflowCtrl;
import com.security.manager.page.PatternFragmentSecurity;
import com.security.manager.page.SecurityThemeFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by superjoy on 2014/8/25.
 */
public class SecurityService extends Service {
    boolean home = false;
    boolean alterShow = false;
    public static final String EXTRA_WORKING = "_work_";
    public static final String EXTRA_LOCK_PKG = "_pkg_";
    public static final int WORKING_LOCK_NEW = 1;
    public static final int WORKING_TOGGLE_ON = 2;
    public static final int WORKING_TOGGLE_OFF = 3;
    public static final int WORKING_LOCK_APP = 4;
    public static final int WORKING_UPGRADE = 5;
    public static final String WORKING_LOCK_NEW_PKG_NAME = "_pkg_";

    public static void startWorking(Context context) {
        context.startService(new Intent(context, SecurityService.class).putExtra(EXTRA_WORKING, WORKING_TOGGLE_ON));
    }

    public static void stopWorking(Context context) {
        context.startService(new Intent(context, SecurityService.class).putExtra(EXTRA_WORKING, WORKING_TOGGLE_OFF));
    }

    public static void startLock(Context context, String packageName) {
        context.startService(new Intent(context, SecurityService.class)
                .putExtra(EXTRA_WORKING, WORKING_LOCK_APP)
                .putExtra(EXTRA_LOCK_PKG, packageName)
        );
    }

    HashMap<String, Long> briefTimes = new HashMap<>();
    private Runnable removeRunner = new Runnable() {
        @Override
        public void run() {
            hideAlertImmediate();
            removing = false;
        }
    };

//    public String getTopPackageName() {
//        String packageName = null;
//
//        if (!SecurityMyPref.getVisitor()) {
//            return "";
//        }
//
//        if (Build.VERSION.SDK_INT > 19) {
//            try {
//                packageName = getActivePackages();
//
//            } catch (Exception | Error e) {
//                e.printStackTrace();
//            }
//            try {
//                if (packageName == null) {
//                    packageName = getTopPackage();
//
//                }
//            } catch (Exception | Error e) {
//                e.printStackTrace();
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> lst = mActivityManager.getRunningTasks(1);
//            if (lst != null && lst.size() > 0) {
//                ActivityManager.RunningTaskInfo runningTaskInfo = lst.get(0);
//                if (runningTaskInfo.numRunning > 0 && runningTaskInfo.topActivity != null) {
//                    packageName = runningTaskInfo.topActivity.getPackageName();
//                }
//            }
//        }
//        if (packageName != null) {
//            if (packageName.equals(getPackageName())) {
//                packageName = null;
//            }
//        }
//
//        return packageName;
//    }


    public String getLauncherTopApp(Context context, ActivityManager activityManager) {
        if (!SecurityMyPref.getVisitor()) {
            return null;
        }

        String packageName = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            } else {
                return null;
            }
        } else {
            //5.0以后需要用这方法
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    packageName = event.getPackageName();
                }
            }

            if (alterShow) {
                return "show";
            }

            return packageName;
        }
    }


    private ActivityManager mActivityManager;

//    String getActivePackages() throws NoSuchFieldException, IllegalAccessException {
//        if (processState == null) {
//            processState = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
//            processState.setAccessible(true);
//        }
//        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
//        if (processInfos != null) {
//            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
////                if (processInfo.importanceReasonCode == 2) continue;
//                int anInt = processState.getInt(processInfo);
//                if (anInt == 2) return processInfo.pkgList[0];
//            }
//        }
//        return null;
//    }


    /**
     * first app user
     */
    public static final int AID_APP = 10000;

    /**
     * offset for uid ranges for each user
     */
    public static final int AID_USER = 100000;

    static HashMap<String, Boolean> excludes = new HashMap<>();

    static {
        excludes.put("com.android.systemui", true);
        excludes.put("android.process.acore", true);
        excludes.put("android.process.media", true);
        excludes.put("com.android.soundrecorder", true);
    }

//    public static String getForegroundApp() {
//        File[] files = new File("/proc").listFiles();
//        int lowestOomScore = Integer.MAX_VALUE;
//        String foregroundProcess = null;
//
//        for (File file : files) {
//            int pid;
//            String name = file.getName();
//            try {
//                pid = Integer.parseInt(name);
//            } catch (NumberFormatException e) {
//                continue;
//            }
//
//            try {
//                String cgroup = read(String.format("/proc/%d/cgroup", pid));
//                if (cgroup.contains("bg_non_interactive")) {
//                    continue;
//                }
//
//                if (!cgroup.endsWith(name)) {
//                    continue;
//                }
//
//                int uid = Integer.parseInt(cgroup.substring(cgroup.indexOf("uid_") + 4, cgroup.lastIndexOf("/")));
//                if (uid >= 1000 && uid <= 1038) {
//                    // system process
//                    continue;
//                }
//
//                int appId = uid - AID_APP;
//                // loop until we get the correct user id.
//                // 100000 is the offset for each user.
//                while (appId > AID_USER) {
//                    appId -= AID_USER;
//                }
//
//                if (appId < 0) {
//                    continue;
//                }
//
//                String cmdline = read(String.format("/proc/%d/cmdline", pid));
//                if (excludes.containsKey(cmdline)) {
//                    continue;
//                }
//
//                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
//                // String uidName = String.format("u%d_a%d", userId, appId);
//
//                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
//                if (oomScoreAdj.canRead()) {
//                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
//                    if (oomAdj != 0) {
//                        continue;
//                    }
//                }
//
//                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
//                if (oomscore < lowestOomScore) {
//                    lowestOomScore = oomscore;
//                    foregroundProcess = cmdline;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        return foregroundProcess;
//    }

    //    private static String read(String path) throws IOException {
//        BufferedReader reader = null;
//        try {
//            StringBuilder output = new StringBuilder();
//            reader = new BufferedReader(new FileReader(path));
//            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//                output.append(line);
//            }
//            return output.toString().trim();
//        } finally {
//            if (reader != null) {
//                reader.close();
//            }
//        }
//    }
    boolean sleep = false;

    public void backHome() {
        backHome_();
        hideAlertIfPossible(true);
    }

    private void backHome_() {
        alterShow = false;
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(setIntent);
    }

    public void hideAlertImmediate() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (alertContainer != null) {
            wm.removeViewImmediate(alertContainer);
            if (alertView != null) {
                ((ViewGroup) alertView).removeAllViews();
            }
            alertContainer.removeAllViews();
            alertContainer = null;
        }
        alertView = null;
        alert = false;
    }

    int delayTime = 0;

    public void hideAlertIfPossible(boolean home) {
        if (alert && !removing) {
            removing = true;
            r.home = home;
            handler.postDelayed(r, delayTime);
        }
    }

    class MyRunnable implements Runnable {
        boolean home = false;

        @Override
        public void run() {
            if (PretentPresenter.isShowing()) {
                PretentPresenter.hide();
                delayTime = 0;
            }
            if (alertView != null) {
                alertView.startAnimation(fadeout);
            } else {
                alert = false;
                removing = false;
            }
            if (home) {
                if (SecurityBridgeImpl.ins.context == null) {
                    SecurityBridgeImpl.ins.context = SecurityService.this;
                }
                SecurityBridgeImpl.ins.detachFromWindow(SecurityBridgeImpl.MENU_IDX_ALL);
            }
        }
    }

    boolean unlocked = false;
    boolean alert = false;
    boolean removing = false;
    public View alertView;
    public FrameLayout alertContainer;
    private MyRunnable r = new MyRunnable();
    Map<String, Boolean> asks = new HashMap<>();

    class LockTask extends Thread {
        private Context mContext;
        String lastPackageName = "";

        public boolean running;

        public LockTask(Context context) {
            mContext = context;
            mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            start();
        }

        static final int sleeptime = 100;

        @Override
        public void run() {


            running = true;
            while (running) {
                if (++lastAsyncTime > 1500) {
                    lastAsyncTime = 0;
                }
                final String packageName = getLauncherTopApp(SecurityService.this, mActivityManager);
                Log.e("appname", packageName + "------");


                if (packageName != null && packageName.equals("show")) {
                    try {
                        alterShow=false;
                        new Thread().sleep(600);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else{
                    alterShow=false;
                }

                if (packageName == null) {
                    try {
                        Thread.sleep(sleeptime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if (!lastPackageName.equals(packageName)) {
                    if (homes.containsKey(packageName)) {
                        home = true;
                        showWidgetIfNecessary(true);
                        if (unlocked) {
                            unlocked = false;
                        }


//                        if(alterShow){
//                            alterShow=false;
//                        }
                        int slot = App.getSharedPreferences().getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
                        switch (slot) {
                            case SecurityMyPref.PREF_BRIEF_EVERY_TIME:
                                tmpUnlockedApps.clear();
                                getSharedPreferences("tmp", MODE_PRIVATE).edit().remove(SecurityMyPref.PREF_TMP_UNLOCK).commit();
                                break;
                            default:
                                if (tmpUnlockedApps.containsKey(lastPackageName)) {
                                    briefTimes.put(lastPackageName, System.currentTimeMillis());
                                    unlockLastApplication(lastPackageName, true);
                                }
                                break;
                        }
                        hideAlertIfPossible(true);
                    } else if (homes.containsKey(lastPackageName)) {
                        showWidgetIfNecessary(false);
                    }
                    lastPackageName = packageName;
                }
                if (sleep) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                if (!alert) {
                    if (!lockApps.containsKey(packageName)) {
                        if (asks.containsKey(packageName) && SecurityMyPref.requireAsk()) {
                            App.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    protect(packageName);
                                }
                            });
                        }
                    } else if (!tmpUnlockedApps.containsKey(packageName)) {
                        if (briefTimes.containsKey(packageName)) {
                            int slot = App.getSharedPreferences().getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
                            if (slot != SecurityMyPref.PREF_BRIEF_EVERY_TIME) {
                                int time = 300;
                                if (slot != SecurityMyPref.PREF_BRIEF_5_MIN) {
                                    time = Integer.MAX_VALUE;
                                }
                                long t = briefTimes.get(packageName);
                                try {
                                    if ((System.currentTimeMillis() - t) / 1000 < time) {
                                        unlockLastApplication(packageName, false);
                                        handler.post(briefToast);
                                    } else
                                        alertForUnlock(packageName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            briefTimes.remove(packageName);
                        } else
                            alertForUnlock(packageName);
                    }
                }

                try {
                    if (home) {
                        Thread.sleep(50);
                    } else {
                        Thread.sleep(sleeptime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

//        public void attachToWindow(WindowManager wm, View v) {
//            SecurityThemeFragment.afterViewCreated(v, ctrl);
//            ((MyFrameLayout) v).setOnBackListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    backHome();
//                }
//            });
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                    0,
//                    PixelFormat.TRANSLUCENT);
//            lp.gravity = Gravity.CENTER;
//            lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//            try {
//                if (alertContainer != null) {
//                    wm.removeViewImmediate(alertContainer);
//                    alertContainer.removeAllViews();
//                }
//                alertContainer = null;
//                alertView = null;
//            } catch (Exception ignore) {
//                ignore.printStackTrace();
//            }
////            if (Build.VERSION.SDK_INT >= 21) {
////                v.setPadding(0, Utils.getDimens(SecurityService.this, 16), 0, 0);
////            }
//            wm.addView(v, lp);
//            alertContainer = (FrameLayout) v;
//            v = ((FrameLayout) v).getChildAt(0);
//            alertView = v;
//        }

        public int lastAsyncTime;

        //        Runnable dismissRunner = new Runnable() {
//            @Override
//            public void run() {
//                delayTime = 500;
//                backHome_();
//            }
//        };
        private void alertForUnlock(final String packageName) {
            lastApp = packageName;
            alterShow = true;
            if (Utils.hasSystemAlertPermission(App.getContext())) {
                alert = true;
                SecurityBridgeImpl.reset(SecurityService.this, false, true, packageName);
                Intent intent = new Intent(mContext.getApplicationContext(), UnlockApp.class).
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).
                        putExtra("action", UnlockApp.ACTION_UNLOCK_OTHER).putExtra("pkg", packageName);
                startActivity(intent);
            } else {

                Intent intent = new Intent(mContext.getApplicationContext(), UnlockApp.class).
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION).
                        putExtra("action", UnlockApp.ACTION_UNLOCK_OTHER).putExtra("pkg", packageName);
                startActivity(intent);

            }
        }
    }


    public String lastApp;
    HashMap<String, Boolean> homes = new HashMap<>();
    HashMap<String, Boolean> tmpUnlockedApps = new HashMap<>();
    IWorker.Stub binder = new IWorker.Stub() {
        @Override
        public void notifyApplockUpdate() throws RemoteException {
            notifyLockedAppsUpdate();
        }

        @Override
        public void updateProtectStatus() throws RemoteException {
            sleep = SecurityMyPref.isProtectStopped();
            if (sleep) {
                onStopProtect();
            }
        }

        @Override
        public void toggleProtectStatus() throws RemoteException {
        }

        @Override
        public void showNotification(boolean yes) throws RemoteException {
            App.getSharedPreferences().edit().putBoolean("sn", yes).apply();
            SecurityService.this.showNotification(yes);
        }

        @Override
        public boolean unlockApp(String pkg) throws RemoteException {
            lockApps.remove(pkg);
            return true;
        }

        @Override
        public boolean unlockLastApp(boolean unlockAlways) throws RemoteException {
            unlockLastApplication(lastApp, false);
            unlockSuccess(unlockAlways);
            return true;
        }

        @Override
        public boolean homeDisplayed() throws RemoteException {
            if (home) {
                home = false;
                return true;
            } else
                return false;
        }

        @Override
        public void notifyShowWidget() throws RemoteException {
        }
    };


    public void showWidgetIfNecessary(boolean show) {
        if (App.getSharedPreferences().getBoolean(SecurityMyPref.PREF_SHOW_WIDGET, false)) {
            final int visible = show ? View.VISIBLE : View.GONE;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (securityWidget != null) {
                        securityWidget.setVisibility(visible);
                    }
                }
            });
        }
    }

    SecurityWidget securityWidget;

    public void notifyLockedAppsUpdate() {
        try {
            long id = SafeDB.defaultDB().getLong(SecurityMyPref.PREF_ACTIVE_PROFILE_ID, 0L);
            SQLiteDatabase db = SecurityProfileHelper.singleton(App.getContext()).getReadableDatabase();
            Map<String, Boolean> apps = SecurityProfileHelper.ProfileEntry.getLockedApps(db, id);
            apps.remove("com.setting.SecuritySettings");
            apps.remove("");
            lockApps.clear();
            lockApps.putAll(apps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlockSuccess(boolean unlockMe) {
        unlocked = true;
        alterShow = false;
        if (unlockMe) {
            try {
                long profileId = SafeDB.defaultDB().getLong(SecurityMyPref.PREF_ACTIVE_PROFILE_ID, 1L);
                lockApps.remove(lastApp);
                SecurityProfileHelper.ProfileEntry.deleteLockedApp(SecuritProfiles.getDB(), profileId, lastApp);
                SecuritProfiles.updateProfiles();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unlockLastApplication(String app, boolean remove) {
        if (remove) {
            tmpUnlockedApps.remove(app);
        } else {
            tmpUnlockedApps.put(app, true);
        }
        StringBuilder sb = new StringBuilder();
        for (String key : tmpUnlockedApps.keySet()) {
            sb.append(key).append(';');
        }
        getSharedPreferences("tmp", MODE_PRIVATE).edit().putString(SecurityMyPref.PREF_TMP_UNLOCK, sb.toString()).commit();
    }

    public void showNotification(boolean yes) {
        if (true) return;
        if (yes) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            PendingIntent pi;
            if (sleep) {
                builder.setContentTitle(getResources().getString(R.string.security_applock_stop));
                builder.setContentText(getResources().getString(R.string.security_lock_to_open));
                builder.setTicker(getResources().getString(R.string.security_applock_stop));
                builder.setSmallIcon(R.drawable.ic_launcher);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.security_applock_stop, Toast.LENGTH_SHORT).show();
                    }
                });
                pi = PendingIntent.getService(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), SecurityService.class).putExtra(WORK_EXTRA_KEY, WORK_TURN_ON_PROTECT),
                        PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                builder.setContentTitle(getResources().getString(R.string.security_applock_open));
                builder.setContentText(getResources().getString(R.string.security_lock_to_stop));
                builder.setTicker(getResources().getString(R.string.security_applock_open));
                builder.setSmallIcon(R.drawable.ic_launcher);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.security_applock_open, Toast.LENGTH_SHORT).show();
                    }
                });
                pi = PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), SecurityTogglePatternActivity.class)
                                .putExtra("action", SecurityTogglePatternActivity.ACTION_TOGGLE_PROTECT)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            builder.setContentIntent(pi);
            builder.setOngoing(true);
            builder.setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());

            Notification n = builder.build();
            startForeground(1, n);
        } else {
            stopForeground(true);
        }
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    String getTopPackage() {
//        long ts = System.currentTimeMillis();
//        if (mUsageStatsManager == null) {
//            mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
//        }
//        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 10000, ts);
//
//        if (usageStats == null || usageStats.size() == 0) {
//
//            String pkgname = getForegroundApp();
//
//
//            return pkgname;
//        } else {
//            Collections.sort(usageStats, mRecentComp);
//            return usageStats.get(0).getPackageName();
//        }
//    }

//    public boolean isAppOnForeground(String pkgname) {
//        List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
//        if (tasksInfo.size() > 0) {
//            // 应用程序位于堆栈的顶层
//            if (pkgname.equals(tasksInfo.get(0).topActivity
//                    .getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }


    static UsageStatsManager mUsageStatsManager;

//    Comparator<UsageStats> mRecentComp = new Comparator<UsageStats>() {
//
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        @Override
//        public int compare(UsageStats lhs, UsageStats rhs) {
//            return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : ((lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1);
//        }
//    };

    LockTask task;

    void startTimer() {
        if (task == null || !task.running) {
            task = new LockTask(getApplicationContext());
        }
    }

    void stopTimer() {
        if (task != null) {
            task.running = false;
            task.interrupt();
            task = null;
        }
    }

    HashMap<String, Boolean> lockApps = new HashMap<>();
    Handler handler;
    Runnable briefToast = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), R.string.security_short_exit_detail, Toast.LENGTH_SHORT).show();
        }
    };

    Animation fadeout;
    boolean create = true;
    boolean hasAccessUsagePermission = false;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            AndroidSdk.onCreate(this);
        } catch (Exception e) {

        }
        Utils.init();
        handler = new Handler(getMainLooper());
        ApplockC.applockA(this);
        Intent serviceIntent = new Intent(this, ApplockC.class);
        startService(serviceIntent);
        if (Build.VERSION.SDK_INT >= 21) {
            hasAccessUsagePermission = Utils.checkPermissionIsGrant(App.getContext(), Utils.OP_GET_USAGE_STATS) == AppOpsManager.MODE_ALLOWED;
        }
        PackageManager pm = getPackageManager();
        SharedPreferences sp = App.getSharedPreferences();
        sleep = sp.getBoolean("stop_service", false);

        new Thread() {
            @Override
            public void run() {
                try {
                    notifyLockedAppsUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

//        if (Build.VERSION.SDK_INT >= 20) {
//            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//            PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, SecurityService.class).putExtra("alarm", true), PendingIntent.FLAG_UPDATE_CURRENT);
//            am.setRepeating(AlarmManager.RTC_WAKEUP, 1000, 1000, pi);
//        }

        if (fadeout == null) {
            fadeout = AnimationUtils.loadAnimation(this, R.anim.security_fade_out);
            fadeout.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    handler.post(removeRunner);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        String[] unlocked = getSharedPreferences("tmp", MODE_PRIVATE).getString(SecurityMyPref.PREF_TMP_UNLOCK, "").split(";");
        for (String unlock : unlocked) {
            tmpUnlockedApps.put(unlock, true);
        }

        showNotification(sp.getBoolean("sn", true));

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
        if (lst != null) {
            for (ResolveInfo resolveInfo : lst) {
                homes.put(resolveInfo.activityInfo.packageName, true);
            }
        }

        startTimer();

        BroadcastReceiver sOnBroadcastReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!App.getSharedPreferences().getBoolean("stop_service", false)) {
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                        sleep = false;
                        onWakeUp();
                    } else {
                        sleep = true;
                    }
                }
            }
        };
        IntentFilter receiverFilter = new IntentFilter();
        receiverFilter.addAction(Intent.ACTION_SCREEN_ON);
        receiverFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(sOnBroadcastReciver, receiverFilter);

        excludesClasses.put(PretentOneActivitySecurityPatternActivity.class.getName(), true);
        excludesClasses.put(PretentTwoActivitySecurityPatternActivity.class.getName(), true);
        excludesClasses.put(SecurityPatternActivity.class.getName(), true);
        excludesClasses.put(SecurityTogglePatternActivity.class.getName(), true);
        excludesClasses.put(UnlockApp.class.getName(), true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public static final int WORK_IDLE = 0;
    public static final int WORK_LOCK_NEW = 1;
    public static final int WORK_TURN_ON_PROTECT = 2;
    public static final String WORK_EXTRA_KEY = "works";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        if (intent != null) {
            if (intent.getBooleanExtra("on", false)) {
                onWakeUp();
            } else if (intent.getBooleanExtra("alarm", false)) {
                return START_STICKY;
            } else {
                switch (intent.getIntExtra(WORK_EXTRA_KEY, WORK_IDLE)) {
                    case WORK_LOCK_NEW:
                        lockNew(intent.getStringExtra("pkg"));
                        break;

                    case WORK_TURN_ON_PROTECT:
                        return START_STICKY;
                }
            }
        }

        return START_STICKY;
    }

    boolean dontaskagain = false;

    public void protect(final String pkg) {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            String label = pi.applicationInfo.loadLabel(getPackageManager()).toString();
            CharSequence protect = "<br/>" + getString(R.string.security_ask_lock_protect);
            String msg = getString(R.string.security_ask_lock, label) + protect;
            Drawable icon = pi.applicationInfo.loadIcon(getPackageManager());

            View view = new TextView(null);

            AlertDialog dialog = new AlertDialog.Builder(this, R.style.MessageBox)
                    .setView(view)
                    .setNegativeButton(R.string.security_later_, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dontaskagain) {
                                SafeDB.defaultDB().putBool("dontask_" + pkg, true).commit();
                            }
                        }
                    })
                    .setPositiveButton(R.string.security_protect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lockApps.put(pkg, true);
                            long profileId = SafeDB.defaultDB().getLong(SecurityMyPref.PREF_ACTIVE_PROFILE_ID, 1L);
                            SecurityProfileHelper.ProfileEntry.addLockedApp(SecuritProfiles.getDB(), profileId, pkg);
                            SecuritProfiles.updateProfiles();
                        }
                    }).create();
            Utils.showDialog(dialog, true);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void lockNew(final String pkg) {
        try {
            if (lockApps.containsKey(pkg)) return;
            PackageInfo pi = getPackageManager().getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            String label = pi.applicationInfo.loadLabel(getPackageManager()).toString();
            String format = getResources().getString(R.string.security_for_lock_new);
            label = String.format(format, label);
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(R.string.app_name).setIcon(R.drawable.ic_launcher).setMessage(label)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    lockApps.put(pkg, true);
                                    long profileId = SafeDB.defaultDB().getLong(SecurityMyPref.PREF_ACTIVE_PROFILE_ID, 1L);
                                    SecurityProfileHelper.ProfileEntry.addLockedApp(SecuritProfiles.getDB(), profileId, pkg);
                                    SecuritProfiles.updateProfiles();
                                }
                            }
                    ).setCancelable(false).create();
            Utils.showDialog(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    HashMap<String, Boolean> excludesClasses = new HashMap<>();

    public void onWakeUp() {
//        String pkgName = getTopPackageName();
        String pkgName = getLauncherTopApp(SecurityService.this, mActivityManager);

        if (getPackageName().equals(pkgName)) {
            String className = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            if (!excludesClasses.containsKey(className)) {
                Intent i = new Intent(getApplicationContext(), SecurityTogglePatternActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }
        int slot = App.getSharedPreferences().getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
        if (slot != SecurityMyPref.PREF_BRIEF_5_MIN) {
            tmpUnlockedApps.clear();
            unlockLastApplication("", true);
            briefTimes.clear();
        }
    }

    public void onStopProtect() {
        tmpUnlockedApps.clear();
        briefTimes.clear();
        getSharedPreferences("tmp", MODE_PRIVATE).edit().remove(SecurityMyPref.PREF_TMP_UNLOCK).commit();
    }

    @Override
    public void onDestroy() {
        Utils.LOGE("SecurityService is destroying...");
        handler = null;
        try {
            hideAlertImmediate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopForeground(true);
        stopTimer();
        super.onDestroy();


    }

}

