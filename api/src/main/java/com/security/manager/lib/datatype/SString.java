package com.security.manager.lib.datatype;

/**
 * Created by song on 15/11/4.
 */
public class SString extends SDataType<String> {
    public SString(String key, String value) {
        super(key, value);
    }

    @Override
    protected void save() {
        sp.edit().putString(key, value).apply();
    }

    @Override
    protected void read() {
        value = sp.getString(key, value);
    }
}
