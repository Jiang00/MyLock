package com.android.theme.internal.data;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/15.
 */

public class LoadMoreEvent {
    public static final int LOAD_MORE = "LOAD_MORE".hashCode();

    public boolean end;
    public ArrayList<Theme> loaded;
    private int id;
    public int page;
    public PageThemes themes;

    public LoadMoreEvent(int id, boolean end, int page, ArrayList<Theme> loaded, PageThemes themes) {
        this.id = id;
        this.end = end;
        this.loaded = loaded;
        this.page = page;
        this.themes = themes;
    }

    public boolean match(String shopTag, String containerTag) {
        return id == (shopTag.hashCode() | containerTag.hashCode());
    }
}
