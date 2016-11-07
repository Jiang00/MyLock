package com.privacy.lock.view;

import android.content.res.Configuration;
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
import com.security.manager.lib.Utils;
import com.privacy.lock.R;
import com.privacy.lock.intf.IThemeBridge;
import com.privacy.lock.meta.ThemeBridge;

/**
 * Created by huale on 2014/11/20.
 */
public class ThemeFragment extends Fragment {
    public static final String TAG_UNLOCK = "unlock";
    public static final String TAG_TLEF_AD = "Leftmenu";
    public static final String TAG_TOP_AD = "TopLocklist";

    public interface ICheckResult {
        void onSuccess();
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
//        if (!Pref.isAdsBlocked()) {
        createAdView((ViewGroup) view);
//        }
//        setupOverflow(view, ctrl);
//        setupDaily(view);
    }

    public static void setupTitle(View v) {
        IThemeBridge bridge = ThemeBridge.bridge;


        TextView appName = new TextView(v.getContext());
        appName.setTag("realAppName");
        appName.setText(bridge.appName());

        Log.e("mttname", bridge.appName() + "---");
        appName.setTextSize(21);
        appName.setTextColor(0xffffffff);
        appName.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Utils.getDimens(v.getContext(), 48));
        lp.leftMargin = Utils.getDimens(v.getContext(), 8);
        ((ViewGroup) v).addView(appName, lp);
        appName.setAlpha(0);

        TextView viewById = (TextView) v.findViewById(R.id.app_name);
        TextView appname =(TextView) v.findViewById(R.id.text_appname);
        appname.setText(bridge.appName());
        viewById.setText(bridge.res().getString(bridge.resId("app_name", "string")));
        /*
        lp = (RelativeLayout.LayoutParams) viewById.getLayoutParams();
        lp.leftMargin = Utils.getDimens(v.getContext(), 8);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, 0);// same as removeRule
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        viewById.setLayoutParams(lp);
        */

        ImageView icon = (ImageView) v.findViewById(R.id.title);
        icon.setBackgroundDrawable(bridge.icon());
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

//        if (AndroidSdk.hasNativeAd(TAG_UNLOCK, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//            Point size = Utils.getScreenSize(view.getContext());
//            if (size.y < 854) {
//                layoutParams.topMargin = Utils.getDimens(view.getContext(), 32);
//            } else {
//                layoutParams.topMargin = Utils.getDimens(view.getContext(), 48);
//            }
////            final TextView realAppName = (TextView) view.findViewWithTag("realAppName");
//////            final TextView appName = (TextView) view.findViewById(R.id.app_name);
////            final ImageView icon = (ImageView) view.findViewById(R.id.title);
////            realAppName.setAlpha(1.0f);
////            appName.setAlpha(0.0f);
//
//
//            View scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_UNLOCK, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.security_native_layout, new ClientNativeAd.NativeAdClickListener() {
//                @Override
//                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {
//
//                }
//            }, new ClientNativeAd.NativeAdScrollListener() {
//                @Override
//                public void onNativeAdScrolled(float v) {
////                    icon.setAlpha(1 - v);
////                    appName.setAlpha(1 - v);
////                    realAppName.setAlpha(v);
//                }
//            });
//            if (scrollView != null) {
//                App.getWatcher().watch(scrollView);
//                view.addView(scrollView, layoutParams);
//            }
//        }


    }


}
