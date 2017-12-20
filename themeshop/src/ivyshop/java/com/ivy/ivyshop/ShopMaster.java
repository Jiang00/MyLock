package com.ivy.ivyshop;

import android.content.Context;
import android.content.Intent;

import com.android.theme.internal.data.ThemeInfo;
import com.android.theme.internal.data.ThemeManager;
import com.android.theme.internal.data.Server;
import com.android.theme.internal.data.ShopProperties;
import com.android.theme.internal.data.Theme;
import com.ivy.ivyshop.internal.ShopActivity;

/**
 * Created by song on 2017/3/3.
 */

public class ShopMaster {

    /**
     * start the theme shop
     * @param context
     * @param localThemes
     */
    public static void launch(Context context, Theme... localThemes) {
        Intent i = new Intent(context, ShopActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.putExtra(ShopActivity.EXTRA_MAIN_TAG, ShopProperties.appTag());
        i.putExtra(ShopActivity.EXTRA_CURRENT_TAG, ShopProperties.appTag());
        i.putExtra(ShopActivity.EXTRA_LOCAL_THEMES, localThemes);
        context.startActivity(i);
    }

    /**
     * launch theme shop and let the #appTag ordered in first position
     * @param context
     * @param appTag first page's tag
     * @param localThemes
     */
    public static void launch(Context context, String appTag, Theme... localThemes) {
        Intent i = new Intent(context, ShopActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.putExtra(ShopActivity.EXTRA_MAIN_TAG, ShopProperties.appTag());
        i.putExtra(ShopActivity.EXTRA_CURRENT_TAG, appTag);
        i.putExtra(ShopActivity.EXTRA_LOCAL_THEMES, localThemes);
        context.startActivity(i);
    }

    public static void onCreate(Context context) {
        ThemeManager.onCreate(context, "theme", ShopProperties.appTag());
        Server.setUrl(ShopProperties.serverUrl());
    }

    public static void onCreate(Context context, String themeBaseDir) {
        ThemeManager.onCreate(context, themeBaseDir, ShopProperties.appTag());
        Server.setUrl(ShopProperties.serverUrl());
    }

    public static boolean applyTheme(Context context, String themePackageName, boolean local) {
        return ThemeManager.applyTheme(context, themePackageName, local);
    }

    public static ThemeInfo currentTheme() {
        return ThemeManager.currentTheme();
    }
}
