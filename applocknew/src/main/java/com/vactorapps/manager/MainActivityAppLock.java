package com.vactorapps.manager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.vactorapps.lib.customview.VacWidgetContainer;
import com.vactorapps.manager.meta.SacProfiles;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.mydb.backgroundData;
import com.vactorapps.manager.page.MyDialog;
import com.vactorapps.manager.page.SharPFiveVac;
import com.vactorapps.manager.page.SlideMenu;
import com.vactorapps.manager.page.VacAppFragement;
import com.vactorapps.manager.page.VacMenu;
import com.vactorappsapi.manager.lib.Utils;
import com.vactorappsapi.manager.lib.io.SafeDB;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by SongHualin on 6/12/2015.
 */
public class MainActivityAppLock extends ClientActivitySecurity {

    private VacWidgetContainer wc;
    private Handler handler;
    private AlertDialog d;
    private LottieAnimationView pre_lottie;
    private LottieAnimationView lottie_good;
    private SharPFiveVac shareFive;
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

    VacAppFragement fragment;

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
    @InjectView(R.id.main_back_pre)
    FrameLayout main_back_pre;
    @InjectView(R.id.main_back_pre_lottie)
    LottieAnimationView main_back_pre_lottie;

    private static final int REQUSETSET = 110;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requirePermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Utils.requireCheckAccessPermission(this)) {
                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    final View alertDialogView = View.inflate(this, R.layout.security_show_permission2, null);
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
                            handler.postDelayed(runnable_us1, 1500);
                            handler.removeCallbacks(runnable_us2);
                            handler.post(runnable_us2);
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
            if (!VacPref.getOpenPermission()) {
                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OPEN, Tracker.ACT_PERMISSION_OPEN, 1L);
                VacPref.setOpenPermission(true);
            }
        }
    }

    Runnable runnable_us1 = new Runnable() {
        @Override
        public void run() {
            wc = new VacWidgetContainer(getApplicationContext(),
                    Gravity.START | Gravity.BOTTOM,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    false);
            View alertDialogView = View.inflate(MainActivityAppLock.this, R.layout.security_show_permission, null);
            final LottieAnimationView pre_lottie2 = (LottieAnimationView) alertDialogView.findViewById(R.id.pre_lottie);
            pre_lottie2.setAnimation("pre.json");
            pre_lottie2.setScale(0.4f);//相对原大小的0.2倍
//                    pre_lottie.setSpeed(0.7f);
            pre_lottie2.loop(true);
            pre_lottie2.playAnimation();
            wc.setWidgetListener(new VacWidgetContainer.IWidgetListener() {
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
    };
    Runnable runnable_us2 = new Runnable() {
        @Override
        public void run() {
            if (!Utils.requireCheckAccessPermission(MainActivityAppLock.this)) {
                startActivity(new Intent(MainActivityAppLock.this, MainActivityAppLock.class).putExtra("pre_open", true));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

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
//                sendEmail("iebuznel@gmail.com", MainActivityAppLock.this);
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
                Utils.rate(MainActivityAppLock.this);
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
        View view = View.inflate(MainActivityAppLock.this, R.layout.dialog_bad, null);
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
        final MyDialog d1 = new MyDialog(MainActivityAppLock.this, 0, 0, view, R.style.dialog);
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
                    Toast.makeText(MainActivityAppLock.this, getString(R.string.least_question), Toast.LENGTH_SHORT).show();
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
        if (SacProfiles.requireUpdateServerStatus()) {
            try {
                server.notifyApplockUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean pre_open = getIntent().getBooleanExtra("pre_open", false);
        Log.e("chfq", "==pre_open==" + pre_open);
        if (pre_open) {
            if (main_back_pre == null || main_back_pre_lottie == null)
                return;
            main_back_pre.setVisibility(View.VISIBLE);
            main_back_pre.setAlpha(1f);
            main_back_pre_lottie.setAnimation("frist4.json");
            main_back_pre_lottie.setScale(2f);//相对原大小的0.2倍
            main_back_pre_lottie.loop(false);//是否循环，true循环
            main_back_pre_lottie.setSpeed(1f);//播放速度
            main_back_pre_lottie.playAnimation();
            main_back_pre_lottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(main_back_pre, "alpha", 1f, 0f);
                    animator.setDuration(1500);
                    animator.start();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationStart(Animator animation) {

                }
            });
        }
    }

    public void setupView() {
        setContentView(R.layout.security_slidemenu_data);
        ButterKnife.inject(this);
        handler = new Handler();
        boolean pre_open = getIntent().getBooleanExtra("pre_open", false);
        Log.e("chfq", "==pre_open==" + pre_open);
        if (pre_open) {
            main_back_pre.setVisibility(View.VISIBLE);
            main_back_pre.setAlpha(1f);
            main_back_pre_lottie.setAnimation("frist4.json");
            main_back_pre_lottie.setScale(2f);//相对原大小的0.2倍
            main_back_pre_lottie.loop(false);//是否循环，true循环
            main_back_pre_lottie.setSpeed(1f);//播放速度
            main_back_pre_lottie.playAnimation();
            main_back_pre_lottie.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(main_back_pre, "alpha", 1f, 0f);
                    animator.setDuration(1500);
                    animator.start();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationStart(Animator animation) {

                }
            });
        }
        shareFive = new SharPFiveVac(this);
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        setupToolbar();
        VacMenu.currentMenuIt = VacMenu.MENU_LOCK_APP;
        setup(R.string.security_lock_app);

        profileName = SafeDB.defaultDB().getString(VacPref.PREF_ACTIVE_PROFILE, VacPref.PREF_DEFAULT_LOCK);
        long profileId = SafeDB.defaultDB().getLong(VacPref.PREF_ACTIVE_PROFILE_ID, 1);

        fragment = (VacAppFragement) getFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new VacAppFragement();
            Bundle args = new Bundle();
            args.putLong(VacAppFragement.PROFILE_ID_KEY, profileId);
            args.putString(VacAppFragement.PROFILE_NAME_KEY, profileName);
            args.putBoolean(VacAppFragement.PROFILE_HIDE, hide);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "fragment").commit();
        }
        requirePermission();
        preferences = getSharedPreferences("five_", Context.MODE_PRIVATE);
        editor = preferences.edit();
        int five_rate = preferences.getInt("five_rate_num", 0);
        if (!Utils.requireCheckAccessPermission(this)) {
            //好评弹窗
            five_rate++;
            editor.putInt("five_rate_num", five_rate).commit();
            if (five_rate >= 2) {
                editor.putInt("five_rate_num", -1).commit();
                if (!shareFive.getFiveRate() || preferences.getBoolean("five_rate_close_f", false)) {
                    goodShow();
                }
            }
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUSETSET) {
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
        if (VacPref.hasIntruder() || VacPref.getFristred()) {
//            VacMenu.currentMenuIt = 3;
//            Intent intent = new Intent(MainActivityAppLock.this, VacIntruderActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
//            finish();
            toolbar.setNavigationIcon(R.drawable.security_slide_menu_red);
        } else {
            toolbar.setNavigationIcon(R.drawable.security_slide_menu);
        }

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
                Uri uri = Uri.parse(VacMenu.FACEBOOK);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_FACEBOOK, Tracker.ACT_FACEBOOK, 1L);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(VacMenu.GOOGLE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLUS, Tracker.ACT_GOOGLE_PLUS, 1L);

            }
        });

        googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(VacMenu.GOOGLEPLAY);
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
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
