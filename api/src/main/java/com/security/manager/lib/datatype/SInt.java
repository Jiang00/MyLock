package com.security.manager.lib.datatype;

/**
 * Created by song on 15/11/4.
 */
public class SInt extends SDataType<Integer> {
    public SInt(String key, int value) {
        super(key, value);
    }

    @Override
    protected void save() {
        sp.edit().putInt(key, value).apply();
    }

    @Override
    protected void read() {
        value = sp.getInt(key, value);
    }
}
