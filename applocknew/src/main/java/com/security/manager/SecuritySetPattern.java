package com.security.manager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.view.View;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.privacy.lock.R;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.LockPatternUtils;
import com.security.manager.page.SecurityPatternView;
import com.security.manager.page.NumberDot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by superjoy on 2014/10/24.
 */
public class SecuritySetPattern extends ClientActivitySecurity implements View.OnClickListener {
    public EditText email_address;
    CharSequence lastPasswd;
    public static final byte SET_EMPTY = 0;
    public static final byte SET_NORMAL_PASSWD = 1;
    public static final byte SET_GRAPH_PASSWD = 2;
    public static final byte SET_EMAIL = 3;

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
    protected void tips() {

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
        if (!App.getSharedPreferences().getBoolean("random", false)) {
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
        /*
        setContentView(R.layout.email_settle);
        email_address = (EditText) findViewById(R.id.passwd);
        email_address.setFocusableInTouchMode(true);
        View back = findViewById(R.id.back);
        if (firstSetup){
            back.setVisibility(View.GONE);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        int len = 0;
        String e = sp.getString("email", "");
        if (emailPattern.matcher(e).matches()) {
            email_address.setText(e);
            len = e.length();
        } else {
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String accountName = account.name;
                    email_address.setText(accountName);
                    len = accountName.length();
                    break;
                }
            }
        }
        email_address.setSelection(len);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftKeyboard(context, email_address, true);
            }
        }, 200);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = email_address.getText().toString();
                if (email.length() == 0) {
                    new AlertDialog.Builder(context).setCancelable(true).setMessage(R.string.sure_no_email).setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showSoftKeyboard(context, null, false);
                                    sp.edit().putString("email", email).apply();
                                    Http.executeGetResult(null, "http://52joyapp.com/superapplock/Mobile.sv.php", "a", "set", "email", email);
                                    startListApp();
                                }
                            }).create().show();
                } else if (!email.matches("[a-zA-Z0-9_.-]{1,32}+@[a-zA-Z0-9_-]+(\\.[a-zA-Z]+){1,6}")) {
                    new AlertDialog.Builder(context).setCancelable(true).setMessage(R.string.email_address_invalid).setIcon(R.drawable.icon).setPositiveButton(android.R.string.ok, null).create().show();
                } else {
                    showSoftKeyboard(context, null, false);
                    sp.edit().putString("email", email).apply();
                    Http.executeGetResult(null, "http://52joyapp.com/superapplock/Mobile.sv.php", "a", "set", "email", email);
                    Tracker.sendEvent(Tracker.CATE_DEFAULT, Tracker.ACT_EMAIL, Tracker.ACT_EMAIL, 1L);
                    startListApp();
                }
            }
        });
        */
        startListApp();
    }

    NumberDot passdot;

    @Override
    public void onClick(View view) {
        Button v = (Button) view;
        passdot.setNumber(v.getText().charAt(0));
        if (togglePattern){
            togglePattern = false;
            Button ok = (Button) findViewById(R.id.ok);
            ok.setText(R.string.security_btn_next);
            ok.setTextColor(getResources().getColor(R.color.security_numpad_font_color));
//            ok.setBackgroundResource(R.drawable.button_bg);
        }
    }

    public void passwdIsEmpty() {
//        Toast t = Toast.makeText(context, R.string.passwd_empty, Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        t.show();
    }

    public void setPasswd() {
        String pwd = lastPasswd.toString();
        String email = email_address.getText().toString();
        App.getSharedPreferences().edit().putString("email", email).apply();
        if (pwd.length() == 0) {
            passwdIsEmpty();
            return;
        }
        SecurityMyPref p = SecurityMyPref.begin();
        p.setPasswd(pwd, true).useNormalPasswd(true).commit();
        Tracker.sendEvent(Tracker.CATE_DEFAULT, Tracker.ACT_APPLOCK, Tracker.ACT_APPLOCK, 1L);
        startListApp();
    }

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

    public SecurityPatternView securityPatternView;
    public boolean confirmMode = false;

    @InjectView(R.id.passwd_cancel)
    Button cancel;

    @InjectView(R.id.tip)
    TextView tip;

    public void enterGraphNormal() {
        confirmMode = false;
        tip.setTextColor(getResources().getColor(R.color.security_body_text_1_inverse));
        tip.setText(R.string.security_draw_pattern);
        tip.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        cancel.setText(R.string.security_use_normal);
        cancel.setTextColor(getResources().getColor(R.color.security_numpad_font_color));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasswdView();
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
        setContentView(R.layout.security_pattern_view_set);
        ButterKnife.inject(this);
        View back = findViewById(R.id.back);
        if (firstSetup){
            back.setVisibility(View.GONE);
        }
        securityPatternView = (SecurityPatternView) findViewById(R.id.lpv_lock);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        securityPatternView.setOnPatternListener(new SecurityPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {
            }

            @Override
            public void onPatternCellAdded(List<SecurityPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<SecurityPatternView.Cell> pattern) {
                if (confirmMode) {
                    if (!LockPatternUtils.checkPattern(pattern, pattern1)) {
                        securityPatternView.setDisplayMode(SecurityPatternView.DisplayMode.Wrong);
                        tip.setTextColor(0xffcc0000);
                        tip.setText(R.string.security_password_not_match);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                securityPatternView.clearPattern();
                                tip.setTextColor(getResources().getColor(R.color.security_body_text_1_inverse));
                                tip.setText(R.string.security_draw_pattern_next);
                            }
                        }, 700);
                    } else {
                        try {
                            SecurityMyPref.begin().setPasswd(LockPatternUtils.patternToString(pattern1), false).useNormalPasswd(false).commit();
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
                        securityPatternView.setDisplayMode(SecurityPatternView.DisplayMode.Wrong);
                        tip.setTextColor(0xffcc0000);
                        tip.setText(R.string.security_pattern_short);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                securityPatternView.clearPattern();
                                tip.setTextColor(getResources().getColor(R.color.security_body_text_1_inverse));
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
        if (firstSetup){
            back.setVisibility(View.GONE);
        }
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
                SecurityMyPref.begin().setPasswd(pass, true).useNormalPasswd(true).commit();
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
        final Button okBtn = (Button) findViewById(R.id.ok);
        okBtn.setText(R.string.security_use_pattern);
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setBackgroundDrawable(null);
        okBtn.setTextColor(getResources().getColor(R.color.security_numpad_font_color));
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (togglePattern){
                    setGraphView();
                    return;
                }
                if (setProgress == 0) {
                    if (passdot.empty()) {
                        passwdIsEmpty();
                        return;
                    }
                    ++setProgress;
                    passdot.setFlag(false);
                    passdot.reset();
//                    okBtn.setVisibility(View.INVISIBLE);
                    okBtn.setText(R.string.security_reset_passwd_2_btn);
                    okBtn.setTextColor(getResources().getColor(R.color.security_numpad_font_color));
                    okBtn.setBackgroundDrawable(null);
                    ((TextView) findViewById(R.id.title)).setText(R.string.security_set_confirm_password);
                    ((TextView) findViewById(R.id.tip)).setText(R.string.security_confirm_passwd_tip);
                } else if (setProgress == 1){
                    setProgress = 0;
                    passdot.setFlag(false);
                    passdot.reset();
                    togglePattern = true;
                    okBtn.setText(R.string.security_use_pattern);
                    ((TextView) findViewById(R.id.title)).setText(R.string.security_set_password);
                    ((TextView) findViewById(R.id.tip)).setText(R.string.security_set_passwd_tip);
                }
            }
        });
        findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passdot.backSpace();
                if (passdot.empty() && !togglePattern){
                    togglePattern = true;
                    okBtn.setText(R.string.security_use_pattern);
                    okBtn.setBackgroundDrawable(null);
                    okBtn.setTextColor(getResources().getColor(R.color.security_numpad_font_color));
                }
            }
        });

        //ignore
        findViewById(R.id.passwd_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setProgress == 0) {
                    onBackPressed();
                } else {
                    --setProgress;
                    passdot.reset();
                    ((TextView) findViewById(R.id.title)).setText(R.string.security_set_password);
                    okBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void startListApp() {
        finish();
    }

    public List<SecurityPatternView.Cell> pattern1, pattern2;
    public byte setProgress = 0;
}
