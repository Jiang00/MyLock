package com.android.theme.internal.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import com.android.common.SdkCache;
import com.android.common.SdkEnv;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 2017/3/6.
 */

public class Local {
    public static final String CACHED_PREVIEW = "com.android.theme.cached.";
    public static final String DATA_FILE = "shop/local.json";

    public static String load() {
        final boolean hasData = SdkCache.cache().has(DATA_FILE, false);
        if (!hasData) {
            return SdkCache.cache().readText(DATA_FILE, true, false);
        }
        return SdkCache.cache().readText(DATA_FILE, false, false);
    }

    public static boolean save(InputStream is) {
        return SdkCache.cache().cacheInputStream(DATA_FILE, is, false);
    }

    public static void loadInstalled(final Context context, final String[] tags, final Theme[] localThemes) {
        Server.execute(new Runnable() {
            @Override
            public void run() {
                final PackageManager pm = context.getPackageManager();
                ArrayList<ThemeTag> themeTags = new ArrayList<>();
                final String ACTION_THEME = ShopProperties.themeAction();

                if (tags != null) {
                    final String mainTag = ShopProperties.appTag();
                    for (int i = 0; i < tags.length; i += 2) {
                        if (tags[i].equals("wallpaper")) {
                            themeTags.add(loadWallpaper(tags[i], tags[i+1]));
                        } else {
                            final String action = ACTION_THEME + tags[i];
                            final ThemeTag tag = new ThemeTag();
                            themeTags.add(tag);
                            tag.tag = tags[i];
                            tag.name = tags[i + 1];
                            final PageThemes pageThemes = new PageThemes(tag.tag, PageThemes.LOCAL);
                            ThemeSegment segment = new ThemeSegment(PageThemes.LOCAL);
                            ThemeContainer container = new ThemeContainer();
                            container.pageThemes = pageThemes;
                            segment.themeContainers.add(container);
                            tag.segments.add(segment);

                            if (tags[i].equals(mainTag) && localThemes != null) {
                                for (Theme localTheme : localThemes) {
                                    final String previewUrl = CACHED_PREVIEW + localTheme.pkgName();
                                    if (!SdkCache.cache().has(previewUrl, true)) {
                                        final Resources resources = SdkEnv.context().getResources();
                                        final boolean success = SdkCache.cache().cacheInputStream(previewUrl, resources.openRawResource(localTheme.iconRes), true);
                                        if (success) {
                                            Theme t = new Theme(SdkCache.cache().makeName(previewUrl, true), localTheme.pkgName());
                                            t.isLocal = true;
                                            pageThemes.themes.add(t);
                                        }
                                    } else {
                                        Theme t = new Theme(SdkCache.cache().makeName(previewUrl, true), localTheme.pkgName());
                                        t.isLocal = true;
                                        pageThemes.themes.add(t);
                                    }
                                }
                            }

                            final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(new Intent(action), 0);
                            if (resolveInfos != null) {
                                for (ResolveInfo resolveInfo : resolveInfos) {
                                    final String packageName = resolveInfo.activityInfo.packageName;
                                    final String previewUrl = CACHED_PREVIEW + packageName;
                                    if (!SdkCache.cache().has(previewUrl, true)) {
                                        try {
                                            final Context packageContext = context.createPackageContext(packageName, 0);
                                            final Resources resources = packageContext.getResources();
                                            final InputStream is = resources.openRawResource(resources.getIdentifier("theme_preview", "raw", packageName));
                                            final boolean success = SdkCache.cache().cacheInputStream(previewUrl, is, true);
                                            if (success) {
                                                Theme theme = new Theme(SdkCache.cache().makeName(previewUrl, true), packageName);
                                                pageThemes.themes.add(theme);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Theme theme = new Theme(SdkCache.cache().makeName(previewUrl, true), packageName);
                                        pageThemes.themes.add(theme);
                                    }
                                }
                            }
                        }
                    }
                }

                SdkEnv.sendEvent(ShopLoadEvent.EVENT_LOAD_COMPLETE, new ShopLoadEvent(themeTags));
            }
        });
    }

    private static ThemeTag loadWallpaper(String tag, String name) {
        final ThemeTag t = new ThemeTag();
        t.tag = tag;
        t.name = name;

        ThemeSegment segment = new ThemeSegment(PageThemes.LOCAL);
        ThemeContainer container = new ThemeContainer();
        segment.themeContainers.add(container);

        container.pageThemes = new PageThemes(tag, PageThemes.LOCAL);//这里使用 Local，是因为本地列表不需要去服务器取分页数据

        container.pageThemes.themes.addAll(WallpaperManager.getThemes());

        return t;
    }
}
