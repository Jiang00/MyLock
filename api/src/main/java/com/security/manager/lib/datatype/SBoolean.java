package com.security.manager.lib.datatype;

/**
 * Created by song on 15/11/4.
 */
public class SBoolean extends SDataType<Boolean> {

    public SBoolean(String key, boolean value) {
        super(key, value);
    }

    @Override
    protected void save() {
        sp.edit().putBoolean(key, value).apply();
    }

    @Override
    protected void read() {
        value = sp.getBoolean(key, value);
    }

    public boolean yes() {
        return value;
    }

    public boolean no() {
        return !value;
    }
}
