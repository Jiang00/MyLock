package com.android.theme.internal.data;

import com.android.common.SdkEnv;

import java.util.HashMap;

/**
 * Created by song on 2017/3/14.
 */

public class ThemeSelector {
    private HashMap<String, Boolean> selectedThemes = new HashMap<>();
    private HashMap<String, String> themeTagMap = new HashMap<>();
    private static final ThemeSelector SELECTOR = new ThemeSelector();
    private String[] tags;

    public static void setTags(String[] tags) {
        SELECTOR.tags = tags;
    }

    public static boolean isSelected(Theme theme) {
        final String key = theme.pkgName();
        return SELECTOR.selectedThemes.containsKey(key)
                || key.equals(ThemeManager.currentTheme().getName());
    }

    public static void refresh() {
        SELECTOR.refresh_();
    }

    private void refresh_() {
        selectedThemes.clear();
        for (final String tag : tags) {
            final String pkg = ThemeManager.getCurrentThemeSelector(tag);
            try {
                SdkEnv.context().getPackageManager().getPackageInfo(pkg, 0);
                selectedThemes.put(pkg, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
