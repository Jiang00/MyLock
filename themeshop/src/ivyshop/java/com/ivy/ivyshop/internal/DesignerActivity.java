package com.ivy.ivyshop.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

public class DesignerActivity extends BaseActivity {
    private ThemeContainer themeContainer;
    public static final String EXTRA_THEME_CONTAINER = "themes";
    public static final String EXTRA_SHOP_TAG = "shop_tags";
    private String shopTag;

    public static void launch(Context context, String shopTag, ThemeContainer container) {
        Intent i = new Intent(context, DesignerActivity.class);
        i.putExtra(EXTRA_THEME_CONTAINER, container);
        i.putExtra(EXTRA_SHOP_TAG, shopTag);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shop_activity_designer);

        setupToolbar();
        setupList();
    }

    private void setupDesigner(View v) {
//        Glide.with(this).load(themeContainer.icon).into((ImageView) v.findViewById(R.id.shop_avatar));
//        ((TextView) v.findViewById(R.id.shop_name)).setText(themeContainer.tag);
//        ((TextView) v.findViewById(R.id.shop_detail)).setText(themeContainer.detail);
    }

    private void setupList() {
        final RecyclerView headerGridView = (RecyclerView) findViewById(R.id.shop_banner_list);
        Utils.setupGridRecycler(this, headerGridView, 3);
//        final View headerView = LayoutInflater.from(this).inflate(R.layout.shop_designer_header, null, false);

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
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, final Theme item) {
                final ImageView iv = (ImageView) holder.itemView.findViewById(R.id.shop_icon);
                BitmapCache.setImageView(iv, item.icon);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SdkEnv.openPlayStore(item.download, "theme-shop-" + ShopProperties.appTag() + "-" + shopTag + "-designer-list");
                    }
                });
            }
        };
        headerGridView.setAdapter(adapter);
        headerGridView.addOnScrollListener(new AutoLoadOnScrollListener(headerGridView, themeContainer.pageThemes, adapter));
//        adapter.addHeaderView(headerView);
//        setupDesigner(headerView);
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

//        final TextView title = (TextView) findViewById(R.id.shop_title);
//        title.setText(themeContainer.tag);

        /*final CollapsingToolbarLayout tb = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        tb.setTitle(themeContainer.tag);
        final int color = getResources().getColor(R.color.shop_tab_title_selected);
        tb.setExpandedTitleColor(color);
        tb.setCollapsedTitleTextColor(color);*/

//        title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }
}
