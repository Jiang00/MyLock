package com.security.manager.meta;

import android.content.Context;

import org.simple.eventbus.EventBus;


public abstract class UseCase<T, F> {
    protected Context context;

    public UseCase(Context context) {
        this.context = context;
    }

    public abstract F execute(T ... params);

    public void stop() {
        context = null;

    }
}
