package com.security.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.privacy.lock.R;
import com.security.manager.meta.Pref;
import com.security.manager.page.showDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by superjoy on 2014/9/4.
 */
public class Settings extends ClientActivity {
    public static byte idx = 0;
    public static final byte SETTING_SLOT = idx++;
    public static final byte SETTING_MODE = idx++;
    public static final byte SETTING_HIDE_GRAPH_PATH = idx++;
    public static final byte SETTING_RANDOM = idx++;
    //    public static final byte SETTING_ADVANCED = idx++;
    //    public static final int SETTING_FAKE_SELECTOR = idx++;
    public static final byte SETTING_LOCK_NEW = idx++;
    public static final byte SETTING_RATE = idx++;
    //    public static final byte SETTING_SHARE = idx++;
    public static final int SETTING_INTRUDER = idx++;

    public static final byte REQ_CODE_PASS = 2;
    public static final byte REQ_CODE_PATTERN = 4;
    static final int[] items = new int[]{
            R.string.security_over_short,
            0,
//            R.string.help_normal_pass,
//            R.string.help_graph_pass,
//            R.string.secure_email,
            R.string.security_hide_path,
//            R.string.random_keyboard,
//            R.string.advanced_security,
//            R.string.fake_selector,
//            R.string.intruder,
//            R.string.pause_protect,
//            R.string.show_noti,
            R.string.security_newapp_lock,
            R.string.security_help_share,
//            R.string.help_share,
//            R.string.lost_found
    };

    ListView lv;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;


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
        normalTitle.setText("   "+getResources().getString(R.string.security_tab_setting));
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
                    view = getLayoutInflater().inflate(R.layout.security_invade_line, viewGroup, false);

                    TextView title = (TextView) view.findViewById(R.id.security_title_bar_te);
                    TextView desc = (TextView) view.findViewById(R.id.security_text_des);
                    title.setText(R.string.security_reset_passwd_2_btn);
                    desc.setText(Pref.isUseNormalPasswd() ? R.string.security_password_lock : R.string.security_use_pattern);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            setPasswd(true, !SharPre.isUseNormalPasswd());
                            showDialog.showResetPasswordDialog(v.getContext());
                        }
                    });
                } else if (i == SETTING_SLOT) {
                    view = LayoutInflater.from(Settings.this).inflate(R.layout.security_invade_line, null, false);
                    LinearLayout it = (LinearLayout) view.findViewById(R.id.security_linera);

                    int slot = App.getSharedPreferences().getInt(Pref.PREF_BRIEF_SLOT, Pref.PREF_DEFAULT);
                    ((TextView) it.findViewById(R.id.security_text_des)).setText(getResources().getStringArray(R.array.brief_slot)[slot]);
                    ((TextView) it.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                }
//                else if (i == SETTING_RANDOM) {
//                    view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.security_notica_it, null, false);
//
//                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
////                    ((TextView) view.findViewById(R.id.desc)).setText(R.string.random_keyboard_desc);
//                    view.findViewById(R.id.security_text_des).setVisibility(View.GONE);
//                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
//                    if (Application.getSharedPreferences().getBoolean("random", false)) {
//                        checkbox.setImageResource(R.drawable.security_setting_check);
//                    } else {
//                        checkbox.setImageResource(R.drawable.security_setting_not_check);
//                    }
//
//                    checkbox.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (Application.getSharedPreferences().getBoolean("random", false)) {
//                                checkbox.setImageResource(R.drawable.security_setting_not_check);
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_RANDOM, MyTrack.ACT_RANDOM, 1L);
//                                Application.getSharedPreferences().edit().putBoolean("random", false).apply();
//                            } else {
//                                checkbox.setImageResource(R.drawable.security_setting_check);
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_RANDOM, MyTrack.ACT_RANDOM, 1L);
//                                Application.getSharedPreferences().edit().putBoolean("random", true).apply();
//
//                            }
//                        }
//                    });
//
//                }
                else if (i == SETTING_INTRUDER) {
                    view = LayoutInflater.from(Settings.this).inflate(R.layout.security_notica_it, null, false);

                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setText(R.string.security_show_noti);
                    final ImageView checkBox = (ImageView) view.findViewById(R.id.security_set_checked);

                    if (Pref.fetchIntruder()) {
                        checkBox.setImageResource(R.drawable.security_setting_check);
                    } else {
                        checkBox.setImageResource(R.drawable.security_setting_not_check);
                    }

                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Pref.fetchIntruder()) {
                                checkBox.setImageResource(R.drawable.security_setting_not_check);
                                Toast.makeText(getApplicationContext(), R.string.security_intruder_off_new, Toast.LENGTH_SHORT).show();
                                Pref.setFetchIntruder(false);
                            } else {
                                checkBox.setImageResource(R.drawable.security_setting_check);
                                Toast.makeText(getApplicationContext(), R.string.security_intruder_on_new, Toast.LENGTH_SHORT).show();

                                Pref.setFetchIntruder(true);

                            }
                        }
                    });


                } else if (i == 3) {
                    view = LayoutInflater.from(Settings.this).inflate(R.layout.security_notica, null, false);
                    ((TextView) view.findViewById(R.id.security_title_bar_te)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.security_text_des)).setVisibility(View.GONE);
                    final ImageView checkbox = (ImageView) view.findViewById(R.id.security_set_checked);
                    if (App.getSharedPreferences().getBoolean(Pref.LOCK_NEW, Pref.LOCK_DEFAULT)) {
                        checkbox.setImageResource(R.drawable.security_setting_check);
                    } else {
                        checkbox.setImageResource(R.drawable.security_setting_not_check);
                    }
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (App.getSharedPreferences().getBoolean(Pref.LOCK_NEW, Pref.LOCK_DEFAULT)) {
                                checkbox.setImageResource(R.drawable.security_setting_not_check);
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_NEW_APP, MyTrack.ACT_NEW_APP, 1L);
                                App.getSharedPreferences().edit().putBoolean(Pref.LOCK_NEW, false).apply();

                            } else {
                                checkbox.setImageResource(R.drawable.security_setting_check);
//                                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_NEW_APP, MyTrack.ACT_NEW_APP, 1L);
                                App.getSharedPreferences().edit().putBoolean(Pref.LOCK_NEW, true).apply();
                            }

                        }
                    });


                } else if (i == SETTING_RATE) {
                    if (Pref.hasOption(Pref.OPT_RATE_REDDOT) && !Pref.isOptionPressed(Pref.OPT_RATE_REDDOT)) {
                        view = LayoutInflater.from(Settings.this).inflate(R.layout.security_new_it, null, false);//lockscreen_red
                    } else {
                        view = LayoutInflater.from(Settings.this).inflate(R.layout.security_new_it, null, false);
                    }
                    Button it = (Button) view.findViewById(R.id.security_abuout_bt);
                    it.setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                } else if (i == SETTING_HIDE_GRAPH_PATH) {
                    view = LayoutInflater.from(Settings.this).inflate(R.layout.security_notica_it, null, false);
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
                        }
                    });

                } else if (i == 4) {
                    view = LayoutInflater.from(Settings.this).inflate(R.layout.security_new_it, null, false);
                    Button it = (Button) view.findViewById(R.id.security_abuout_bt);
                    it.setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                }

                return view;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
//        AndroidSdk.track("SettingActivity");

    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == SETTING_SLOT) {
                SharedPreferences sp = App.getSharedPreferences();
                int idx = sp.getInt(Pref.PREF_BRIEF_SLOT, Pref.PREF_DEFAULT);
                new AlertDialog.Builder(context).setTitle(R.string.security_short_exit_slot).setSingleChoiceItems(R.array.brief_slot, idx, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.getSharedPreferences().edit().putInt(Pref.PREF_BRIEF_SLOT, i).apply();
                        notifyDatasetChanged();
                        dialogInterface.dismiss();
                    }
                }).create().show();
            } else if (id == 4) {
                if (!Pref.isOptionPressed(Pref.OPT_RATE_REDDOT)) {
                    Pref.pressOption(Pref.OPT_RATE_REDDOT);
                }
                Share.rate(context);
//                MyTrack.sendEvent(MyTrack.CATE_SETTING, MyTrack.ACT_RATE, MyTrack.ACT_RATE, 1L);
                notifyDatasetChanged();
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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,AppLock.class);
        startActivity(intent);
        overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
        super.onBackPressed();
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_new_intruder);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }


}