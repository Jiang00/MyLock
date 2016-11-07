package com.security.manager.myinterface;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.security.manager.meta.Daily;
import com.security.manager.meta.OverflowMenu;

/**
 * Created by huale on 2014/11/20.
 */
public interface IThemeBridge {
    CharSequence appName();

    Drawable icon();

    boolean check(String passwd, boolean normal);

    Resources res();

    int resId(String name, String type);

    boolean random();

    OverflowMenu[] menus();

    Daily daily();

    void visitDaily(boolean persistent);

    void toggle(boolean normal);

    boolean hasPattern();

    boolean hasPasswd();

    void back();

    void switchTheme();

    String currentPkg();
}
