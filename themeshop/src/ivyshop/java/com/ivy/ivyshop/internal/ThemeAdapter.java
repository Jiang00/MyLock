package com.ivy.ivyshop.internal;

import com.android.theme.internal.data.PageThemes;
import com.android.theme.internal.data.Pageable;
import com.android.theme.internal.data.Theme;
import com.android.theme.internal.data.ThemeContainer;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/27.
 */

public class ThemeAdapter implements Pageable{
    public class ThemeWrapper {
        public Theme theme;
        public String title;
        public boolean singleTitle;

        public ThemeWrapper(Theme theme) {
            this.theme = theme;
        }

        public ThemeWrapper(String title) {
            this.title = title;
        }

        public ThemeWrapper(boolean singleTitle) {
            this.singleTitle = singleTitle;
        }

        public boolean isTitle() {
            return title != null;
        }

        public boolean isSingleTitle() {
            return singleTitle;
        }
    }

    public ArrayList<ThemeWrapper> themeWrappers = new ArrayList<>();
    public ArrayList<Theme> originThemes = new ArrayList<>();
    private PageThemes lastPages;
    private int[] titles;

    public ThemeAdapter(ArrayList<ThemeContainer> containers) {
        titles = new int[containers.size() + 1];
        if (containers.size() == 1) {
            for (ThemeContainer container : containers) {
                ThemeWrapper title = new ThemeWrapper(true);
                titles[0] = themeWrappers.size();
                themeWrappers.add(title);
                final ArrayList<Theme> themes = container.pageThemes.themes;
                originThemes.addAll(themes);
                for (Theme theme : themes) {
                    ThemeWrapper t = new ThemeWrapper(theme);
                    themeWrappers.add(t);
                }
            }
        } else {
            int idx = 0;
            for (ThemeContainer container : containers) {
                ThemeWrapper title = new ThemeWrapper(container.name);
                titles[idx] = themeWrappers.size();
                ++idx;
                themeWrappers.add(title);
                final ArrayList<Theme> themes = container.pageThemes.themes;
                originThemes.addAll(themes);
                for (Theme theme : themes) {
                    ThemeWrapper t = new ThemeWrapper(theme);
                    themeWrappers.add(t);
                }
            }
        }
        titles[titles.length-1] = themeWrappers.size();

        if (containers.size() > 0) {
            lastPages = containers.get(containers.size() - 1).pageThemes;
        }
    }

    public int fixPosition(int pos) {
        for (int i = 0; i < titles.length; i++) {
            if (pos == titles[i]) {
                return -1;
            } else if (pos < titles[i]) {
                return pos + i * 2;
            } else {
                continue;
            }
        }
        return -1;
    }

    public void loadNextPage(final Notifiable notifiable) {
        lastPages.loadNextPage(new Notifiable() {
            @Override
            public void notifyDataSetChanged() {
                final int size = lastPages.themes.size();
                ArrayList<ThemeWrapper> wrappers = new ArrayList<>();
                for (int i = size - lastPages.loadedCount; i < size; ++i) {
                    ThemeWrapper t = new ThemeWrapper(lastPages.themes.get(i));
                    wrappers.add(t);
                }
                themeWrappers.addAll(wrappers);
                titles[titles.length-1] = themeWrappers.size();
                notifiable.notifyDataSetChanged();
            }

            @Override
            public void complete() {
                notifiable.complete();
            }
        });
    }

    public boolean isEOF() {
        return lastPages.isEOF();
    }

    public int size() {
        return themeWrappers.size();
    }

    public int loadedCount() {
        return lastPages.loadedCount;
    }
}
