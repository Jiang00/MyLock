package com.security.manager.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;


import com.android.common.SdkCache;
import com.android.theme.internal.data.Theme;
import com.ivy.ivyshop.ShopMaster;
import com.ivy.util.Utility;
import com.ivymobi.applock.free.R;
import com.security.lib.customview.SecurityDotImage;
import com.security.manager.App;
import com.security.manager.SecurityUnlockSettings;
import com.security.manager.Tools;
import com.security.manager.Tracker;
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
        View passwordView = inflate("security_number_password", container, inflater.getContext());
        ((MyFrameLayout) passwordView).setOverflowCtrl(ctrl);
        final NumberDot dot = (NumberDot) passwordView.findViewWithTag("passwd_dot_id");
        dot.init(new NumberDot.ICheckListener() {
            @Override
            public void match(String passwd) {
                if (bridge.check(passwd, true)) {
                    if (callback != null) {
                        callback.onSuccess();
                        Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_SUSSFUL, Tracker.CATE_ACTION__LOCK_SUSSFUL, 1);

                    }
                }
            }
        });
        dot.reset();
        ViewStub forbidden = new ViewStub(App.getContext(), R.layout.security_myforbidden);
        ErrorBiddenView errorBiddenView = new ErrorBiddenView(forbidden);
        dot.setErrorBiddenView(errorBiddenView);
        ((MyFrameLayout) passwordView).addView(forbidden);
        errorBiddenView.init();


        try {
            Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_PAGE_PKG, SecurityTheBridge.bridge.currentPkg().toString() + "", 1);
            SecurityDotImage mydly = (SecurityDotImage) passwordView.findViewWithTag("dly");
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
            SecurityDotImage dlyp = (SecurityDotImage) passwordView.findViewWithTag("icon_persistent");
//            Picasso.with(App.getContext()).load(SecurityMyPref.getDailyUrl()).into(dlyp);
//            Utility.loadImg(App.getContext(),SecurityMyPref.getDailyUrl(),dlyp,App.getContext().getResources().getIdentifier("security_icon_daily", "drawable", App.getContext().getPackageName()));
            String dailyUrl = SecurityMyPref.getDailyUrl();
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

        passwordView.findViewWithTag("setting_advance").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    ISecurityBridge bridge = SecurityTheBridge.bridge;
                    if (Utility.isGrantedAllPermission(App.getContext())) {
                        if (bridge != null) {
                            if (bridge.currentPkg().equals(App.getContext().getPackageName())) {
                                Intent intent = new Intent(App.getContext(), SecurityUnlockSettings.class);
                                intent.putExtra("lock_setting", true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getContext().startActivity(intent);
                            } else {
                                Intent intent = new Intent(App.getContext(), SecurityUnlockSettings.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.getContext().startActivity(intent);
                            }

                        }
                    } else {
                        if (bridge != null) {
                            if (bridge.currentPkg().equals(App.getContext().getPackageName())) {
                                Utility.goPermissionCenter(App.getContext(), "ivy.intent.action.pattern");
                            } else {
                                Utility.goPermissionCenter(App.getContext(), "");

                            }
                        } else {
                            Utility.goPermissionCenter(App.getContext(), "");

                        }
                    }
                    callback.unLock();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        passwordView.findViewWithTag("use_pattern").setVisibility(View.GONE);
        passwordView.findViewWithTag("backspace").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dot.backSpace();
            }
        });
        String[] buttons = {
                "button0", "button1", "button2", "button3", "button4",
                "button5", "button6", "button7", "button8", "button9",
        };
        Tools.RandomNumpad(bridge, passwordView, buttons);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dot.setNumber(((Button) v).getText().charAt(0));
            }
        };
        for (String btn : buttons) {
            passwordView.findViewWithTag(btn).setOnClickListener(clickListener);
        }

        passwordView.setOnClickListener(ctrl.hideOverflow);

        passwordView.findViewWithTag("number_cancel").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), SecuritySettingsAdvance.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                App.getContext().startActivity(intent);
//                callback.unLock();
            }
        });

        return passwordView;
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
            ((NumberDot) passwd.findViewWithTag("passwd_dot_id")).reset();
        }
    }
}
