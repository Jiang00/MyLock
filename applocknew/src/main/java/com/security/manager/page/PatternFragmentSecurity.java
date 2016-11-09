package com.security.manager.page;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.security.manager.App;
import com.privacy.lock.R;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;
import com.security.manager.meta.SecurityCusTheme;

import java.util.List;

/**
 * Created by huale on 2014/11/21.
 */
public class PatternFragmentSecurity extends SecurityThemeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        } else {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }
        return pattern = getView(inflater, container, ctrl, new ICheckResult() {
            @Override
            public void onSuccess() {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();

        if (pattern != null) {
            ((SecurityPatternView) pattern.findViewById(R.id.lpv_lock)).resetPattern();
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
        View v = inflater.inflate(R.layout.security_pattern_view, container, false);
        ((MyFrameLayout) v).setOverflowCtrl(ctrl);

        if (App.getSharedPreferences().getString("theme", "").equals("custom")) {
            Bitmap bitmap = SecurityCusTheme.getBitmap();
            if (bitmap != null) {
                v.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }


        final ISecurityBridge bridge = SecurityTheBridge.bridge;
        final SecurityPatternView lock = (SecurityPatternView) v.findViewById(R.id.lpv_lock);
        LinearLayout parent = (LinearLayout) lock.getParent();
        ((LinearLayout.LayoutParams) parent.getLayoutParams()).weight = 1.5f;
        parent.requestLayout();

        v.findViewById(R.id.passwd_cancel).setVisibility(View.GONE);
        /*
        View cancel = v.findViewById(R.id.passwd_cancel);
        if (bridge.hasPasswd()){
            cancel.setBackgroundDrawable(null);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bridge.toggle(true);
                }
            });
        } else {
            cancel.setVisibility(View.INVISIBLE);
        }
        */

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

    private void setTranslucentStatus(boolean on) {
        Window win = getActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
