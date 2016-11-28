package com.security.manager.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.dev.ICacheHandler;
import com.security.manager.App;
import com.security.manager.page.ShowDialogview;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huale on 2015/2/6.
 */
public class backgroundData implements ICacheHandler {


    public static final String KEY_NEW_VERSION = "version";
    public static final String KEY_VERSION_CODE = "versionCode";
    public static final String KEY_VERSION_EDITION = "versionEdition";
    public static final String KEY_NEW_VERSION_DESC = "desc";


    private static final backgroundData data = new backgroundData();

    public static void onReceiveData(Context context, String extraJson) {
        try {
            JSONObject newData = new JSONObject(extraJson);
            data.onReceive(context, newData);
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
        try {
            if (newObj.has(KEY_NEW_VERSION)) {
                Log.e("mtt", "version" + "");
                int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                JSONObject version = newObj.getJSONObject(KEY_NEW_VERSION);
                int serverCode = version.getInt(KEY_VERSION_CODE);
                if (serverCode > versionCode) {
                    ShowDialogview.showNewVersion(context);
                }

//                if (version.getInt(KEY_VERSION_CODE) > versionCode) {
//                    sp.edit().putBoolean(KEY_NEW_VERSION, true).putString(KEY_VERSION_EDITION, version.getString(KEY_VERSION_EDITION))
//                            .putString(KEY_NEW_VERSION_DESC, version.getString(KEY_NEW_VERSION_DESC)).apply();
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", e.getMessage());
        }
    }

    @Override
    public void addCache(String s, String s1) {

    }
}