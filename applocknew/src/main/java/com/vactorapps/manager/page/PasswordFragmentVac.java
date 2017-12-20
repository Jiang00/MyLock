package com.vactorapps.manager.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.android.common.SdkCache;
import com.ivymobi.applock.free.R;
import com.vactorapps.lib.customview.SecurityDotImage;
import com.vactorapps.manager.MyApp;
import com.vactorapps.manager.Tools;
import com.vactorapps.manager.Tracker;
import com.vactorapps.manager.meta.TheBridgeVac;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.myinterface.ISecurityBridge;

/**
 * Created by huale on 2014/11/19.
 */


public class PasswordFragmentVac extends VacThemeFragment {


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
        final ISecurityBridge bridge = TheBridgeVac.bridge;
        inflater = TheBridgeVac.themeContext == null ? inflater : LayoutInflater.from(TheBridgeVac.themeContext);
        View passwordView = inflate("whitelist_security_number_password", container, inflater.getContext());
        ((VacFrameLayout) passwordView).setOverflowCtrl(ctrl);
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
        ViewStub forbidden = new ViewStub(MyApp.getContext(), R.layout.security_myforbidden);
        ErrorBiddenView errorBiddenView = new ErrorBiddenView(forbidden);
        dot.setErrorBiddenView(errorBiddenView);
        ((VacFrameLayout) passwordView).addView(forbidden);
        errorBiddenView.init();


        try {
            Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__LOCK_PAGE_PKG, TheBridgeVac.bridge.currentPkg().toString() + "", 1);
            SecurityDotImage mydly = (SecurityDotImage) passwordView.findViewWithTag("dly");
            mydly.setImageDrawable(MyApp.getContext().getResources().getDrawable(R.drawable.security_icon_daily));
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
            SecurityDotImage dlyp = (SecurityDotImage) passwordView.findViewWithTag("icon_persistent");
//            Picasso.with(MyApp.getContext()).load(VacPref.getDailyUrl()).into(dlyp);
//            Utility.loadImg(MyApp.getContext(),VacPref.getDailyUrl(),dlyp,MyApp.getContext().getResources().getIdentifier("security_icon_daily", "drawable", MyApp.getContext().getPackageName()));
            String dailyUrl = VacPref.getDailyUrl();
            Bitmap bitmap = SdkCache.cache().readBitmap(dailyUrl, null, true);
            if (bitmap == null) {
                dlyp.setImageDrawable(MyApp.getContext().getResources().getDrawable(R.drawable.security_icon_theme));
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

//        passwordView.findViewWithTag("setting_advance").setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//
//                    ISecurityBridge bridge = TheBridgeVac.bridge;
//                    if (Utility.isGrantedAllPermission(MyApp.getContext())) {
//                        if (bridge != null) {
//                            if (bridge.currentPkg().equals(MyApp.getContext().getPackageName())) {
//                                Intent intent = new Intent(MyApp.getContext(), VacUnlockSettings.class);
//                                intent.putExtra("lock_setting", true);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                MyApp.getContext().startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(MyApp.getContext(), VacUnlockSettings.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                MyApp.getContext().startActivity(intent);
//                            }
//
//                        }
//                    } else {
//                        if (bridge != null) {
//                            if (bridge.currentPkg().equals(MyApp.getContext().getPackageName())) {
//                                Utility.goPermissionCenter(MyApp.getContext(), "ivy.intent.action.pattern");
//                            } else {
//                                Utility.goPermissionCenter(MyApp.getContext(), "");
//
//                            }
//                        } else {
//                            Utility.goPermissionCenter(MyApp.getContext(), "");
//
//                        }
//                    }
//                    callback.unLock();
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
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
                int length = v.getTag().toString().length();
                dot.setNumber(v.getTag().toString().charAt(length - 1));
            }
        };
        for (String btn : buttons) {
            passwordView.findViewWithTag(btn).setOnClickListener(clickListener);
        }

        passwordView.setOnClickListener(ctrl.hideOverflow);
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
