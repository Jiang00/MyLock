package com.android.theme.internal.data;

/**
 * Created by song on 2016/10/18.
 */

public class ThemeChangedEvent {
    public static final int EVENT_THEME_CHANGED = "theme_changed_event".hashCode();

    public ThemeInfo themeInfo;

    public ThemeChangedEvent(ThemeInfo themeInfo) {
        this.themeInfo = themeInfo;
    }
}
