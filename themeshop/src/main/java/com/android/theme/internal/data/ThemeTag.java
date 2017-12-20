package com.android.theme.internal.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/3.
 */

public class ThemeTag {
    public ArrayList<ThemeSegment> segments = new ArrayList<>();
    public String tag;
    public String name;

    public ThemeTag() {

    }

    public ThemeTag(JSONObject obj) {
        this.tag = obj.optString("tag");
        this.name = obj.optString("name");

        final JSONArray segmentsArray = obj.optJSONArray("segments");
        final int length = segmentsArray.length();
        for(int i=0; i<length; ++i) {
            final JSONObject segmentObj = segmentsArray.optJSONObject(i);
            if (segmentObj != null) {
                ThemeSegment segment = new ThemeSegment(tag, segmentObj);
                segments.add(segment);
            }
        }
    }
}
