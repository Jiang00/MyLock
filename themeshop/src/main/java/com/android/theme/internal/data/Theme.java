package com.android.theme.internal.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RawRes;

import org.json.JSONObject;

/**
 * Created by song on 2017/3/3.
 */

public class Theme implements Parcelable {
    public String icon;
    public String download;
    public boolean isLocal;
    public int iconRes;

    public Theme(JSONObject obj) {
        icon = obj.optString("icon");
        download = obj.optString("download");
    }

    protected Theme(Parcel in) {
        icon = in.readString();
        download = in.readString();
        iconRes = in.readInt();
        isLocal = in.readInt() == 1;
    }

    public boolean local() {
        return isLocal;
    }

    public static final Creator<Theme> CREATOR = new Creator<Theme>() {
        @Override
        public Theme createFromParcel(Parcel in) {
            return new Theme(in);
        }

        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };

    public Theme(String previewUrl, String packageName) {
        this.icon = previewUrl;
        this.download = packageName;
    }

    public Theme(@RawRes int iconRes, String packageName) {
        this.iconRes = iconRes;
        isLocal = true;
        this.download = packageName;
        this.icon = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icon);
        dest.writeString(download);
        dest.writeInt(iconRes);
        dest.writeInt(isLocal? 1 : 0);
    }

    @Override
    public int hashCode() {
        return icon.hashCode() | download.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return hashCode() == o.hashCode();
    }

    public String pkgName() {
        return download;
    }
}
