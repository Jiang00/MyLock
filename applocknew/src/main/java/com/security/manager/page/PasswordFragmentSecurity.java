package com.security.manager.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;


import com.privacy.lock.R;
import com.security.manager.App;

import com.security.manager.SecuritySettingsAdvance;
import com.security.manager.Tools;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;
import com.security.manager.meta.SecurityCusTheme;

import butterknife.InjectView;

/**
 * Created by huale on 2014/11/19.
 */



public class PasswordFragmentSecurity extends SecurityThemeFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return passwd = getView(inflater, container, ctrl, new ICheckResult() {
            @Override
            public void onSuccess() {
                getActivity().finish();
            }

            @Override
            public void unLock() {

            }
        });
    }

    public View passwd;
    public static View getView(LayoutInflater inflater, ViewGroup container, OverflowCtrl ctrl, final ICheckResult callback) {
        final ISecurityBridge bridge = SecurityTheBridge.bridge;
        inflater = SecurityTheBridge.themeContext == null ? inflater : LayoutInflater.from(SecurityTheBridge.themeContext);
        View v = inflater.inflate(R.layout.security_number_password, container, false);
        ((MyFrameLayout) v).setOverflowCtrl(ctrl);

        if (App.getSharedPreferences().getString("theme", "").equals("custom")) {
            Bitmap bitmap = SecurityCusTheme.getBitmap();
            if (bitmap != null)
            {
                v.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }     }

        final NumberDot dot = (NumberDot) v.findViewById(R.id.passwd_dot_id);
        dot.init(new NumberDot.ICheckListener() {
            @Override
            public void match(String passwd) {
                if (bridge.check(passwd, true)) {
                    if (callback != null) {
                        callback.onSuccess();
                    }
                }
            }
        });
        dot.reset();
        ViewStub forbidden = new ViewStub(App.getContext(), R.layout.security_myforbidden);
        dot.errorBiddenView = new ErrorBiddenView(forbidden);
        dot.errorBiddenView.init();
        ((MyFrameLayout) v).addView(forbidden);

        v.findViewById(R.id.passwd_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ISecurityBridge bridge = SecurityTheBridge.bridge;
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
                }            }
        });

        v.findViewById(R.id.use_pattern).setVisibility(View.GONE);
        /*
        View usep = v.findViewById(R.id.use_pattern);
        if (bridge.hasPattern()){
            usep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bridge.toggle(false);
                }
            });
        } else {
            usep.setVisibility(View.INVISIBLE);
        }
        */

        v.findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dot.backSpace();
            }
        });
        int[] buttons = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
        };
        Tools.RandomNumpad(bridge, v, buttons);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dot.setNumber(((Button) v).getText().charAt(0));
            }
        };
        for(int btn : buttons){
            v.findViewById(btn).setOnClickListener(clickListener);
        }

        v.setOnClickListener(ctrl.hideOverflow);

        v.findViewById(R.id.setting_advance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(intent);
                callback.unLock();
            }
        });


        return v;
    }

    @Override
    public void onDestroyView() {
        if (passwd != null){
            ViewGroup group = (ViewGroup) getView();
            if (group != null) {
                group.removeView(passwd);
            }
            passwd = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (passwd != null){
            ((NumberDot)passwd.findViewById(R.id.passwd_dot_id)).reset();
        }
    }
}
