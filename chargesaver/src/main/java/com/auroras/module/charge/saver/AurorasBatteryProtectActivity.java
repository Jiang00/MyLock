package com.auroras.module.charge.saver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.auroras.module.charge.saver.entry.BatteryEntry;
import com.auroras.module.charge.saver.protectview.ProtectBatteryView;

public class AurorasBatteryProtectActivity extends Activity {
    private ProtectBatteryView batteryView;
    private BatteryEntry entry;

    private void doBar() {
        try {
            batteryView = (ProtectBatteryView) LayoutInflater.from(this).inflate(R.layout.charge_saver, null);
            setContentView(batteryView);
            batteryView.setUnlockListener(new ProtectBatteryView.UnlockListener() {
                @Override
                public void onUnlock() {
                    AurorasBatteryProtectActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, R.anim.charge_exit);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void hideBottomUIMenu() {
        try {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
                View v = this.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception e) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
        this.finish();
        overridePendingTransition(R.anim.charge_exit, R.anim.charge_exit);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.charge_exit, R.anim.charge_exit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideBottomUIMenu();
        String type;
        try {
            type = getIntent().getExtras().getString("type");
        } catch (Exception e) {
            type = "bar";
        }
        if (TextUtils.equals(type, "bar")) {
            doBar();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mReceiver, intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(Intent.ACTION_BATTERY_CHANGED, intent.getAction())) {
                batteryChange(intent);
            } else if (TextUtils.equals(Intent.ACTION_SCREEN_ON, intent.getAction())) {
                Log.d("MyTest", "ON  batteryView = " + batteryView);
                if (batteryView != null) {
                    batteryView.reStartBubble();
                }
            } else if (TextUtils.equals(Intent.ACTION_SCREEN_OFF, intent.getAction())) {
                Log.d("MyTest", "OFF  batteryView = " + batteryView);
                if (batteryView != null) {
                    batteryView.pauseBubble();
                }
            } else if (TextUtils.equals(Intent.ACTION_POWER_CONNECTED, intent.getAction())) {
                batteryView.setCharing(true);
            } else if (TextUtils.equals(Intent.ACTION_POWER_DISCONNECTED, intent.getAction())) {
                batteryView.setCharing(false);
            }
        }
    };

    public void batteryChange(Intent intent) {
        if (entry == null) {
            entry = new BatteryEntry(this, intent);
        } else {
            entry.update(intent);
            entry.evaluate();
        }
        if (batteryView != null) {
            batteryView.bind(entry);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (batteryView != null) {
            batteryView.pauseBubble();
        }
    }

    @Override
    protected void onDestroy() {
        if (batteryView != null) {
            batteryView.pauseBubble();
        }
        batteryView = null;
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
