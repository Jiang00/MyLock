package com.security.manager;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.client.AndroidSdk;
import com.auroras.module.charge.saver.aurorasutils.AurorasUtils;
import com.auroras.module.charge.saver.aurorasutils.BatteryConstants;
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
    private FingerprintManagerCompat managerCompat;
    private KeyguardManager keyguardManager;
    private FrameLayout setting_family;
    private FrameLayout help_sug;
    private FrameLayout setting_follow;


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
        fingerprintFlag = SecurityMyPref.getFingerprintl();
        managerCompat = FingerprintManagerCompat.from(App.getContext());
        keyguardManager = (KeyguardManager) App.getContext().getSystemService(App.getContext().KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (!managerCompat.isHardwareDetected()) { //判断设备是否支持
                setting_fingerprint_fl.setVisibility(View.GONE);
            } else if (!managerCompat.hasEnrolledFingerprints()) {
                fingerprintFlag = false;
            }
        } else {
            setting_fingerprint_fl.setVisibility(View.GONE);
        }
        if (fingerprintFlag) {
            setting_fingerprint_iv.setImageResource(R.drawable.security_setting_check);
        } else {
            setting_fingerprint_iv.setImageResource(R.drawable.security_setting_not_check);
        }
        //充电屏保
        setting_battery_fl = (FrameLayout) findViewById(R.id.setting_battery_fl);
        setting_battery_iv = (ImageView) findViewById(R.id.setting_battery_iv);
        batteryFlag = (Boolean) AurorasUtils.readData(this, BatteryConstants.CHARGE_SAVER_SWITCH, true);
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
        //family
        setting_family = (FrameLayout) findViewById(R.id.setting_family);
        //联系我们
        help_sug = (FrameLayout) findViewById(R.id.help_sug);
        //关注我们
        setting_follow = (FrameLayout) findViewById(R.id.setting_follow);

        try {
            JSONObject jsonObject = new JSONObject(AndroidSdk.getExtraData());
            show_fingerprint = jsonObject.getInt("show_fingerprint");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (show_fingerprint == 0) {
            setting_fingerprint_fl.setVisibility(View.GONE);
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
        setting_family.setOnClickListener(onClickListener);
        help_sug.setOnClickListener(onClickListener);
        setting_follow.setOnClickListener(onClickListener);

    }


    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();


    }

    private LottieAnimationView fingerprint;
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
                    Log.e("chfq", "===");
                    if (!managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
                        final View alert = View.inflate(SecuritySettings.this, R.layout.security_fingerprint_alert, null);
                        final AlertDialog alertDialog = new AlertDialog.Builder(SecuritySettings.this,R.style.dialog).create();
                        alertDialog.setView(alert);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        fingerprint = (LottieAnimationView) alert.findViewById(R.id.setting_lottie);
                        fingerprint.setAnimation("fingerprint.json");
//                        fingerprint.setScale(0.07f);//相对原大小的0.2倍
                        fingerprint.loop(true);
                        fingerprint.playAnimation();

                        alert.findViewById(R.id.security_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                                fingerprintFlag = false;
                                SecurityMyPref.setFingerprintl(false);
                                setting_fingerprint_iv.setImageResource(R.drawable.security_setting_not_check);
                            }
                        });
                        alert.findViewById(R.id.security_setup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                                startActivityForResult(intent, 110);
                            }
                        });
                    } else {
                        Log.e("chfq", "=====");
                        if (fingerprintFlag) {
                            Log.e("chfq", "===false==");
                            fingerprintFlag = false;
                            setting_fingerprint_iv.setImageResource(R.drawable.security_setting_not_check);
                            SecurityMyPref.setFingerprintl(false);
                        } else {
                            Log.e("chfq", "===true==");
                            fingerprintFlag = true;
                            setting_fingerprint_iv.setImageResource(R.drawable.security_setting_check);
                            SecurityMyPref.setFingerprintl(true);
                        }
                    }
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, "指纹点击", "", 1L);

                    break;
                case R.id.setting_battery_fl:
                    //充电屏保
                    if (batteryFlag) {
                        batteryFlag = false;
                        setting_battery_iv.setImageResource(R.drawable.security_setting_not_check);
                        AurorasUtils.writeData(SecuritySettings.this, BatteryConstants.CHARGE_SAVER_SWITCH, false);
                    } else {
                        batteryFlag = true;
                        setting_battery_iv.setImageResource(R.drawable.security_setting_check);
                        AurorasUtils.writeData(SecuritySettings.this, BatteryConstants.CHARGE_SAVER_SWITCH, true);
                    }
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, "充电屏保点击", "", 1L);
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
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, "权限中心点击", "", 1L);
                    break;
                case R.id.setting_rote_fl:
                    Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE, 1L);
                    SecurityShare.rate(context);
                    break;
                case R.id.setting_family:
                    //family    https://play.google.com/store/search?q=CLEANMOBI&c=apps
                    Tools.openPlayStore(SecuritySettings.this, getPackageName(), "https://play.google.com/store/apps/developer?id=VectorApps_Team");
                    break;
                case R.id.help_sug:
                    //联系我们
                    sendEmail("iebuznel@gmail.com", SecuritySettings.this);
                    break;
                case R.id.setting_follow:
                    //关注我们
                    Intent intentfb = newFacebookIntent(SecuritySettings.this.getPackageManager(), "https://www.facebook.com/Applock_VectorApps-832626453583360/");
                    startActivity(intentfb);
                    break;


            }
        }
    };

    public static void sendEmail(String email, Context context) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + email));
        try {
            context.startActivity(data);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.setting_no_email), Toast.LENGTH_SHORT).show();
        }
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
// http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

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

    @Override
    protected void onReceiveActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (requestCode == 110) {
            if (managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
                SecurityMyPref.setFingerprintl(true);
                setting_fingerprint_iv.setImageResource(R.drawable.security_setting_check);
            }
        }
    }
}