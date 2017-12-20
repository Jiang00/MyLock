package com.android.theme.internal.data;

import com.android.common.SdkCache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by song on 2017/3/13.
 */

public class WallpaperManager {
    private static final WallpaperManager MANAGER = new WallpaperManager();
    private ArrayList<Theme> localThemes = new ArrayList<>();
    private HashMap<Theme, Boolean> map = new HashMap<>();
    private static final String WALLPAPER_INDEX_FILE = "shop/wallpaper.json";

    public WallpaperManager() {
        load_();
    }

    private void load_() {
        final String s = SdkCache.cache().readText(WALLPAPER_INDEX_FILE, false, false);
        if (s != null) {
            final String[] split = s.split("\n");
            for (int i = 0; i < split.length; i += 2) {
                try {
                    Theme t = new Theme(split[i], split[i + 1]);
                    localThemes.add(t);
                    map.put(t, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void save_() {
        StringBuilder sb = new StringBuilder();
        for (Theme theme : localThemes) {
            sb.append(theme.icon).append("\n").append(theme.download).append("\n");
        }
        SdkCache.cache().cache(WALLPAPER_INDEX_FILE, sb.toString().getBytes(), false);
    }

    public static void saveWallpaper(Theme theme) {
        MANAGER.saveWallpaper_(theme);
    }

    private void saveWallpaper_(Theme theme) {
        if (!map.containsKey(theme)) {
            map.put(theme, true);
            localThemes.add(theme);
            save_();
        }
    }

    public static void removeWallpaper(Theme theme) {
        MANAGER.removeWallpaper_(theme);
    }

    private void removeWallpaper_(Theme theme) {
        if (map.containsKey(theme)) {
            map.remove(theme);
            localThemes.remove(theme);
            final String s = SdkCache.cache().makeName(theme.download, true);
            new File(s).delete();
            save_();
        }
    }

    public static ArrayList<Theme> getThemes() {
        return MANAGER.localThemes;
    }
}
