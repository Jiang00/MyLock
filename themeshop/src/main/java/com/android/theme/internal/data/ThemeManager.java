package com.android.theme.internal.data;

import android.content.Context;

import com.android.common.SdkCache;
import com.android.common.SdkEnv;

/**
 * Created by song on 2016/10/17.
 */

public class ThemeManager {
    private static final String CURRENT_THEME_KEY = "current_theme";
    private static final String CURRENT_THEME_LOCAL_KEY = "current_theme_local";
    private static final String THEME_SELECTOR = "theme_selector/";

    private static ThemeInfo currentThemeInfo;
    private static String tag;
    private static String baseDir;

    public static void onCreate(Context context, String baseDir, String appTag) {
        ThemeManager.baseDir = baseDir;
        ThemeManager.tag = appTag;
        String currentTheme = SdkCache.cache().readText(CURRENT_THEME_KEY, false, false);
        boolean isLocal = false;
        try {
            isLocal = Boolean.parseBoolean(SdkCache.cache().readText(CURRENT_THEME_LOCAL_KEY, false, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        useTheme(context, currentTheme, isLocal);
    }

    public static boolean useTheme(Context context, String themePackageName, boolean local) {
        try {
            context = context.getApplicationContext();
            if (context.getPackageName().equals(themePackageName)) {
                local = false;
            }
            ThemeInfo i = save(context, themePackageName, local);
            if (currentThemeInfo != null) {
                currentThemeInfo.destroy();
            }
            currentThemeInfo = i;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (currentThemeInfo == null) {
                try {
                    themePackageName = context.getPackageName();
                    currentThemeInfo = save(context, themePackageName, false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        }
    }

    private static ThemeInfo save(Context context, String themePackageName, boolean local) throws Exception {
        ThemeInfo i = new ThemeInfo(context, baseDir, themePackageName, local);
        SdkCache.cache().cache(CURRENT_THEME_KEY, themePackageName.getBytes(), false);
        SdkCache.cache().cache(CURRENT_THEME_LOCAL_KEY, local ? "true".getBytes() : "false".getBytes(), false);
        SdkCache.cache().cache(THEME_SELECTOR + tag, themePackageName.getBytes(), true);
        return i;
    }

    public static String getCurrentThemeSelector(String tag) {
        return SdkCache.cache().readText(THEME_SELECTOR + tag, false, true);
    }

    public static boolean applyTheme(Context context, String themePackageName, boolean local) {
        final boolean b = useTheme(context, themePackageName, local);
        sendThemeChangedEvent();
        return b;
    }

    public static void sendThemeChangedEvent() {
        SdkEnv.sendEvent(ThemeChangedEvent.EVENT_THEME_CHANGED, new ThemeChangedEvent(currentThemeInfo));
    }

    public static ThemeInfo currentTheme() {
        return currentThemeInfo;
    }

}
