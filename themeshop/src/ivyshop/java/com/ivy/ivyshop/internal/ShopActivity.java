package com.ivy.ivyshop.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.common.SdkCache;
import com.android.theme.internal.UIController;
import com.android.theme.internal.data.EventHook;
import com.android.theme.internal.data.Theme;
import com.android.theme.internal.data.ThemeSelector;
import com.android.theme.internal.data.ThemeTag;
import com.android.themeshop.R;
import com.sample.lottie.LottieAnimationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends BaseActivity {
    public static final String APP_TAG = "shop_app";
    private ViewPager viewPager;
    private TabLayout tabLayout;
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
        LottieAnimationView loading;

        @Override
        public void showLoading() {
            if (loading == null) {
                loading = (LottieAnimationView) findViewById(R.id.shop_loading_animation);
            }
            try {
                loading.setVisibility(View.VISIBLE);
                loading.setAnimation(new JSONObject(SdkCache.cache().readText("shop.anim", true, false)));
                loading.loop(true);
                loading.playAnimation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void hideLoading() {
            if (loading != null) {
                loading.cancelAnimation();
                loading.setVisibility(View.GONE);
                loading = null;
            }
        }

        @Override
        public void onReceiveNone() {
            loadFails.setVisibility(View.VISIBLE);
            loadFails.setImageResource(R.drawable.shop_load_fails);
            Toast.makeText(ShopActivity.this, "no data", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceiveOne(ThemeTag one) {
            final ArrayList<ThemeTag> data = new ArrayList<>();
            data.add(one);
            setup(data);
            tabLayout.setVisibility(View.GONE);
//            title.setText(one.tag);
            getSupportActionBar().setTitle(one.name);
        }

        @Override
        public void onReceiveSome(final List<ThemeTag> data) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            setup(data);
        }

        @Override
        public void onReceiveMany(List<ThemeTag> data) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            setup(data);
        }

        private void setup(final List<ThemeTag> data) {
            loadFails.setVisibility(View.GONE);
            if (themeTags == null) {
                themeTags = data;
            }
            getSupportActionBar().setTitle(local ? R.string.shop_app_name_local : R.string.shop_app_name);
            tabLayout.setVisibility(View.VISIBLE);

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
                    View[] pages = new View[data.size()];

                    @Override
                    public CharSequence getPageTitle(int position) {
                        return data.get(position).name;
                    }

                    @Override
                    public int getCount() {
                        return data.size();
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        if (pages[position] == null) {
                            View view = ThemeTagView.newView(container.getContext(), mainTag, container, data.get(position), local);
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

                tabLayout.setupWithViewPager(viewPager);
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
        tabLayout = (TabLayout) findViewById(R.id.shop_tab);
        loadFails = (ImageView) findViewById(R.id.shop_load_fails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.shop_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!local) {
            getMenuInflater().inflate(R.menu.shop_main, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.shop_local_themes) {
            startLocal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationIcon() {
        if (!local && mainTag.equals(APP_TAG)) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.shop_icon);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void startLocal() {
        Intent i = new Intent(this, LocalShopActivity.class);
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
