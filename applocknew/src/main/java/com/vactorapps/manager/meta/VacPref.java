package com.vactorapps.manager.meta;

import android.content.SharedPreferences;

import com.android.common.SdkCache;
import com.vactorapps.manager.MyApp;
import com.vactorappsapi.manager.lib.datatype.SInt;
import com.vactorappsapi.manager.lib.io.SafeDB;
import com.vactorapps.manager.page.VacMenu;

/**
 * Created by mt on 2014/11/28.
 */
public class VacPref {
    SharedPreferences.Editor editor;

    public static VacPref securityMyPref = new VacPref();


    public static void upgrade() {
        SafeDB safeDB = SafeDB.defaultDB();
        int version = safeDB.getInt("version", 0);
        if (version == 0) {
            boolean useNormal = MyApp.getSharedPreferences().getBoolean("nor", false);
            safeDB.putBool("nor", useNormal);
            String numpass = MyApp.getSharedPreferences().getString("pp", "");
            safeDB.putString("pp", numpass);
            String patternPass = MyApp.getSharedPreferences().getString("pg", "");
            safeDB.putString("pg", patternPass);
            safeDB.putInt("version", 1);
            safeDB.commit();
        }
    }

    public static VacPref begin() {
        if (securityMyPref.editor != null)
            throw new RuntimeException("VacPref.begin() is called but haven't called VacPref.commit()");
        securityMyPref.editor = MyApp.getSharedPreferences().edit();
        return securityMyPref;
    }

    public void commit() {
        if (editor == null)
            throw new RuntimeException("VacPref.commit() is called but haven't called VacPref.Begin()");
        editor.commit();
        editor = null;
    }

    public VacPref useNormalPasswd(boolean normal) {
        SafeDB.defaultDB().putBool("nor", normal);
        SafeDB.defaultDB().commit();
//        editor.putBoolean("nor", normal);
        return this;
    }

    public static boolean isUseNormalPasswd() {
        return SafeDB.defaultDB().getBool("nor", false);
//        return MyApp.getSharedPreferences().getBoolean("nor", false);
    }

    public VacPref setPasswd(String pass, boolean normal) {
        editor.putString(normal ? "pp" : "pg", pass);
        SafeDB.defaultDB().putString(normal ? "pp" : "pg", pass);
        SafeDB.defaultDB().commit();
        SdkCache.cache().cache("applock_passwd_", pass.getBytes(), false);

        return this;
    }

    public static boolean checkPasswd(String pass, boolean normal) {
//        String passed = SafeDB.defaultDB().getString(normal ? "pp" : "pg", "");
//        Utils.LOGER("passwd " + passed);

        String savePass = getPasswd();
        return savePass.equals(pass);
//        return MyApp.getSharedPreferences().getString(normal ? "pp" : "pg", "").equals(pass);
    }

    public static String getPasswd() {

        String passWord = SdkCache.cache().readText("applock_passwd_", false, false);
        if (passWord != null) {
            return passWord;
        } else {
            return "";
        }
    }

    public static boolean isPasswdSet(boolean normal) {
//        String string = MyApp.getSharedPreferences().getString(normal ? "pp" : "pg", null);
//        return string != null && string.length() > 0;

        String passwd = SdkCache.cache().readText("applock_passwd_", false, false);
        return passwd != null && passwd.length() > 0;
    }


    public VacPref remove(String tag) {
        editor.remove(tag);
        return this;
    }

    public VacPref putString(String tag, String value) {
        editor.putString(tag, value);
        return this;
    }

    public VacPref putInt(String tag, int value) {
        editor.putInt(tag, value);
        return this;
    }

    public VacPref putLong(String tag, long value) {
        editor.putLong(tag, value);
        return this;
    }

    public VacPref putFloat(String tag, float value) {
        editor.putFloat(tag, value);
        return this;
    }

    public static boolean hasMigComplete(String tag) {
        return MyApp.getSharedPreferences().getBoolean(tag, false);
    }

    public static boolean isANewDay() {
        long yest = MyApp.getSharedPreferences().getLong("yesterday", 0L);
        boolean newday = (System.currentTimeMillis() - yest) / 1000 >= 86400;
        if (newday) {
            MyApp.getSharedPreferences().edit().putLong("yesterday", System.currentTimeMillis()).apply();
        }
        return newday;
    }

    public static void pressMenu(int menuId) {
        SharedPreferences sp = MyApp.getSharedPreferences();
        SharedPreferences.Editor e = sp.edit();
        int redDotCount = 0;
        for (int i = 1; i < VacMenu.newidkeys.length; ++i) {
            if (i != menuId && !sp.getBoolean(VacMenu.newidkeys[i], false)) {
                ++redDotCount;
            }
        }
        if (redDotCount == 0) {
            e.putBoolean("reddot", false);
        }
        e.putBoolean(VacMenu.newidkeys[menuId], true).apply();
    }

    public static boolean isMenuPressed(int menuId) {
        return MyApp.getSharedPreferences().getBoolean(VacMenu.newidkeys[menuId], false);
    }

    public static void clearMenuPressedState(int menuId) {
        MyApp.getSharedPreferences().edit().putBoolean(VacMenu.newidkeys[menuId], false).putBoolean("reddot", true).apply();
    }

    public static void clearMenuPressedStateWithOptions(int menuId, int optId) {
        MyApp.getSharedPreferences().edit().putBoolean(VacMenu.newidkeys[menuId], false).putBoolean(setting_reddot_key[optId], false).putBoolean("reddot", true).apply();
    }

    public static boolean hasReddot() {
        return MyApp.getSharedPreferences().getBoolean("reddot", true);
    }

    public static void forceToggleReddot(boolean show) {
        MyApp.getSharedPreferences().edit().putBoolean("reddot", show).apply();
    }

    public static void launchNow() {
        int cnt = MyApp.getSharedPreferences().getInt("adv_se", 0);
        ++cnt;
        MyApp.getSharedPreferences().edit().putInt("adv_se", cnt).apply();

        int count = MyApp.getSharedPreferences().getInt("rate", 0);
        ++count;
        if (count < 23) {
            if (count == 12 && !hasOption(OPT_RATE_REDDOT)) {
                clearMenuPressedStateWithOptions(VacMenu.MENU_SETTING, OPT_RATE_REDDOT);
            } else if (count == 15 && !hasOption(OPT_ADVANCE_REDDOT)) {
                clearMenuPressedStateWithOptions(VacMenu.MENU_SETTING, OPT_ADVANCE_REDDOT);
            } else if (count == 22 && !hasOption(OPT_NOTIFICATION_REDDOT)) {
                clearMenuPressedStateWithOptions(VacMenu.MENU_SETTING, OPT_NOTIFICATION_REDDOT);
            }
            MyApp.getSharedPreferences().edit().putInt("rate", count).apply();
        }
    }

    public static void setFakeCover(int fakeCover) {
        SafeDB.defaultDB().putInt("fake", fakeCover).commit();
    }

    public static int getFakeCover(int def) {
        return SafeDB.defaultDB().getInt("fake", def);
    }

    public static boolean tip4Rate() {
        return MyApp.getSharedPreferences().getInt("rate", 0) > 1 && !MyApp.getSharedPreferences().contains("rate_showed");
    }

    public static boolean tip4Security() {
        return MyApp.getSharedPreferences().getInt("adv_se", 0) >= 1 && !MyApp.getSharedPreferences().contains("advance_se_showed");
    }

    public static void tip4SecurityComplete() {
        MyApp.getSharedPreferences().edit().putBoolean("advance_se_showed", true).apply();
    }

    public static void tip4RateComplete() {
        MyApp.getSharedPreferences().edit().putBoolean("rate_showed", true).apply();
    }

    public static final int OPT_RATE_REDDOT = 0;
    public static final int OPT_ADVANCE_REDDOT = 1;
    public static final int OPT_NOTIFICATION_REDDOT = 2;
    static final String[] setting_reddot_key = {
            "rate_red", "advance_red", "toggle_red"
    };

    public static boolean hasOption(int idx) {
        return MyApp.getSharedPreferences().contains(setting_reddot_key[idx]);
    }

    public static void pressOption(int idx) {
        MyApp.getSharedPreferences().edit().putBoolean(setting_reddot_key[idx], true).apply();
    }

    public static boolean isOptionPressed(int idx) {
        return MyApp.getSharedPreferences().getBoolean(setting_reddot_key[idx], false);
    }

    public static boolean isAdvanceEnabled() {
        return MyApp.getSharedPreferences().getBoolean("advanced", false);
    }

    public static void enableAdvance(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("advanced", yes).apply();
    }

    public static boolean requireFetchAgain() {
        SharedPreferences sp = MyApp.getSharedPreferences();
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
        MyApp.getSharedPreferences().edit().putLong("help_fetch_time", System.currentTimeMillis()).apply();
    }

    public static boolean isProtectStopped() {
        return MyApp.getSharedPreferences().getBoolean("stop_service", false);
    }

    public static void stopProtect(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("stop_service", yes).apply();
    }

    public static void selectLanguage(boolean english) {
        MyApp.getSharedPreferences().edit().putBoolean(PREF_LANG, english).apply();
    }

    public static boolean isEnglish() {
        return MyApp.getSharedPreferences().getBoolean(PREF_LANG, false);
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
        long last = MyApp.getSharedPreferences().getLong("last-ask-time", 0L);
        if (System.currentTimeMillis() - last > 86400000) {
            MyApp.getSharedPreferences().edit().putLong("last-ask-time", System.currentTimeMillis()).apply();
            return true;
        }
        return false;
    }

    public static boolean hasNewVersion() {
//        boolean has = MyApp.getSharedPreferences().getBoolean(ServerData.KEY_NEW_VERSION, false);
//        if (has) {
//            MyApp.getSharedPreferences().edit().remove(ServerData.KEY_NEW_VERSION).apply();
//        }
        return false;
    }

    public static String getNewVersionDesc() {
        return null;
    }

    public static boolean hasIntruder() {
        return MyApp.getSharedPreferences().getBoolean("intruder", true);
    }

    public static void setHasIntruder(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("intruder", yes).apply();
    }

    public static boolean getFristred() {
        return MyApp.getSharedPreferences().getBoolean("fristred", true);
    }

    public static void setFristred(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("fristred", yes).apply();
    }

    public static boolean fetchIntruder() {
        return MyApp.getSharedPreferences().getBoolean("fetch_intruder", true);
    }

    public static void setFetchIntruder(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("fetch_intruder", yes).apply();
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

    public static void setVisitor(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("visitor_op_", yes).apply();
    }

    public static boolean getVisitor() {
        return MyApp.getSharedPreferences().getBoolean("visitor_op_", false);
    }


    public static void setNotification(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("notification_", yes).apply();
    }

    public static boolean getNotification() {
        return MyApp.getSharedPreferences().getBoolean("notification_", false);
    }


    public static void setintruderCamer(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("intruder-came_", yes).apply();
    }

    public static boolean getintruderCamer() {
        return MyApp.getSharedPreferences().getBoolean("intruder-came_", false);
    }


    public static void setFirstLeader(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("first_lea", yes).apply();
    }

    public static boolean getFirstLeader() {
        return MyApp.getSharedPreferences().getBoolean("first_lea", true);
    }


    public static void setOpenPermission(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("open_perssion_", yes).apply();
    }

    public static boolean getOpenPermission() {
        return MyApp.getSharedPreferences().getBoolean("open_perssion_", false);
    }


    public static void setFingerPrint(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("set_finger_", yes).apply();
    }

    public static boolean getFingerPrint() {
        return MyApp.getSharedPreferences().getBoolean("set_finger_", false);
    }


    public static void setshowLockAll(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("show_lock_al", yes).apply();
    }

    public static boolean getshowLockAll() {
        return MyApp.getSharedPreferences().getBoolean("show_lock_al", true);
    }


    public static void setDailyUrl(String url) {
        MyApp.getSharedPreferences().edit().putString("daily_string_url", url).apply();
    }

    public static String getDailyUrl() {
        return MyApp.getSharedPreferences().getString("daily_string_url", "");
    }


    public static void setThemeValue(int value) {
        MyApp.getSharedPreferences().edit().putInt("choose_theme_defaul_", value).apply();
    }

    public static int getThemeValue() {
        return MyApp.getSharedPreferences().getInt("choose_theme_defaul_", 1);
    }


    public static void setClickOK(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("show_lock_al", yes).apply();
    }

    public static boolean getClickOK() {
        return MyApp.getSharedPreferences().getBoolean("show_lock_al", false);
    }


    public static void setShowCross(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("show_cross_f_al", yes).apply();
    }

    public static boolean getShowCross() {
        return MyApp.getSharedPreferences().getBoolean("show_cross_f_al", true);
    }


    public static void setUseBatteryShowAD(int time) {
        MyApp.getSharedPreferences().edit().putInt("show_user_battery_show_ad", time).apply();
    }

    public static int getUseBatteryShowAD() {
        return MyApp.getSharedPreferences().getInt("show_user_battery_show_ad", 0);
    }


    public static void setFingerprintl(boolean yes) {
        MyApp.getSharedPreferences().edit().putBoolean("fingerprintflag", yes).apply();
    }

    public static boolean getFingerprintl() {
        return MyApp.getSharedPreferences().getBoolean("fingerprintflag", false);
    }


}
