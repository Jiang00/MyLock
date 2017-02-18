package com.security.manager.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.ShowDialogview;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huale on 2015/2/6.
 */
public class backgroundData {


    public static final String KEY_NEW_VERSION = "version";
    public static final String KEY_VERSION_CODE = "versionCode";
    public static final String KEY_VERSION_EDITION = "versionEdition";
    public static final String KEY_NEW_VERSION_DESC = "desc";
    public static final String KEY_SHOW_LOCK_ALL = "show_lockall";
    public static final String KEY_THEME_ICON = "icon_theme";


    private static final backgroundData data = new backgroundData();

    public static void onReceiveData(Context context, String extraJson) {
        Log.e("myurl","data---"+extraJson);

        try {
            JSONObject newData = new JSONObject(extraJson);
            data.onReceive(context, newData);

            Log.e("data",newData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject newObj;

    public void onReceive(Context context, JSONObject obj) throws JSONException {
        try {
            newObj = obj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("myurl","---------mynewurl");
        if (newObj.has(KEY_THEME_ICON)) {
            String dailyurl = newObj.getString(KEY_THEME_ICON);
            Log.e("myurl",dailyurl+"------1");
            SecurityMyPref.setDailyUrl(dailyurl);
        }
        try {
//            if (newObj.has(KEY_NEW_VERSION)) {
//                Log.e("mtt", "version" + "");
//                int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
//                JSONObject version = newObj.getJSONObject(KEY_NEW_VERSION);
//                int serverCode = version.getInt(KEY_VERSION_CODE);
//                if (serverCode > versionCode) {
//                    ShowDialogview.showNewVersion(context);
//                }
////                if (version.getInt(KEY_VERSION_CODE) > versionCode) {
////                    sp.edit().putBoolean(KEY_NEW_VERSION, true).putString(KEY_VERSION_EDITION, version.getString(KEY_VERSION_EDITION))
////                            .putString(KEY_NEW_VERSION_DESC, version.getString(KEY_NEW_VERSION_DESC)).apply();
//
//            }
            if (newObj.has(KEY_SHOW_LOCK_ALL)) {
                int lockvalue = newObj.getInt(KEY_SHOW_LOCK_ALL);
                if (lockvalue == 1) {
                    SecurityMyPref.setshowLockAll(true);
                } else {
                    SecurityMyPref.setshowLockAll(false);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
            Log.e("dowloadexception", e.getMessage());
        }


    }

}
