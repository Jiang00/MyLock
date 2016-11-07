package com.privacy.lock.meta;

import android.content.Context;

import com.privacy.lock.intf.IThemeBridge;
import com.privacy.lock.view.ForbiddenView;

/**
 * Created by huale on 2014/11/27.
 */
public class ThemeBridge {
    public static IThemeBridge bridge;
    public static Context themeContext;
    public static boolean needUpdate = false;
    public static boolean requestTheme = false;
}
