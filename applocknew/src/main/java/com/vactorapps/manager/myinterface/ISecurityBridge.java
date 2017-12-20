package com.vactorapps.manager.myinterface;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.vactorapps.manager.page.OverflowMenu;

/**
 * Created by huale on 2014/11/20.
 */
public interface ISecurityBridge {
    CharSequence appName();

    Drawable icon();

    boolean check(String passwd, boolean normal);

    Resources res();


    boolean random();

    String currentPkg();

    OverflowMenu[] menus();
}
