package com.ivy.ivyshop.internal;

import android.content.Context;

import com.android.common.EventBus;
import com.android.common.SdkEnv;
import com.android.theme.internal.UIController;
import com.android.theme.internal.data.Local;
import com.android.theme.internal.data.Server;
import com.android.theme.internal.data.ShopLoadEvent;
import com.android.theme.internal.data.Theme;
import com.android.theme.internal.data.ThemeTag;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/3.
 */

public class ShopPresenter implements EventBus.EventListener {
    private UIController<ThemeTag> controller;
    private boolean local;

    public ShopPresenter(UIController<ThemeTag> controller) {
        this.controller = controller;
        SdkEnv.registerEvent(this, ShopLoadEvent.EVENT_LOAD_COMPLETE);
    }

    public void start() {
        controller.nothing();
    }

    public void stop() {
        SdkEnv.unregisterEvents(this, ShopLoadEvent.EVENT_LOAD_COMPLETE);
        controller = null;
    }

    public void loading() {
        controller.showLoading();
        Server.load();
    }

    public void loadLocal(Context context, String[] tags, Theme[] localThemes) {
        controller.showLoading();
        Local.loadInstalled(context, tags, localThemes);
        local = true;
    }

    @Override
    public void onReceiveEvent(int i, Object... objects) {
        if (i == ShopLoadEvent.EVENT_LOAD_COMPLETE) {
            controller.hideLoading();
            ShopLoadEvent e = (ShopLoadEvent) objects[0];
            if (e.local == local) {
                if (e.success()) {
                    final ArrayList<ThemeTag> tags = e.tags;
                    final int size = tags.size();
                    if (size == 0) {
                        controller.onReceiveNone();
                    } else if (size == 1) {
                        controller.onReceiveOne(tags.get(0));
                    } else if (size < 5) {
                        controller.onReceiveSome(tags);
                    } else {
                        controller.onReceiveMany(tags);
                    }
                } else {
                    controller.onError(e.errorCode);
                }
            }
        }
    }
}
