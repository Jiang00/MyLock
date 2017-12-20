package com.android.theme.internal.data;

/**
 * Created by song on 2017/3/29.
 */

public interface Pageable {
    boolean isEOF();

    void loadNextPage(Notifiable notifiable);

    int size();

    int loadedCount();

    interface Notifiable {
        void notifyDataSetChanged();

        void complete();
    }
}
