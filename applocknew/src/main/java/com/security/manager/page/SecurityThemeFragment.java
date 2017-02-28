package com.security.manager.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.android.launcher3.theme.ThemeManager;
import com.ivy.module.huojian.CleanManager;
import com.ivy.module.huojian.Huojian;
import com.ivymobi.applock.free.R;
import com.security.manager.App;
import com.security.manager.Tracker;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.lib.Utils;

import com.security.manager.myinterface.ISecurityBridge;

import ivy.battery.cooling.CoolingActivity;
import ivy.battery.cooling.CrossTranslate;


/**
 * Created by huale on 2014/11/20.
 */
public class SecurityThemeFragment extends Fragment {
    public static final String TAG_UNLOCK = "unlock";
    public static final String TAG_LOADING = "loading";
    public static final String TAG_TLEF_AD = "Leftmenu";
    public static final String TAG_TOP_AD = "TopLocklist";
    public static View adView = null;
    static CountDownTimer mytimer = null;


    public interface ICheckResult {
        void onSuccess();

        void unLock();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctrl = new OverflowCtrl();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        afterViewCreated(view, ctrl);

    }

    public static void afterViewCreated(View view, OverflowCtrl ctrl) {
        try {
            if (!SecurityMyPref.isUseNormalPasswd()) {
                crossPromote((ViewGroup) view);
            }
            createAdView((ViewGroup) view);

        } catch (Exception e) {
            e.printStackTrace();
        }
        setupTitle(view);
    }

    public static void setupTitle(View v) {
        ISecurityBridge bridge = SecurityTheBridge.bridge;
        TextView appName = new TextView(v.getContext());
        appName.setTag("realAppName");
        appName.setText(bridge.appName());
        appName.setTextSize(21);
        appName.setTextColor(0xffffffff);
        appName.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Utils.getDimens(v.getContext(), 48));
        lp.leftMargin = Utils.getDimens(v.getContext(), 8);
        ((ViewGroup) v).addView(appName, lp);
        appName.setAlpha(0);
        TextView appname = (TextView) v.findViewWithTag("text_appname");
        ImageView icon = (ImageView) v.findViewWithTag("title");
        ImageView statusicon = (ImageView) v.findViewWithTag("app_icon");
        appname.setText(bridge.appName());
        icon.setBackgroundDrawable(bridge.icon());
        if (adView != null) {
            icon.setVisibility(View.GONE);
            appname.setVisibility(View.GONE);
            statusicon.setBackgroundDrawable(bridge.icon());
        }
    }


    public static void crossPromote(ViewGroup v) {


        boolean showCross = SecurityMyPref.getShowCross();


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        FrameLayout.LayoutParams trigonParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        Point size = Utils.getScreenSize(v.getContext());

        if (size.y < 854) {
            layoutParams.bottomMargin = Utils.getDimens(v.getContext(), 5);
        } else {
            layoutParams.bottomMargin = Utils.getDimens(v.getContext(), 10);
        }

        LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View crossView = inflater.inflate(R.layout.security_cross_promote, null);
        final View trigon = inflater.inflate(R.layout.security_trigon, null);

        final View crossBattery = crossView.findViewById(R.id.cross_battery);
        final View crossClear = crossView.findViewById(R.id.cross_clear);
        final View crossOther = crossView.findViewById(R.id.cross_flash);
        final View crossClose = crossView.findViewById(R.id.security_cross_close);
        crossBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__CROSS_ONE, Tracker.CATE_ACTION__CROSS_ONE, 1L);

                Intent intent = new Intent(v.getContext(), CoolingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

            }
        });


        crossClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__CROSS_TWO, Tracker.CATE_ACTION__CROSS_TWO, 1L);
                CleanManager.Instance().runClean(v.getContext(), new Huojian.CallbackListener() {
                    @Override
                    public void cleanSuccess(Context context, long size, boolean isXianshi) {
                        ((Activity) context).finish();
                        Intent intent = new Intent(context, CrossTranslate.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("value", "ad2");
                        context.startActivity(intent);
                    }
                });

            }
        });

        crossOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker.sendEvent(Tracker.CATE_ACTION__LOCK_PAGE, Tracker.CATE_ACTION__CROSS_THREE, Tracker.CATE_ACTION__CROSS_THREE, 1L);
                Intent intent = new Intent(v.getContext(), CrossTranslate.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("value", "ad3");
                v.getContext().startActivity(intent);

            }
        });

        crossClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_pop_exit_anim);
                crossView.setVisibility(View.GONE);
                crossView.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        trigon.setVisibility(View.VISIBLE);
                        crossBattery.setEnabled(false);
                        crossClear.setEnabled(false);
                        crossOther.setEnabled(false);
                        SecurityMyPref.setShowCross(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                });
            }
        });

        crossBattery.setEnabled(false);
        crossClear.setEnabled(false);
        crossOther.setEnabled(false);

        trigon.findViewById(R.id.trigon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trigon.setVisibility(View.GONE);
                crossView.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_pop_enter_anim);
                crossView.startAnimation(animation);
                SecurityMyPref.setShowCross(true);

            }
        });

        trigon.findViewById(R.id.trigon_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trigon.setVisibility(View.GONE);
                crossView.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_pop_enter_anim);
                crossView.startAnimation(animation);
                crossBattery.setEnabled(true);
                crossClear.setEnabled(true);
                crossOther.setEnabled(true);
                SecurityMyPref.setShowCross(true);

            }
        });

        crossView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x1 = 0;
                float x2 = 0;
                float y1 = 0;
                float y2 = 0;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                    y1 = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    x2 = event.getX();
                    y2 = event.getY();
                    if (y1 - y2 > 40) {
                        //向上滑动

                    } else if (y2 - y1 > 20) {
                        //向下滑动
//                        Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_pop_exit_anim);
//                        crossView.setVisibility(View.GONE);
//                        crossView.startAnimation(animation);
//                        animation.setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//                                trigon.setVisibility(View.VISIBLE);
//                                crossBattery.setEnabled(false);
//                                crossClear.setEnabled(false);
//                                crossOther.setEnabled(false);
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//
//                            }
//                        });
                    }
                }
                return true;
            }
        });

        trigon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x1 = 0;
                float x2 = 0;
                float y1 = 0;
                float y2 = 0;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                    y1 = event.getY();
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //向上滑动
                    x2 = event.getX();
                    y2 = event.getY();
                    if (y1 - y2 > 30) {
                        trigon.setVisibility(View.GONE);
                        crossView.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_pop_enter_anim);
                        crossView.startAnimation(animation);
                        crossBattery.setEnabled(true);
                        crossClear.setEnabled(true);
                        crossOther.setEnabled(true);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                SecurityMyPref.setShowCross(true);


                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationStart(Animation animation) {

                            }
                        });

                    }
                }


                return true;
            }
        });

        crossView.setVisibility(View.GONE);
        trigon.setVisibility(View.VISIBLE);
        v.addView(trigon, trigonParams);
        v.addView(crossView, layoutParams);


        if (showCross) {
            trigon.setVisibility(View.GONE);
            crossView.setVisibility(View.VISIBLE);
//            Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_pop_enter_anim);
//            animation.setFillAfter(true);
//            crossView.startAnimation(animation);
            crossBattery.setEnabled(true);
            crossClear.setEnabled(true);
            crossOther.setEnabled(true);
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ViewGroup group = (ViewGroup) getView();
        if (group != null) {
            group.removeAllViewsInLayout();
            View v = onCreateView(LayoutInflater.from(getActivity()), group, null);
            group.addView(v);
            onViewCreated(v, null);
        }
    }

    protected static Animation out, in;
    protected OverflowCtrl ctrl;

    @Override
    public void onDestroyView() {
        ctrl.hideOverflow = null;
        if (ctrl.overflowStub != null) {
            ctrl.overflowStub.removeAllViews();
        }
        ctrl.overflowStub = null;
        ctrl.ovf = null;
        ctrl = null;
        ViewGroup group = (ViewGroup) getView();
        if (group != null) {
            group.removeAllViews();
        }
        super.onDestroyView();
//        try {
//            AndroidSdk.destroyNativeAdView(TAG_UNLOCK, adView);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    protected static void createAdView(ViewGroup view) {
        if (AndroidSdk.hasNativeAd(TAG_UNLOCK, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            Point size = Utils.getScreenSize(view.getContext());
            if (size.y < 854) {
                layoutParams.topMargin = Utils.getDimens(view.getContext(), 32);
            } else {
                layoutParams.topMargin = Utils.getDimens(view.getContext(), 48);
            }
//            final TextView realAppName = (TextView) view.findViewWithTag("realAppName");
////            final TextView appName = (TextView) view.findViewById(R.id.app_name);
//            final ImageView icon = (ImageView) view.findViewById(R.id.title);
//            realAppName.setAlpha(1.0f);
//            appName.setAlpha(0.0f);

            adView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_UNLOCK, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.security_native_layout, new ClientNativeAd.NativeAdClickListener() {
                @Override
                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {

                }
            }, new ClientNativeAd.NativeAdScrollListener() {
                @Override
                public void onNativeAdScrolled(float v) {
//                    icon.setAlpha(1 - v);
//                    appName.setAlpha(1 - v);
//                    realAppName.setAlpha(v);
                }
            });
            if (adView != null) {
                App.getWatcher().watch(adView);
                view.addView(adView, layoutParams);
            }
        }
    }

    public static MyFrameLayout inflate(String layoutId, ViewGroup container, Context c) {
        Context themeContext = null;
        try {
            themeContext = ThemeManager.currentTheme().getThemeContext();
        } catch (Exception e) {
            themeContext = c;
        }
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        Log.e("haha", "pkg " + themeContext.getPackageName());
        int layout = themeContext.getResources().getIdentifier(layoutId, "layout", themeContext.getPackageName());
        MyFrameLayout v = (MyFrameLayout) inflater.inflate(layout, container, false);
        return v;
    }


}
