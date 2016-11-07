package com.security.manager.meta;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.RemoteException;

import com.security.manager.App;
import com.security.manager.db.ProfileDBHelper;
import com.security.manager.lib.async.LoadingTask;
import com.security.manager.lib.io.SafeDB;
import com.privacy.lock.aidl.IWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by SongHualin on 6/26/2015.
 */
public class MProfiles {
    private static List<ProfileDBHelper.ProfileEntry> profileList;
    private static String[] profiles;
    public static final String KEY_UPGRADED = "upgrade_profile";

    static SQLiteDatabase db;
    static boolean updateServerStatus = false;

    static final LoadingTask loadingTask = new LoadingTask() {
        @Override
        protected void doInBackground() {
            db = ProfileDBHelper.singleton(App.getContext()).getWritableDatabase();
            upgrade();
            profileList = ProfileDBHelper.ProfileEntry.getProfiles(db);
            updateProfiles();
        }

        private void upgrade(){
            SharedPreferences sp = App.getSharedPreferences();
            if (sp.contains(Pref.PREF_PROFILES) && !SafeDB.defaultDB().getBool(KEY_UPGRADED, false)){
                Set<String> profiles_key = sp.getStringSet(Pref.PREF_PROFILES, null);
                if (profiles_key != null){
                    String currentProfile = sp.getString(Pref.PREF_ACTIVE_PROFILE, Pref.PREF_DEFAULT_LOCK);
                    long currentProfileId = 1L;
                    List<String> apps = new ArrayList<>();
                    try {
                        currentProfileId = ProfileDBHelper.ProfileEntry.createProfile(db, currentProfile, apps);
                        SafeDB.defaultDB().putLong(Pref.PREF_ACTIVE_PROFILE_ID, currentProfileId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (String profile : profiles_key) {
                        String[] appPkgNames = sp.getString(profile, "").split(";");
                        Collections.addAll(apps, appPkgNames);
                        try {
                            if (profile.equals(currentProfile)){
                                ProfileDBHelper.ProfileEntry.updateProfile(db, currentProfileId, apps);
                                updateServerStatus = true;
                            } else {
                                ProfileDBHelper.ProfileEntry.createProfile(db, profile, apps);
                            }
                            sp.edit().remove(profile).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        apps.clear();
                    }
                    SafeDB.defaultDB().putBool(KEY_UPGRADED, true).commit();
                    sp.edit().remove(Pref.PREF_PROFILES).apply();
                }
            } else if (SafeDB.defaultDB().getLong(Pref.PREF_ACTIVE_PROFILE_ID, 0L) == 0L) {
                SafeDB.defaultDB().putLong(Pref.PREF_ACTIVE_PROFILE_ID, sp.getLong(Pref.PREF_ACTIVE_PROFILE_ID, 1L)).commit();
            }
        }
    };

    public static SQLiteDatabase getDB(){
        return db;
    }

    public static void updateProfiles() {
        profiles = new String[profileList.size()];
        for (int i = 0; i < profiles.length; ++i) {
            profiles[i] = profileList.get(i).name;
        }
    }

    public static boolean requireUpdateServerStatus() {
        return updateServerStatus;
    }

    public static void init() {
        loadingTask.start();
    }

    public static List<ProfileDBHelper.ProfileEntry> getEntries() {
        return profileList;
    }

    public static String[] getProfiles() {
        return profiles;
    }

    public static int getActiveProfileIdx(String activeProfile) {
        for (int i = 0; i < profiles.length; ++i) {
            if (profiles[i].equals(activeProfile)) return i;
        }
        return 0;
    }

    public static void waiting(Runnable waiting) {
        loadingTask.waiting(waiting);
    }

    public static void addProfile(ProfileDBHelper.ProfileEntry entry){
        profileList.add(entry);
        updateProfiles();
    }

    public static void removeProfile(ProfileDBHelper.ProfileEntry entry){
        profileList.remove(entry);
        updateProfiles();
    }

    public static void switchProfile(ProfileDBHelper.ProfileEntry entry, IWorker server){
        SafeDB.defaultDB().putLong(Pref.PREF_ACTIVE_PROFILE_ID, entry.id).putString(Pref.PREF_ACTIVE_PROFILE, entry.name).commit();
        if (server != null){
            try {
                server.notifyApplockUpdate();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isLoading() {
        return !loadingTask.isFinished();
    }
}
