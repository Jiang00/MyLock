package com.themesvactor.eshop.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.SdkEnv;
import com.android.theme.internal.UIController;
import com.android.theme.internal.data.EventHook;
import com.android.theme.internal.data.Theme;
import com.android.theme.internal.data.ThemeSelector;
import com.android.theme.internal.data.ThemeTag;
import com.android.themeshop.R;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends BaseActivity {
    public static final String APP_TAG = "shop_app";
    private ViewPager viewPager;
    private TextView title;
    private boolean local;

    public static final String EXTRA_MAIN_TAG = "main_tag";
    public static final String EXTRA_LOCAL = "local";
    public static final String EXTRA_TAGS = "tags";
    public static final String EXTRA_CURRENT_TAG = "current_tag";
    public static final String EXTRA_LOCAL_THEMES = "local_themes";

    private String mainTag;
    private String currentTag;
    protected Theme[] localThemes;
    protected String[] tags;
    private List<ThemeTag> themeTags;
    private ImageView loadFails;

    protected ShopPresenter presenter;
    protected final UIController<ThemeTag> controller = new UIController<ThemeTag>() {

        @Override
        public void showLoading() {
            Log.e("chfq", "==showLoading=2=");
//            if (loading == null) {
//                loading = (LottieAnimationView) findViewById(R.id.shop_loading_animation);
//            }
//            try {
//                loading.setVisibility(View.VISIBLE);
//                loading.setAnimation(new JSONObject(SdkCache.cache().readText("shop.anim", true, false)));
//                loading.loop(true);
//                loading.playAnimation();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void hideLoading() {
            Log.e("chfq", "==hideLoading=2=");
//            if (loading != null) {
//                loading.cancelAnimation();
//                loading.setVisibility(View.GONE);
//                loading = null;
//            }
        }

        @Override
        public void onReceiveNone() {
            Log.e("chfq", "==onReceiveNone=2=");
            loadFails.setVisibility(View.VISIBLE);
            loadFails.setImageResource(R.drawable.shop_load_fails);
            Toast.makeText(ShopActivity.this, "no data", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceiveOne(ThemeTag one) {
            Log.e("chfq", "==onReceiveOne=2=");
            final ArrayList<ThemeTag> data = new ArrayList<>();
            data.add(one);
            setup(data);
            title.setText(one.name);
        }

        @Override
        public void onReceiveSome(final List<ThemeTag> data) {
            Log.e("chfq", "==onReceiveSome=2=");
            setup(data);
        }

        @Override
        public void onReceiveMany(List<ThemeTag> data) {
            Log.e("chfq", "==onReceiveMany=2=");
            setup(data);
        }

        private void setup(final List<ThemeTag> data) {
            loadFails.setVisibility(View.GONE);
            if (themeTags == null) {
                themeTags = data;
            }
            Log.e("chfq", "==setup=2=" + data.size());
            title.setText(local ? R.string.shop_app_name_local : R.string.shop_app_name);

            for (int i = data.size() - 1; i >= 0; --i) {
                final ThemeTag themeTag = data.get(i);
                if (currentTag.equals(themeTag.tag)) {
                    data.remove(i);
                    data.add(0, themeTag);
                    break;
                }
            }

            tags = new String[data.size() * 2];
            for (int i = data.size() - 1; i >= 0; --i) {
                final ThemeTag themeTag = data.get(i);
                tags[i * 2] = themeTag.tag;
                tags[i * 2 + 1] = themeTag.name;
            }
            refreshThemeSelector(tags);

            if (viewPager.getAdapter() == null) {
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        EventHook.hooked(EventHook.EVENT_PAGE_SELECTED, position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                viewPager.setAdapter(new PagerAdapter() {
                    View[] pages = new View[1];

                    @Override
                    public CharSequence getPageTitle(int position) {
                        return data.get(position).name;
                    }

                    @Override
                    public int getCount() {
                        return 1;
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        Log.e("chfq", "===instantiateItem===");
                        if (pages[position] == null) {
                            View view = ThemeTagView.newView(container.getContext(), container, data.get(position), local);
                            pages[position] = view;
                            container.addView(view);
                            return view;
                        } else {
                            return pages[position];
                        }
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {

                    }
                });

            } else {
                for (ThemeTag themeTag : themeTags) {
                    for (ThemeTag tag : data) {
                        if (themeTag.tag.equals(tag.tag)) {
                            EventHook.hooked(EventHook.EVENT_DATA_SET_CHANGED, themeTag.tag, tag.segments.get(0).themeContainers.get(0).pageThemes.themes);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void onCorrect(Object data) {

        }

        @Override
        public void onError(Object data) {
            Toast.makeText(ShopActivity.this, "error: " + data, Toast.LENGTH_SHORT).show();
            loadFails.setVisibility(View.VISIBLE);
            loadFails.setImageResource(R.drawable.shop_load_fails);
        }

        @Override
        public void done(Object data) {

        }

        @Override
        public void nothing() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity_main);
        setupToolbar();
        setupNavigationIcon();
        setupPresenter();
    }

    protected void setupPresenter() {
        presenter = new ShopPresenter(controller);
        presenter.start();
        presenter.loading();
    }

    private void setupToolbar() {
        viewPager = (ViewPager) findViewById(R.id.shop_view_pager);
        title = (TextView) findViewById(R.id.shop_title);
        loadFails = (ImageView) findViewById(R.id.shop_load_fails);

        //标题
        Toolbar toolbar = (Toolbar) findViewById(R.id.shop_toolbar);
        if (!local) {
            toolbar.inflateMenu(R.menu.shop_main);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final int itemId = item.getItemId();
                    if (itemId == R.id.shop_local_themes) {
                        startLocal();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    private void setupNavigationIcon() {
        if (!local && mainTag.equals(APP_TAG)) {
            title.setClickable(false);
            title.setCompoundDrawablePadding(SdkEnv.dp2px(4));
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shop_icon, 0, 0, 0);
        } else {
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    protected void startLocal() {
        Intent i = new Intent(this, ShopLocalActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.putExtra(ShopActivity.EXTRA_MAIN_TAG, mainTag);
        i.putExtra(ShopActivity.EXTRA_CURRENT_TAG, currentTag);
        if (tags == null) {
            tags = new String[]{
                    mainTag,
                    mainTag
            };
        }
        i.putExtra(EXTRA_TAGS, tags);
        i.putExtra(EXTRA_LOCAL, true);
        i.putExtra(EXTRA_LOCAL_THEMES, localThemes);
        startActivity(i);
    }

    protected void onReceiveIntent(Intent intent) {
        if (intent != null) {
            mainTag = intent.getStringExtra(EXTRA_MAIN_TAG);
            currentTag = intent.getStringExtra(EXTRA_CURRENT_TAG);
            local = intent.getBooleanExtra(EXTRA_LOCAL, false);
            tags = intent.getStringArrayExtra(EXTRA_TAGS);
            final Parcelable[] ps = intent.getParcelableArrayExtra(EXTRA_LOCAL_THEMES);
            if (ps != null) {
                localThemes = new Theme[ps.length];
                for (int i = 0; i < ps.length; ++i) {
                    localThemes[i] = (Theme) ps[i];
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_MAIN_TAG, mainTag);
        outState.putString(EXTRA_CURRENT_TAG, currentTag);
        outState.putBoolean(EXTRA_LOCAL, local);
        outState.putStringArray(EXTRA_TAGS, tags);
        outState.putParcelableArray(EXTRA_LOCAL_THEMES, localThemes);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mainTag = savedInstanceState.getString(EXTRA_MAIN_TAG);
        currentTag = savedInstanceState.getString(EXTRA_CURRENT_TAG);
        local = savedInstanceState.getBoolean(EXTRA_LOCAL);
        tags = savedInstanceState.getStringArray(EXTRA_TAGS);
        final Parcelable[] ps = savedInstanceState.getParcelableArray(EXTRA_LOCAL_THEMES);
        if (ps != null) {
            localThemes = new Theme[ps.length];
            for (int i = 0; i < ps.length; ++i) {
                localThemes[i] = (Theme) ps[i];
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.stop();
        super.onDestroy();
    }

    protected void refreshThemeSelector(String[] tags) {
        String[] ts = new String[tags.length / 2];
        for (int i = 0; i < ts.length; ++i) {
            ts[i] = tags[i * 2];
        }
        ThemeSelector.setTags(ts);
        ThemeSelector.refresh();
    }
}
