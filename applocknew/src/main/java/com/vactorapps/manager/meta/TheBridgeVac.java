package com.vactorapps.manager.meta;

import android.content.Context;

import com.vactorapps.manager.myinterface.ISecurityBridge;

/**
 * Created by huale on 2014/11/27.
 */
public class TheBridgeVac {
    public static ISecurityBridge bridge;
    public static Context themeContext;
    public static boolean needUpdate = false;
    public static boolean requestTheme = false;
}
