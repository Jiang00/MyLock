package com.security.manager.page;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.client.AdListener;
import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.ivy.ivyshop.ShopMaster;
import com.ivymobi.applock.free.R;
import com.security.manager.App;
import com.security.manager.SecuritySettingsAdvance;
import com.security.manager.db.PreData;
import com.security.manager.lib.Utils;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by huale on 2014/11/20.
 */
public class SecurityThemeFragment extends Fragment {
    public static final String TAG_UNLOCK = "unlock";
    public static final String TAG_LOADING = "loading";
    public static final String TAG_TLEF_AD = "Leftmenu";
    public static final String TAG_TOP_AD = "TopLocklist";
    public static View adView = null;
    static CountDownTimer mytimer = null;
    private static LottieAnimationView password_main_ad;
    private static LottieAnimationView fingerprint;
    private static LottieAnimationView ad_full;
    private static FrameLayout ad_full_fl;
    private static Handler handler;
    private static ImageView password_pre;
    private static int show_fingerprint;
    private static LinearLayout password_ad_native;


    public interface ICheckResult {
        void onSuccess();

        void unLock();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctrl = new OverflowCtrl();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        afterViewCreated(view, ctrl);

    }

    public static void afterViewCreated(View view, OverflowCtrl ctrl) {
        setupTitle(view);
    }

    public static void setupTitle(View v) {
        LinearLayout layout = (LinearLayout) v.findViewWithTag("action_bar_passwd");//原标题去了
        layout.removeAllViews();
        //重新加载的标题
        layout.addView(LayoutInflater.from(App.getContext()).inflate(R.layout.security_password_status_bar1, null));
        password_ad_native = (LinearLayout) v.findViewWithTag("password_ad_native");
        AndroidSdk.loadFullAd("start_full", null);
        try {
            createAdView((ViewGroup) v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler = new Handler();
        password_pre = (ImageView) v.findViewWithTag("password_pre");
        ad_full_fl = (FrameLayout) v.findViewWithTag("ad_full_fl");
        ad_full = (LottieAnimationView) v.findViewWithTag("ad_full");
        password_main_ad = (LottieAnimationView) v.findViewWithTag("password_main_ad");
        password_main_ad.setAnimation("ad.json");
        password_main_ad.setScale(0.7f);//相对原大小的0.2倍
        password_main_ad.setSpeed(0.7f);
        password_main_ad.loop(true);
        password_main_ad.playAnimation();
        fingerprint = (LottieAnimationView) v.findViewWithTag("fingerprint");
        fingerprint.setAnimation("fingerprint.json");
        fingerprint.setScale(0.07f);//相对原大小的0.2倍
//        password_main_ad.setSpeed(0.7f);
        fingerprint.loop(true);
        fingerprint.playAnimation();

        ISecurityBridge bridge = SecurityTheBridge.bridge;
        TextView appName = new TextView(v.getContext());
        appName.setTag("realAppName");
        appName.setText(bridge.appName());
        appName.setTextSize(21);
        appName.setTextColor(0xffffffff);
        appName.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Utils.getDimens(v.getContext(), 48));
        lp.leftMargin = Utils.getDimens(v.getContext(), 8);
        ((ViewGroup) v).addView(appName, lp);
        appName.setAlpha(0);
        TextView appname = (TextView) v.findViewWithTag("text_appname");
        TextView app_name = (TextView) v.findViewWithTag("app_name");
        ImageView icon = (ImageView) v.findViewWithTag("title");
        ImageView statusicon = (ImageView) v.findViewWithTag("app_icon");
        appname.setText(bridge.appName());
        icon.setBackgroundDrawable(bridge.icon());
        if (adView != null) {
            icon.setVisibility(View.GONE);
            appname.setVisibility(View.GONE);
            app_name.setText(bridge.appName());
            statusicon.setBackgroundDrawable(bridge.icon());
        }
        password_main_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password_main_ad.cancelAnimation();
                showFullAnimator();
            }
        });
        password_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
        try {
            String flurryString = AndroidSdk.getExtraData();
            JSONObject baseJson = new JSONObject(flurryString);
            show_fingerprint = baseJson.getInt("show_fingerprint");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (show_fingerprint == 1) {
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(App.getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (!managerCompat.isHardwareDetected()) { //判断设备是否支持
                    fingerprint.setVisibility(View.GONE);
                } else if (!managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
                    fingerprint.setVisibility(View.GONE);
                } else if (!SecurityMyPref.getFingerprintl()) {
                    fingerprint.setVisibility(View.GONE);
                }
            } else {
                fingerprint.setVisibility(View.GONE);
            }
        } else {
            fingerprint.setVisibility(View.GONE);
        }
        if (App.getContext().getResources().getString(R.string.app_name) != null || App.getContext().getResources().getString(R.string.app_name).isEmpty()) {
            Log.e("chfq", "====App====");
            if ((App.getContext().getResources().getString(R.string.app_name)).equals(bridge.appName())) {
                Log.e("chfq", "====myApp====");
                String tag = "ad_interval_minute_slock";
                long lastPopAdTime = PreData.getDB(v.getContext(), tag, 0l);
                if (lastPopAdTime == -1) {
                    PreData.putDB(v.getContext(), tag, 0l);
                    return;
                }
                long l = System.currentTimeMillis();
                Log.e("chfq", "====lastPopAdTime====" + lastPopAdTime);
                if (lastPopAdTime == 0) {
                    showFullAd();
                    PreData.putDB(v.getContext(), tag, l);
                    return;
                }
                try {
                    long minute = new JSONObject(AndroidSdk.getExtraData()).optLong("show_interval_time");
                    Log.e("chfq", "minute=" + minute + "=time=" + (l - lastPopAdTime));
                    if (l - lastPopAdTime >= minute * 60 * 1000) {
                        Log.e("chfq", "====l - lastPopAdTime=====");
                        showFullAd();
                        PreData.putDB(v.getContext(), tag, l);
                    }
                } catch (Exception e) {
                }
            } else {
                Log.e("chfq", "====otherApp====");
                String tag = "ad_interval_minute_unlock";
                long lastPopAdTime = PreData.getDB(v.getContext(), tag, 0l);
                Log.e("chfq", "====lastPopAdTime====" + lastPopAdTime);
                if (lastPopAdTime == -1) {
                    PreData.putDB(v.getContext(), tag, 0l);
                    return;
                }
                long l = System.currentTimeMillis();
                if (lastPopAdTime == 0) {
                    Log.e("chfq", "====lastPopAdTime=0====");
                    showFullAd();
                    PreData.putDB(v.getContext(), tag, l);
                    return;
                }
                try {
                    long minute = new JSONObject(AndroidSdk.getExtraData()).optLong("show_interval_time");
                    Log.e("chfq", "minute=" + minute + "=time=" + (l - lastPopAdTime));
                    if (l - lastPopAdTime >= minute * 60 * 1000) {
                        Log.e("chfq", "====l - lastPopAdTime=====");
                        showFullAd();
                        PreData.putDB(v.getContext(), tag, l);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private static void showFullAd() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidSdk.showFullAd("start_full");
            }
        }, 500);
    }

    //全屏广告动画
    private static void showFullAnimator() {
        ad_full_fl.setVisibility(View.VISIBLE);
        ad_full.setAnimation("ad.json");
        ad_full.setScale(3f);//相对原大小的0.2倍
//        password_main_ad.setSpeed(0.7f);
        ad_full.loop(true);
        ad_full.playAnimation();

        AndroidSdk.loadFullAd("gift_full", new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ad_full_fl != null) {
                            ad_full_fl.setVisibility(View.GONE);
                        }
                        if (ad_full != null) {
                            ad_full.cancelAnimation();
                        }
                    }
                }, 800);
                handler.removeCallbacks(runnable_load);
            }
        });
        handler.postDelayed(runnable_load, 4000);
    }

    static Runnable runnable_load = new Runnable() {
        @Override
        public void run() {
            AndroidSdk.showFullAd("gift_full");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ad_full_fl != null) {
                        ad_full_fl.setVisibility(View.GONE);
                    }
                    if (ad_full != null) {
                        ad_full.cancelAnimation();
                    }
                }
            }, 800);

        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ViewGroup group = (ViewGroup) getView();
        if (group != null) {
            group.removeAllViewsInLayout();
            View v = onCreateView(LayoutInflater.from(getActivity()), group, null);
            group.addView(v);
            onViewCreated(v, null);
        }
    }

    protected static Animation out, in;
    protected OverflowCtrl ctrl;
    boolean onPause = false;

    @Override
    public void onPause() {
        super.onPause();
        onPause = true;
        if (password_main_ad != null) {
            password_main_ad.cancelAnimation();
        }
        if (fingerprint != null) {
            fingerprint.cancelAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onPause) {
            onPause = false;
            if (password_main_ad != null) {
                password_main_ad.playAnimation();
            }
            if (fingerprint != null) {
                fingerprint.playAnimation();
            }
        }
    }

    @Override
    public void onDestroyView() {
        onPause = false;
        ctrl.hideOverflow = null;
        if (ctrl.overflowStub != null) {
            ctrl.overflowStub.removeAllViews();
        }
        ctrl.overflowStub = null;
        ctrl.ovf = null;
        ctrl = null;
        ViewGroup group = (ViewGroup) getView();
        if (group != null || group.getChildCount() > 0) {
            group.removeAllViews();
        }
        super.onDestroyView();
        try {
            AndroidSdk.destroyNativeAdView(TAG_UNLOCK, adView);
            adView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (password_main_ad != null) {
            password_main_ad.cancelAnimation();
        }
        if (fingerprint != null) {
            fingerprint.cancelAnimation();
        }
    }

    protected static void createAdView(ViewGroup view) {
        if (AndroidSdk.hasNativeAd(TAG_UNLOCK)) {
            adView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_UNLOCK, AndroidSdk.HIDE_BEHAVIOR_NO_HIDE, R.layout.security_native_layout, new ClientNativeAd.NativeAdClickListener() {
                @Override
                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {

                }
            }, new ClientNativeAd.NativeAdScrollListener() {
                @Override
                public void onNativeAdScrolled(float v) {

                }
            });
            if (adView != null && password_ad_native != null) {
                App.getWatcher().watch(adView);
                password_ad_native.addView(adView);
            }
        }
    }

    public static MyFrameLayout inflate(String layoutId, ViewGroup container, Context c) {
        Context themeContext = null;
        try {
            themeContext = ShopMaster.currentTheme().getThemeContext();
        } catch (Exception e) {
            themeContext = c;
        }
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        Log.e("haha", "pkg " + themeContext.getPackageName());
        int layout = themeContext.getResources().getIdentifier(layoutId, "layout", themeContext.getPackageName());
        MyFrameLayout v = (MyFrameLayout) inflater.inflate(layout, container, false);
        return v;
    }
}
