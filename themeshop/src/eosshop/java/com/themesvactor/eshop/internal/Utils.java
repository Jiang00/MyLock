package com.themesvactor.eshop.internal;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.common.SdkEnv;

import org.byteam.superadapter.SuperAdapter;

/**
 * Created by song on 2017/3/13.
 */

public class Utils {
    public static void setupGridRecycler(Context context, RecyclerView v, int columns) {
        final int padding = SdkEnv.dp2px(4);
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
                    outRect.left = padding / 2;
                    outRect.right = padding / 2;
                    outRect.bottom = padding * 2;
                } else if (i == 1) {
                    outRect.left = padding / 2;
                    outRect.right = padding / 2;
                    outRect.bottom = padding * 2;
                } else {
                    outRect.left = padding / 2;
                    outRect.right = padding / 2;
                    outRect.bottom = padding * 2;
                }
            }
        });
    }
}
