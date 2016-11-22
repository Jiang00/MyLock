package com.security.manager.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.privacy.lock.R;
import com.security.manager.App;
import com.security.manager.SecuritySettingsAdvance;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;
import com.security.manager.meta.SecurityCusTheme;

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

        v.findViewById(R.id.setting_advance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ISecurityBridge bridge = SecurityTheBridge.bridge;
                    Log.e("name",bridge.appName()+"");
                    if (bridge != null) {
                        if (bridge.appName().equals(R.string.app_name)) {
                            Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
                            intent.putExtra("launchname", bridge + "");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.getContext().startActivity(intent);
                        }else{
                            Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.getContext().startActivity(intent);

                        }


                    } else {
                        Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.getContext().startActivity(intent);

                    }


                    callback.unLock();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        final ISecurityBridge bridge = SecurityTheBridge.bridge;
        final SecurityPatternView lock = (SecurityPatternView) v.findViewById(R.id.lpv_lock);
        LinearLayout parent = (LinearLayout) lock.getParent();
        ((LinearLayout.LayoutParams) parent.getLayoutParams()).weight = 1.5f;
        parent.requestLayout();

        v.findViewById(R.id.passwd_cancel).setVisibility(View.GONE);
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.security_setting_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
