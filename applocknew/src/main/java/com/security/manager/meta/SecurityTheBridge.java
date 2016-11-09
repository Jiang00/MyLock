package com.security.manager.meta;

import android.content.Context;

import com.security.manager.myinterface.ISecurityBridge;

/**
 * Created by huale on 2014/11/27.
 */
public class SecurityTheBridge {
    public static ISecurityBridge bridge;
    public static Context themeContext;
    public static boolean needUpdate = false;
    public static boolean requestTheme = false;
}
