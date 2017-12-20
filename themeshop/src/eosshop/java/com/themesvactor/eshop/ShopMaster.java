package com.themesvactor.eshop;

import android.content.Context;
import android.content.Intent;

import com.android.theme.internal.data.ThemeInfo;
import com.android.theme.internal.data.ThemeManager;
import com.android.theme.internal.data.Server;
import com.themesvactor.eshop.internal.ShopLocalActivity;
import com.themesvactor.eshop.internal.ShopActivity;
import com.android.theme.internal.data.ShopProperties;
import com.android.theme.internal.data.Theme;

/**
 * Created by song on 2017/3/3.
 */

public class ShopMaster {

    private static String[] tags;

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
        Intent i = new Intent(context, ShopLocalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        i.putExtra(ShopActivity.EXTRA_MAIN_TAG, ShopProperties.appTag());
//        i.putExtra(ShopActivity.EXTRA_CURRENT_TAG, appTag);
//        i.putExtra(ShopActivity.EXTRA_LOCAL_THEMES, localThemes);
//        context.startActivity(i);


        i.putExtra(ShopActivity.EXTRA_MAIN_TAG, ShopProperties.appTag());
        i.putExtra(ShopActivity.EXTRA_CURRENT_TAG, "Cleanmobi_Applock");
        if (tags == null) {
            tags = new String[]{
                    ShopProperties.appTag(),
                    ShopProperties.appTag()
            };
        }
        i.putExtra("tags", tags);
        i.putExtra("local", true);
        i.putExtra("local_themes", localThemes);
        context.startActivity(i);
    }

    public static void onCreate(Context context) {
        ThemeManager.onCreate(context, "theme", ShopProperties.appTag());
        Server.setUrl(ShopProperties.serverUrl());
    }

    /**
     * set theme base dir under assets folder
     * @param context
     * @param themeBaseDir the old default dir is assets/theme/
     */
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
