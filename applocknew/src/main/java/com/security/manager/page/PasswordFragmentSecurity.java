package com.security.manager.page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.fingerprint.FingerUtil;
import com.privacy.lock.R;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.security.manager.App;

import com.security.manager.SecurityAppLock;
import com.security.manager.SecurityPatternActivity;
import com.security.manager.SecuritySettingsAdvance;
import com.security.manager.Tools;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.myinterface.ISecurityBridge;

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
        final View v = inflater.inflate(R.layout.security_number_password, container, false);
        ((MyFrameLayout) v).setOverflowCtrl(ctrl);
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

        ErrorBiddenView errorBiddenView=new ErrorBiddenView(forbidden);
        dot.setErrorBiddenView(errorBiddenView);
        ((MyFrameLayout) v).addView(forbidden);
        errorBiddenView.init();

        v.findViewById(R.id.setting_advance).setOnClickListener(new View.OnClickListener() {
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
                        } else {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (SecurityMyPref.getFingerPrint()) {

            FingerUtil fingerPrint = new FingerUtil();
            fingerPrint.init(v.getContext());
            boolean haveFinger = false;
            try {
                haveFinger = fingerPrint.checkhasFingerPrint();
            } catch (SsdkUnsupportedException e) {
                e.printStackTrace();
            }
            if (fingerPrint.isFeatureEnabled_fingerprint && haveFinger) {

                final ImageView finger = (ImageView) v.findViewById(R.id.use_pass_finger);
                final LinearLayout fingerpatternview = (LinearLayout) v.findViewById(R.id.numpad);
                final TextView userpassword = (TextView) v.findViewById(R.id.finger_user_number);
                finger.setVisibility(View.VISIBLE);
                fingerpatternview.setVisibility(View.GONE);
                userpassword.setVisibility(View.VISIBLE);
                userpassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finger.setVisibility(View.GONE);
                        fingerpatternview.setVisibility(View.VISIBLE);
                        userpassword.setVisibility(View.GONE);
                    }
                });


                fingerPrint.setListener(new FingerUtil.onFingerPrintCompletedListener() {
                                            @Override
                                            public void AfterUnlock() {
                                                try {
                                                    finger.setBackgroundResource(R.drawable.security_finger_right);
                                                    new Thread().sleep(250);
                                                    ISecurityBridge bridge = SecurityTheBridge.bridge;
                                                    String currentApp = bridge.currentPkg();
                                                    if (App.getContext().getPackageName().equals(currentApp)) {
                                                        Intent intent = new Intent(App.getContext(), SecurityAppLock.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        App.getContext().startActivity(intent);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                if (callback != null) {
                                                    ((SecurityPatternActivity) v.getContext()).unlockSuccess(false);
                                                    callback.onSuccess();

                                                }
                                            }

                                            @Override
                                            public void unlockFailed() {
//                                            fingerPrint.cancelIdentify();


                                                finger.setBackgroundResource(R.drawable.security_finger_wrong);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // TODO Auto-generated method stub
                                                        finger.setBackgroundResource(R.drawable.security_fingerprint);
                                                    }
                                                }, 250);
                                            }
                                        }
                );
                fingerPrint.startFingerprint();
            }
        }


        v.findViewById(R.id.use_pattern).setVisibility(View.GONE);


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
        for (int btn : buttons) {
            v.findViewById(btn).setOnClickListener(clickListener);
        }

        v.setOnClickListener(ctrl.hideOverflow);

        v.findViewById(R.id.number_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                App.getContext().startActivity(intent);
//                callback.unLock();
            }
        });


        return v;
    }

    @Override
    public void onDestroyView() {
        if (passwd != null) {
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
        if (passwd != null) {
            ((NumberDot) passwd.findViewById(R.id.passwd_dot_id)).reset();
        }
    }
}
