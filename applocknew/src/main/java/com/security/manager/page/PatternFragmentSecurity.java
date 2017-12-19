package com.security.manager.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.android.common.SdkCache;
import com.android.theme.internal.data.Theme;
import com.ivy.ivyshop.ShopMaster;
import com.ivymobi.applock.free.R;
import com.security.lib.customview.SecurityDotImage;
import com.security.manager.App;
import com.security.manager.Tracker;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by huale on 2014/11/21.
 */
public class PatternFragmentSecurity extends SecurityThemeFragment {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return pattern = getView(inflater, container, ctrl, new ICheckResult() {
            @Override
            public void onSuccess() {
                getActivity().finish();
            }

            @Override
            public void unLock() {
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (pattern != null) {
            ((SecurityPatternView) pattern.findViewWithTag("lpv_lock")).resetPattern();
        }
    }

    @Override
    public void onDestroyView() {
        if (pattern != null) {
            ViewGroup group = (ViewGroup) getView();
            if (group != null) {
                group.removeView(pattern);
            }
            pattern = null;
        }
        super.onDestroyView();
    }

    View pattern;

    public static View getView(LayoutInflater inflater, final ViewGroup container, OverflowCtrl ctrl, final ICheckResult callback) {
        inflater = SecurityTheBridge.themeContext == null ? inflater : LayoutInflater.from(SecurityTheBridge.themeContext);

        final View patternView = inflate("whitelist_security_pattern_view", container, inflater.getContext());
        ((MyFrameLayout) patternView).setOverflowCtrl(ctrl);

        final ISecurityBridge bridge = SecurityTheBridge.bridge;
        try {
            Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_PAGE_PKG, SecurityTheBridge.bridge.currentPkg().toString() + "", 1);
            SecurityDotImage mydly = (SecurityDotImage) patternView.findViewWithTag("dly");
            mydly.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.security_icon_daily));
            mydly.setVisibility(View.VISIBLE);
            mydly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("ivy.intent.action.full");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.getContext().startActivity(intent);
                }
            });
            SecurityDotImage dlyp = (SecurityDotImage) patternView.findViewWithTag("icon_persistent");
//            Picasso.with(App.getContext()).load(SecurityMyPref.getDailyUrl()).into(dlyp);
//            Utility.loadImg(App.getContext(),SecurityMyPref.getDailyUrl(),dlyp,App.getContext().getResources().getIdentifier("security_icon_daily", "drawable", App.getContext().getPackageName()));
            String dailyUrl = SecurityMyPref.getDailyUrl();
            Log.e("myurl",dailyUrl+"------2");
            Bitmap bitmap = SdkCache.cache().readBitmap(dailyUrl, null, true);
            if (bitmap == null) {
                dlyp.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.security_icon_theme));
                SdkCache.cache().cacheUrl(dailyUrl, true);
            } else {
                dlyp.setImageBitmap(bitmap);
            }
            dlyp.setVisibility(View.VISIBLE);
            dlyp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShopMaster.launch(App.getContext(),
                            new Theme(R.raw.theme_preview, App.getContext().getPackageName()),
                            new Theme(R.raw.theme_preview_two, "theme_preview_two"));
                    callback.unLock();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        final SecurityPatternView lock = (SecurityPatternView) patternView.findViewWithTag("lpv_lock");
        LinearLayout parent = (LinearLayout) lock.getParent();
        ((LinearLayout.LayoutParams) parent.getLayoutParams()).weight = 1.5f;
        parent.requestLayout();

        final ViewStub forbidden = new ViewStub(App.getContext(), R.layout.security_myforbidden);
        ((MyFrameLayout) patternView).addView(forbidden);
        final ErrorBiddenView errorBiddenView = new ErrorBiddenView(forbidden);
        errorBiddenView.init();

        lock.setOnPatternListener(new SecurityPatternView.OnPatternListener() {
            public void onPatternStart() {
            }

            public void onPatternDetected(List<SecurityPatternView.Cell> pattern) {
                if (!bridge.check(LockPatternUtils.patternToString(pattern), false)) {
                    errorBiddenView.wrong();
                    lock.setDisplayMode(SecurityPatternView.DisplayMode.Wrong);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lock.clearPattern();
                        }
                    }, 500);
                } else {
                    errorBiddenView.right();
                    callback.onSuccess();
                    Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_SUSSFUL, Tracker.CATE_ACTION__LOCK_SUSSFUL, 1);
                }
            }

            public void onPatternCleared() {
            }

            public void onPatternCellAdded(List<SecurityPatternView.Cell> pattern) {
            }
        });


        lock.clearPattern();
        patternView.setOnClickListener(ctrl.hideOverflow);


        return patternView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.security_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
