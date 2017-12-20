package com.android.theme.internal.data;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/3.
 */

public class ShopLoadEvent {
    public static final int EVENT_LOAD_COMPLETE = "EVENT_LOAD_COMPLETE".hashCode();

    public ArrayList<ThemeTag> tags = new ArrayList<>();

    public ShopLoadEvent(ArrayList<ThemeTag> tags) {
        this.tags = tags;
        this.local = true;
    }

    public ShopLoadEvent(String data) {
        try {
            final JSONArray tagArray = new JSONArray(data);
            final int length = tagArray.length();
            for (int i = 0; i < length; ++i) {
                ThemeTag tag = new ThemeTag(tagArray.getJSONObject(i));
                tags.add(tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int errorCode = Integer.MAX_VALUE;
    public boolean local;

    public ShopLoadEvent(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean success() {
        return errorCode == Integer.MAX_VALUE;
    }
}
