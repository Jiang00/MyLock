package com.security.manager;


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
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ivymobi.applock.free.R;
import com.security.lib.customview.MyWidgetContainer;
import com.security.manager.lib.Utils;

/**
 * Created by superjoy on 2014/9/4.
 */
public class SecuritySettingsAdvance extends AppCompatActivity {
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

    private MyWidgetContainer wc;


    ListView lv;

    Intent intent;
    private Handler handler;
    private boolean onPause;
    private LottieAnimationView pre_lottie2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_settings_advance);

        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        intent = getIntent();
        final int[] items;
        handler = new Handler();
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

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_power_mode_des2);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (isAccessibilitySettingsOn(SecuritySettingsAdvance.this)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                        powerFlag = true;
                    } else {
                        powerFlag = false;
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isAccessibilitySettingsOn(SecuritySettingsAdvance.this)) {
                                showSaveMode(SecuritySettingsAdvance.this, SETTING_POWER_MODE);
                                Log.e("chfq", "==showSaveMode===");
                                //启动服务
                                Intent intent = new Intent(SecuritySettingsAdvance.this, PreferenceService.class);
                                intent.putExtra("setting_power_mode", true);
                                startService(intent);
                            } else {
                                Toast.makeText(SecuritySettingsAdvance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else if (i == SETTING_PERMISSION) {
                    //5.0权限
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_service_description2);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (!Utils.requireCheckAccessPermission(SecuritySettingsAdvance.this)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                        permissionFlag = true;
                    } else {
                        permissionFlag = false;
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utils.requireCheckAccessPermission(SecuritySettingsAdvance.this)) {
                                showSettingPermission50(SecuritySettingsAdvance.this);
                                //启动服务
                                Intent intent = new Intent(SecuritySettingsAdvance.this, PreferenceService.class);
                                intent.putExtra("setting_permission", true);
                                startService(intent);
                            } else {
                                Toast.makeText(SecuritySettingsAdvance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else if (i == SETTING_CAMERA) {
                    //照相机
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.open_camera_describe);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (ContextCompat.checkSelfPermission(SecuritySettingsAdvance.this,
                            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //先判断有没有权限
                        checkbox.setImageResource(R.drawable.security_setting_check);
                    } else {
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }

                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(SecuritySettingsAdvance.this,
                                    android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(SecuritySettingsAdvance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            } else {
                                showSaveMode(SecuritySettingsAdvance.this, SETTING_CAMERA);
                                //启动服务
                                Intent intent = new Intent(SecuritySettingsAdvance.this, PreferenceService.class);
                                intent.putExtra("setting_camera", true);
                                startService(intent);
                            }
                        }
                    });
                } else if (i == SETTING_ALERT_WINDOW) {
                    //悬浮窗
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.open_suspension_window_describe);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (Settings.canDrawOverlays(SecuritySettingsAdvance.this)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                        windowFlag = true;
                    } else {
                        windowFlag = false;
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!Settings.canDrawOverlays(SecuritySettingsAdvance.this)) {
                                showSaveMode(SecuritySettingsAdvance.this, SETTING_ALERT_WINDOW);
                                //启动服务
                                Intent intent = new Intent(SecuritySettingsAdvance.this, PreferenceService.class);
                                intent.putExtra("setting_alert_window", true);
                                startService(intent);
                            } else {
                                Toast.makeText(SecuritySettingsAdvance.this, R.string.pre_open, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                return view;
            }
        });
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
                    wc = new MyWidgetContainer(getApplicationContext(),
                            Gravity.START | Gravity.BOTTOM,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            false);
                    View alertDialogView = View.inflate(SecuritySettingsAdvance.this, R.layout.security_show_permission, null);
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

    //使用记录，和5.0权限一样
    public void showSettingPermission50(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUSETSET);
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    wc = new MyWidgetContainer(getApplicationContext(),
                            Gravity.START | Gravity.BOTTOM,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            false);
                    View alertDialogView = View.inflate(SecuritySettingsAdvance.this, R.layout.security_show_permission, null);
                    pre_lottie2 = (LottieAnimationView) alertDialogView.findViewById(R.id.pre_lottie);
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
            stopService(new Intent(this, PreferenceService.class));
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
        final String service = getPackageName() + "/" + AccessibilityService.class.getCanonicalName();
//        Log.e("chfq", "service:" + service);
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService
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
        stopService(new Intent(this, PreferenceService.class));
    }
}