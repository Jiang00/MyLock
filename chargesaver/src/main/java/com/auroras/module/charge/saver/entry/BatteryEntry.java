package com.auroras.module.charge.saver.entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.android.internal.os.PowerProfile;

/**
 * Created by song on 16/4/19.
 */
public class BatteryEntry {
    public int status;
    public int scale;
    public int level;
    public int plugged;
    public int voltage;
    public int health;
    public int temp;
    public String tech;

    private int averagePercent1DisChargingTime;
    private int capacity;
    private boolean charging;
    private int averagePercent1ChargingTime;

    LevelStep chargingStep;
    LevelStep disChargingStep;

    public BatteryEntry(Context context, Intent info) {
        update(info);
        update(context);
        evaluate();
    }

    private void update(Context context) {
//
        PowerProfile pp = new PowerProfile(context);
        capacity = (int) pp.getBatteryCapacity();
        final int chargingCurrent = getElectricCurrent();
        averagePercent1ChargingTime = (capacity / chargingCurrent * 3600) / 100;

        chargingStep = new LevelStep();
        disChargingStep = new LevelStep();

        chargingStep.start(level);
        disChargingStep.start(level);

        averagePercent1DisChargingTime = 900;
    }

    public int getLevel() {
        return (int) (((float) level) / (scale <= 0 ? 100.f : (float) scale) * 100);
    }

    public int getLeftTime() {
        return charging ? (averagePercent1ChargingTime * (scale - level)) : (averagePercent1DisChargingTime * level);
    }

    public int getLeftUseTime() {
        return (averagePercent1DisChargingTime * level);
    }

    public int extractHours(int time) {
        return time / 3600;
    }

    public int extractMinutes(int time) {
        return (time % 3600) / 60;
    }

    @SuppressLint("NewApi")
    public void update(Intent info) {
        status = info.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        scale = info.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        level = info.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        plugged = info.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        voltage = info.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        health = info.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        temp = info.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        tech = info.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }

    @Override
    public String toString() {
        return "status=" + status + ",scale=" + scale + ",level=" + level + ",plugged=" + plugged +
                ",voltage=" + voltage + ",health=" + health + ",temp=" + temp + ",tech=" + tech;
    }

    public void evaluate() {
        if (plugged == 0) {
            charging = false;
            final int offsetTime = disChargingStep.update(level);
            if (offsetTime > 0 && offsetTime < 10000) {
                averagePercent1DisChargingTime = (averagePercent1DisChargingTime + offsetTime) / 2;
            }
        } else {
            charging = true;
            final int offsetTime = chargingStep.update(level);
            if (offsetTime > 0 && offsetTime < 10000) {
                if (offsetTime > (averagePercent1ChargingTime >> 1) && offsetTime < (averagePercent1ChargingTime << 1)) {
                    averagePercent1ChargingTime = (averagePercent1ChargingTime + offsetTime) / 2;
                }
            }
        }
    }

    public boolean isCharging() {
        return charging;
    }

    public int getElectricCurrent() {
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return 1000;

            case BatteryManager.BATTERY_PLUGGED_USB:
                return 600;

            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return 800;

            default:
                return 500;
        }
    }

    public int getBatteryCapacity() {
        return capacity;
    }
}
