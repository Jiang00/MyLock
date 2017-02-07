package com.security.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

//import com.ivy.util.Utility;
import com.ivymobi.applock.free.R;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.SlideMenu;
import com.security.manager.page.showDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by superjoy on 2014/9/4.
 */
public class SecuritySettings extends ClientActivitySecurity {
    public static byte idx = 0;
    public static byte SETTING_SLOT;
    public static byte SETTING_MODE;
    public static byte SETTING_FINGERPRINT;
    public static byte SETTING_HIDE_GRAPH_PATH;
    public static byte SETTING_NOTIFICATION;

    public static byte SETTING_LOCK_NEW;
    public static byte SETTING_SETTING_ADVANCE;
    public static byte SETTING_RATE;

    int[] items;
    int[] icon;

    ListView lv;

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

    BaseAdapter adapter;

    Intent intent;



    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void setupView() {
        setContentView(R.layout.security_settings);
        ButterKnife.inject(this);
        setupToolbar();
        SETTING_SLOT = 0;
        SETTING_MODE = 1;
        SETTING_HIDE_GRAPH_PATH = 2;
        SETTING_LOCK_NEW = 3;
        SETTING_NOTIFICATION = 4;

        SETTING_SETTING_ADVANCE = 5;
        SETTING_RATE = 6;
        items = new int[]{
                R.string.security_over_short,
                R.string.security_reset_password,
                R.string.security_hide_path,
                R.string.security_newapp_lock,
                R.string.security_nofification,
                R.string.security_settings_preference,
                R.string.security_help_share
        };


        icon = new int[]{
                R.drawable.security_brif_setting,
                R.drawable.security_reset_password,
                R.drawable.security_hide_pattern,
                R.drawable.security_lock_new,
                R.drawable.security_notification,
                R.drawable.security_permission_center,
                R.drawable.security_rate_me
        };

        setup(R.string.security_tab_setting);
        normalTitle.setText("   " + getResources().getString(R.string.security_tab_setting));
        normalTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.security_back), null, null, null);
        normalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

                if (i == SETTING_MODE) {
                    view = getLayoutInflater().inflate(R.layout.security_setting_item, viewGroup, false);
                    TextView title = (TextView) view.findViewById(R.id.security_title_bar_te);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);
                    TextView desc = (TextView) view.findViewById(R.id.security_text_des);
                    title.setText(R.string.security_reset_passwd_2_btn);
                    desc.setText(SecurityMyPref.isUseNormalPasswd() ? R.string.security_password_lock : R.string.security_use_pattern);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            setPasswd(true, !SharPre.isUseNormalPasswd());
                            showDialog.showResetPasswordDialog(v.getContext());
                            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_RESETPAS, Tracker.ACT_SETTING_RESETPAS, 1L);
                        }
                    });
                } else if (i == SETTING_SLOT) {
                    view = LayoutInflater.from(SecuritySettings.this).inflate(R.layout.security_setting_item, null, false);
                    LinearLayout it = (LinearLayout) view.findViewById(R.id.security_linera);
                    int slot = App.getSharedPreferences().getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
                    ((TextView) it.findViewById(R.id.security_text_des)).setText(getResources().getStringArray(R.array.brief_slot)[slot]);
                    ((TextView) it.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);

                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                } else if (i == SETTING_LOCK_NEW) {
                    view = LayoutInflater.from(SecuritySettings.this).inflate(R.layout.security_setting_item_two, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setVisibility(View.GONE);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);

                    if (App.getSharedPreferences().getBoolean(SecurityMyPref.LOCK_NEW, SecurityMyPref.LOCK_DEFAULT)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                    } else {
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (App.getSharedPreferences().getBoolean(SecurityMyPref.LOCK_NEW, SecurityMyPref.LOCK_DEFAULT)) {
                                checkbox.setImageResource(R.drawable.security_setting_not_check);
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_NEW_APP, MyTrack.ACT_NEW_APP, 1L);
                                App.getSharedPreferences().edit().putBoolean(SecurityMyPref.LOCK_NEW, false).apply();

                            } else {
                                checkbox.setImageResource(R.drawable.security_setting_check);
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_NEW_APP, MyTrack.ACT_NEW_APP, 1L);
                                App.getSharedPreferences().edit().putBoolean(SecurityMyPref.LOCK_NEW, true).apply();
                            }

                            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_LOCK_NEW, Tracker.ACT_SETTING_LOCK_NEW, 1L);
                        }
                    });

                } else if (i == SETTING_NOTIFICATION) {
                    view = LayoutInflater.from(SecuritySettings.this).inflate(R.layout.security_setting_item_two, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setVisibility(View.GONE);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);

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
                                stopService(new Intent(SecuritySettings.this, NotificationService.class));

                            } else {
                                checkbox.setImageResource(R.drawable.security_setting_check);
                                SecurityMyPref.setNotification(true);
                                stopService(new Intent(SecuritySettings.this, NotificationService.class));
                                startService(new Intent(SecuritySettings.this, NotificationService.class));
                            }

                            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_LOCK_NOTIFICAO, Tracker.ACT_SETTING_LOCK_NOTIFICAO, 1L);


                        }
                    });


                } else if (i == SETTING_SETTING_ADVANCE) {
                    view = LayoutInflater.from(SecuritySettings.this).inflate(R.layout.security_setting_item, null, false);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);

                    LinearLayout it = (LinearLayout) view.findViewById(R.id.security_linera);
                    ((TextView) it.findViewById(R.id.security_text_des)).setText(R.string.security_settings_preference_desc);
                    ((TextView) it.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                } else if (i == SETTING_RATE) {
                    view = LayoutInflater.from(SecuritySettings.this).inflate(R.layout.security_rate_it, null, false);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);

                    FrameLayout it = (FrameLayout) view.findViewById(R.id.security_rate);
                    TextView textRate = (TextView) view.findViewById(R.id.security_rate_text);
                    textRate.setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                } else if (i == SETTING_HIDE_GRAPH_PATH) {
                    view = LayoutInflater.from(SecuritySettings.this).inflate(R.layout.security_setting_item_two, null, false);
                    view.findViewById(R.id.setting_icon).setBackgroundResource(icon[i]);

                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setVisibility(View.GONE);
                    final ImageView b = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (App.getSharedPreferences().getBoolean("hide_path", false)) {
                        b.setImageResource(R.drawable.security_setting_check);
                    } else {
                        b.setImageResource(R.drawable.security_setting_not_check);
                    }
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (App.getSharedPreferences().getBoolean("hide_path", false)) {
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_HIDE_PATH, MyTrack.ACT_HIDE_PATH, 1L);
                                App.getSharedPreferences().edit().putBoolean("hide_path", false).apply();
                                b.setImageResource(R.drawable.security_setting_not_check);


                            } else {
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_HIDE_PATH, MyTrack.ACT_HIDE_PATH, 1L);
                                App.getSharedPreferences().edit().putBoolean("hide_path", true).apply();
                                b.setImageResource(R.drawable.security_setting_check);

                            }

                            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_HIDEPATH, Tracker.ACT_SETTING_HIDEPATH, 1L);

                        }
                    });


                }

                initclick();

                return view;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        intent=getIntent();
        if (lv != null) {
            try {
                ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                setupView();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == SETTING_SLOT) {
                SharedPreferences sp = App.getSharedPreferences();
                int idx = sp.getInt(SecurityMyPref.PREF_BRIEF_SLOT, SecurityMyPref.PREF_DEFAULT);
                new AlertDialog.Builder(context).setTitle(R.string.security_short_exit_slot).setSingleChoiceItems(R.array.brief_slot, idx, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.getSharedPreferences().edit().putInt(SecurityMyPref.PREF_BRIEF_SLOT, i).apply();
                        notifyDatasetChanged();
                        dialogInterface.dismiss();
                        if (i == 0) {
                            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_EVERY_TIME, Tracker.ACT_SETTING_EVERY_TIME, 1L);

                        } else if (i == 1) {
                            Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_FIVE_MINIUTE, Tracker.ACT_SETTING_FIVE_MINIUTE, 1L);

                        } else if (i == 2) {
                            Tracker.sendEvent(Tracker.ACT_SETTING_SCREEN_OFF, Tracker.ACT_SETTING_SCREEN_OFF, Tracker.ACT_SETTING_FIVE_MINIUTE, 1L);

                        }

                    }
                }).create().show();
                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_BRIEF, Tracker.ACT_SETTING_BRIEF, 1L);


            } else if (id == SETTING_RATE) {
                if (!SecurityMyPref.isOptionPressed(SecurityMyPref.OPT_RATE_REDDOT)) {
                    SecurityMyPref.pressOption(SecurityMyPref.OPT_RATE_REDDOT);
                }
                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE, 1L);
                SecurityShare.rate(context);
                notifyDatasetChanged();
            } else if (id == SETTING_SETTING_ADVANCE) {
//                Intent intent = new Intent(SecuritySettings.this, SecuritySettingsAdvance.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_PERMISSION, Tracker.ACT_SETTING_PERMISSION, 1L);
//                Utility.goPermissionCenter(SecuritySettings.this, "");
            }
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

    public void notifyDatasetChanged() {
        if (lv != null) {
            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
        }
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
}