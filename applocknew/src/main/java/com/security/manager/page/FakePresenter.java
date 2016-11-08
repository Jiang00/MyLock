package com.security.manager.page;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.security.lib.customview.WidgetContainer;
import com.security.manager.App;
import com.security.manager.FakeOneActivity;
import com.security.manager.FakeThreeActivity;
import com.security.manager.PatternActivity;
import com.security.manager.meta.Pref;
import com.security.manager.lib.Utils;
import com.security.manager.FakeTwoActivity;
import com.security.manager.FakeFourActivity;
import com.security.manager.FakeFiveActivity;
import com.privacy.lock.R;

/**
 * Created by song on 15/8/14.
 */
public class FakePresenter {
    public static final int FAKE_NONE = 0;
    public static final int FAKE_FC = 1;
    public static final int FAKE_SCAN = 2;
    public static final int FAKE_SCAN_SETTING = -1;

    public static final int FAKE_ICON_NORMAL = 0;
    public static final int FAKE_ICON_FAKE1 = 1;
    public static final int FAKE_ICON_FAKE2 = 2;
    public static final int FAKE_ICON_FAKE4 = 4;
    public static final int FAKE_ICON_FAKE5 = 5;
    public static final int FAKE_ICON_FAKE3 = 3;

    public static final int FAKE_ICON_COUNT = 6;

    private static WidgetContainer container;
    private static View fingerprint;
    private static Animation alpha;
    private static Animation scanLine;
    private static long lastTouchTime1 = 0;
    private static long lastTouchTime2 = 0;
    private static AlertDialog alertDialog;

    public static boolean isFakeCover() {
        return Pref.getFakeCover(FAKE_NONE) != FAKE_NONE;
    }

    public static void show(Context context, int fakeType, CharSequence label, final Runnable ok, final Runnable cancel) {
        if (container == null) {
            container = new WidgetContainer(context.getApplicationContext(), Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true);
            container.setBackgroundColor(context.getResources().getColor(R.color.background_bg));
        }
        container.addToWindow();
        switch (fakeType) {
            case FAKE_FC:
                container.setWidgetListener(new WidgetContainer.IWidgetListener() {
                    @Override
                    public boolean onBackPressed() {
                        cancel.run();
                        alertDialog = null;
                        return true;
                    }

                    @Override
                    public boolean onMenuPressed() {
                        return false;
                    }

                    @Override
                    public void onClick() {

                    }
                });
                container.removeAllViews();
                if (Utils.xiaomi) {
                    container.removeFromWindow();
                }
                showFC(context, MessageBox.NO_TITLE, context.getString(R.string.security_fake_force_close, label),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancel.run();
                                alertDialog = null;
                            }
                        },
                        new MessageBox.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                ok.run();
                                if (dialog != null) {
                                    dialog.cancel();
                                    dialog = null;
                                }
                                alertDialog = null;
                                return true;
                            }
                        }
                );
                break;


            case FAKE_SCAN_SETTING:
            case FAKE_SCAN:
                if (fingerprint == null) {
                    fingerprint = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.security_myfinger, null, false);
                    alpha = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_fade_you);
                    scanLine = AnimationUtils.loadAnimation(App.getContext(), R.anim.security_scan_line);
                    fingerprint.findViewById(R.id.fingerprint).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            long now = System.currentTimeMillis();
                            if (lastTouchTime2 != 0 || now - lastTouchTime1 > 1000) {
                                lastTouchTime1 = now;
                                lastTouchTime2 = 0;
                            } else {
                                lastTouchTime2 = now;
                            }
                        }
                    });
                    container.addView(fingerprint);
                }
                if (!fingerprint.isShown()) {
                    container.addView(fingerprint);
                }
                if (fakeType == FAKE_SCAN_SETTING) {
                    fingerprint.findViewById(R.id.tip).setVisibility(View.VISIBLE);
                } else {
                    fingerprint.findViewById(R.id.tip).setVisibility(View.GONE);
                }
                fingerprint.findViewById(R.id.bg_alpha_anim_target).startAnimation(alpha);
                fingerprint.findViewById(R.id.scan_line_anim_target).startAnimation(scanLine);
                View fingerIcon = fingerprint.findViewById(R.id.fingerprint);
                container.setWidgetListener(new WidgetContainer.IWidgetListener() {
                    @Override
                    public boolean onBackPressed() {
                        alpha.cancel();
                        scanLine.cancel();
                        cancel.run();
                        return true;
                    }

                    @Override
                    public boolean onMenuPressed() {
                        return false;
                    }

                    @Override
                    public void onClick() {

                    }
                });
                fingerIcon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (System.currentTimeMillis() - lastTouchTime2 <= 1000) {
                            alpha.cancel();
                            scanLine.cancel();
                            ok.run();
                        }
                        lastTouchTime2 = 0;
                        return true;
                    }
                });
                break;
        }
    }

    public static boolean isShowing() {
        return container != null && container.isShown();
    }

    public static void hide() {
        container.removeFromWindow();
        if (alertDialog != null) {
            alertDialog.cancel();
            alertDialog = null;
        }
    }

    public static AlertDialog showFC(Context context, int title, CharSequence msg, final DialogInterface.OnClickListener onyes, final MessageBox.OnLongClickListener onyeslong) {
        final MessageBox.Data data = new MessageBox.Data();
        data.style = Build.VERSION.SDK_INT > 20 ? R.style.AlertDialog_AppCompat_Alert : MessageBox.NO_TITLE;
        data.title = title;
        data.alert = true;
        data.messages = msg;
        data.icon = MessageBox.NO_TITLE;
        data.yes = R.string.security_my_close;
        data.button = MessageBox.BUTTON_YES;
        data.onyes = onyes;
        alertDialog = MessageBox.showLegacy(context, data);
        onyeslong.dialog = alertDialog;
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnTouchListener(new View.OnTouchListener() {
            int xStart = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        xStart = (int) event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (v.isPressed() && Math.abs(event.getX() - xStart) > v.getWidth() * .5f) {
                            onyeslong.onLongClick(null);
                        } else {
                            return v.onTouchEvent(event);
                        }
                    case MotionEvent.ACTION_CANCEL:
                        v.setPressed(false);
                        break;

                    default:
                        return v.onTouchEvent(event);
                }
                return true;
            }
        });
        return alertDialog;
    }

    public static void switchFakeIcon(int idx) {
        Class[] classes = new Class[FAKE_ICON_COUNT];
        switch (idx) {
            case FAKE_ICON_FAKE1:
                classes[0] = FakeOneActivity.class;
                classes[1] = FakeTwoActivity.class;
                classes[2] = FakeThreeActivity.class;
                classes[3] = PatternActivity.class;
                classes[4] = FakeFourActivity.class;
                classes[5] = FakeFiveActivity.class;
                break;

            case FAKE_ICON_FAKE2:
                classes[1] = FakeOneActivity.class;
                classes[0] = FakeTwoActivity.class;
                classes[2] = FakeThreeActivity.class;
                classes[3] = PatternActivity.class;
                classes[4] = FakeFourActivity.class;
                classes[5] = FakeFiveActivity.class;
                break;

            case FAKE_ICON_NORMAL:
                classes[3] = FakeOneActivity.class;
                classes[2] = FakeTwoActivity.class;
                classes[1] = FakeThreeActivity.class;
                classes[4] = FakeFourActivity.class;
                classes[5] = FakeFiveActivity.class;
                classes[0] = PatternActivity.class;
                break;

            case FAKE_ICON_FAKE3:
                classes[3] = FakeOneActivity.class;
                classes[2] = FakeTwoActivity.class;
                classes[0] = FakeThreeActivity.class;
                classes[4] = FakeFourActivity.class;
                classes[5] = FakeFiveActivity.class;
                classes[1] = PatternActivity.class;


                break;

            case FAKE_ICON_FAKE4:
                classes[0] = FakeFourActivity.class;
                classes[4] = FakeFiveActivity.class;
                classes[5] = FakeOneActivity.class;
                classes[3] = FakeTwoActivity.class;
                classes[2] = FakeThreeActivity.class;
                classes[1] = PatternActivity.class;
                break;

            case FAKE_ICON_FAKE5:
                classes[0] = FakeFiveActivity.class;
                classes[4] = FakeFourActivity.class;
                classes[5] = FakeOneActivity.class;
                classes[3] = FakeTwoActivity.class;
                classes[2] = FakeThreeActivity.class;
                classes[1] = PatternActivity.class;
                break;
        }
        switchLauncher(classes);
    }

    private static void switchLauncher(Class... classes) {
        if (classes.length > 0) {
            App.getContext().getPackageManager()
                    .setComponentEnabledSetting(new ComponentName(App.getContext(), classes[0])
                            , PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            for (int i = 1; i < classes.length; ++i) {
                App.getContext().getPackageManager()
                        .setComponentEnabledSetting(new ComponentName(App.getContext(), classes[i])
                                , PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }

    public static int fakeIconDesc() {
        int idx = fakeIconIdx();
        int[] descs = {
                R.string.security_fake_icon_default,
                R.string.security_fake_icon_1,
                R.string.security_fake_icon_2,
                R.string.security_fake_icon_3
        };
        return descs[idx];
    }

    public static int fakeIconIdx() {
        Class[] classes = new Class[FAKE_ICON_COUNT];
        classes[5] = FakeFiveActivity.class;
        classes[4] = FakeFourActivity.class;
        classes[3] = FakeThreeActivity.class;
        classes[2] = FakeTwoActivity.class;
        classes[1] = FakeOneActivity.class;
        classes[0] = PatternActivity.class;
        int i = 0;
        for (Class clazz : classes) {
            int componentEnabledSetting = App.getContext().getPackageManager().getComponentEnabledSetting(new ComponentName(App.getContext(), clazz));
            if (componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                return i;
            }
            ++i;
        }
        return 0;
    }
}
