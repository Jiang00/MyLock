package com.privacy.lock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.privacy.lock.meta.Pref;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by superjoy on 2014/9/4.
 */
public class Setting extends ClientActivity {
    public static byte idx = 0;
    public static final byte SETTING_SLOT = idx++;
    public static final byte SETTING_MODE = idx++;
    public static final byte SETTING_HIDE_GRAPH_PATH = idx++;
    public static final byte SETTING_RANDOM = idx++;
    public static final byte SETTING_ADVANCED = idx++;
    //    public static final int SETTING_FAKE_SELECTOR = idx++;
    public static final byte SETTING_LOCK_NEW = idx++;
    public static final byte SETTING_RATE = idx++;
    public static final byte SETTING_SHARE = idx++;
    public static final int SETTING_INTRUDER = idx++;
    public static final int SETTING_LOST_FOUND = idx++;

//    public static final byte REQ_CODE_ADVANCE = 5;
    public static final byte REQ_CODE_PASS = 2;
    public static final byte REQ_CODE_PATTERN = 4;

    static final int[] items = new int[]{
            R.string.overf_brief,
            0,
//            R.string.help_normal_pass,
//            R.string.help_graph_pass,
//            R.string.secure_email,
            R.string.hide_path,
//            R.string.random_keyboard,
//            R.string.advanced_security,
//            R.string.fake_selector,
//            R.string.intruder,
//            R.string.pause_protect,
//            R.string.show_noti,
            R.string.lock_new_app,
            R.string.help_rate,
            R.string.help_share,
//            R.string.lost_found
    };

    ListView lv;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void setupView() {
        setContentView(R.layout.mat_list_v_nor);
        ButterKnife.inject(this);

        setup(R.string.setting);
        normalTitle.setText("   "+getResources().getString(R.string.setting));
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
                    view = getLayoutInflater().inflate(R.layout.two_line_it, viewGroup, false);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView desc = (TextView) view.findViewById(R.id.desc);
                    title.setText(R.string.reset_passwd_2_btn);
                    desc.setText(Pref.isUseNormalPasswd() ? R.string.use_normal : R.string.use_graphic);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPasswd(true, !Pref.isUseNormalPasswd());
                        }
                    });
                } else if (i == SETTING_SLOT) {
                    view = LayoutInflater.from(Setting.this).inflate(R.layout.two_line_it, null, false);
                    LinearLayout it = (LinearLayout) view.findViewById(R.id.line);
                    int slot = App.getSharedPreferences().getInt(Pref.PREF_BRIEF_SLOT, Pref.PREF_DEFAULT);
                    ((TextView) it.findViewById(R.id.desc)).setText(getResources().getStringArray(R.array.brief_slot)[slot]);
                    ((TextView) it.findViewById(R.id.title)).setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                } /*else if (i == SETTING_ADVANCED) {
                    if (Pref.hasOption(Pref.OPT_ADVANCE_REDDOT) && !Pref.isOptionPressed(Pref.OPT_ADVANCE_REDDOT)) {
                        view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it_red, null, false);
                    } else {
                        view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it, null, false);
                    }
                    ((TextView) view.findViewById(R.id.title)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.desc)).setText(R.string.advanced_security_detail);
                    CheckBox b = (CheckBox) view.findViewById(R.id.checkBox);
                    b.setChecked(Pref.isAdvanceEnabled());
                    b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            Log.e("haha", "here now " + b);
                            if (b) {
                                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_ADVANCE, MyTracker.ACT_ADVANCE, 1L);
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(context, DeviceAdmin.class));
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.dev_admin_desc));
                                startActivityForResult(intent, REQ_CODE_ADVANCE);
                            } else {
                                DevicePolicyManager p = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                                p.removeActiveAdmin(new ComponentName(context, DeviceAdmin.class));
                                Pref.enableAdvance(false);
                                MyMenu.removeUninstallMenu(menu);
                                Toast.makeText(context, R.string.dev_admin_canceled, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } */
//                else if (i == SETTING_RANDOM) {
//                    view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it, null, false);
//                    ((TextView) view.findViewById(R.id.title)).setText(items[i]);
////                    ((TextView) view.findViewById(R.id.desc)).setText(R.string.random_keyboard_desc);
//                    view.findViewById(R.id.desc).setVisibility(View.GONE);
//                    CheckBox b = (CheckBox) view.findViewById(R.id.checkBox);
//
//                    b.setChecked(App.getSharedPreferences().getBoolean("random", false));
//
//                    b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                            if (b)
//                                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_RANDOM, MyTracker.ACT_RANDOM, 1L);
//                            App.getSharedPreferences().edit().putBoolean("random", b).apply();
//                        }
//                    });
//                }
                /*else if (i == SETTING_FAKE) {
                    view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it, null, false);
                    ((TextView) view.findViewById(R.id.title)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.desc)).setText(R.string.fake_cover_desc);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

                    checkBox.setChecked(Pref.isFakeCover());

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                            if (b) {
                                AlertDialog dialog1 = FakePresenter.show(R.string.fake, Html.fromHtml(getString(R.string.fake_setting_msg)),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(), R.string.set_fake_fails, Toast.LENGTH_SHORT).show();
                                            }
                                        }, new MessageBox.OnLongClickListener<AlertDialog>() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                Pref.setFakeCover(true);
                                                dialog.cancel();
                                                MessageBox.Data data = new MessageBox.Data();
                                                data.msg = R.string.set_fake_success;
                                                MessageBox.show(Setting.this, data);
                                                return true;
                                            }
                                        });
                                dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        notifyDatasetChanged();
                                    }
                                });
                            } else {
                                Pref.setFakeCover(false);
                            }
                        }
                    });
                }*/ else if (i == SETTING_INTRUDER) {
                    view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it, null, false);
                    ((TextView) view.findViewById(R.id.title)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.desc)).setText(R.string.intruder_desc);
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

                    checkBox.setChecked(Pref.fetchIntruder());

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                            Pref.setFetchIntruder(b);
                            if (b) {
                                Toast.makeText(getApplicationContext(), R.string.intruder_on, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.intruder_off, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (i-2 == SETTING_LOCK_NEW) {
                    view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it, null, false);
                    ((TextView) view.findViewById(R.id.title)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.desc)).setText(R.string.lock_new_app_desc);
                    CheckBox b = (CheckBox) view.findViewById(R.id.checkBox);
                    b.setChecked(App.getSharedPreferences().getBoolean(Pref.LOCK_NEW, Pref.LOCK_DEFAULT));
                    b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b)
                                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_NEW_APP, MyTracker.ACT_NEW_APP, 1L);
                            App.getSharedPreferences().edit().putBoolean(Pref.LOCK_NEW, b).apply();
                        }
                    });
                } else if (i == SETTING_RATE) {
                    if (Pref.hasOption(Pref.OPT_RATE_REDDOT) && !Pref.isOptionPressed(Pref.OPT_RATE_REDDOT)) {
                        view = LayoutInflater.from(Setting.this).inflate(R.layout.security_myabout_red_item, null, false);
                    } else {
                        view = LayoutInflater.from(Setting.this).inflate(R.layout.security_myabout_item, null, false);
                    }
                    Button it = (Button) view.findViewById(R.id.about_it);
                    it.setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                } else if (i == SETTING_HIDE_GRAPH_PATH) {
                    view = LayoutInflater.from(Setting.this).inflate(R.layout.show_noti_it, null, false);
                    ((TextView) view.findViewById(R.id.title)).setText(items[i]);
                    ((TextView) view.findViewById(R.id.desc)).setVisibility(View.GONE);
                    CheckBox b = (CheckBox) view.findViewById(R.id.checkBox);
                    b.setChecked(App.getSharedPreferences().getBoolean("hide_path", false));
                    b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_HIDE_PATH, MyTracker.ACT_HIDE_PATH, 1L);
                            App.getSharedPreferences().edit().putBoolean("hide_path", isChecked).apply();
                        }
                    });
                } else {
                    view = LayoutInflater.from(Setting.this).inflate(R.layout.security_myabout_item, null, false);
                    Button it = (Button) view.findViewById(R.id.about_it);
                    it.setText(items[i]);
                    it.setOnClickListener(onClickListener);
                    it.setId(i);
                }

                return view;
            }
        });
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            /*
            if (id == SETTING_NORMAL) {
                startActivity(new Intent(context, SetupPassword.class).putExtra("set", SetupPassword.SET_NORMAL_PASSWD));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (id == SETTING_GRAPH) {
                setPasswd(false, true);
            } else if (id == SETTING_EMAIL) {
                startActivity(new Intent(context, SetupPassword.class).putExtra("set", SetupPassword.SET_EMAIL));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else*/
            if (id == SETTING_SLOT) {
                SharedPreferences sp = App.getSharedPreferences();
                int idx = sp.getInt(Pref.PREF_BRIEF_SLOT, Pref.PREF_DEFAULT);
                new AlertDialog.Builder(context).setTitle(R.string.brief_exit_slot).setSingleChoiceItems(R.array.brief_slot, idx, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.getSharedPreferences().edit().putInt(Pref.PREF_BRIEF_SLOT, i).apply();
                        notifyDatasetChanged();
                        dialogInterface.dismiss();
                    }
                }).create().show();
            } else if (id == SETTING_RATE) {
                if (!Pref.isOptionPressed(Pref.OPT_RATE_REDDOT)) {
                    Pref.pressOption(Pref.OPT_RATE_REDDOT);
                }
                Share.rate(context);
                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_RATE, MyTracker.ACT_RATE, 1L);
                notifyDatasetChanged();
            } else if (id == SETTING_SHARE) {
                Resources resources = context.getResources();
                String title = resources.getString(R.string.share_title);
                String msg = resources.getString(R.string.share_msg, Share.GOOGLE_PALY_URL + context.getPackageName());
                Share.share(context, title, msg);
                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_SHARE, MyTracker.ACT_SHARE, 1L);
            }
        }
    };

    public void setPasswd(boolean forResult, boolean pattern) {
        if (forResult)
            startActivityForResult(new Intent(context, SetupPattern.class).putExtra("set", pattern ? SetupPattern.SET_GRAPH_PASSWD : SetupPattern.SET_NORMAL_PASSWD), pattern ? REQ_CODE_PATTERN : REQ_CODE_PASS);
        else
            startActivity(new Intent(context, SetupPattern.class).putExtra("set", pattern ? SetupPattern.SET_GRAPH_PASSWD : SetupPattern.SET_NORMAL_PASSWD));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case REQ_CODE_ADVANCE:
//                DevicePolicyManager p = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//                boolean b = p.isAdminActive(new ComponentName(context, DeviceAdmin.class));
//                if (b) {
//                    MessageBox.Data d = new MessageBox.Data();
//                    d.title = R.string.advanced_security;
//                    d.msg = R.string.dev_admin_actived;
//                    MessageBox.show(this, d);
//                    MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_ADVANCE, MyTracker.LABEL_ADVANCE, 1);
//                } else {
//                    Toast.makeText(context, R.string.dev_admin_canceled, Toast.LENGTH_SHORT).show();
//                }
//                Pref.enableAdvance(b);
//                if (b) {
//                    if (!Pref.isOptionPressed(Pref.OPT_ADVANCE_REDDOT)) {
//                        Pref.pressOption(Pref.OPT_ADVANCE_REDDOT);
//                    }
//                    if (menu != null)
//                        MyMenu.addUninstallMenu(menu);
//                } else {
//                    if (menu != null)
//                        MyMenu.removeUninstallMenu(menu);
//                }
//                notifyDatasetChanged();
//                break;
            case REQ_CODE_PATTERN:
                if (resultCode == 1) {
//                    Pref.begin().useNormalPasswd(false).commit();
                    notifyDatasetChanged();
                }
                break;
            case REQ_CODE_PASS:
                if (resultCode == 1) {
//                    Pref.begin().useNormalPasswd(true).commit();
                    notifyDatasetChanged();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        notifyDatasetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,AppLock.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
    }

    public void notifyDatasetChanged() {
        if (lv != null) {
            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
        }
    }



}