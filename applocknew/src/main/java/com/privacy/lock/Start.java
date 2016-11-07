package com.privacy.lock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by huale on 2014/12/10.
 */
public class Start extends Service {
    public static void start(Context context) {
        if (Build.VERSION.SDK_INT >= 1) {
            return;
        }
        int pid = Process.myPid();
        File pidFile = new File(context.getFilesDir() + "/pid");
        try {
            pidFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(pidFile);
            fos.write((pid + "").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.startService(new Intent(context, Start.class).putExtra("pid", pid));
    }

    public static native int e(String process, String id, String component, int pid, String pidFile);

    public static native boolean k(int pid);

    static {
//        System.loadLibrary("dog");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        int pid = getSharedPreferences("cc", MODE_PRIVATE).getInt("pid", -1);
        if (pid != -1) {
            if (k(pid)) {
                stopSelf(startId);
                return START_NOT_STICKY;
            }
        }

        getSharedPreferences("cc", MODE_PRIVATE).edit().putInt("pid", Process.myPid()).apply();
        if (intent != null) {
            new Thread() {
                @Override
                public void run() {
                    process(Start.this, getUserSerial(), intent.getIntExtra("pid", -1));
                }
            }.start();
        }
        return START_NOT_STICKY;
    }

    private String getUserSerial() {
        Object userManager = getSystemService("user");
        if (userManager == null) {
            return null;
        }

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);

            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static int process(Context context, String serial, int pid) {
        try {
            String t = context.getDir("t", Context.MODE_PRIVATE) + "/core";
            File defTheme = new File(t);
            FileOutputStream fos = new FileOutputStream(defTheme);
            int dog = 0;
            if (Build.CPU_ABI.startsWith("x86")) {
                dog = R.raw.xdog;
            } else {
                dog = Build.CPU_ABI.startsWith("armeabi-v7a") ? R.raw.a7dog : R.raw.adog;
            }

            InputStream is = context.getResources().openRawResource(dog);
            int available = is.available();
            byte buffer[] = new byte[available];
            is.read(buffer);
            fos.write(buffer);
            is.close();
            fos.close();
            defTheme.setExecutable(true);
            e(t, serial == null ? "s" : serial, context.getPackageName() + "/" + Worker.class.getName(), pid, context.getFilesDir() + "/pid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
