package com.security.manager.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IceStar on 16/9/7.
 */
public final class PreData {
    private PreData() {
    }
    // ****************** SharedPreference StartService ******************//
    private static int _putCount = 0;
    private static SharedPreferences db = null;
    private static SharedPreferences getDB(Context context) {
        if (db == null && context != null)
            db = context.getSharedPreferences("shared_files", 0);
        return db;
    }
    public static void saveDB(Context context) {
        SharedPreferences db = getDB(context);
        if (db != null) {
            saveDB(db.edit(), true);
        }
    }
    private static void saveDB(SharedPreferences.Editor e, boolean force) {
        if (e != null) {
            if (force) {
                e.commit();
                _putCount = 0;
            } else {
                if (_putCount++ >= 9) {
                    e.commit();
                    _putCount = 0;
                } else {
                    if (Build.VERSION.SDK_INT >= 9) {
                        e.apply();
                    } else {
                        e.commit();
                        _putCount = 0;
                    }
                }
            }
        }
    }
    public static <T> void putDB(Context context, String key, T value) {
        try {
            SharedPreferences db = getDB(context);
            if (db != null) {
                SharedPreferences.Editor e = db.edit();
                if (value instanceof String) {
                    e.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    e.putInt(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    e.putBoolean(key, (Boolean) value);
                } else if (value instanceof Long) {
                    e.putLong(key, (Long) value);
                } else if (value instanceof Float) {
                    e.putFloat(key, (Float) value);
                }
                saveDB(e, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean removeDB(Context context, String key) {
        try {
            SharedPreferences db = getDB(context);
            if (db != null) {
                SharedPreferences.Editor e = db.edit();
                e.remove(key);
                saveDB(e, false);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean hasDB(Context context, String key) {
        SharedPreferences db = getDB(context);
        if (db != null) {
            return db.contains(key);
        }
        return false;
    }
    public static long getDB(Context context, String key, int defValue) {
        SharedPreferences db = getDB(context);
        if (db != null) {
            if (db.contains(key)) {
                return db.getLong(key, defValue);
            } else {
                db.edit().putLong(key, defValue).commit();
            }
        }
        return defValue;
    }
    public static long getDB(Context context, String key, long defValue) {
        SharedPreferences db = getDB(context);
        if (db != null) {
            if (db.contains(key)) {
                return db.getLong(key, defValue);
            } else {
                db.edit().putLong(key, defValue).commit();
            }
        }
        return defValue;
    }
    public static String getDB(Context context, String key, String defValue) {
        SharedPreferences db = getDB(context);
        if (db != null) {
            if (db.contains(key)) {
                return db.getString(key, defValue);
            } else {
                db.edit().putString(key, defValue).commit();
            }
        }
        return defValue;
    }
    public static boolean getDB(Context context, String key, boolean defValue) {
        SharedPreferences db = getDB(context);
        if (db != null) {
            if (db.contains(key)) {
                return db.getBoolean(key, defValue);
            } else {
                db.edit().putBoolean(key, defValue).commit();
            }
        }
        return defValue;
    }
    // ******************* SharedPreference End *******************//
    //    whiteList
    public static void addName(Context context, String name, String key) {
        String nameList = getDB(context, key, "");
        if (nameList != null) {
            String[] tmp = nameList.split(",");
            ArrayList<String> lst = new ArrayList<>();
            for (int i = 0; i < tmp.length; i++) {
                lst.add(tmp[i]);
            }
            if (!lst.contains(name)) {
                lst.add(name);
                nameList = "";
                Iterator<String> it = lst.iterator();
                while (it.hasNext()) {
                    nameList += it.next() + ",";
                }
                putDB(context, key, nameList);
            }
        } else {
            putDB(context, key, name);
        }
    }
    public static ArrayList<String> getNameList(Context context, String key) {
        String nameList = getDB(context, key, "");
        String[] tmp = nameList.split(",");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < tmp.length; i++) {
            list.add(tmp[i]);
        }
        return list;
    }
    public static void removeName(Context context, String name, String key) {
        String nameList = getDB(context, key, "");
        if (nameList != null) {
            String[] tmp = nameList.split(",");
            ArrayList<String> lst = new ArrayList<>();
            for (int i = 0; i < tmp.length; i++) {
                lst.add(tmp[i]);
            }
            if (lst.contains(name)) {
                lst.remove(name);
                nameList = "";
                Iterator<String> it = lst.iterator();
                while (it.hasNext()) {
                    nameList += it.next() + ",";
                }
                putDB(context, key, nameList);
            }
        }
    }
}