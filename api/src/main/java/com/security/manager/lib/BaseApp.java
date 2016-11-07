package com.security.manager.lib;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.security.manager.lib.io.SafeDB;

/**
 * Created by SongHualin on 6/25/2015.
 */
public class BaseApp extends Application {
    private static Handler handler;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        if (requireEarlyReturn()) {
            return;
        }
        handler = new Handler(getMainLooper());
        context = getApplicationContext();
        SafeDB.defaultDB();
    }

    protected boolean requireEarlyReturn() {
        String processName = getCurProcessName(this);
        //非主进程，不要进行下面的初始化
        if (processName != null && processName.contains(":")) {
            return true;
        } else {
            return false;
        }
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        handler = null;
        super.onTerminate();
    }

    public static void runOnUiThread(Runnable runnable){
        if (runnable != null){
            handler.post(runnable);
        }
    }

    public static Handler getHandler(){
        return handler;
    }

    public static Context getContext() {
        return context;
    }


}
