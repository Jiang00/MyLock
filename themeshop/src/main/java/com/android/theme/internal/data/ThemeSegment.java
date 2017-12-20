package com.android.theme.internal.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/22.
 */

public class ThemeSegment {
    public static final String SPECIAL_BANNERS = "banners";
    public static final String SPECIAL_DESIGNERS = "designers";

    public ThemeSegment(String name) {
        this.tag = name;
        this.css = new Css();
    }

    public boolean isSpecial(String special) {
        return tag.equals(special);
    }

    public Css css;
    public ArrayList<ThemeContainer> themeContainers = new ArrayList<>();
    public String tag;

    public ThemeSegment(String tag, JSONObject obj) {
        this.tag = obj.optString("tag");

        final JSONObject cssObj = obj.optJSONObject("css");
        if (cssObj != null) {
            css = new Css(cssObj);
        }

        JSONArray banners = obj.optJSONArray("containers");
        final int length = banners.length();
        for (int i = 0; i < length; ++i) {
            final JSONObject container = banners.optJSONObject(i);
            if (container != null) {
                themeContainers.add(new ThemeContainer(tag, container));
            }
        }
    }
}
