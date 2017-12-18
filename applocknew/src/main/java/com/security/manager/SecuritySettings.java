package com.security.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.MyDialog;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.SlideMenu;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by superjoy on 2014/9/4.
 */
public class SecuritySettings extends ClientActivitySecurity {
    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.facebook)
    ImageView facebook;

    @InjectView(R.id.goolge)
    ImageView google;

    @InjectView(R.id.googleplay)
    ImageView googleplay;

    Intent intent;
    private FrameLayout setting_frequency_fl;
    private TextView setting_frequency;
    private SharedPreferences sp;
    private FrameLayout setting_rebuild_fl;
    private TextView setting_rebuild;
    private FrameLayout setting_hide_fl;
    private ImageView setting_hide_iv;
    private boolean hideFlag;
    private FrameLayout setting_newlock_fl;
    private ImageView setting_newlock_iv;
    private boolean newLockFlag;
    private FrameLayout setting_notice_fl;
    private ImageView setting_notice_iv;
    private boolean noticeFlag;
    private FrameLayout setting_fingerprint_fl;
    private ImageView setting_fingerprint_iv;
    private boolean fingerprintFlag;
    private FrameLayout setting_battery_fl;
    private ImageView setting_battery_iv;
    private boolean batteryFlag;
    private FrameLayout setting_widget_fl;
    private ImageView setting_widget_iv;
    private boolean widgetFlag;
    private FrameLayout setting_power_fl;
    private FrameLayout setting_rote_fl;
    private int show_fingerprint;
    private int show_notification;
    private int show_charging;
    private int show_widget;


    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void setupView() {
        setContentView(R.layout.security_settings);
        ButterKnife.inject(this);
        setupToolbar();
        setup(R.string.security_tab_setting);
        normalTitle.setText("   " + getResources().getString(R.string.security_tab_setting));
        normalTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.security_back), null, null, null);
        normalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setViewVisible(View.GONE, R.id.search_button, R.id.bottom_action_bar);

        sp = App.getSharedPreferences();
//锁定设置
        setting_frequency_fl = (FrameLayout) findViewById(R.id.setting_frequency_fl);
        setting_frequency = (TextView) findViewById(R.id.setting_frequency);
        int slot = sp.getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
        setting_frequency.setText(getResources().getStringArray(R.array.brief_slot)[slot]);
        //重设密码
        setting_rebuild_fl = (FrameLayout) findViewById(R.id.setting_rebuild_fl);
        setting_rebuild = (TextView) findViewById(R.id.setting_rebuild);
        setting_rebuild.setText(SecurityMyPref.isUseNormalPasswd() ? R.string.security_password_lock : R.string.security_use_pattern);
        //隐藏图形路径
        setting_hide_fl = (FrameLayout) findViewById(R.id.setting_hide_fl);
        setting_hide_iv = (ImageView) findViewById(R.id.setting_hide_iv);
        hideFlag = sp.getBoolean("hide_path", false);
        if (hideFlag) {
            setting_hide_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_hide_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //新应用加锁
        setting_newlock_fl = (FrameLayout) findViewById(R.id.setting_newlock_fl);
        setting_newlock_iv = (ImageView) findViewById(R.id.setting_newlock_iv);
        newLockFlag = sp.getBoolean(SecurityMyPref.LOCK_NEW, SecurityMyPref.LOCK_DEFAULT);
        if (newLockFlag) {
            setting_newlock_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_newlock_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //通知栏
        setting_notice_fl = (FrameLayout) findViewById(R.id.setting_notice_fl);
        setting_notice_iv = (ImageView) findViewById(R.id.setting_notice_iv);
        noticeFlag = SecurityMyPref.getNotification();
        if (noticeFlag) {
            setting_notice_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_notice_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //指纹
        setting_fingerprint_fl = (FrameLayout) findViewById(R.id.setting_fingerprint_fl);
        setting_fingerprint_iv = (ImageView) findViewById(R.id.setting_fingerprint_iv);
//        fingerprintFlag = SecurityMyPref.getNotification();
        if (fingerprintFlag) {
            setting_fingerprint_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_fingerprint_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //充电屏保
        setting_battery_fl = (FrameLayout) findViewById(R.id.setting_battery_fl);
        setting_battery_iv = (ImageView) findViewById(R.id.setting_battery_iv);
//        batteryFlag = SecurityMyPref.getNotification();
        if (batteryFlag) {
            setting_battery_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_battery_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //桌面widget
        setting_widget_fl = (FrameLayout) findViewById(R.id.setting_widget_fl);
        setting_widget_iv = (ImageView) findViewById(R.id.setting_widget_iv);
//        widgetFlag = SecurityMyPref.getNotification();
        if (widgetFlag) {
            setting_widget_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_widget_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //权限中心
        setting_power_fl = (FrameLayout) findViewById(R.id.setting_power_fl);
        //评价
        setting_rote_fl = (FrameLayout) findViewById(R.id.setting_rote_fl);

        try {
            JSONObject jsonObject = new JSONObject(AndroidSdk.getExtraData());
            show_fingerprint = jsonObject.getInt("show_fingerprint");
            show_notification = jsonObject.getInt("show_notification");
            show_charging = jsonObject.getInt("show_charging");
            show_widget = jsonObject.getInt("show_widget");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (show_fingerprint == 0) {
            setting_fingerprint_fl.setVisibility(View.GONE);
        }
        if (show_notification == 0) {
            setting_notice_fl.setVisibility(View.GONE);
        }
        if (show_charging == 0) {
            setting_battery_fl.setVisibility(View.GONE);
        }
        if (show_widget == 0) {
            setting_widget_fl.setVisibility(View.GONE);
        }


        setting_frequency_fl.setOnClickListener(onClickListener);
        setting_rebuild_fl.setOnClickListener(onClickListener);
        setting_hide_fl.setOnClickListener(onClickListener);
        setting_newlock_fl.setOnClickListener(onClickListener);
        setting_notice_fl.setOnClickListener(onClickListener);
        setting_fingerprint_fl.setOnClickListener(onClickListener);
        setting_battery_fl.setOnClickListener(onClickListener);
        setting_widget_fl.setOnClickListener(onClickListener);
        setting_power_fl.setOnClickListener(onClickListener);
        setting_rote_fl.setOnClickListener(onClickListener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();


    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.setting_frequency_fl:
                    showFrequencyDialog(SecuritySettings.this);

                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_BRIEF, Tracker.ACT_SETTING_BRIEF, 1L);
                    break;
                case R.id.setting_rebuild_fl:
                    showResetPasswordDialog(SecuritySettings.this);
                    break;
                case R.id.setting_hide_fl:
                    if (hideFlag) {
                        hideFlag = false;
                        sp.edit().putBoolean("hide_path", false).apply();
                        setting_hide_iv.setImageResource(R.drawable.security_setting_not_check);
                    } else {
                        hideFlag = true;
                        sp.edit().putBoolean("hide_path", true).apply();
                        setting_hide_iv.setImageResource(R.drawable.security_setting_check);
                    }
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_HIDEPATH, Tracker.ACT_SETTING_HIDEPATH, 1L);
                    break;
                case R.id.setting_newlock_fl:
                    if (newLockFlag) {
                        newLockFlag = false;
                        sp.edit().putBoolean(SecurityMyPref.LOCK_NEW, false).apply();
                        setting_newlock_iv.setImageResource(R.drawable.security_setting_not_check);
                    } else {
                        newLockFlag = true;
                        sp.edit().putBoolean(SecurityMyPref.LOCK_NEW, true).apply();
                        setting_newlock_iv.setImageResource(R.drawable.security_setting_check);
                    }
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_LOCK_NEW, Tracker.ACT_SETTING_LOCK_NEW, 1L);
                    break;
                case R.id.setting_notice_fl:
                    if (noticeFlag) {
                        noticeFlag = false;
                        SecurityMyPref.setNotification(false);
                        stopService(new Intent(SecuritySettings.this, NotificationService.class));
                        setting_notice_iv.setImageResource(R.drawable.security_setting_not_check);
                    } else {
                        noticeFlag = true;
                        SecurityMyPref.setNotification(true);
                        stopService(new Intent(SecuritySettings.this, NotificationService.class));
                        startService(new Intent(SecuritySettings.this, NotificationService.class));
                        setting_notice_iv.setImageResource(R.drawable.security_setting_check);
                    }
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_LOCK_NOTIFICAO, Tracker.ACT_SETTING_LOCK_NOTIFICAO, 1L);
                    break;
                case R.id.setting_fingerprint_fl:
                    //指纹
                    if (fingerprintFlag) {
                        fingerprintFlag = false;
                        setting_fingerprint_iv.setImageResource(R.drawable.security_setting_not_check);
                    } else {
                        fingerprintFlag = true;
                        setting_fingerprint_iv.setImageResource(R.drawable.security_setting_check);
                    }
                    break;
                case R.id.setting_battery_fl:
                    //充电屏保
                    if (batteryFlag) {
                        batteryFlag = false;
                        setting_battery_iv.setImageResource(R.drawable.security_setting_not_check);
                    } else {
                        batteryFlag = true;
                        setting_battery_iv.setImageResource(R.drawable.security_setting_check);
                    }
                    break;
                case R.id.setting_widget_fl:
                    if (widgetFlag) {
                        widgetFlag = false;
                        setting_widget_iv.setImageResource(R.drawable.security_setting_not_check);
                    } else {
                        widgetFlag = true;
                        setting_widget_iv.setImageResource(R.drawable.security_setting_check);
                    }
                    break;
                case R.id.setting_power_fl:
                    //权限中心
                    Intent intent = new Intent(SecuritySettings.this, SecuritySettingsAdvance.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                    break;
                case R.id.setting_rote_fl:
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE, 1L);
                    SecurityShare.rate(context);
                    break;


            }
//                Intent intent = new Intent(SecuritySettings.this, SecuritySettingsAdvance.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_PERMISSION, Tracker.ACT_SETTING_PERMISSION, 1L);
//                Utility.goPermissionCenter(SecuritySettings.this, "");
        }
    };

//    public void setPasswd(boolean forResult, boolean pattern) {
//        if (forResult)
//            startActivityForResult(new Intent(context, SecuritySetPasswordActivity.class).putExtra("set", pattern ? SecuritySetPasswordActivity.SET_GRAPH_PASSWD : SecuritySetPasswordActivity.SET_NORMAL_PASSWD), pattern ? REQ_CODE_PATTERN : REQ_CODE_PASS);
//        else
//            startActivity(new Intent(context, SecuritySetPasswordActivity.class).putExtra("set", pattern ? SecuritySetPasswordActivity.SET_GRAPH_PASSWD : SecuritySetPasswordActivity.SET_NORMAL_PASSWD));
//        overridePendingTransition(R.anim.security_huadong_left_in, R.anim.security_huadong_right_out);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//
//            case REQ_CODE_PATTERN:
//                if (resultCode == 1) {
////                    SharPre.begin().useNormalPasswd(false).commit();
//                    notifyDatasetChanged();
//                }
//                break;
//            case REQ_CODE_PASS:
//                if (resultCode == 1) {
////                    SharPre.begin().useNormalPasswd(true).commit();
//                    notifyDatasetChanged();
//                }
//                break;
//            default:
//                super.onActivityResult(requestCode, resultCode, data);
//                break;
//        }
//    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        notifyDatasetChanged();
//    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.security_slide_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_tab_setting);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SlideMenu.Status status = menu.getStatus();
            if (status == SlideMenu.Status.Close)
                menu.open();
            else if (status == SlideMenu.Status.OpenRight) {
                menu.close();
            } else
                askForExit();
        }
        return true;
    }


    public void initclick() {
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(SecurityMenu.FACEBOOK);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_FACEBOOK, Tracker.ACT_FACEBOOK, 1L);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(SecurityMenu.GOOGLE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLUS, Tracker.ACT_GOOGLE_PLUS, 1L);

            }
        });

        googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(SecurityMenu.GOOGLEPLAY);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLAY, Tracker.ACT_GOOGLE_PLAY, 1L);

            }
        });

    }

    public void showFrequencyDialog(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_show_frequency, null);
        final MyDialog d = new MyDialog(c, 0, 0, alertDialogView, R.style.dialog);

        FrameLayout resetPattern = (FrameLayout) alertDialogView.findViewById(R.id.pattern);
        FrameLayout resetPassword = (FrameLayout) alertDialogView.findViewById(R.id.digital);
        FrameLayout five_time = (FrameLayout) alertDialogView.findViewById(R.id.five_time);
        ImageView every_time_iv = (ImageView) alertDialogView.findViewById(R.id.every_time_iv);
        ImageView five_time_iv = (ImageView) alertDialogView.findViewById(R.id.five_time_iv);
        ImageView lock_screen_iv = (ImageView) alertDialogView.findViewById(R.id.lock_screen_iv);
        int idx = sp.getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
        if (idx == 0) {
            every_time_iv.setImageResource(R.drawable.check);
            five_time_iv.setImageResource(R.drawable.uncheck);
            lock_screen_iv.setImageResource(R.drawable.uncheck);
        } else if (idx == 2) {
            every_time_iv.setImageResource(R.drawable.uncheck);
            five_time_iv.setImageResource(R.drawable.uncheck);
            lock_screen_iv.setImageResource(R.drawable.check);
        } else if (idx == 1) {
            every_time_iv.setImageResource(R.drawable.uncheck);
            five_time_iv.setImageResource(R.drawable.check);
            lock_screen_iv.setImageResource(R.drawable.uncheck);
        }

        d.getWindow().setWindowAnimations(R.style.dialog_animation);
        d.getWindow().setGravity(Gravity.CENTER);
        d.show();

        try {
            resetPattern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    sp.edit().putInt(SecurityMyPref.PREF_BRIEF_SLOT, 0).apply();
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_EVERY_TIME, Tracker.ACT_SETTING_EVERY_TIME, 1L);
                    setting_frequency.setText(getResources().getStringArray(R.array.brief_slot)[0]);
                }
            });

            resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    sp.edit().putInt(SecurityMyPref.PREF_BRIEF_SLOT, 2).apply();
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_EVERY_TIME, Tracker.ACT_SETTING_FIVE_MINIUTE, 1L);
                    setting_frequency.setText(getResources().getStringArray(R.array.brief_slot)[2]);
                }
            });
            five_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    sp.edit().putInt(SecurityMyPref.PREF_BRIEF_SLOT, 1).apply();
                    Tracker.sendEvent(Tracker.ACT_SETTING_SCREEN_OFF, Tracker.ACT_SETTING_SCREEN_OFF, Tracker.ACT_SETTING_FIVE_MINIUTE, 1L);
                    setting_frequency.setText(getResources().getStringArray(R.array.brief_slot)[1]);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showResetPasswordDialog(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_show_reset_password, null);
        final MyDialog d = new MyDialog(c, 0, 0, alertDialogView, R.style.dialog);

        FrameLayout resetPattern = (FrameLayout) alertDialogView.findViewById(R.id.pattern);
        FrameLayout resetPassword = (FrameLayout) alertDialogView.findViewById(R.id.digital);

        d.getWindow().setWindowAnimations(R.style.dialog_animation);
        d.getWindow().setGravity(Gravity.CENTER);
        d.show();

        try {
            resetPattern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();

                    Intent intent = new Intent(c, SecuritySetPattern.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("set", SecuritySetPattern.SET_GRAPH_PASSWD);
                    c.startActivity(intent);
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_LEADER_SETTINGPASS, Tracker.ACT_LEADER_SETTINGPASS, 1L);


                }
            });

            resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();

                    Intent intent = new Intent(c, SecuritySetPattern.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("set", SecuritySetPattern.SET_NORMAL_PASSWD);
                    c.startActivity(intent);
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_LEADER_SETTINGPASS_PASSWORD, Tracker.ACT_LEADER_SETTINGPASS_PASSWORD, 1L);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}