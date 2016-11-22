package com.security.manager;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.privacy.lock.R;
import com.security.manager.lib.Utils;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.ShowDialogview;
import com.security.manager.page.SlideMenu;
import com.security.manager.page.showDialog;

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


    ListView lv;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    Intent intent;


    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void setupView() {
        setContentView(R.layout.security_settings);
        intent = getIntent();
        final int[] items;
        if (Build.VERSION.SDK_INT >= 21) {
            SETTING_PERMISSION = 0;
            SETTING_NOTIFICATION = 1;
            SETTING_POWER_MODE = 2;
            items = new int[]{
                    R.string.security_service_title,
                    R.string.security_nofification,
                    R.string.security_power_mode,
            };
        } else {
            SETTING_NOTIFICATION = 0;
            SETTING_POWER_MODE = 1;
            items = new int[]{
                    R.string.security_nofification,
                    R.string.security_power_mode,
            };

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

                if (i == SETTING_NOTIFICATION) {
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
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
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
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
                            ShowDialogview.showPermission50(SecuritySettingsAdvance.this);
                        }
                    });
                } else if (i == SETTING_POWER_MODE) {
                    view = LayoutInflater.from(SecuritySettingsAdvance.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_power_mode_des);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);

                    checkbox.setImageResource(R.drawable.security_ne);
                    view.findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShowDialogview.showAccess(SecuritySettingsAdvance.this);
                        }
                    });


                }
                return view;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupView();
    }

    private void setupToolbar() {
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
            if (intent.getExtra("launchname") != null) {
                this.finish();
                Intent nIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName() + "");
                nIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nIntent);
            }else{
                this.finish();

            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (intent.getExtra("launchname") != null) {
            this.finish();
            Intent nIntent = new Intent(this,SecurityPatternActivity.class);
            nIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(nIntent);
        }else{
            this.finish();

        }
    }
}