package com.security.manager.lib.datatype;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by song on 15/11/4.
 */
public abstract class SDataType<T> {
    protected T value;
    protected String key;
    protected static SharedPreferences sp;

    public static void init(Context context) {
        sp = context.getSharedPreferences("_data_", Context.MODE_PRIVATE);
    }

    public SDataType(String key, T value) {
        this.value = value;
        this.key = key;
        read();
    }

    public T getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public void setValue(T value) {
        this.value = value;
        save();
    }

    protected abstract void save();

    protected abstract void read();
}
