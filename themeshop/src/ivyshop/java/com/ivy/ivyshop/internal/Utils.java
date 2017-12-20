package com.ivy.ivyshop.internal;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.common.SdkEnv;

import org.byteam.superadapter.SuperAdapter;

/**
 * Created by song on 2017/3/13.
 */

public class Utils {
    public static void setupGridRecycler(Context context, RecyclerView v, int columns) {
        final int padding = SdkEnv.dp2px(7);
        final int bottomPadding = SdkEnv.dp2px(5);
        v.setLayoutManager(new GridLayoutManager(context, columns));
        v.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int headerCount;
                if (((SuperAdapter) parent.getAdapter()).hasHeaderView()) {
                    headerCount = 1;
                } else {
                    headerCount = 0;
                }

                final int childAdapterPosition = parent.getChildAdapterPosition(view) - headerCount;
                if (childAdapterPosition < 0) {
                    return;
                }

                final int i = childAdapterPosition % 3;
                if (i == 0) {
                    outRect.left = padding;
                    outRect.right = 0;
                    outRect.bottom = bottomPadding;
                } else if (i == 1) {
                    outRect.left = padding;
                    outRect.right = 0;
                    outRect.bottom = bottomPadding;
                } else {
                    outRect.left = padding;
                    outRect.right = padding;
                    outRect.bottom = bottomPadding;
                }
            }
        });
    }

    public static void setupGridRecycler(Context context, RecyclerView v, int columns, final ThemeAdapter adapter) {
        final int padding = SdkEnv.dp2px(7);
        final int bottomPadding = SdkEnv.dp2px(5);
        v.setLayoutManager(new GridLayoutManager(context, columns));
        v.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int headerCount;
                if (((SuperAdapter) parent.getAdapter()).hasHeaderView()) {
                    headerCount = 1;
                } else {
                    headerCount = 0;
                }

                final int childAdapterPosition = parent.getChildAdapterPosition(view) - headerCount;
                if (childAdapterPosition < 0) {
                    return;
                }

                final int pos = adapter.fixPosition(childAdapterPosition);
                if (pos == -1) {
                    return;
                }

                final int i = pos % 3;
                if (i == 0) {
                    outRect.left = padding;
                    outRect.right = 0;
                    outRect.bottom = bottomPadding;
                } else if (i == 1) {
                    outRect.left = padding;
                    outRect.right = 0;
                    outRect.bottom = bottomPadding;
                } else {
                    outRect.left = padding;
                    outRect.right = padding;
                    outRect.bottom = bottomPadding;
                }
            }
        });
    }
}
