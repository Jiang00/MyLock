package com.security.manager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.security.manager.lib.Utils;


/**
 * Created by superjoy on 2014/8/25.
 */
public class PreferenceService extends Service {

    private long time;
    private Handler handler;
    private boolean setting_permission;
    private boolean setting_power_mode;
    private boolean setting_camera;
    private boolean setting_alert_window;
    private boolean setting_permission_main;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setting_permission = intent.getBooleanExtra("setting_permission", false);
        setting_permission_main = intent.getBooleanExtra("setting_permission_main", false);
        setting_power_mode = intent.getBooleanExtra("setting_power_mode", false);
        setting_camera = intent.getBooleanExtra("setting_camera", false);
        setting_alert_window = intent.getBooleanExtra("setting_alert_window", false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        time = System.currentTimeMillis();
        handler = new Handler();

        handler.postDelayed(runnable, 2000);
        super.onCreate();
    }

    Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            Log.e("chfq", "===handler===");
            //5.0权限
            if (setting_permission) {
                if (!Utils.requireCheckAccessPermission(PreferenceService.this)) {
                    Log.e("chfq", "==PreferenceService=requireCheckAccessPermission=");
                    stopService(new Intent(PreferenceService.this, PreferenceService.class));
                    Intent intent = new Intent(PreferenceService.this, SecuritySettingsAdvance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    handler.removeCallbacks(runnable);
                } else {
                    long time2 = System.currentTimeMillis();
                    Log.e("chfq", "=====time2 - time=====" + (time2 - time));
                    if (time2 - time > 5 * 60 * 1000) {
                        handler.removeCallbacks(runnable);
                    } else {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            } else if (setting_permission_main) {
                if (!Utils.requireCheckAccessPermission(PreferenceService.this)) {
                    Log.e("chfq", "==PreferenceService=requireCheckAccessPermission=");
//                    MyApplication.getInstance().destoryActivity();
                    stopService(new Intent(PreferenceService.this, PreferenceService.class));
                    Intent intent = new Intent(PreferenceService.this, SecurityAppLock.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//若是跳转不过去，通过Intent.FLAG_ACTIVITY_CLEAR_TOP重新加载
                    startActivity(intent);
                    handler.removeCallbacks(runnable);
                } else {
                    long time2 = System.currentTimeMillis();
                    Log.e("chfq", "=====time2 - time=====" + (time2 - time));
                    if (time2 - time > 5 * 60 * 1000) {
                        handler.removeCallbacks(runnable);
                    } else {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            } else if (setting_camera) {
                //照相机
                if (ContextCompat.checkSelfPermission(PreferenceService.this,
                        android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(PreferenceService.this, SecuritySettingsAdvance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    handler.removeCallbacks(runnable);
                } else {
                    long time2 = System.currentTimeMillis();
                    Log.e("chfq", "=====time2 - time=====" + (time2 - time));
                    if (time2 - time > 5 * 60 * 1000) {
                        handler.removeCallbacks(runnable);
                    } else {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            } else if (setting_alert_window) {
                if (Settings.canDrawOverlays(PreferenceService.this)) {
                    Intent intent = new Intent(PreferenceService.this, SecuritySettingsAdvance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    handler.removeCallbacks(runnable);
                } else {
                    long time2 = System.currentTimeMillis();
                    Log.e("chfq", "=====time2 - time=====" + (time2 - time));
                    if (time2 - time > 5 * 60 * 1000) {
                        handler.removeCallbacks(runnable);
                    } else {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            } else {
                //省电模式
                Log.e("chfq", "====isAccessibilitySettingsOn====" + isAccessibilitySettingsOn(PreferenceService.this));
                if (isAccessibilitySettingsOn(PreferenceService.this)) {
                    Intent intent = new Intent(PreferenceService.this, SecuritySettingsAdvance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    handler.removeCallbacks(runnable);
                } else {
                    long time2 = System.currentTimeMillis();
                    Log.e("chfq", "=====time2 - time=====" + (time2 - time));
                    if (time2 - time > 5 * 60 * 1000) {
                        handler.removeCallbacks(runnable);
                    } else {
                        handler.postDelayed(runnable, 1000);
                    }
                }
            }

        }
    };

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = getPackageName() + "/" + AccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        stopService(new Intent(PreferenceService.this, PreferenceService.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

