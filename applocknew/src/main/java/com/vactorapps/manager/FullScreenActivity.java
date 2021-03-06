package com.vactorapps.manager;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.MAppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.ivymobi.applock.free.R;
import com.mingle.circletreveal.CircularRevealCompat;
import com.mingle.widget.animation.CRAnimation;
import com.mingle.widget.animation.SimpleAnimListener;

import static com.vactorapps.manager.page.VacThemeFragment.TAG_LOADING;

/**
 * Created by song on 16/1/4.
 */
public class FullScreenActivity extends MAppCompatActivity {


    FrameLayout adView;
    ImageView adIcon;
    View nativeView;
    ImageView adLoading;
    FrameLayout fullLayout;
    LinearLayout close;
    ImageView newAdLoading;
    TextView changeLock;
    Animation loadingAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.native_ad_fullscreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
        close = (LinearLayout) this.findViewById(R.id.ad_closeme);
        fullLayout = (FrameLayout) this.findViewById(R.id.full);
        adView = (FrameLayout) this.findViewById(R.id.adview);
        adIcon = (ImageView) this.findViewById(R.id.for_anima);
        adLoading = (ImageView) this.findViewById(R.id.ad_loading);
        loadingAni = AnimationUtils.loadAnimation(this, R.anim.ad_loading);
        LinearInterpolator lin = new LinearInterpolator();
        loadingAni.setInterpolator(lin);
        adLoading.startAnimation(loadingAni);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CRAnimation crA = new CircularRevealCompat(fullLayout).circularReveal(adIcon.getLeft() + adIcon.getWidth() / 2, adIcon.getTop() + adIcon.getHeight() / 2, fullLayout.getHeight(), 0);
                if (crA != null) {
                    crA.addListener(new SimpleAnimListener() {
                        @Override
                        public void onAnimationEnd(CRAnimation animation) {
                            super.onAnimationEnd(animation);
                            fullLayout.setVisibility(View.GONE);
                            finish();
                        }
                    });
                    crA.start();
                }

            }
        });

        AndroidSdk.loadNativeAd(TAG_LOADING, R.layout.native_ad_loading, new ClientNativeAd.NativeAdLoadListener() {
            @Override
            public void onNativeAdLoadSuccess(View view) {
                nativeView = view;
                adView.addView(nativeView);
                newAdLoading = (ImageView) nativeView.findViewWithTag("loading_native");
                changeLock = (TextView) nativeView.findViewWithTag("ac_action_click_change");
                changeLock.setVisibility(View.VISIBLE);
                changeLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reloadNativeAd();
                    }
                });
                newAdLoading.setVisibility(View.GONE);
                adLoading.clearAnimation();
                adLoading.setVisibility(View.GONE);
            }

            @Override
            public void onNativeAdLoadFails() {
                Log.e("inf", "fail");
                adLoading.clearAnimation();
                adLoading.setVisibility(View.GONE);
                adView.findViewById(R.id.ad_loading).setVisibility(View.GONE);
                Toast.makeText(adView.getContext(), R.string.security_loading_fullad_fail, Toast.LENGTH_LONG).show();
                finish();
                nativeView = null;
            }
        });
    }

    public void reloadNativeAd() {
        Tracker.sendEvent(Tracker.CATE_ACTION__NATIVE_FULL_SCREEN, Tracker.CATE_ACTION__NATIVE_NEXT, Tracker.CATE_ACTION__NATIVE_NEXT, 1L);
        changeLock.setVisibility(View.INVISIBLE);
        newAdLoading.setVisibility(View.VISIBLE);
        newAdLoading.startAnimation(loadingAni);
        AndroidSdk.reLoadNativeAd(TAG_LOADING, nativeView, new ClientNativeAd.NativeAdLoadListener() {
            @Override
            public void onNativeAdLoadSuccess(View view) {
                Log.e("inf", "reload success");
                changeLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reloadNativeAd();
                    }
                });
                changeLock.setVisibility(View.VISIBLE);
                newAdLoading.clearAnimation();
                newAdLoading.setVisibility(View.GONE);
            }

            @Override
            public void onNativeAdLoadFails() {
                Log.e("inf", "reload fails");
                changeLock.setVisibility(View.VISIBLE);
                newAdLoading.clearAnimation();
                newAdLoading.setVisibility(View.GONE);
                Toast.makeText(adView.getContext(), R.string.security_loading_fullad_fail, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (nativeView != null) {
            CRAnimation crA = new CircularRevealCompat(fullLayout).circularReveal(adIcon.getLeft() + adIcon.getWidth() / 2, adIcon.getTop() + adIcon.getHeight() / 2, fullLayout.getHeight(), 0);
            if (crA != null) {
                crA.addListener(new SimpleAnimListener() {
                    @Override
                    public void onAnimationEnd(CRAnimation animation) {
                        super.onAnimationEnd(animation);
                        fullLayout.setVisibility(View.GONE);
                        finish();
                    }
                });
                crA.start();
            }
        } else {
            super.onBackPressed();
        }


    }
}
