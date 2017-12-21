package com.vactorapps.manager.page;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.client.AdListener;
import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.ivymobi.applock.free.R;
import com.themesvactor.eshop.ShopMaster;
import com.vactorapps.manager.MyApp;
import com.vactorapps.manager.VacAccessibilityService;
import com.vactorapps.manager.meta.TheBridgeVac;
import com.vactorapps.manager.mydb.PreData;
import com.vactorapps.manager.myinterface.ISecurityBridge;
import com.vactorappsapi.manager.lib.Utils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by huale on 2014/11/20.
 */
public class VacThemeFragment extends Fragment {
    public static final String TAG_UNLOCK = "unlock";
    public static final String TAG_LOADING = "loading";
    public static final String TAG_TLEF_AD = "Leftmenu";
    public static final String TAG_TOP_AD = "TopLocklist";
    public static View adView = null;
    static CountDownTimer mytimer = null;
    private static LottieAnimationView password_main_ad;
    //    private static LottieAnimationView fingerprint;
    private static LottieAnimationView ad_full;
    private static FrameLayout ad_full_fl;
    private static Handler handler;
    //    private static ImageView password_pre;
    private static int show_fingerprint;
    private static LinearLayout password_ad_native;
    private static ObjectAnimator animator;


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
        setupOverflow(view, ctrl);
    }

    public static void setupOverflow(View root, final OverflowCtrl ctrl) {
        Context context = MyApp.getContext();
        ISecurityBridge bridge = TheBridgeVac.bridge;
        ctrl.overflowStub = (LinearLayout) root.findViewWithTag("overflow_stub");
        ctrl.overflowStub.removeAllViews();
        ((ViewGroup) root).removeView(ctrl.overflowStub);
        ((ViewGroup) root).addView(ctrl.overflowStub);
        OverflowMenu[] menus = bridge.menus();
        for (final OverflowMenu menu : menus) {
//            Button button = (Button) LayoutInflater.from(context).inflate(R.layout.overflow_menu, null, false);
            Button button = (Button) LayoutInflater.from(MyApp.getContext()).inflate(R.layout.overflow_menu, null);

            if (menu.checkable) {
                checkMenu(button, menu.checked);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ctrl.ovf.performClick();
                    menu.onClick(v);
                    if (menu.checkable) {
                        checkMenu((Button) v, menu.checked);
                    }
                }
            });
            button.setText(bridge.res().getText(menu.title));
            ctrl.overflowStub.addView(button);
        }
        if (out == null)
            out = AnimationUtils.loadAnimation(context, R.anim.overflow_out);
        if (in == null)
            in = AnimationUtils.loadAnimation(context, R.anim.overflow_in);
        ctrl.ovf = (ImageButton) root.findViewWithTag("password_pre");

        if (isAccessibilitySettingsOn(MyApp.getContrext()) && !Utils.requireCheckAccessPermission(MyApp.getContrext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(MyApp.getContrext())) {
                    ctrl.ovf.setVisibility(View.GONE);
                }
            } else {
                ctrl.ovf.setVisibility(View.GONE);
            }
        }
        ctrl.ovf.setOnClickListener(new View.OnClickListener() {
            boolean show = false;

            @Override
            public void onClick(View v) {
                if (show) {
                    ctrl.overflowStub.setVisibility(View.INVISIBLE);
                    ctrl.overflowStub.startAnimation(out);
                    show = false;
                } else {
                    ctrl.overflowStub.setVisibility(View.VISIBLE);
                    ctrl.overflowStub.startAnimation(in);
                    show = true;
                }
            }
        });
    }

    public static void checkMenu(Button button, boolean check) {
        if (check)
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
        else
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.uncheck, 0);
    }

    public static void setupTitle(View v) {
        LinearLayout layout = (LinearLayout) v.findViewWithTag("action_bar_passwd");//原标题去了
        layout.removeAllViews();
        //重新加载的标题
        layout.addView(LayoutInflater.from(MyApp.getContext()).inflate(R.layout.security_password_status_bar1, null));
        password_ad_native = (LinearLayout) v.findViewWithTag("password_ad_native");
        AndroidSdk.loadFullAd("start_full", null);
        try {
            createAdView((ViewGroup) v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler = new Handler();
//        password_pre = (ImageView) v.findViewWithTag("password_pre");
        ad_full_fl = (FrameLayout) v.findViewWithTag("ad_full_fl");
        ad_full = (LottieAnimationView) v.findViewWithTag("ad_full");
        password_main_ad = (LottieAnimationView) v.findViewWithTag("password_main_ad");
        password_main_ad.setAnimation("ad.json");
        password_main_ad.setScale(0.7f);//相对原大小的0.2倍
        password_main_ad.setSpeed(0.5f);
        password_main_ad.loop(true);
        password_main_ad.playAnimation();
//        fingerprint = (LottieAnimationView) v.findViewWithTag("fingerprint");
//        fingerprint.setAnimation("fingerprint.json");
//        fingerprint.setScale(0.07f);//相对原大小的0.2倍
//        fingerprint.loop(true);
//        fingerprint.playAnimation();

        ISecurityBridge bridge = TheBridgeVac.bridge;
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
//                password_main_ad.cancelAnimation();
                showFullAnimator();
            }
        });
//        password_pre.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), VacPrevance.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                v.getContext().startActivity(intent);
//            }
//        });
        try {
            String flurryString = AndroidSdk.getExtraData();
            JSONObject baseJson = new JSONObject(flurryString);
            show_fingerprint = baseJson.getInt("show_fingerprint");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        if (show_fingerprint == 1) {
//            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(MyApp.getContext());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                if (!managerCompat.isHardwareDetected()) { //判断设备是否支持
////                    fingerprint.setVisibility(View.GONE);
//                } else if (!managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
////                    fingerprint.setVisibility(View.GONE);
//                } else if (!VacPref.getFingerprintl()) {
////                    fingerprint.setVisibility(View.GONE);
//                }
//            } else {
////                fingerprint.setVisibility(View.GONE);
//            }
//        } else {
////            fingerprint.setVisibility(View.GONE);
//        }
        if (MyApp.getContext().getResources().getString(R.string.app_name) != null || MyApp.getContext().getResources().getString(R.string.app_name).isEmpty()) {
            Log.e("chfq", "====MyApp====");
            if ((MyApp.getContext().getResources().getString(R.string.app_name)).equals(bridge.appName())) {
                switch (PretentPresenter.pretentIconIdx()) {
                    case 1:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.security_myfake_1));
                        break;
                    case 2:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.fakes_files));
                        break;
                    case 3:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.fakes_email));
                        break;
                    case 4:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.fakes_camera));
                        break;
                    case 5:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.fakes_compass));
                        break;
                    case 6:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.fakes_music));
                        break;
                    case 7:
                        icon.setBackgroundDrawable(MyApp.getContrext().getResources().getDrawable(R.drawable.security_myfake_2));
                        break;
                }
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
        ad_full.setScale(2.5f);//相对原大小的0.2倍
        ad_full.setSpeed(0.7f);
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
//        if (fingerprint != null) {
//            fingerprint.cancelAnimation();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onPause) {
            onPause = false;
            if (password_main_ad != null && !password_main_ad.isAnimating()) {
                password_main_ad.playAnimation();
            }
//            if (fingerprint != null) {
//                fingerprint.playAnimation();
//            }
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
//        if (fingerprint != null) {
//            fingerprint.cancelAnimation();
//        }
        if (animator != null) {
            animator.cancel();
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
                MyApp.getWatcher().watch(adView);
                password_ad_native.addView(adView);
                animator = ObjectAnimator.ofFloat(password_ad_native, "scaleX", 1f, 1.1f, 1f);
                animator.setRepeatCount(2);
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(2000);
                animator.start();
            }
        }
    }

    public static VacFrameLayout inflate(String layoutId, ViewGroup container, Context c) {
        Context themeContext = null;
        try {

            themeContext = ShopMaster.currentTheme().getThemeContext();
        } catch (Exception e) {
            themeContext = c;
        }
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        Log.e("haha", "pkg " + themeContext.getPackageName());
        int layout = themeContext.getResources().getIdentifier(layoutId, "layout", themeContext.getPackageName());
        VacFrameLayout v = (VacFrameLayout) inflater.inflate(layout, container, false);
        return v;
    }

    private static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = MyApp.getContrext().getPackageName() + "/" + VacAccessibilityService.class.getCanonicalName();
//        Log.e("chfq", "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.VacAccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
