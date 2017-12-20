package com.android.theme.internal.adapter;

import android.content.Context;

import com.android.common.EventBus;
import com.android.common.SdkEnv;
import com.android.theme.internal.data.LoadMoreEvent;

import org.byteam.superadapter.IMulItemViewType;
import org.byteam.superadapter.SuperAdapter;

import java.util.List;

/**
 * Created by song on 2017/3/15.
 */

public abstract class LoadMoreAdapter<T> extends SuperAdapter<T> implements EventBus.EventListener {

    public LoadMoreAdapter(Context context, List<T> items, int layoutResId) {
        super(context, items, layoutResId);
        SdkEnv.registerEvent(this, LoadMoreEvent.LOAD_MORE);
    }

    public LoadMoreAdapter(Context context, List<T> items, IMulItemViewType<T> mulItemViewType) {
        super(context, items, mulItemViewType);
        SdkEnv.registerEvent(this, LoadMoreEvent.LOAD_MORE);
    }

    @Override
    public void onReceiveEvent(int i, Object... objects) {
        if (i == LoadMoreEvent.LOAD_MORE) {
            onReceiveMoreEvent((LoadMoreEvent) objects[0]);
        }
    }

    protected abstract void onReceiveMoreEvent(LoadMoreEvent event);
}
