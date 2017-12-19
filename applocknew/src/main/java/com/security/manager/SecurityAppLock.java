package com.security.manager;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.security.lib.customview.MyWidgetContainer;
import com.security.manager.db.backgroundData;
import com.security.manager.lib.Utils;
import com.security.manager.lib.io.SafeDB;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.AppFragementSecurity;
import com.security.manager.page.MyDialog;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.SecuritySharPFive;
import com.security.manager.page.SlideMenu;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by SongHualin on 6/12/2015.
 */
public class SecurityAppLock extends ClientActivitySecurity {

    private MyWidgetContainer wc;
    private Handler handler;
    private AlertDialog d;
    private LottieAnimationView pre_lottie;
    private LottieAnimationView lottie_good;
    private SecuritySharPFive shareFive;
    private boolean questionFlag4;
    private boolean questionFlag1;
    private boolean questionFlag2;
    private boolean questionFlag3;
    private AlertDialog dialog1;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    AppFragementSecurity fragment;

    private String profileName;

    boolean hide;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.facebook)
    ImageView facebook;

    @InjectView(R.id.goolge)
    ImageView google;

    @InjectView(R.id.googleplay)
    ImageView googleplay;

    private static final int REQUSETSET = 110;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requirePermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Utils.requireCheckAccessPermission(this)) {
                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    final View alertDialogView = View.inflate(this, R.layout.security_show_permission, null);
                    d = new AlertDialog.Builder(this, R.style.dialog).create();
                    d.setView(alertDialogView);
                    d.setCanceledOnTouchOutside(false);
                    d.show();
                    pre_lottie = (LottieAnimationView) alertDialogView.findViewById(R.id.pre_lottie);
                    pre_lottie.setAnimation("pre.json");
                    pre_lottie.setScale(0.4f);//相对原大小的0.2倍
//                    pre_lottie.setSpeed(0.7f);
                    pre_lottie.loop(true);
                    pre_lottie.playAnimation();
                    alertDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.cancel();
                            pre_lottie.cancelAnimation();
                            startActivityForResult(intent, REQUSETSET);
                            //启动服务
                            Intent intent = new Intent(SecurityAppLock.this, PreferenceService.class);
                            intent.putExtra("setting_permission_main", true);
                            startService(intent);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    wc = new MyWidgetContainer(getApplicationContext(),
                                            Gravity.START | Gravity.BOTTOM,
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            false);
                                    View alertDialogView = View.inflate(SecurityAppLock.this, R.layout.security_show_permission, null);
                                    final LottieAnimationView pre_lottie2 = (LottieAnimationView) alertDialogView.findViewById(R.id.pre_lottie);
                                    pre_lottie2.setAnimation("pre.json");
                                    pre_lottie2.setScale(0.4f);//相对原大小的0.2倍
//                    pre_lottie.setSpeed(0.7f);
                                    pre_lottie2.loop(true);
                                    pre_lottie2.playAnimation();
                                    wc.setWidgetListener(new MyWidgetContainer.IWidgetListener() {
                                        @Override
                                        public boolean onBackPressed() {
                                            return false;
                                        }

                                        @Override
                                        public boolean onMenuPressed() {
                                            return false;
                                        }

                                        @Override
                                        public void onClick() {
                                            if (pre_lottie2 != null) {
                                                pre_lottie2.cancelAnimation();
                                            }
                                            if (wc != null) {
                                                wc.removeFromWindow();
                                                wc = null;
                                            }
                                        }
                                    });
                                    wc.addView(alertDialogView);
                                    wc.addToWindow();
                                }
                            }, 1500);

                            Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_OK, 1L);

                        }
                    });

                    alertDialogView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_CANCLE, 1L);
                            pre_lottie.cancelAnimation();
                            d.cancel();
                        }
                    });
                }
            }

        } else {
            if (!SecurityMyPref.getOpenPermission()) {
                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OPEN, Tracker.ACT_PERMISSION_OPEN, 1L);
                SecurityMyPref.setOpenPermission(true);
            }
        }
    }

    public void goodShow() {
        final View alertDialogView = View.inflate(this, R.layout.goodshow, null);
        d = new AlertDialog.Builder(this, R.style.dialog).create();
        d.setView(alertDialogView);
        d.setCanceledOnTouchOutside(false);
        d.show();
        lottie_good = (LottieAnimationView) alertDialogView.findViewById(R.id.lottie_good);
        lottie_good.setAnimation("good.json");
        lottie_good.setScale(0.3f);//相对原大小的0.2倍
        lottie_good.loop(true);
        lottie_good.playAnimation();

        alertDialogView.findViewById(R.id.security_bad_tit).setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(final View v) {
                d.dismiss();
                lottie_good.cancelAnimation();
                shareFive.setFiveRate(true);
                editor.putBoolean("five_rate_close_f", false).commit();
//                sendEmail("iebuznel@gmail.com", SecurityAppLock.this);
                showBadDialog();
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_PERMISSION_CANCLE, 1L);
            }
        });
        alertDialogView.findViewById(R.id.security_good_tit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                d.dismiss();
                lottie_good.cancelAnimation();
                shareFive.setFiveRate(true);
                Utils.rate(SecurityAppLock.this);
                editor.putBoolean("five_rate_close_f", false).commit();
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_GOOD_RATE_GOOD, 1L);

            }
        });
        alertDialogView.findViewById(R.id.security_rat_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                lottie_good.cancelAnimation();
                shareFive.setFiveRate(true);
                if (preferences.getBoolean("five_rate_close_f", false)) {
                    editor.putBoolean("five_rate_close_f", false).commit();
                    editor.putBoolean("five_rate_close_a", false).commit();
                } else {
                    editor.putBoolean("five_rate_close_a", true).commit();
                }
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_GOOD_RATE_CLOSE, 1L);
            }
        });
    }

    //差评反馈
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showBadDialog() {
        View view = View.inflate(SecurityAppLock.this, R.layout.dialog_bad, null);
        LinearLayout question1 = (LinearLayout) view.findViewById(R.id.question1);
        LinearLayout question2 = (LinearLayout) view.findViewById(R.id.question2);
        LinearLayout question3 = (LinearLayout) view.findViewById(R.id.question3);
        LinearLayout question4 = (LinearLayout) view.findViewById(R.id.question4);
        final ImageView question_v1 = (ImageView) view.findViewById(R.id.question_v1);
        final ImageView question_v2 = (ImageView) view.findViewById(R.id.question_v2);
        final ImageView question_v3 = (ImageView) view.findViewById(R.id.question_v3);
        final ImageView question_v4 = (ImageView) view.findViewById(R.id.question_v4);
        ImageView good_close = (ImageView) view.findViewById(R.id.good_close);
        TextView main_bad = (TextView) view.findViewById(R.id.main_bad);
        TextView main_good = (TextView) view.findViewById(R.id.main_good);
        final EditText edittext = (EditText) view.findViewById(R.id.edittext);
        final MyDialog d1 = new MyDialog(SecurityAppLock.this, 0, 0, view, R.style.dialog);
        d1.show();

        question1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag1) {
                    questionFlag1 = false;
                    question_v1.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag1 = true;
                    question_v1.setImageResource(R.drawable.check);
                }
            }
        });
        question2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag2) {
                    questionFlag2 = false;
                    question_v2.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag2 = true;
                    question_v2.setImageResource(R.drawable.check);
                }
            }
        });
        question3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag3) {
                    questionFlag3 = false;
                    question_v3.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag3 = true;
                    question_v3.setImageResource(R.drawable.check);
                }
            }
        });
        question4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag4) {
                    questionFlag4 = false;
                    question_v4.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag4 = true;
                    question_v4.setImageResource(R.drawable.check);
                }
            }
        });
        main_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d1.dismiss();
                Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "提交点击", 1L);
            }
        });
        good_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d1.dismiss();
                Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "叉号点击", 1L);
            }
        });
        main_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                if (questionFlag1) {
                    str += "1广告";
                }
                if (questionFlag2) {
                    str += "2不锁";
                }
                if (questionFlag3) {
                    str += "3界面";
                }
                if (questionFlag4) {
                    str += "4其他";
                }
                Log.e("chfq", "===edittext.getText().toString().isEmpty()==" + edittext.getText().toString().isEmpty());
                if (str.isEmpty() && edittext.getText().toString().isEmpty()) {
                    Toast.makeText(SecurityAppLock.this, getString(R.string.least_question), Toast.LENGTH_SHORT).show();
                } else {
                    d1.dismiss();
                    Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "放弃点击" + str, 1L);
                }
            }
        });
    }

    @Override
    protected void onIntent(Intent intent) {
        hide = intent.getBooleanExtra("hide", false);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        hide = savedInstanceState.getBoolean("hide");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hide", hide);
    }

    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (SecuritProfiles.requireUpdateServerStatus()) {
            try {
                server.notifyApplockUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setupView() {
        setContentView(R.layout.security_slidemenu_data);
        ButterKnife.inject(this);
        handler = new Handler();
        shareFive = new SecuritySharPFive(this);
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        setupToolbar();
        SecurityMenu.currentMenuIt = SecurityMenu.MENU_LOCK_APP;
        setup(R.string.security_lock_app);

        profileName = SafeDB.defaultDB().getString(SecurityMyPref.PREF_ACTIVE_PROFILE, SecurityMyPref.PREF_DEFAULT_LOCK);
        long profileId = SafeDB.defaultDB().getLong(SecurityMyPref.PREF_ACTIVE_PROFILE_ID, 1);

        fragment = (AppFragementSecurity) getFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new AppFragementSecurity();
            Bundle args = new Bundle();
            args.putLong(AppFragementSecurity.PROFILE_ID_KEY, profileId);
            args.putString(AppFragementSecurity.PROFILE_NAME_KEY, profileName);
            args.putBoolean(AppFragementSecurity.PROFILE_HIDE, hide);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "fragment").commit();
        }
        requirePermission();
        preferences = getSharedPreferences("five_", Context.MODE_PRIVATE);
        editor = preferences.edit();
        if (!Utils.requireCheckAccessPermission(this)) {
            //好评弹窗
            SecurityMyPref.launchNow();
            if (!shareFive.getFiveRate() || preferences.getBoolean("five_rate_close_f", false)) {
                goodShow();
            } /*else if (shareFive.getFiveRate()) {
                if (!shareFive.getFiveRate2()) {
                    goodShow();
                }
            }*/

        }

        initclick();

        initgetData();
    }

    private boolean onPause = false;

    @Override
    protected void onPause() {
        fragment.saveOrCreateProfile(profileName, server);
        super.onPause();
        if (pre_lottie != null) {
            pre_lottie.cancelAnimation();
        }
        onPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onPause) {
//            setAnimators(view);
            onPause = false;
            stopService(new Intent(this, PreferenceService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUSETSET) {
            stopService(new Intent(this, PreferenceService.class));
            if (d != null) {
                d.dismiss();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void setupToolbar() {
        if (SecurityMyPref.hasIntruder()) {
            Intent intent = new Intent(SecurityAppLock.this, IntruderActivitySecurity.class);
            startActivity(intent);
        }
        toolbar.setNavigationIcon(R.drawable.security_slide_menu);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
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
        return false;
    }

    private void initgetData() {
        String data = AndroidSdk.getExtraData();
        if (data != null) {
            backgroundData.onReceiveData(this, data);

        }
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

    BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                if (wc != null) {
                    wc.removeFromWindow();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHomeKeyEventReceiver);
        if (wc != null) {
            wc.removeFromWindow();
        }
        if (pre_lottie != null) {
            pre_lottie.cancelAnimation();
        }
        if (lottie_good != null) {
            lottie_good.cancelAnimation();
        }
        stopService(new Intent(this, PreferenceService.class));
    }
}
