package com.security.manager.meta;

import android.content.SharedPreferences;

import com.core.common.SdkCache;
import com.security.manager.App;
import com.security.manager.lib.Utils;
import com.security.manager.lib.datatype.SInt;
import com.security.manager.lib.io.SafeDB;
import com.security.manager.page.SecurityMenu;

/**
 * Created by huale on 2014/11/28.
 */
public class SecurityMyPref {
    SharedPreferences.Editor editor;

    static SecurityMyPref securityMyPref = new SecurityMyPref();

    public static void upgrade() {
        SafeDB safeDB = SafeDB.defaultDB();
        int version = safeDB.getInt("version", 0);
        if (version == 0) {
            boolean useNormal = App.getSharedPreferences().getBoolean("nor", false);
            safeDB.putBool("nor", useNormal);
            String numpass = App.getSharedPreferences().getString("pp", "");
            safeDB.putString("pp", numpass);
            String patternPass = App.getSharedPreferences().getString("pg", "");
            safeDB.putString("pg", patternPass);
            safeDB.putInt("version", 1);
            safeDB.commit();
        }
    }

    public static SecurityMyPref begin() {
        if (securityMyPref.editor != null)
            throw new RuntimeException("SecurityMyPref.begin() is called but haven't called SecurityMyPref.commit()");
        securityMyPref.editor = App.getSharedPreferences().edit();
        return securityMyPref;
    }

    public void commit() {
        if (editor == null)
            throw new RuntimeException("SecurityMyPref.commit() is called but haven't called SecurityMyPref.Begin()");
        editor.commit();
        editor = null;
    }

    public SecurityMyPref useNormalPasswd(boolean normal) {
        SafeDB.defaultDB().putBool("nor", normal);
        SafeDB.defaultDB().commit();
//        editor.putBoolean("nor", normal);
        return this;
    }

    public static boolean isUseNormalPasswd() {
        return SafeDB.defaultDB().getBool("nor", false);
//        return App.getSharedPreferences().getBoolean("nor", false);
    }

    public SecurityMyPref setPasswd(String pass, boolean normal) {
        editor.putString(normal ? "pp" : "pg", pass);
        SafeDB.defaultDB().putString(normal ? "pp" : "pg", pass);
        SafeDB.defaultDB().commit();
        SdkCache.cache().cache("applock_passwd_", pass.getBytes(), false);

        return this;
    }

    public static boolean checkPasswd(String pass, boolean normal) {
//        String passed = SafeDB.defaultDB().getString(normal ? "pp" : "pg", "");
//        Utils.LOGER("passwd " + passed);

        String savePass = SdkCache.cache().readText("applock_passwd_", false, false);


        return savePass.equals(pass);
//        return App.getSharedPreferences().getString(normal ? "pp" : "pg", "").equals(pass);
    }

    public static String getPasswd() {

        if(SdkCache.cache().readText("applock_passwd_", false, false)!=null){
            return SdkCache.cache().readText("applock_passwd_", false, false);

        }else{
            return "";

        }
//        return App.getSharedPreferences().getString("pp", "");
    }

    public static String getPattern() {
        return  SdkCache.cache().readText("applock_passwd_", false, false);
    }

    public static boolean isPasswdSet(boolean normal) {
        String string = SafeDB.defaultDB().getString(normal ? "pp" : "pg", "");
//        String string = App.getSharedPreferences().getString(normal ? "pp" : "pg", null);
//        return string != null && string.length() > 0;

        String passwd = SdkCache.cache().readText("applock_passwd_", false, false);
        return  passwd != null && passwd.length()>0;
    }



    public SecurityMyPref remove(String tag) {
        editor.remove(tag);
        return this;
    }

    public SecurityMyPref putString(String tag, String value) {
        editor.putString(tag, value);
        return this;
    }

    public SecurityMyPref putInt(String tag, int value) {
        editor.putInt(tag, value);
        return this;
    }

    public SecurityMyPref putLong(String tag, long value) {
        editor.putLong(tag, value);
        return this;
    }

    public SecurityMyPref putFloat(String tag, float value) {
        editor.putFloat(tag, value);
        return this;
    }

    public static boolean hasMigComplete(String tag) {
        return App.getSharedPreferences().getBoolean(tag, false);
    }

    public static boolean isANewDay() {
        long yest = App.getSharedPreferences().getLong("yesterday", 0L);
        boolean newday = (System.currentTimeMillis() - yest) / 1000 >= 86400;
        if (newday) {
            App.getSharedPreferences().edit().putLong("yesterday", System.currentTimeMillis()).apply();
        }
        return newday;
    }

    public static void pressMenu(int menuId) {
        SharedPreferences sp = App.getSharedPreferences();
        SharedPreferences.Editor e = sp.edit();
        int redDotCount = 0;
        for (int i = 1; i < SecurityMenu.newidkeys.length; ++i) {
            if (i != menuId && !sp.getBoolean(SecurityMenu.newidkeys[i], false)) {
                ++redDotCount;
            }
        }
        if (redDotCount == 0) {
            e.putBoolean("reddot", false);
        }
        e.putBoolean(SecurityMenu.newidkeys[menuId], true).apply();
    }

    public static boolean isMenuPressed(int menuId) {
        return App.getSharedPreferences().getBoolean(SecurityMenu.newidkeys[menuId], false);
    }

    public static void clearMenuPressedState(int menuId) {
        App.getSharedPreferences().edit().putBoolean(SecurityMenu.newidkeys[menuId], false).putBoolean("reddot", true).apply();
    }

    public static void clearMenuPressedStateWithOptions(int menuId, int optId) {
        App.getSharedPreferences().edit().putBoolean(SecurityMenu.newidkeys[menuId], false).putBoolean(setting_reddot_key[optId], false).putBoolean("reddot", true).apply();
    }

    public static boolean hasReddot() {
        return App.getSharedPreferences().getBoolean("reddot", true);
    }

    public static void forceToggleReddot(boolean show) {
        App.getSharedPreferences().edit().putBoolean("reddot", show).apply();
    }

    public static void launchNow() {
        int cnt = App.getSharedPreferences().getInt("adv_se", 0);
        ++cnt;
        App.getSharedPreferences().edit().putInt("adv_se", cnt).apply();

        int count = App.getSharedPreferences().getInt("rate", 0);
        ++count;
        if (count < 23) {
            if (count == 12 && !hasOption(OPT_RATE_REDDOT)) {
                clearMenuPressedStateWithOptions(SecurityMenu.MENU_SETTING, OPT_RATE_REDDOT);
            } else if (count == 15 && !hasOption(OPT_ADVANCE_REDDOT)) {
                clearMenuPressedStateWithOptions(SecurityMenu.MENU_SETTING, OPT_ADVANCE_REDDOT);
            } else if (count == 22 && !hasOption(OPT_NOTIFICATION_REDDOT)) {
                clearMenuPressedStateWithOptions(SecurityMenu.MENU_SETTING, OPT_NOTIFICATION_REDDOT);
            }
            App.getSharedPreferences().edit().putInt("rate", count).apply();
        }
    }

    public static void setFakeCover(int fakeCover) {
        SafeDB.defaultDB().putInt("fake", fakeCover).commit();
    }

    public static int getFakeCover(int def) {
        return SafeDB.defaultDB().getInt("fake", def);
    }

    public static boolean tip4Rate() {
        return App.getSharedPreferences().getInt("rate", 0) >= 12 && !App.getSharedPreferences().contains("rate_showed");
    }

    public static boolean tip4Security() {
        return App.getSharedPreferences().getInt("adv_se", 0) >= 1 && !App.getSharedPreferences().contains("advance_se_showed");
    }

    public static void tip4SecurityComplete() {
        App.getSharedPreferences().edit().putBoolean("advance_se_showed", true).apply();
    }

    public static void tip4RateComplete() {
        App.getSharedPreferences().edit().putBoolean("rate_showed", true).apply();
    }

    public static final int OPT_RATE_REDDOT = 0;
    public static final int OPT_ADVANCE_REDDOT = 1;
    public static final int OPT_NOTIFICATION_REDDOT = 2;
    static final String[] setting_reddot_key = {
            "rate_red", "advance_red", "toggle_red"
    };

    public static boolean hasOption(int idx) {
        return App.getSharedPreferences().contains(setting_reddot_key[idx]);
    }

    public static void pressOption(int idx) {
        App.getSharedPreferences().edit().putBoolean(setting_reddot_key[idx], true).apply();
    }

    public static boolean isOptionPressed(int idx) {
        return App.getSharedPreferences().getBoolean(setting_reddot_key[idx], false);
    }

    public static boolean isAdvanceEnabled() {
        return App.getSharedPreferences().getBoolean("advanced", false);
    }

    public static void enableAdvance(boolean yes) {
        App.getSharedPreferences().edit().putBoolean("advanced", yes).apply();
    }

    public static boolean requireFetchAgain() {
        SharedPreferences sp = App.getSharedPreferences();
        long l = sp.getLong("__last_fetch", 0L);
        long current = System.currentTimeMillis();
        if (current - l > 10000) {
            sp.edit().putLong("__last_fetch", current).apply();
            long last = sp.getLong("help_fetch_time", 0L);
            return current - last > 3600000;
        } else {
            return false;
        }
    }

    public static void fetchAgainSuccess() {
        App.getSharedPreferences().edit().putLong("help_fetch_time", System.currentTimeMillis()).apply();
    }

    public static boolean isProtectStopped() {
        return App.getSharedPreferences().getBoolean("stop_service", false);
    }

    public static void stopProtect(boolean yes) {
        App.getSharedPreferences().edit().putBoolean("stop_service", yes).apply();
    }

    public static void selectLanguage(boolean english) {
        App.getSharedPreferences().edit().putBoolean(PREF_LANG, english).apply();
    }

    public static boolean isEnglish() {
        return App.getSharedPreferences().getBoolean(PREF_LANG, false);
    }

    public static final int PREF_BRIEF_EVERY_TIME = 0;
    public static final int PREF_BRIEF_5_MIN = 1;
    public static final int PREF_BRIEF_AFTER_SCREEN_OFF = 2;
    public static final int PREF_DEFAULT = PREF_BRIEF_EVERY_TIME;
    public static final String PREF_BRIEF_SLOT = "brief_slot";

    public static final String PREF_DEFAULT_LOCK = "Default";
    public static final String PREF_DEFAULT_LOCK_ALL = "All";
    public static final String PREF_DEFAULT_LOCK_EMPTY = "Empty";
    public static final String PREF_TMP_UNLOCK = "tmp-unlock";
    public static final String PREF_ACTIVE_PROFILE = "active_profile";
    public static final String PREF_ACTIVE_PROFILE_ID = "active_profile_id";
    public static final String PREF_PROFILES = "lock_profiles";

    public static final String LOCK_NEW = "lock_new";
    public static final boolean LOCK_DEFAULT = true;

    public static final String PREF_SHOW_WIDGET = "widget";

    public static final String PREF_LANG = "lang";

    public static boolean requireAsk() {
        long last = App.getSharedPreferences().getLong("last-ask-time", 0L);
        if (System.currentTimeMillis() - last > 86400000) {
            App.getSharedPreferences().edit().putLong("last-ask-time", System.currentTimeMillis()).apply();
            return true;
        }
        return false;
    }

    public static boolean hasNewVersion() {
//        boolean has = App.getSharedPreferences().getBoolean(ServerData.KEY_NEW_VERSION, false);
//        if (has) {
//            App.getSharedPreferences().edit().remove(ServerData.KEY_NEW_VERSION).apply();
//        }
        return false;
    }

    public static String getNewVersionDesc() {
        return null;
    }

    public static boolean hasIntruder() {
        return App.getSharedPreferences().getBoolean("intruder", false);
    }

    public static void setHasIntruder(boolean yes) {
        App.getSharedPreferences().edit().putBoolean("intruder", yes).apply();
    }

    public static boolean fetchIntruder() {
        return App.getSharedPreferences().getBoolean("fetch_intruder", true);
    }

    public static void setFetchIntruder(boolean yes) {
        App.getSharedPreferences().edit().putBoolean("fetch_intruder", yes).apply();
    }

    public static final SInt blockAdsTime = new SInt("tf_block_ads", 0);

    public static boolean isAdsBlocked() {
        int time = blockAdsTime.getValue();
        if ((System.currentTimeMillis() / 1000L - time) < 86400) {
            return true;
        } else {
            return false;
        }
    }

    public static void blockAds() {
        blockAdsTime.setValue((int) (System.currentTimeMillis() / 1000L));
    }


    public static void setintruderCamer(boolean yes) {
        App.getSharedPreferences().edit().putBoolean("intruder-came_", yes).apply();
    }

    public static boolean getintruderCamer() {
        return App.getSharedPreferences().getBoolean("intruder-came_", false);
    }


}
