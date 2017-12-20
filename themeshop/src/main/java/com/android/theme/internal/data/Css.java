package com.android.theme.internal.data;

import org.json.JSONObject;

/**
 * Created by song on 2017/3/22.
 */

public class Css {
    public boolean largeHeight;

    public Css(JSONObject obj) {
        largeHeight = obj.optString("height", "small").equals("large");
    }

    public Css() {

    }
}
