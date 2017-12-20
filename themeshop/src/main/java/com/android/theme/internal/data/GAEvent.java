package com.android.theme.internal.data;

import com.android.client.AndroidSdk;
import com.android.common.SdkEnv;

/**
 * Created by song on 2017/3/16.
 */

public class GAEvent {
    public static final String CATEGORY = "商店/";
    public static final String ACTION_CLICK = "/点击";
    public static final String ACTION_DOWNLOAD = "/安装";
    public static final String ACTION_APPLY = "/应用";

    public static void track(String shopTag, String action, String theme) {
        AndroidSdk.track(CATEGORY + SdkEnv.env().versionName, shopTag + action, theme, 1);
    }
}
