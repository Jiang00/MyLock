package com.ivy.ivyshop.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.SdkEnv;
import com.android.theme.internal.adapter.AutoLoadOnScrollListener;
import com.android.theme.internal.adapter.BitmapCache;
import com.android.theme.internal.adapter.LoadMoreAdapter;
import com.android.theme.internal.data.EventHook;
import com.android.theme.internal.data.GAEvent;
import com.android.theme.internal.data.LoadMoreEvent;
import com.android.theme.internal.data.ShopProperties;
import com.android.theme.internal.data.Theme;
import com.android.theme.internal.data.ThemeManager;
import com.android.theme.internal.data.ThemeSegment;
import com.android.theme.internal.data.ThemeSelector;
import com.android.theme.internal.data.ThemeTag;
import com.android.theme.internal.data.WallpaperManager;
import com.android.themeshop.R;

import org.byteam.superadapter.IMulItemViewType;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.SuperViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by song on 2017/3/7.
 */

public class ThemeTagView {

    public static View newView(final Context context, final String mainTag, ViewGroup parent, final ThemeTag themeTag, final boolean local) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final RecyclerView gridView = (RecyclerView) inflater.inflate(R.layout.shop_theme_tag, parent, false);

        final boolean widget = themeTag.tag.equals("widget");
        final boolean wallpaper = themeTag.tag.equals("wallpaper");
        final int shop_theme_item, columns;

        if (widget || local) {
            gridView.setPadding(gridView.getPaddingLeft(), SdkEnv.dp2px(8), gridView.getPaddingRight(), gridView.getPaddingBottom());
        }

        if (widget) {
            shop_theme_item = R.layout.shop_theme_widget;
            columns = 2;
        } else {
            columns = 3;
            shop_theme_item = R.layout.shop_theme_item;
        }

        LinearLayout header = (LinearLayout) inflater.inflate(R.layout.shop_header_wrapper, null, false);
        ThemeAdapter tmpAdapter = null;

        for (ThemeSegment segment : themeTag.segments) {
            if (segment.isSpecial(ThemeSegment.SPECIAL_BANNERS)) {
                header.addView(BannerView.create(context, themeTag.tag, header, segment));
            } else if (segment.isSpecial(ThemeSegment.SPECIAL_DESIGNERS)) {
                header.addView(DesignerView.create(context, themeTag.tag, header, segment));
            } else {
                tmpAdapter = new ThemeAdapter(segment.themeContainers);
            }
        }


        if (tmpAdapter != null) {
            final ThemeAdapter a = tmpAdapter;
            if (local) {
                Utils.setupGridRecycler(context, gridView, columns);
            } else {
                Utils.setupGridRecycler(context, gridView, columns, a);
            }
            final GridLayoutManager lm = (GridLayoutManager) gridView.getLayoutManager();
            final SuperAdapter adapter = local ?
                    editableAdapter(themeTag.tag, context, a.originThemes, shop_theme_item, wallpaper) :
                    normalAdapter(context, mainTag, themeTag.tag, "commons", a, shop_theme_item, wallpaper);
            gridView.addOnScrollListener(new AutoLoadOnScrollListener(gridView, a, adapter));
            lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (adapter.hasHeaderView()) {
                        position -= 1;
                    }
                    ThemeAdapter.ThemeWrapper themeWrapper = a.themeWrappers.get(position);
                    final boolean title = themeWrapper.isTitle() || themeWrapper.isSingleTitle();
                    return title ? 3 : 1;
                }
            });
            gridView.setAdapter(adapter);
            if (header.getChildCount() > 0) {
                adapter.addHeaderView(header);
            }
            if (!local) {
                adapter.addFooterView(inflater.inflate(R.layout.shop_load_more_footer, null, false));
                adapter.getFooterView().setVisibility(View.INVISIBLE);
            }
        }

        return gridView;
    }

    private static SuperAdapter<Theme> editableAdapter(final String shopTag, Context context, final ArrayList<Theme> themes, int layoutId, final boolean isWallpaper) {
        class EditorAdapter extends SuperAdapter<Theme> implements EventHook.Hooker {
            private boolean editing;

            public EditorAdapter(Context context, List<Theme> items, int layoutResId) {
                super(context, items, layoutResId);
                EventHook.register(this,
                        EventHook.EVENT_BACK_PRESSED,
                        EventHook.EVENT_PAGE_SELECTED,
                        EventHook.EVENT_DATA_SET_CHANGED);
            }

            @Override
            public void onBind(final SuperViewHolder holder, int viewType, int layoutPosition, final Theme theme) {
                final ImageView iv = (ImageView) holder.itemView.findViewById(R.id.shop_icon);
                BitmapCache.setImageView(iv, theme.icon);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editing) {

                        } else {
                            final Context context = v.getContext();
                            if (isWallpaper) {
                                WallpaperActivity.launch(context, shopTag, theme);
                            } else {
                                apply(context, shopTag, theme);
                            }
                        }
                    }

                    private void apply(Context context, String shopTag, Theme theme) {
                        GAEvent.track(shopTag, GAEvent.ACTION_APPLY, theme.pkgName());
                        if (theme.isLocal) {
                            if (ThemeManager.applyTheme(context, theme.pkgName(), true)) {
                                Toast.makeText(context, R.string.shop_theme_apply_success, Toast.LENGTH_SHORT).show();
                                ThemeSelector.refresh();
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, R.string.shop_theme_apply_fails, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            final String pkg = theme.download;
                            Intent i = new Intent(Intent.ACTION_ANSWER);
                            i.setPackage(pkg);
                            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                context.startActivity(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        editing = true;
                        notifyDataSetChanged();
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                                        | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        return true;
                    }
                });

                final boolean selected = ThemeSelector.isSelected(theme);
                final View remove = holder.itemView.findViewById(R.id.shop_remove);
                if (selected) {
                    holder.itemView.findViewById(R.id.shop_selected).setVisibility(View.VISIBLE);
                    remove.setVisibility(View.GONE);
                } else {
                    holder.itemView.findViewById(R.id.shop_selected).setVisibility(View.GONE);
                    if (editing) {
                        remove.setVisibility(View.VISIBLE);
                        remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isWallpaper) {
                                    WallpaperManager.removeWallpaper(theme);
                                    themes.remove(theme);
                                    notifyDataSetChanged();
                                } else {
                                    Intent intent = new Intent(
                                            Intent.ACTION_DELETE, Uri.fromParts("package", theme.pkgName(), ""));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    v.getContext().startActivity(intent);
                                }
                            }
                        });
                    } else {
                        remove.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public boolean onHook(int event, Object... params) {
                if (event == EventHook.EVENT_BACK_PRESSED) {
                    if (editing) {
                        if (getContext() == params[0]) {
                            editing = false;
                            notifyDataSetChanged();
                            return true;
                        }
                    }
                } else if (event == EventHook.EVENT_PAGE_SELECTED) {
                    if (editing) {
                        editing = false;
                        notifyDataSetChanged();
                        return true;
                    }
                } else if (event == EventHook.EVENT_DATA_SET_CHANGED) {
                    if (shopTag.equals(params[0])) {
                        getData().clear();
                        getData().addAll((List<Theme>) params[1]);
                        notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }
        }

        return new EditorAdapter(context, themes, layoutId);
    }

    private static SuperAdapter<ThemeAdapter.ThemeWrapper> normalAdapter(Context context, final String mainTag, final String shopTag, final String containerTag, ThemeAdapter a, int layoutId, final boolean isWallpaper) {
        return new LoadMoreAdapter<ThemeAdapter.ThemeWrapper>(context, a.themeWrappers, new IMulItemViewType<ThemeAdapter.ThemeWrapper>() {
            @Override
            public int getViewTypeCount() {
                return 3;
            }

            @Override
            public int getItemViewType(int position, ThemeAdapter.ThemeWrapper themeWrapper) {
                return themeWrapper.isTitle() ? 1 : (themeWrapper.isSingleTitle() ? 3 : 2);
            }

            @Override
            public int getLayoutId(int viewType) {
                switch (viewType) {
                    case 1:
                        return R.layout.shop_theme_title;

                    case 2:
                        return R.layout.shop_theme_item;

                    default:
                        return R.layout.shop_theme_single_title;
                }
            }
        }) {

            @Override
            protected void onReceiveMoreEvent(LoadMoreEvent event) {
                if (event.match(shopTag, containerTag)) {
                    removeFooterView();
                }
            }

            @Override
            public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, final ThemeAdapter.ThemeWrapper theme) {
                if (viewType == 1) {
                    ((TextView) holder.itemView.findViewById(R.id.shop_title)).setText(theme.title);
                } else if (viewType == 2){
                    final ImageView iv = (ImageView) holder.itemView.findViewById(R.id.shop_icon);
                    BitmapCache.setImageView(iv, theme.theme.icon);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Context context = v.getContext();
                            if (isWallpaper) {
                                WallpaperActivity.launch(context, shopTag, theme.theme);
                            } else {
                                GAEvent.track(shopTag, GAEvent.ACTION_CLICK, theme.theme.pkgName());
                                SdkEnv.openPlayStore(theme.theme.download, "theme-shop-" + ShopProperties.appTag() + "-" + shopTag + "-commons");
                            }
                        }
                    });
                }
            }
        };
    }
}
