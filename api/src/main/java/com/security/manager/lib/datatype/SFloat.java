package com.security.manager.lib.datatype;

/**
 * Created by song on 15/11/4.
 */
public class SFloat extends SDataType<Float> {
    public SFloat(String key, Float value) {
        super(key, value);
    }

    @Override
    protected void save() {
        sp.edit().putFloat(key, value).apply();
    }

    @Override
    protected void read() {
        value = sp.getFloat(key, value);
    }
}
