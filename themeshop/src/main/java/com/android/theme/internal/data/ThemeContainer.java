package com.android.theme.internal.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by song on 2017/3/3.
 */

public class ThemeContainer implements Parcelable {
    public String tag;
    public String icon;
    public String name;
    public String detail;
    public PageThemes pageThemes;

    public ThemeContainer() {

    }

    public ThemeContainer(String shopTag, JSONObject obj) {
        tag = obj.optString("tag");
        icon = obj.optString("icon");
        name = obj.optString("tag");
        detail = obj.optString("detail");
        final JSONArray themesArray = obj.optJSONArray("themes");
        pageThemes = new PageThemes(shopTag, tag);
        if (themesArray != null) {
            final int length = themesArray.length();
            for (int i = 0; i < length; ++i) {
                Theme theme = new Theme(themesArray.optJSONObject(i));
                pageThemes.themes.add(theme);
            }
        }
    }

    protected ThemeContainer(Parcel in) {
        tag = in.readString();
        icon = in.readString();
        name = in.readString();
        detail = in.readString();
        pageThemes = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<ThemeContainer> CREATOR = new Creator<ThemeContainer>() {
        @Override
        public ThemeContainer createFromParcel(Parcel in) {
            return new ThemeContainer(in);
        }

        @Override
        public ThemeContainer[] newArray(int size) {
            return new ThemeContainer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tag);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeString(detail);
        dest.writeParcelable(pageThemes, flags);
    }
}
