package com.vactorapps.manager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ivymobi.applock.free.R;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.page.LockPatternUtils;
import com.vactorapps.manager.page.NumberDot;
import com.vactorapps.manager.page.PatternViewVac;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by superjoy on 2014/10/24.
 */
public class VacSetPattern extends ClientActivitySecurity implements View.OnClickListener {
    public EditText email_address;
    CharSequence lastPasswd;
    public static final byte SET_EMPTY = 0;
    public static final byte SET_NORMAL_PASSWD = 1;
    public static final byte SET_GRAPH_PASSWD = 2;
    public static final byte SET_EMAIL = 3;
    private LottieAnimationView setpattern_lottie;
    private LottieAnimationView setpassword_lottie;

    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.RGBA_8888);
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setupView() {
        byte setting = getIntent().getByteExtra("set", SET_EMPTY);
        switch (setting) {
            case SET_EMAIL:
                setEmail();
                break;

            case SET_NORMAL_PASSWD:
                setPasswdView();
                break;

            case SET_GRAPH_PASSWD:
                setGraphView();
                break;
        }
    }

    public void randomNumpadIfPossible() {
        if (!MyApp.getSharedPreferences().getBoolean("random", false)) {
            return;
        }

        int[] buttons = new int[]{
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };
        ArrayList<Integer> idx = new ArrayList<Integer>();
        for (int i = 0; i < 10; ++i)
            idx.add(i);

        for (int button : buttons) {
            int i = getRandomInt(0, idx.size() - 1);
            Integer v = idx.remove(i);
            ((Button) findViewById(button)).setText(v.toString());
        }
    }

    // 返回a到b之間(包括a,b)的任意一個自然数,如果a > b || a < 0，返回-1
    public static int getRandomInt(int a, int b) {
        if (a > b || a < 0)
            return -1;
        // 下面两种形式等价
        // return a + (int) (new Random().nextDouble() * (b - a + 1));
        return a + (int) (Math.random() * (b - a + 1));
    }

    public void setEmail() {
        startListApp();
    }

    NumberDot passdot;
    int size = 0;

    @Override
    public void onClick(View view) {
        TextView v = (TextView) view;
        int length = v.getTag().toString().length();
        passdot.setNumber(v.getTag().toString().charAt(length - 1));
        if (setProgress == 0 && size < 6) {
            size++;
        }
        if (togglePattern) {
            togglePattern = false;
            TextView ok = (TextView) findViewById(R.id.ok);
            ok.setText(R.string.security_btn_next);
        }
    }

    public void passwdIsEmpty() {
//        Toast t = Toast.makeText(context, R.string.awtwd_empty, Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        t.show();
    }

//    public void setPasswd() {
//        String pwd = lastPasswd.toString();
//        String email = email_address.getText().toString();
//        MyApp.getSharedPreferences().edit().putString("email", email).apply();
//        if (pwd.length() == 0) {
//            passwdIsEmpty();
//            return;
//        }
//        VacPref p = VacPref.begin();
//        p.setPasswd(pwd, true).useNormalPasswd(true).commit();
//        Tracker.sendEvent(Tracker.CATE_DEFAULT, Tracker.ACT_APPLOCK, Tracker.ACT_APPLOCK, 1L);
//        startListApp();
//    }

    public Drawable getIcon() {
        String packageName = getIntent().getStringExtra("pkg");
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return pi.applicationInfo.loadIcon(getPackageManager());
        } catch (Exception e) {
            return super.getResources().getDrawable(R.drawable.ic_launcher);
        }
    }

    public void setupTitle(ImageView tv) {

        String packageName = getIntent().getStringExtra("pkg");
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            Drawable ic = pi.applicationInfo.loadIcon(getPackageManager());
            tv.setImageDrawable(ic);
        } catch (Exception e) {
            tv.setImageResource(R.drawable.ic_launcher);
        }
    }

    public PatternViewVac securityPatternView;
    public boolean confirmMode = false;

    @InjectView(R.id.number_cancel)
    TextView cancel;

    @InjectView(R.id.tip)
    TextView tip;

    public void enterGraphNormal() {
        confirmMode = false;
        tip.setTextColor(getResources().getColor(R.color.security_body_text_1_inverse));
        tip.setText(R.string.security_draw_pattern);
        tip.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(R.string.switch_password);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasswdView();
                if (setpattern_lottie != null) {
                    setpattern_lottie.cancelAnimation();
                }
                Tracker.sendEvent(Tracker.CATE_SETTING, Tracker.ACT_LEADER_SETTINGPASS_PASSWORD, Tracker.ACT_LEADER_SETTINGPASS_PASSWORD, 1L);
            }
        });
    }

    public void enterConfirmMode() {
        confirmMode = true;
        tip.setText(R.string.security_draw_pattern_next);
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(R.string.security_reset_passwd_2_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterGraphNormal();
            }
        });
    }

    void setGraphView() {
        if (VacPref.getFirstLeader()) {
            VacPref.setFirstLeader(false);
            Tracker.sendEvent(Tracker.ACT_LEADER, Tracker.ACT_LEADER_SETTINGPASS, Tracker.ACT_LEADER_SETTINGPASS, 1L);
        } else {
            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_LEADER_SETTINGPASS, Tracker.ACT_LEADER_SETTINGPASS, 1L);
        }
        setContentView(R.layout.security_pattern_view_set);
        ButterKnife.inject(this);
        View back = findViewById(R.id.back);

        securityPatternView = (PatternViewVac) findViewById(R.id.lpv_lock);
        setpattern_lottie = (LottieAnimationView) findViewById(R.id.setpattern_lottie);
        setpattern_lottie.setAnimation("frist2.json");
        setpattern_lottie.setScale(0.5f);//相对原大小的0.2倍
        setpattern_lottie.setSpeed(0.7f);
        setpattern_lottie.loop(true);
        setpattern_lottie.playAnimation();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setpattern_lottie != null) {
                    setpattern_lottie.cancelAnimation();
                }
                onBackPressed();
            }
        });
        securityPatternView.setOnPatternListener(new PatternViewVac.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {
            }

            @Override
            public void onPatternCellAdded(List<PatternViewVac.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternViewVac.Cell> pattern) {
                if (confirmMode) {
                    if (!LockPatternUtils.checkPattern(pattern, pattern1)) {
                        securityPatternView.setDisplayMode(PatternViewVac.DisplayMode.Wrong);
//                        tip.setTextColor(0xffcc0000);
                        tip.setText(R.string.security_password_not_match);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                securityPatternView.clearPattern();
                                tip.setTextColor(getResources().getColor(R.color.A3));
                                tip.setText(R.string.security_draw_pattern_next);
                            }
                        }, 700);
                    } else {
                        try {
                            VacPref.begin().setPasswd(LockPatternUtils.patternToString(pattern1), false).useNormalPasswd(false).commit();
                            Toast.makeText(context, R.string.security_password_setsuccessful, Toast.LENGTH_SHORT).show();
                            setResult(1);
                            if (firstSetup) {
                                setEmail();
                                firstSetup = false;
                            } else {
                                startListApp();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, R.string.security_set_password_successful, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (pattern.size() < 3) {
                        securityPatternView.setDisplayMode(PatternViewVac.DisplayMode.Wrong);
//                        tip.setTextColor(0xffcc0000);
                        tip.setText(R.string.security_pattern_short);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                securityPatternView.clearPattern();
                                tip.setTextColor(getResources().getColor(R.color.A3));
                                tip.setText(R.string.security_draw_pattern_next);
                            }
                        }, 700);
                    } else {
                        if (pattern1 == null)
                            pattern1 = new ArrayList<>(pattern);
                        else {
                            pattern1.clear();
                            pattern1.addAll(pattern);
                        }
                        securityPatternView.clearPattern();
                        enterConfirmMode();
                    }
                }
            }
        });
        enterGraphNormal();
    }

    public boolean firstSetup = false;
    public boolean togglePattern = true;

    public void setPasswdView() {
        setContentView(R.layout.security_password_setting);
//        randomNumpadIfPossible();
        View back = findViewById(R.id.back);
        setpassword_lottie = (LottieAnimationView) findViewById(R.id.setpassword_lottie);
        setpassword_lottie.setAnimation("frist3.json");
        setpassword_lottie.setScale(0.5f);//相对原大小的0.2倍
        setpassword_lottie.setSpeed(0.7f);
        setpassword_lottie.loop(true);
        setpassword_lottie.playAnimation();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((ImageView) findViewById(R.id.backspace)).setColorFilter(getResources().getColor(R.color.security_numpad_color));

        passdot = (NumberDot) findViewById(R.id.passwd_dot_id);


        passdot.setFlag(true);
        passdot.init(new NumberDot.ICheckListener() {
            @Override
            public void match(String pass) {
                VacPref.begin().setPasswd(pass, true).useNormalPasswd(true).commit();
                setResult(1);
                Toast.makeText(context, R.string.security_password_setsuccessful, Toast.LENGTH_SHORT).show();
                if (firstSetup) {
                    setEmail();
                    firstSetup = false;
                } else {
                    startListApp();
                }
            }
        });
        final TextView okBtn = (TextView) findViewById(R.id.ok);
        okBtn.setText(R.string.switch_pattern);
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (togglePattern) {
                    setGraphView();
                    if (setpassword_lottie != null) {
                        setpassword_lottie.cancelAnimation();
                    }
                    Tracker.sendEvent(Tracker.CATE_SETTING, Tracker.ACT_LEADER_SETTINGPASS, Tracker.ACT_LEADER_SETTINGPASS, 1L);
                    return;
                }
                if (setProgress == 0) {
                    if (size < 6) {
                        size = 0;
                        passdot.reset();
                        Toast.makeText(VacSetPattern.this, R.string.security_password_short, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (passdot.empty()) {
                        passwdIsEmpty();
                        return;
                    }
                    ++setProgress;
                    passdot.setFlag(false);
                    passdot.reset();

//                    okBtn.setVisibility(View.INVISIBLE);
                    okBtn.setText(R.string.security_reset_passwd_2_btn);
                    ((TextView) findViewById(R.id.title)).setText(R.string.security_set_confirm_password);
                    ((TextView) findViewById(R.id.tip)).setText(R.string.security_confirm_passwd_tip);
                } else if (setProgress == 1) {
                    setProgress = 0;
                    passdot.setFlag(false);
                    passdot.reset();
                    togglePattern = true;
                    okBtn.setText(R.string.switch_pattern);
                    ((TextView) findViewById(R.id.title)).setText(R.string.security_set_password);
                    ((TextView) findViewById(R.id.tip)).setText(R.string.security_set_passwd_tip);
                }
            }
        });
        findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passdot.backSpace();
                try {
                    if (size > 0) {
                        size--;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (passdot.empty() && !togglePattern) {
                    togglePattern = true;
                    okBtn.setText(R.string.switch_pattern);
                }
            }
        });
    }

    public void startListApp() {
        finish();
    }

    public List<PatternViewVac.Cell> pattern1, pattern2;
    public byte setProgress = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if (setpattern_lottie != null) {
            setpattern_lottie.cancelAnimation();
        }
        if (setpassword_lottie != null) {
            setpassword_lottie.cancelAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (setpattern_lottie != null) {
            setpattern_lottie.cancelAnimation();
        }
        if (setpassword_lottie != null) {
            setpassword_lottie.cancelAnimation();
        }
    }
}
