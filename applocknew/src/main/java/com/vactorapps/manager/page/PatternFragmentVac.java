package com.vactorapps.manager.page;

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
import com.ivymobi.applock.free.R;
import com.vactorapps.lib.customview.SecurityDotImage;
import com.vactorapps.manager.MyApp;
import com.vactorapps.manager.Tracker;
import com.vactorapps.manager.meta.TheBridgeVac;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.myinterface.ISecurityBridge;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by huale on 2014/11/21.
 */
public class PatternFragmentVac extends VacThemeFragment {

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
            ((PatternViewVac) pattern.findViewWithTag("lpv_lock")).resetPattern();
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
        inflater = TheBridgeVac.themeContext == null ? inflater : LayoutInflater.from(TheBridgeVac.themeContext);

        final View patternView = inflate("whitelist_security_pattern_view", container, inflater.getContext());
        ((VacFrameLayout) patternView).setOverflowCtrl(ctrl);

        final ISecurityBridge bridge = TheBridgeVac.bridge;
        try {
            Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_PAGE_PKG, TheBridgeVac.bridge.currentPkg().toString() + "", 1);
            SecurityDotImage mydly = (SecurityDotImage) patternView.findViewWithTag("dly");
//            mydly.setImageDrawable(MyApp.getContext().getResources().getDrawable(R.drawable.security_icon_daily));
            mydly.setVisibility(View.VISIBLE);
            mydly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction("ivy.intent.action.full");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApp.getContext().startActivity(intent);
                }
            });
            SecurityDotImage dlyp = (SecurityDotImage) patternView.findViewWithTag("icon_persistent");
//            Picasso.with(MyApp.getContext()).load(VacPref.getDailyUrl()).into(dlyp);
//            Utility.loadImg(MyApp.getContext(),VacPref.getDailyUrl(),dlyp,MyApp.getContext().getResources().getIdentifier("security_icon_daily", "drawable", MyApp.getContext().getPackageName()));
            String dailyUrl = VacPref.getDailyUrl();
            Log.e("myurl",dailyUrl+"------2");
            Bitmap bitmap = SdkCache.cache().readBitmap(dailyUrl, null, true);
            if (bitmap == null) {
//                dlyp.setImageDrawable(MyApp.getContext().getResources().getDrawable(R.drawable.security_icon_theme));
                SdkCache.cache().cacheUrl(dailyUrl, true);
            } else {
                dlyp.setImageBitmap(bitmap);
            }
            dlyp.setVisibility(View.VISIBLE);
            dlyp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ShopMaster.launch(MyApp.getContext(),
//                            new Theme(R.raw.theme_preview, MyApp.getContext().getPackageName()),
//                            new Theme(R.raw.theme_preview_two, "theme_preview_two"));
                    callback.unLock();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        final PatternViewVac lock = (PatternViewVac) patternView.findViewWithTag("lpv_lock");
        LinearLayout parent = (LinearLayout) lock.getParent();
        ((LinearLayout.LayoutParams) parent.getLayoutParams()).weight = 1.5f;
        parent.requestLayout();

        final ViewStub forbidden = new ViewStub(MyApp.getContext(), R.layout.security_myforbidden);
        ((VacFrameLayout) patternView).addView(forbidden);
        final ErrorBiddenView errorBiddenView = new ErrorBiddenView(forbidden);
        errorBiddenView.init();

        lock.setOnPatternListener(new PatternViewVac.OnPatternListener() {
            public void onPatternStart() {
            }

            public void onPatternDetected(List<PatternViewVac.Cell> pattern) {
                if (!bridge.check(LockPatternUtils.patternToString(pattern), false)) {
                    errorBiddenView.wrong();
                    lock.setDisplayMode(PatternViewVac.DisplayMode.Wrong);
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

            public void onPatternCellAdded(List<PatternViewVac.Cell> pattern) {
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
