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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.client.AndroidSdk;
import com.android.common.SdkCache;
import com.android.launcher3.theme.ThemeManager;
import com.ivy.module.themestore.main.ThemeStoreBuilder;
import com.ivy.util.Constants;
import com.ivy.util.Utility;

import com.ivymobi.applock.free.R;
import com.security.lib.customview.SecurityDotImage;
import com.security.manager.App;
import com.security.manager.SecurityAppLock;
import com.security.manager.SecurityPatternActivity;
import com.security.manager.SecuritySettingsAdvance;
import com.security.manager.SecurityUnlockSettings;
import com.security.manager.Tracker;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;
import com.squareup.picasso.Picasso;

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
        View v = inflate("security_pattern_view", container, inflater.getContext());

        ((MyFrameLayout) v).setOverflowCtrl(ctrl);
        final ImageButton button = (ImageButton) v.findViewWithTag("setting_advance");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    ISecurityBridge bridge = SecurityTheBridge.bridge;
                    if (Utility.isGrantedAllPermission(App.getContext())) {
                        Log.e("name","one");
                        if (bridge != null) {
                            Log.e("name","two");
                            if (bridge.currentPkg().equals(App.getContext().getPackageName())) {
                                Intent intent = new Intent(App.getContext(), SecurityUnlockSettings.class);
                                intent.putExtra("lock_setting", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getContext().startActivity(intent);
                            } else {
                                Log.e("name","three");
                                Intent intent = new Intent(App.getContext(), SecurityUnlockSettings.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getContext().startActivity(intent);
                            }

                        }
                    } else {
                        Log.e("name","four");
                        if (bridge != null) {
                            if (bridge.currentPkg().equals(App.getContext().getPackageName())) {
                                Log.e("name","five");
                                Utility.goPermissionCenter(App.getContext(), "ivy.intent.action.pattern");
                            } else {
                                Log.e("name","six");
                                Utility.goPermissionCenter(App.getContext(), "");

                            }
                        } else {
                            Log.e("name","seven");
                            Utility.goPermissionCenter(App.getContext(), "");

                        }
                    }
                    callback.unLock();


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        final ISecurityBridge bridge = SecurityTheBridge.bridge;
        try {
            Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_PAGE_PKG, SecurityTheBridge.bridge.currentPkg().toString() + "", 1);
            SecurityDotImage dlyp = (SecurityDotImage) v.findViewWithTag("icon_persistent");
//            Picasso.with(App.getContext()).load(SecurityMyPref.getDailyUrl()).into(dlyp);
//            Utility.loadImg(App.getContext(),SecurityMyPref.getDailyUrl(),dlyp,App.getContext().getResources().getIdentifier("security_icon_daily", "drawable", App.getContext().getPackageName()));
            String dailyUrl = SecurityMyPref.getDailyUrl();
            Bitmap bitmap = SdkCache.cache().readBitmap(dailyUrl, null, true);
            if (bitmap == null) {
                dlyp.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.security_icon_daily));
                SdkCache.cache().cacheUrl(dailyUrl, true);
            } else {
                dlyp.setImageBitmap(bitmap);
            }
            dlyp.setVisibility(View.VISIBLE);
            dlyp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThemeStoreBuilder.openThemeStore(App.getContext(), "ivy.intent.action.pattern");
                    callback.unLock();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        final SecurityPatternView lock = (SecurityPatternView) v.findViewWithTag("lpv_lock");
        LinearLayout parent = (LinearLayout) lock.getParent();
        ((LinearLayout.LayoutParams) parent.getLayoutParams()).weight = 1.5f;
        parent.requestLayout();

        v.findViewWithTag("number_cancel").setVisibility(View.GONE);
        final ViewStub forbidden = new ViewStub(App.getContext(), R.layout.security_myforbidden);
        ((MyFrameLayout) v).addView(forbidden);
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
        v.setOnClickListener(ctrl.hideOverflow);


        return v;
    }


    private int getId(String id, String type) {

        return getResources().getIdentifier(id, type, getContext().getPackageName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.security_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
