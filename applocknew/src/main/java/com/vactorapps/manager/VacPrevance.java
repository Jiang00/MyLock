package com.vactorapps.manager;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ivymobi.applock.free.R;
import com.vactorapps.lib.customview.VacWidgetContainer;
import com.vactorappsapi.manager.lib.Utils;

/**
 * Created by superjoy on 2014/9/4.
 */
public class VacPrevance extends AppCompatActivity {
    public static byte idx = 0;
    public static int SETTING_PERMISSION;//使用记录，和5.0权限一样
    public static int SETTING_POWER_MODE;//省电模式
    public static int SETTING_CAMERA;//照相机权限
    public static int SETTING_ALERT_WINDOW;//悬浮窗权限
    private static final int REQUSETSET = 110;
    private static final int REQUSETSET2 = 120;
    private static final int REQUSETSET3 = 130;
    private static final int REQUSETSET4 = 140;
    private boolean permissionFlag = false;
    private boolean powerFlag = false;
    private boolean windowFlag = false;

    private VacWidgetContainer wc;


    ListView lv;
    Toolbar toolbar;
    Intent intent;
    private Handler handler;
    private boolean onPause;
    private LottieAnimationView pre_lottie2;
    FrameLayout main_back_pre;
    LottieAnimationView main_back_pre_lottie;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean pre_open = intent.getBooleanExtra("pre_open", false);
        if (pre_open) {
            if (main_back_pre != null && main_back_pre_lottie != null) {
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
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_settings_advance);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        intent = getIntent();
        final int[] items;
        handler = new Handler();
        main_back_pre = (FrameLayout) findViewById(R.id.main_back_pre);
        main_back_pre_lottie = (LottieAnimationView) findViewById(R.id.main_back_pre_lottie);
        boolean pre_open = intent.getBooleanExtra("pre_open", false);
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
        //6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SETTING_PERMISSION = 0;
            SETTING_POWER_MODE = 1;
            SETTING_CAMERA = 2;
            SETTING_ALERT_WINDOW = 3;
            items = new int[]{
                    R.string.security_service_title2,
                    R.string.security_power_mode,
                    R.string.open_camera,
                    R.string.open_suspension_window,
            };
        } else if (Build.VERSION.SDK_INT >= 21) {//5.0

            SETTING_PERMISSION = 0;
            SETTING_POWER_MODE = 1;
            SETTING_CAMERA = 2;
            SETTING_ALERT_WINDOW = 3;
            items = new int[]{
                    R.string.security_service_title,
                    R.string.security_power_mode,
            };
        } else {
            SETTING_POWER_MODE = 0;
            SETTING_PERMISSION = 1;
            SETTING_CAMERA = 2;
            SETTING_ALERT_WINDOW = 3;
            items = new int[]{
                    R.string.security_power_mode,
            };
        }

//        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


        lv = (ListView) this.findViewById(R.id.my_abs_list);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public Object getItem(int i) {
                return i;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                if (i == SETTING_POWER_MODE) {
                    //省电模式
                    view = LayoutInflater.from(VacPrevance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_power_mode_des2);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (isAccessibilitySettingsOn(VacPrevance.this)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                        powerFlag = true;
                    } else {
                        powerFlag = false;
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isAccessibilitySettingsOn(VacPrevance.this)) {
                                showSaveMode(VacPrevance.this, SETTING_POWER_MODE);
                                Log.e("chfq", "==showSaveMode===");
                                //启动服务
                                handler.removeCallbacks(runnable_acc);
                                handler.post(runnable_acc);
                            } else {
                                Toast.makeText(VacPrevance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else if (i == SETTING_PERMISSION) {
                    //5.0权限
                    view = LayoutInflater.from(VacPrevance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_service_description2);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (!Utils.requireCheckAccessPermission(VacPrevance.this)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                        permissionFlag = true;
                    } else {
                        permissionFlag = false;
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utils.requireCheckAccessPermission(VacPrevance.this)) {
                                showSettingPermission50(VacPrevance.this);
                                //启动服务
//                                Intent intent = new Intent(VacPrevance.this, PreferenceService.class);
//                                intent.putExtra("setting_permission", true);
//                                startService(intent);
                                handler.removeCallbacks(runnable_ust);
                                handler.post(runnable_ust);
                            } else {
                                Toast.makeText(VacPrevance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else if (i == SETTING_CAMERA) {
                    //照相机
                    view = LayoutInflater.from(VacPrevance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.open_camera_describe);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (ContextCompat.checkSelfPermission(VacPrevance.this,
                            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //先判断有没有权限
                        checkbox.setImageResource(R.drawable.security_setting_check);
                    } else {
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }

                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(VacPrevance.this,
                                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(VacPrevance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            } else {
                                showSaveMode(VacPrevance.this, SETTING_CAMERA);
                                handler.removeCallbacks(runnable_camera);
                                handler.post(runnable_camera);
                                //启动服务
//                                Intent intent = new Intent(VacPrevance.this, PreferenceService.class);
//                                intent.putExtra("setting_camera", true);
//                                startService(intent);
                            }
                        }
                    });
                } else if (i == SETTING_ALERT_WINDOW) {
                    //悬浮窗
                    view = LayoutInflater.from(VacPrevance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.open_suspension_window_describe);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (Settings.canDrawOverlays(VacPrevance.this)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                        windowFlag = true;
                    } else {
                        windowFlag = false;
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!Settings.canDrawOverlays(VacPrevance.this)) {
                                showSaveMode(VacPrevance.this, SETTING_ALERT_WINDOW);
                                //启动服务
//                                Intent intent = new Intent(VacPrevance.this, PreferenceService.class);
//                                intent.putExtra("setting_alert_window", true);
//                                startService(intent);
                                handler.removeCallbacks(runnable_xuanfu);
                                handler.post(runnable_xuanfu);
                            } else {
                                Toast.makeText(VacPrevance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                return view;
            }
        });
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.setting_power);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void showSaveMode(Context context, final int pre) {
        try {
//            Log.e("chfq", "==省电模式==");
            if (pre == SETTING_CAMERA) {
                try {
                    //Open the specific App Info page:
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    startActivityForResult(intent, REQUSETSET3);
                } catch (ActivityNotFoundException e) {
                    //Open the generic Apps page:
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivityForResult(intent, REQUSETSET3);
                }
                //申请WRITE_EXTERNAL_STORAGE权限
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            } else if (pre == SETTING_ALERT_WINDOW) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUSETSET4);
            } else {
                final Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, REQUSETSET2);
            }
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    wc = new VacWidgetContainer(getApplicationContext(),
                            Gravity.START | Gravity.BOTTOM,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            false);
                    View alertDialogView = View.inflate(VacPrevance.this, R.layout.security_show_permission, null);
                    TextView pre_tip = (TextView) alertDialogView.findViewById(R.id.pre_tip);
//                    TextView cancle = (TextView) alertDialogView.findViewById(R.id.cancle);
                    pre_lottie2 = (LottieAnimationView) alertDialogView.findViewById(R.id.pre_lottie);
                    pre_lottie2.setAnimation("pre.json");
                    pre_lottie2.setScale(0.4f);//相对原大小的0.2倍
//                    pre_lottie.setSpeed(0.7f);
                    pre_lottie2.loop(true);
                    pre_lottie2.playAnimation();
                    if (pre == SETTING_POWER_MODE) {
                        pre_tip.setText(getResources().getString(R.string.pre_tip3));
//                        cancle.setVisibility(View.VISIBLE);
                    } else {
//                        per_tip.setText(getResources().getString(R.string.pre_tip2));
//                        cancle.setVisibility(View.GONE);
                    }

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
                                pre_lottie2 = null;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable runnable_acc = new Runnable() {
        @Override
        public void run() {
            if (isAccessibilitySettingsOn(VacPrevance.this)) {
                startActivity(new Intent(VacPrevance.this, VacPrevance.class).putExtra("pre_open", true));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };
    Runnable runnable_ust = new Runnable() {
        @Override
        public void run() {
            if (!Utils.requireCheckAccessPermission(VacPrevance.this)) {
                startActivity(new Intent(VacPrevance.this, VacPrevance.class).putExtra("pre_open", true));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };
    Runnable runnable_camera = new Runnable() {
        @Override
        public void run() {
            if (ContextCompat.checkSelfPermission(VacPrevance.this,
                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(VacPrevance.this, VacPrevance.class).putExtra("pre_open", true));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };
    Runnable runnable_xuanfu = new Runnable() {
        @Override
        public void run() {
            if (Settings.canDrawOverlays(VacPrevance.this)) {
                startActivity(new Intent(VacPrevance.this, VacPrevance.class).putExtra("pre_open", true));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    //使用记录，和5.0权限一样
    public void showSettingPermission50(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUSETSET);
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    wc = new VacWidgetContainer(getApplicationContext(),
                            Gravity.START | Gravity.BOTTOM,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            false);
                    View alertDialogView = View.inflate(VacPrevance.this, R.layout.security_show_permission, null);
                    pre_lottie2 = (LottieAnimationView) alertDialogView.findViewById(R.id.pre_lottie);
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
                                pre_lottie2 = null;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
    }

    @Override
    protected void onResume() {
//        lv.getAdapter().notify();
        super.onResume();
        if (onPause) {
            onPause = false;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("chfq", "==requestCode======" + requestCode);
        if (pre_lottie2 != null) {
            pre_lottie2.cancelAnimation();
            pre_lottie2 = null;
        }
        if (requestCode == REQUSETSET) {
            Log.e("chfq", "=110=");
            if (!Utils.requireCheckAccessPermission(this)) {
                Log.e("chfq", "==true==");
                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, "偏好设置", "使用记录权限开启", 1L);
            }
        } else if (requestCode == REQUSETSET2) {
            Log.e("chfq", "=120=");
            if (isAccessibilitySettingsOn(this)) {
                Log.e("chfq", "==true==");
                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, "偏好设置", "省电模式开启", 1L);
            }
        }
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        // TestService为对应的服务
        final String service = getPackageName() + "/" + VacAccessibilityService.class.getCanonicalName();
//        Log.e("chfq", "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.VacAccessibilityService
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHomeKeyEventReceiver);
        if (wc != null) {
            wc.removeFromWindow();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}