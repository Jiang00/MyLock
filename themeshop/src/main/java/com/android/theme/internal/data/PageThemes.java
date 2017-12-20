package com.android.theme.internal.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.android.common.EventBus;
import com.android.common.SdkCache;
import com.android.common.SdkEnv;
import com.android.network.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by song on 2017/3/13.
 */

public class PageThemes implements Parcelable, EventBus.EventListener, Pageable {
    protected PageThemes(Parcel in) {
        shopTag = in.readString();
        containerTag = in.readString();
        page = in.readInt();
        end = in.readInt() != 0;
        in.readList(themes, getClass().getClassLoader());
    }

    public static final Creator<PageThemes> CREATOR = new Creator<PageThemes>() {
        @Override
        public PageThemes createFromParcel(Parcel in) {
            return new PageThemes(in);
        }

        @Override
        public PageThemes[] newArray(int size) {
            return new PageThemes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shopTag);
        dest.writeString(containerTag);
        dest.writeInt(page);
        dest.writeInt(end ? 1 : 0);
        dest.writeList(themes);
    }

    @Override
    public void onReceiveEvent(int i, Object... objects) {
        if (i == LoadMoreEvent.LOAD_MORE) {
            LoadMoreEvent e = (LoadMoreEvent) objects[0];
            if (e.match(shopTag, containerTag)) {
                if (e.themes != this) {
                    this.page = e.page;
                    this.end = e.end;
                    this.themes.addAll(e.loaded);
                }
            }
        }
    }

    private String shopTag;
    private String containerTag;
    private int page;
    private boolean end;
    public int loadedCount;
    public static final String LOCAL = "local";

    public ArrayList<Theme> themes = new ArrayList<>();

    public PageThemes(String shopTag, String containerTag) {
        this.shopTag = shopTag;
        this.containerTag = containerTag;
        ++page;
        if (containerTag.equals(LOCAL)) {
            end = true;
        } else {
            SdkEnv.registerEvent(this, LoadMoreEvent.LOAD_MORE);
        }
    }

    public void load(JSONArray data) {
        final ArrayList<Theme> loaded = loadThemes(data);
        themes.addAll(loaded);
    }

    @NonNull
    private ArrayList<Theme> loadThemes(JSONArray data) {
        final int length = data.length();
        final ArrayList<Theme> loaded = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            try {
                JSONObject obj = data.getJSONObject(i);
                Theme t = new Theme(obj);
                loaded.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return loaded;
    }

    private String url(String base) {
        return base + "/pages/" + shopTag + "/" + containerTag + "_" + page + ".json";
    }

    public boolean isEOF() {
        return end;
    }

    public void loadNextPage(final Notifiable notifiable) {
        final String url = url(Server.baseUrl());
        if (!Server.requireReload() && SdkCache.cache().has(url, false)) {
            try {
                final ArrayList<Theme> loaded = loadThemes(new JSONArray(SdkCache.cache().readText(url, false, false)));
                ++page;
                themes.addAll(loaded);
                loadedCount = loaded.size();
                notifiable.notifyDataSetChanged();
                LoadMoreEvent e = new LoadMoreEvent(shopTag.hashCode() | containerTag.hashCode(), end, page, loaded, PageThemes.this);
                SdkEnv.sendEvent(LoadMoreEvent.LOAD_MORE, e);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Server.execute(new Request(url) {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection) throws Exception {
                SdkCache.cache().cacheInputStream(url, httpURLConnection.getInputStream(), false);
                String s = SdkCache.cache().readText(url, false, false);
                if (s == null || s.equals("null") || s.trim().length() < 10) {
                    end = true;
                    notifiable.complete();
                    LoadMoreEvent e = new LoadMoreEvent(shopTag.hashCode() | containerTag.hashCode(), end, page, null, PageThemes.this);
                    SdkEnv.sendEvent(LoadMoreEvent.LOAD_MORE, e);
                } else {
                    JSONArray data = new JSONArray(s);
                    final int length = data.length();
                    final ArrayList<Theme> loaded = new ArrayList<>();
                    for (int i = 0; i < length; ++i) {
                        JSONObject obj = data.getJSONObject(i);
                        Theme t = new Theme(obj);
                        loaded.add(t);
                    }
                    ++page;
                    SdkEnv.post(new Runnable() {
                        @Override
                        public void run() {
                            themes.addAll(loaded);
                            loadedCount = loaded.size();
                            notifiable.notifyDataSetChanged();
                            LoadMoreEvent e = new LoadMoreEvent(shopTag.hashCode() | containerTag.hashCode(), end, page, loaded, PageThemes.this);
                            SdkEnv.sendEvent(LoadMoreEvent.LOAD_MORE, e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(int code, String exception) {
                super.onFailure(code, exception);
                end = true;
                LoadMoreEvent e = new LoadMoreEvent(shopTag.hashCode() | containerTag.hashCode(), end, page, null, PageThemes.this);
                SdkEnv.sendEvent(LoadMoreEvent.LOAD_MORE, e);
                notifiable.complete();
            }
        });
    }

    @Override
    public int size() {
        return themes.size();
    }

    @Override
    public int loadedCount() {
        return loadedCount;
    }
}
