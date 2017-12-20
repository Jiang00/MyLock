package com.android.theme.internal.data;

import com.android.common.SdkEnv;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by song on 2017/3/17.
 */

public class ShopProperties {
    public String serverUrl;
    public String actionPrefix;
    public String appTag;

    private static final ShopProperties PROPERTIES = new ShopProperties();

    public ShopProperties() throws RuntimeException {
        Properties p = new Properties();
        try {
            p.load(SdkEnv.context().getAssets().open("shop.properties"));
            serverUrl = p.getProperty("theme_url");
            actionPrefix = p.getProperty("theme_action");
            appTag = p.getProperty("app_tag");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serverUrl() {
        return PROPERTIES.serverUrl;
    }

    public static String themeAction() {
        return PROPERTIES.actionPrefix;
    }

    public static String appTag() {
        return PROPERTIES.appTag;
    }
}
