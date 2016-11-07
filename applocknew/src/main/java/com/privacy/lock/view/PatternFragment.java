package com.privacy.lock.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.privacy.lock.App;
import com.privacy.lock.R;
import com.privacy.lock.intf.IThemeBridge;
import com.privacy.lock.meta.CustomTheme;
import com.privacy.lock.meta.ThemeBridge;

import java.util.List;

/**
 * Created by huale on 2014/11/21.
 */
public class PatternFragment extends ThemeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if (pattern != null){
            ((LockPatternView) pattern.findViewById(R.id.lpv_lock)).resetPattern();
        }
    }

    @Override
    public void onDestroyView() {
        if (pattern != null){
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
        inflater = ThemeBridge.themeContext == null ? inflater : LayoutInflater.from(ThemeBridge.themeContext);
        View v = inflater.inflate(R.layout.security_pattern_view, container, false);
        ((MyFrameLayout) v).setOverflowCtrl(ctrl);

        if (App.getSharedPreferences().getString("theme", "").equals("custom")) {
            Bitmap bitmap = CustomTheme.getBitmap();
            if (bitmap != null) {
                v.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
        }

        final IThemeBridge bridge = ThemeBridge.bridge;
        final LockPatternView lock = (LockPatternView) v.findViewById(R.id.lpv_lock);
        LinearLayout parent = (LinearLayout) lock.getParent();
        ((LinearLayout.LayoutParams)parent.getLayoutParams()).weight = 1.5f;
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
        final ForbiddenView forbiddenView = new ForbiddenView(forbidden);
        forbiddenView.init();

        lock.setOnPatternListener(new LockPatternView.OnPatternListener() {

            public void onPatternStart() {

            }

            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                if (!bridge.check(LockPatternUtils.patternToString(pattern), false)){
                    forbiddenView.wrong();
                    lock.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lock.clearPattern();
                        }
                    }, 500);
                } else {
                    forbiddenView.right();
                    callback.onSuccess();
                }
            }

            public void onPatternCleared() {
            }

            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }
        });
        lock.clearPattern();
        v.setOnClickListener(ctrl.hideOverflow);

        return v;
    }
}
