package com.batteryvactorapps.module.charge.saver.utilsvac;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by on 2016/12/14.
 */

public class MyUtils {

    public static void writeData(Context context, String key, Object value) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(BatteryConstants.MODULE_FILL_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            if (value instanceof Integer) {
                editor.putInt(key, (int) value).apply();
            } else if (value instanceof String) {
                editor.putString(key, String.valueOf(value)).apply();
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value).apply();
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value).apply();
            }
        }
    }

    public static Object readData(Context context, String key, Object defaultValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(BatteryConstants.MODULE_FILL_NAME, Context.MODE_PRIVATE);
            Map<String, ?> map = sp.getAll();
            if (map != null && map.containsKey(key)) {
                return map.get(key);
            }
        }
        return defaultValue;
    }


    public static String getDistanceTime(long time1, long time2) {
        long hours = 0;
        long minutes = 1;
        try {
            SimpleDateFormat hm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = hm.format(time1);
            String end = hm.format(time2);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = df.parse(start);
            Date d2 = df.parse(end);
            long diff = d1.getTime() - d2.getTime();
            if (diff < 0) {
                diff *= -1;
            }
            hours = diff / (3600 * 1000);
            minutes = (diff % (3600 * 1000)) / (60 * 1000);
            if (hours == 0 && minutes <= 0) {
                minutes = 1;
            }
            return hours + " h " + minutes + " min";
        } catch (Exception e) {
            e.printStackTrace();
            return hours + " h " + minutes + " min";
        }
    }

    public static long getTotalMemory() {
        try {
            FileReader fr = new FileReader("/proc/meminfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split("\\s+");
            return Long.valueOf(array[1]) * 1024; // 原为kb, 转为b
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getAvailMemory(Context context) {// 获取android当前可用内存大小
        long availMemory = 0;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null && mi != null) {
                am.getMemoryInfo(mi);
                availMemory = mi.availMem;
            }
        } catch (Exception e) {
        }
//        return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        return availMemory;
    }

}
