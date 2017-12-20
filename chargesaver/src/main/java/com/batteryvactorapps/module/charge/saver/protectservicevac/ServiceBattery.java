package com.batteryvactorapps.module.charge.saver.protectservicevac;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.batteryvactorapps.module.charge.saver.BatteryProtectActivityVac;
import com.batteryvactorapps.module.charge.saver.R;
import com.batteryvactorapps.module.charge.saver.utilsvac.BatteryConstants;
import com.batteryvactorapps.module.charge.saver.utilsvac.MyUtils;
import com.batteryvactorapps.module.charge.saver.utilsvac.WidgetContainer;
import com.batteryvactorapps.module.charge.saver.entry.BatteryEntry;
import com.batteryvactorapps.module.charge.saver.protectview.ProtectBatteryView;

/**
 * Created by on 2016/10/20.
 */
public class ServiceBattery extends Service {
    private boolean b = false;
    private static final int MSG_SCREEN_ON_DELAYED = 100;
    private static final int MSG_BATTERY_CHANGE_DELAYED = 5000;
    private Handler mHandler = new Handler();
    private WidgetContainer container;
    private ProtectBatteryView batteryView = null;
    public BatteryEntry entry;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    private Runnable batteryChangeRunnable = new Runnable() {
        @Override
        public void run() {
            if (batteryView != null) {
                batteryView.bind(entry);
            }
        }
    };

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();

            if (TextUtils.equals(Intent.ACTION_BATTERY_CHANGED, action)) {
                batteryChange(intent);
                mHandler.removeCallbacks(batteryChangeRunnable);
                mHandler.postDelayed(batteryChangeRunnable, MSG_BATTERY_CHANGE_DELAYED);
            } else if (Intent.ACTION_POWER_CONNECTED.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
                showChargeView();
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("show") && intent.getBooleanExtra("show", false)) {
            b = true;
            showChargeView();
        }
        return START_STICKY_COMPATIBILITY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        try {
            Intent localIntent = new Intent();
            localIntent.setClass(this, ServiceBattery.class);
            this.startService(localIntent);
        } catch (Exception e) {
        }
    }

    private void showChargeView() {
        boolean isChargeScreenSaver = (boolean) MyUtils.readData(this, BatteryConstants.CHARGE_SAVER_SWITCH, true);
        if (!isChargeScreenSaver) {
            return;
        }
        if ((Boolean) MyUtils.readData(this, BatteryConstants.IS_ACTIVITY, true)) {
            if (entry == null || !entry.isCharging()) {
                return;
            }
            Intent intent = new Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (MyUtils.readData(this, BatteryConstants.KEY_SAVER_TYPE, BatteryConstants.TYPE_HOR_BAR).equals(BatteryConstants.TYPE_HOR_BAR)) {
                intent.putExtra("type", "bar");
            } else {
                intent.putExtra("type", "duck");
            }
            intent.setClass(this, BatteryProtectActivityVac.class);
            startActivity(intent);
            return;
        }

        if (entry == null) {
            return;
        }
        boolean isCharging = entry.isCharging();
        if (!isCharging && !b) {
            if (batteryView != null) {
                batteryView.bind(entry);
            }
            return;
        }
        try {
            if (container == null) {
                container = new WidgetContainer.Builder()
                        .setHeight(WidgetContainer.MATCH_PARENT)
                        .setWidth(WidgetContainer.MATCH_PARENT)
                        .setOrientation(WidgetContainer.PORTRAIT)
                        .build(this);
            }
            if (MyUtils.readData(this, BatteryConstants.KEY_SAVER_TYPE, BatteryConstants.TYPE_HOR_BAR).equals(BatteryConstants.TYPE_HOR_BAR)) {
                if (batteryView == null) {
                    batteryView = (ProtectBatteryView) LayoutInflater.from(this).inflate(R.layout.charge_saver, null);
                    batteryView.bind(entry);
                    batteryView.setUnlockListener(horUnlock);
                }
                container.removeAllViews();
                container.addView(batteryView,
                        container.makeLayoutParams(
                                WidgetContainer.MATCH_PARENT, WidgetContainer.MATCH_PARENT, Gravity.CENTER));
                container.addToWindow();
            }
        } catch (Exception e) {
        }
    }

    ProtectBatteryView.UnlockListener horUnlock = new ProtectBatteryView.UnlockListener() {
        @Override
        public void onUnlock() {
            if (container != null) {
                container.removeFromWindow();
                container.removeAllViews();
                container = null;
                batteryView = null;
            }
        }
    };
}
