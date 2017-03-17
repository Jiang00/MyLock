package com.security.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import com.ivymobi.applock.free.R;
import com.security.manager.meta.SecurityFlowMenu;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;
import com.security.manager.page.MessageBox;

/**
 * Created by huale on 2015/2/2.
 */
public class SecurityBridgeImpl implements ISecurityBridge {
    boolean unlockSelf;
    Context context;
    Drawable icon;
    //有SYSTEM_ALERT_WINDOW的权限，小米系列默认禁止此权限
    boolean hasPermission;
    boolean unlockMe;
    String pkgName;
    CharSequence appName;
    Object[] addedToWindow = new Object[MENU_IDX_COUNT];

    static final SecurityBridgeImpl ins = new SecurityBridgeImpl();

    public static void reset(Context context, boolean unlockSelf, boolean hasPermission, String pkgName) {
        pkgName = pkgName == null ? context.getPackageName() : pkgName;
        ins.unlockSelf = unlockSelf;
        ins.context = context;
        ins.pkgName = pkgName;
        ins.icon = ins.getIcon(pkgName);
        SecurityTheBridge.bridge = ins;
        ins.hasPermission = hasPermission;
    }

    public Drawable getIcon(String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo pi = packageManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            appName = pi.applicationInfo.loadLabel(packageManager);
            return pi.applicationInfo.loadIcon(packageManager);
        } catch (Exception e) {
            appName = context.getResources().getString(R.string.app_name);
            return context.getResources().getDrawable(R.drawable.ic_launcher);
        }
    }

    @Override
    public CharSequence appName() {
        return appName;
    }

    @Override
    public Drawable icon() {
        return icon;
    }

    @Override
    public boolean check(String passwd, boolean normal) {
        if (SecurityMyPref.checkPasswd(passwd, normal)) {
            if (context instanceof Activity) {
                ((SecurityPatternActivity) context).unlockSuccess(unlockMe);
            } else {
                if (context == null) {
                    try {
                        ((SecurityPatternActivity) UnlockApp.context).unlockSuccess(unlockMe);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ((SecurityService) context).unlockSuccess(unlockMe);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Resources res() {
        return App.getContext().getResources();
    }


    @Override
    public boolean random() {
        return App.getSharedPreferences().getBoolean("random", false);
    }

    SecurityFlowMenu[] menus;
    public static final int MENU_IDX_ALL = -1;
    public static final int MENU_IDX_BRIEF = 0;
    public static final int MENU_IDX_UNLOCKME = 1;
    //    public static final int MENU_IDX_FORGET = 2;
    public static final int MENU_IDX_THEME = 2;
    public static final int MENU_IDX_TOGGLE = 3;
    public static final int MENU_IDX_COUNT = 4;

    public void detachFromWindow(int idx) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (idx == MENU_IDX_ALL) {
            for (int i = 0; i < MENU_IDX_COUNT; ++i) {
                detatchSingleView(i, wm);
            }
        } else {
            detatchSingleView(idx, wm);
        }
    }

    public void detatchSingleView(int idx, WindowManager wm) {
        try {
            if (addedToWindow[idx] instanceof View) {
                wm.removeViewImmediate((View) addedToWindow[idx]);
            } else {
                ((AlertDialog) addedToWindow[idx]).dismiss();
            }
        } catch (Exception ignore) {

        } finally {
            addedToWindow[idx] = null;
        }
    }
    @Override
    public String currentPkg() {
        return pkgName;
    }

    public static void clear() {
        ins.context = null;
        ins.icon = null;
    }
}
