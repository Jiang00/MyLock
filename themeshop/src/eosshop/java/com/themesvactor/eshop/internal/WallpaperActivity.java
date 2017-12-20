package com.themesvactor.eshop.internal;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.common.SdkCache;
import com.android.common.SdkEnv;
import com.android.theme.internal.adapter.BitmapCache;
import com.android.theme.internal.data.GAEvent;
import com.android.theme.internal.data.Theme;
import com.android.themeshop.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by song on 2017/3/10.
 */

public class WallpaperActivity extends BaseActivity {
    private Theme theme;
    private String shopTag;

    public static final String EXTRA_THEME = "theme";
    public static final String EXTRA_SHOP_TAG = "shop_tag";


    public static void launch(Context context, String shopTag, Theme theme) {
        Intent i = new Intent(context, WallpaperActivity.class);
        i.putExtra(EXTRA_THEME, theme);
        i.putExtra(EXTRA_SHOP_TAG, shopTag);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity_wallpaper);

        final ImageView iv = (ImageView) findViewById(R.id.shop_wallpaper_background);
        BitmapCache.setImageView(iv, theme.icon);
        BitmapCache.setImageView(theme.download, null, iv,
                SdkEnv.env().screenWidth / 4, SdkEnv.env().screenHeight / 4, new BitmapCache.LoadingListener() {
                    @Override
                    public void onStart() {
                        findViewById(R.id.shop_wallpaper_loading).setVisibility(View.VISIBLE);
                        findViewById(R.id.shop_wallpaper_apply).setClickable(false);
                    }

                    @Override
                    public void onSuccess() {
                        findViewById(R.id.shop_wallpaper_loading).setVisibility(View.GONE);
                        findViewById(R.id.shop_wallpaper_apply).setClickable(true);
                        com.android.theme.internal.data.WallpaperManager.saveWallpaper(theme);
                    }

                    @Override
                    public void onFails() {
                        findViewById(R.id.shop_wallpaper_loading).setVisibility(View.GONE);
                        final Button apply = (Button) findViewById(R.id.shop_wallpaper_apply);
                        apply.setClickable(false);
                        apply.setText(R.string.shop_wallpaper_load_fails);
                    }
                });

        final Date date1 = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd EEEE", Locale.getDefault());
        final String date = dateFormat.format(date1);
        ((TextView) findViewById(R.id.shop_wallpaper_date)).setText(date);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final String time = timeFormat.format(date1);
        ((TextView) findViewById(R.id.shop_wallpaper_time)).setText(time);

        findViewById(R.id.shop_wallpaper_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaper(theme.download);
            }
        });
    }

    @Override
    protected void onReceiveIntent(Intent intent) {
        theme = intent.getParcelableExtra(EXTRA_THEME);
        shopTag = intent.getStringExtra(EXTRA_SHOP_TAG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_THEME, theme);
        outState.putString(EXTRA_SHOP_TAG, shopTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        theme = savedInstanceState.getParcelable(EXTRA_THEME);
        shopTag = savedInstanceState.getString(EXTRA_SHOP_TAG);
    }

    private void setWallpaper(String url) {
        try {
            GAEvent.track(shopTag, GAEvent.ACTION_APPLY, String.valueOf(theme.pkgName().hashCode()));
            WallpaperManager.getInstance(this).setStream(SdkCache.cache().openCache(url, true));
            Toast.makeText(this, R.string.shop_wallpaper_apply_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            final ImageView iv = (ImageView) findViewById(R.id.shop_wallpaper_background);
            if (iv.getDrawable() instanceof BitmapDrawable) {
                try {
                    WallpaperManager.getInstance(this).setBitmap(((BitmapDrawable)iv.getDrawable()).getBitmap());
                    Toast.makeText(this, R.string.shop_wallpaper_apply_success, Toast.LENGTH_SHORT).show();
                    return;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Toast.makeText(this, R.string.shop_wallpaper_apply_fails, Toast.LENGTH_SHORT).show();
        }
    }
}
