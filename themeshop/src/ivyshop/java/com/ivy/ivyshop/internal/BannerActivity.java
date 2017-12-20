package com.ivy.ivyshop.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.common.SdkEnv;
import com.android.theme.internal.adapter.AutoLoadOnScrollListener;
import com.android.theme.internal.adapter.BitmapCache;
import com.android.theme.internal.adapter.LoadMoreAdapter;
import com.android.theme.internal.data.LoadMoreEvent;
import com.android.theme.internal.data.ShopProperties;
import com.android.theme.internal.data.Theme;
import com.android.theme.internal.data.ThemeContainer;
import com.android.themeshop.R;

import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

/**
 * Created by song on 2017/3/9.
 */

public class BannerActivity extends BaseActivity {
    private ThemeContainer themeContainer;
    public static final String EXTRA_THEME_CONTAINER = "themes";
    public static final String EXTRA_SHOP_TAG = "shop_tags";
    private String shopTag;

    public static void launch(Context context, String shopTag, ThemeContainer container) {
        Intent i = new Intent(context, BannerActivity.class);
        i.putExtra(EXTRA_THEME_CONTAINER, container);
        i.putExtra(EXTRA_SHOP_TAG, shopTag);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shop_activity_banner);

        setupToolbar();
        setupList();
    }

    private void setupList() {
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.shop_banner_list);
        Utils.setupGridRecycler(this, recyclerView, 3);
        final SuperAdapter<Theme> adapter = new LoadMoreAdapter<Theme>(this, themeContainer.pageThemes.themes, R.layout.shop_theme_item) {
            @Override
            protected void onReceiveMoreEvent(LoadMoreEvent event) {
                if (event.match(shopTag, themeContainer.tag)) {
                    if (event.end) {
                        removeFooterView();
                    }
                }
            }

            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, final Theme theme) {
                final ImageView iv = (ImageView) holder.itemView.findViewById(R.id.shop_icon);
                BitmapCache.setImageView(iv, theme.icon);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SdkEnv.openPlayStore(theme.download, "theme-shop-" + ShopProperties.appTag() + "-" + shopTag + "-banner-list");
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new AutoLoadOnScrollListener(recyclerView, themeContainer.pageThemes, adapter));
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.shop_load_more_footer, null, false));
        adapter.getFooterView().setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onReceiveIntent(Intent intent) {
        themeContainer = intent.getParcelableExtra(EXTRA_THEME_CONTAINER);
        shopTag = intent.getStringExtra(EXTRA_SHOP_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_THEME_CONTAINER, themeContainer);
        outState.putString(EXTRA_SHOP_TAG, shopTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        themeContainer = savedInstanceState.getParcelable(EXTRA_THEME_CONTAINER);
        shopTag = savedInstanceState.getString(EXTRA_SHOP_TAG);
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.shop_toolbar));

        getSupportActionBar().setTitle(themeContainer.name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
