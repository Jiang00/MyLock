package com.ivy.ivyshop.internal;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.SdkEnv;
import com.android.theme.internal.adapter.BitmapCache;
import com.android.theme.internal.data.ShopProperties;
import com.android.theme.internal.data.ThemeContainer;
import com.android.theme.internal.data.ThemeSegment;
import com.android.themeshop.R;
import com.android.view.PagerIndicator;

/**
 * Created by song on 2017/3/8.
 */

public class BannerView {
    public static View create(final Context context, final String shopTag, ViewGroup parent, final ThemeSegment segment) {
        View v = LayoutInflater.from(context).inflate(R.layout.shop_banner_list, parent, false);
        final TextView bannerIndicator = (TextView) v.findViewById(R.id.shop_banner_indicator);
        final ViewPager banner = (ViewPager) v.findViewById(R.id.shop_banner);

        if (segment.css.largeHeight) {
            final ViewGroup.LayoutParams lp = banner.getLayoutParams();
            lp.height = SdkEnv.dp2px(312);
            banner.setLayoutParams(lp);
        }

        final int size = segment.themeContainers.size();
        bannerIndicator.setText("1/" + size);
        if (size > 1) {
            banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    bannerIndicator.setText((position + 1) + "/" + size);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            bannerIndicator.setVisibility(View.GONE);
        }
        banner.setAdapter(new PagerAdapter() {
            View[] pages = new View[segment.themeContainers.size()];

            @Override
            public int getCount() {
                return segment.themeContainers.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (pages[position] == null) {
                    final ThemeContainer container1 = segment.themeContainers.get(position);
                    final View v = LayoutInflater.from(container.getContext()).inflate(R.layout.shop_banner_card, container, false);
                    ImageView iv = (ImageView) v.findViewById(R.id.shop_icon);
                    iv.setImageResource(R.color.shop_loading_bg);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    BitmapCache.setImageView(iv, container1.icon);
                    container.addView(v);
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (container1.pageThemes.themes.size() == 1) {
                                SdkEnv.openPlayStore(container1.pageThemes.themes.get(0).download, "theme-shop-" + ShopProperties.appTag() + "-" + shopTag + "-banner");
                            } else {
                                BannerActivity.launch(v.getContext(), shopTag, container1);
                            }
                        }
                    });
                    pages[position] = v;
                    return v;
                } else {
                    return pages[position];
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
            }
        });
        return v;
    }
}
