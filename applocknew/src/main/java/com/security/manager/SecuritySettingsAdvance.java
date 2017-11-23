package com.security.manager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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

import com.ivymobi.applock.free.R;
import com.security.lib.customview.MyWidgetContainer;
import com.security.manager.lib.Utils;
import com.security.manager.meta.SecurityMyPref;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by superjoy on 2014/9/4.
 */
public class SecuritySettingsAdvance extends ClientActivitySecurity {
    public static byte idx = 0;
    public static int SETTING_PERMISSION;
    public static byte SETTING_NOTIFICATION;
    public static byte SETTING_POWER_MODE;
    private static final int REQUSETSET = 110;
    private static final int REQUSETSET2 = 120;


    ListView lv;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    Intent intent;
    private Handler handler;
    private MyWidgetContainer wc;


    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void setupView() {
        setContentView(R.layout.security_settings);

        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        handler = new Handler();
        intent = getIntent();
        final int[] items;
        if (Build.VERSION.SDK_INT >= 21) {
            if (SecurityMyPref.getshowLockAll()) {
                SETTING_PERMISSION = 0;
                SETTING_NOTIFICATION = 1;
                SETTING_POWER_MODE = 2;
                items = new int[]{
                        R.string.security_service_title,
                        R.string.security_nofification,
                        R.string.security_power_mode,
                };
            } else {
                SETTING_PERMISSION = 0;
                SETTING_POWER_MODE = 1;
                items = new int[]{
                        R.string.security_service_title,
                        R.string.security_power_mode,
                };

            }

        } else {

            if (SecurityMyPref.getshowLockAll()) {
                SETTING_NOTIFICATION = 0;
                SETTING_POWER_MODE = 1;
                items = new int[]{
                        R.string.security_nofification,
                        R.string.security_power_mode,
                };
            } else {
                SETTING_POWER_MODE = 0;
                items = new int[]{
                        R.string.security_power_mode,
                };
            }
        }

        ButterKnife.inject(this);
        setupToolbar();

        setup(R.string.security_settings_preference);
        setViewVisible(View.GONE, R.id.search_button, R.id.bottom_action_bar, R.id.progressBar);
        findViewById(R.id.abs_list).setVisibility(View.VISIBLE);


        lv = (ListView) findViewById(R.id.abs_list);
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

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                Log.e("ivalue", i + "---");

                if (SecurityMyPref.getshowLockAll()) {

                    if (i == SETTING_POWER_MODE) {
                        view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_setting_item_two, null, false);
                        ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                        ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_power_mode_des);
                        final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);

                        checkbox.setImageResource(R.drawable.security_ne);
                        view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSaveMode(SecuritySettingsAdvance.this);
                            }
                        });
                    } else if (i == SETTING_NOTIFICATION) {
                        view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_setting_item_two, null, false);
                        ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                        ((TextView) view.findViewById(R.id.security_text_des)).setVisibility(View.GONE);
                        final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                        if (SecurityMyPref.getNotification()) {
                            checkbox.setImageResource(R.drawable.security_setting_check);
                        } else {
                            checkbox.setImageResource(R.drawable.security_setting_not_check);
                        }
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SecurityMyPref.getNotification()) {
                                    checkbox.setImageResource(R.drawable.security_setting_not_check);
                                    SecurityMyPref.setNotification(false);
                                    stopService(new Intent(SecuritySettingsAdvance.this, NotificationService.class));

                                } else {
                                    checkbox.setImageResource(R.drawable.security_setting_check);
                                    SecurityMyPref.setNotification(true);
                                    stopService(new Intent(SecuritySettingsAdvance.this, NotificationService.class));
                                    startService(new Intent(SecuritySettingsAdvance.this, NotificationService.class));

                                }

                                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_LOCK_NOTIFICAO, Tracker.ACT_SETTING_LOCK_NOTIFICAO, 1L);


                            }
                        });
                    } else if (i == SETTING_PERMISSION) {
                        view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_setting_item_two, null, false);
                        ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                        ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_service_description);
                        final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                        if (!Utils.requireCheckAccessPermission(SecuritySettingsAdvance.this)) {
                            checkbox.setImageResource(R.drawable.security_setting_check);
                        } else {
                            checkbox.setImageResource(R.drawable.security_setting_not_check);
                        }
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSettingPermission50(SecuritySettingsAdvance.this);
                            }
                        });
                    }

                } else {

                    if (i == SETTING_POWER_MODE) {
                        view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_setting_item_two, null, false);
                        ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                        ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_power_mode_des);
                        final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);

                        checkbox.setImageResource(R.drawable.security_ne);
                        view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSaveMode(SecuritySettingsAdvance.this);
                            }
                        });


                    } else if (i == SETTING_PERMISSION) {
                        view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_setting_item_two, null, false);
                        ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                        ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_service_description);
                        final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                        if (!Utils.requireCheckAccessPermission(SecuritySettingsAdvance.this)) {
                            checkbox.setImageResource(R.drawable.security_setting_check);
                        } else {
                            checkbox.setImageResource(R.drawable.security_setting_not_check);
                        }
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showSettingPermission50(SecuritySettingsAdvance.this);
                            }
                        });
                    }
                }
                return view;
            }
        });
    }

    public void showSaveMode(Context context) {
        try {
//            Log.e("chfq", "==省电模式==");
            final Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, REQUSETSET2);
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    wc = new MyWidgetContainer(getApplicationContext(),
                            Gravity.START | Gravity.BOTTOM,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            false);
                    View alertDialogView = View.inflate(SecuritySettingsAdvance.this, R.layout.permission_translate2, null);


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
                            wc.removeFromWindow();
                            wc = null;
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

    public void showSettingPermission50(Context context) {
        //使用记录，和5.0权限一样
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
                    View alertDialogView = View.inflate(SecuritySettingsAdvance.this, R.layout.permission_translate, null);


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
                            wc.removeFromWindow();
                            wc = null;
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
    protected void onResume() {
//        lv.getAdapter().notify();
        super.onResume();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_tab_setting);
            actionBar.setDisplayHomeAsUpEnabled(true);

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
    }
}