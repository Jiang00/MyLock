package com.security.manager.page;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;

//import com.android.client.AndroidSdk;
//import com.android.client.ClientNativeAd;
import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.android.launcher3.theme.ThemeManager;
import com.privacy.lock.R;
import com.security.manager.App;
import com.security.manager.Tracker;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.lib.Utils;

import com.security.manager.myinterface.ISecurityBridge;

/**
 * Created by huale on 2014/11/20.
 */
public class SecurityThemeFragment extends Fragment {
    public static final String TAG_UNLOCK = "unlock";
    public static final String TAG_TLEF_AD = "Leftmenu";
    public static final String TAG_TOP_AD = "TopLocklist";
    public static View adView = null;


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
        try {
            createAdView((ViewGroup) view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupTitle(view);
    }


    public static void setupTitle(View v) {
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
        ImageView icon = (ImageView) v.findViewWithTag("title");
        ImageView statusicon = (ImageView) v.findViewWithTag("app_icon");
        appname.setText(bridge.appName());
        icon.setBackgroundDrawable(bridge.icon());


        if (adView != null) {
            icon.setVisibility(View.GONE);
            appname.setVisibility(View.GONE);
            statusicon.setBackgroundDrawable(bridge.icon());
        }
    }

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

    @Override
    public void onDestroyView() {
        ctrl.hideOverflow = null;
        if (ctrl.overflowStub != null) {
            ctrl.overflowStub.removeAllViews();
        }
        ctrl.overflowStub = null;
        ctrl.ovf = null;
        ctrl = null;
        ViewGroup group = (ViewGroup) getView();
        if (group != null) {
            group.removeAllViews();
        }
        super.onDestroyView();
    }

    protected static void createAdView(ViewGroup view) {
        if (AndroidSdk.hasNativeAd(TAG_UNLOCK, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            Point size = Utils.getScreenSize(view.getContext());
            if (size.y < 854) {
                layoutParams.topMargin = Utils.getDimens(view.getContext(), 32);
            } else {
                layoutParams.topMargin = Utils.getDimens(view.getContext(), 48);
            }
//            final TextView realAppName = (TextView) view.findViewWithTag("realAppName");
////            final TextView appName = (TextView) view.findViewById(R.id.app_name);
//            final ImageView icon = (ImageView) view.findViewById(R.id.title);
//            realAppName.setAlpha(1.0f);
//            appName.setAlpha(0.0f);

            adView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_UNLOCK, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.security_native_layout, new ClientNativeAd.NativeAdClickListener() {
                @Override
                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {

                }
            }, new ClientNativeAd.NativeAdScrollListener() {
                @Override
                public void onNativeAdScrolled(float v) {
//                    icon.setAlpha(1 - v);
//                    appName.setAlpha(1 - v);
//                    realAppName.setAlpha(v);
                }
            });
            if (adView != null) {
                App.getWatcher().watch(adView);
                view.addView(adView, layoutParams);
            }
        }

    }


    public static MyFrameLayout inflate(String layoutId, ViewGroup container, Context c) {
        Context themeContext = ThemeManager.currentTheme().getThemeContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        int layout = themeContext.getResources().getIdentifier(layoutId, "layout", themeContext.getPackageName());
        MyFrameLayout v = (MyFrameLayout) inflater.inflate(layout, container, false);

//        ViewStub forbidden = new ViewStub(ApplicationModule.getModule().provideContext(), R.layout.forbidden);
//        v.addView(forbidden);
//        final ForbiddenView forbiddenView = new ForbiddenView(forbidden);
//        v.setTag(forbiddenView);
//        v.post(new Runnable() {
//            @Override
//            public void run() {
//                ApplicationModule.getModule().provideHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        forbiddenView.init();
//                    }
//                });
//            }
//        });
        return v;
    }


}
