package com.android.theme.internal.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.theme.internal.data.PageThemes;
import com.android.theme.internal.data.Pageable;
import com.android.theme.internal.data.Theme;

import org.byteam.superadapter.SuperAdapter;

/**
 * Created by xian on 2016/10/22.
 */

public class AutoLoadOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean loading = false;

    private LinearLayoutManager mLinearLayoutManager;
    private Pageable themes;
    private RecyclerView recyclerView;
    private SuperAdapter adapter;

    public AutoLoadOnScrollListener(RecyclerView view, Pageable themes, SuperAdapter adapter) {
        this.mLinearLayoutManager = (LinearLayoutManager) view.getLayoutManager();
        this.themes = themes;
        this.recyclerView = view;
        this.adapter = adapter;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = mLinearLayoutManager.getItemCount();
        int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

        if (!loading && (lastVisibleItem > totalItemCount - 3) && dy > 0) {
            setLoading(true);
            onLoadMore();
        }
    }

    private void onLoadMore() {
        if (themes.isEOF()) {
            setLoading(false);
        } else {
            themes.loadNextPage(new PageThemes.Notifiable() {
                @Override
                public void notifyDataSetChanged() {
                    setLoading(false);
                    recyclerView.getAdapter().notifyItemRangeInserted(themes.size() - themes.loadedCount() + 1, themes.loadedCount());
                }

                @Override
                public void complete() {
                    setLoading(false);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.removeFooterView();
                        }
                    });
                }
            });
        }
    }

    private void setLoading(final boolean loading) {
        this.loading = loading;
        if (adapter.hasFooterView()) {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (adapter.hasFooterView()) {
                        adapter.getFooterView().setVisibility(loading ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
    }
}
