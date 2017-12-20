package com.vactorapps.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivymobi.applock.free.R;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.mydb.VacPreference;
import com.vactorappsapi.manager.lib.BaseActivity;
import com.vactorapps.manager.page.MyDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ma on 15/12/25.
 */
public class IntruderSetting extends BaseActivity {

    //    @InjectView(R.id.suo_title_bar_te)
//    TextView title;
    @InjectView(R.id.title_back)
    ImageView title_back;
    @InjectView(R.id.intrude_open_fl)
    FrameLayout intrude_open_fl;
    @InjectView(R.id.intrude_open)
    TextView intrude_open;
    @InjectView(R.id.intrude_open_iv)
    ImageView intrude_open_iv;
    @InjectView(R.id.intrude_time_fl)
    FrameLayout intrude_time_fl;
    @InjectView(R.id.intrude_time)
    TextView intrude_time;
    @InjectView(R.id.setting_shutter_fl)
    FrameLayout setting_shutter_fl;
    @InjectView(R.id.intruder_shutter)
    ImageView intruder_shutter;
    private boolean openFlag;
    private boolean shutterFlag;
    private String[] summaries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.security_intrude_set);
        ButterKnife.inject(this);
        setupToolbar();
        openFlag = VacPref.fetchIntruder();
        intrude_open_iv.setImageResource(openFlag ? R.drawable.security_setting_check : R.drawable.security_setting_not_check);
        intrude_open.setText(openFlag ? getResources().getString(R.string.security_intruder_on_new) : getResources().getString(R.string.security_intruder_off_new));
        summaries = getResources().getStringArray(R.array.suo_ruqinzhe_slot);
        int slot = VacPreference.getIntruderSlot();
        intrude_time.setText(summaries[slot]);
        shutterFlag = VacPreference.isShutterSoundEnabled();
        intruder_shutter.setImageResource(shutterFlag ? R.drawable.security_setting_check : R.drawable.security_setting_not_check);

        intrude_open_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openFlag) {
                    openFlag = false;
                    VacPref.setFetchIntruder(openFlag);
                    intrude_open_iv.setImageResource(R.drawable.security_setting_not_check);
                    intrude_open.setText(getResources().getString(R.string.security_intruder_off_new));
                    if (shutterFlag) {
                        intruder_shutter.setImageResource(R.drawable.security_setting_not_check);
                    }
                } else {
                    openFlag = true;
                    VacPref.setFetchIntruder(openFlag);
                    intrude_open_iv.setImageResource(R.drawable.security_setting_check);
                    intrude_open.setText(getResources().getString(R.string.security_intruder_on_new));
                    if (shutterFlag) {
                        intruder_shutter.setImageResource(R.drawable.security_setting_check);
                    }
                }
            }
        });
        intrude_time_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFrequencyDialog(IntruderSetting.this);
            }
        });
        setting_shutter_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shutterFlag) {
                    shutterFlag = false;
                    VacPreference.setShutterSoundEnabled(shutterFlag);
                    intruder_shutter.setImageResource(R.drawable.security_setting_not_check);
                } else {
                    shutterFlag = true;
                    VacPreference.setShutterSoundEnabled(shutterFlag);
                    intruder_shutter.setImageResource(R.drawable.security_setting_check);
                }
            }
        });
    }

    private void setupToolbar() {
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void showFrequencyDialog(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.intruder_dialog, null);
        final MyDialog d = new MyDialog(c, 0, 0, alertDialogView, R.style.dialog);

        FrameLayout resetPattern = (FrameLayout) alertDialogView.findViewById(R.id.pattern);
        FrameLayout resetPassword = (FrameLayout) alertDialogView.findViewById(R.id.digital);
        FrameLayout five_time = (FrameLayout) alertDialogView.findViewById(R.id.five_time);
        FrameLayout five_time4 = (FrameLayout) alertDialogView.findViewById(R.id.five_time4);
        FrameLayout five_time5 = (FrameLayout) alertDialogView.findViewById(R.id.five_time5);
        ImageView every_time_iv = (ImageView) alertDialogView.findViewById(R.id.every_time_iv);
        ImageView five_time_iv = (ImageView) alertDialogView.findViewById(R.id.five_time_iv);
        ImageView five_time4_iv = (ImageView) alertDialogView.findViewById(R.id.five_time4_iv);
        ImageView five_time5_iv = (ImageView) alertDialogView.findViewById(R.id.five_time5_iv);
        ImageView lock_screen_iv = (ImageView) alertDialogView.findViewById(R.id.lock_screen_iv);
        int idx = VacPreference.getIntruderSlot();
        if (idx == 0) {
            every_time_iv.setImageResource(R.drawable.check);
            five_time_iv.setImageResource(R.drawable.uncheck);
            lock_screen_iv.setImageResource(R.drawable.uncheck);
            five_time4_iv.setImageResource(R.drawable.uncheck);
            five_time5_iv.setImageResource(R.drawable.uncheck);
        } else if (idx == 2) {
            every_time_iv.setImageResource(R.drawable.uncheck);
            five_time_iv.setImageResource(R.drawable.uncheck);
            five_time4_iv.setImageResource(R.drawable.uncheck);
            five_time5_iv.setImageResource(R.drawable.uncheck);
            lock_screen_iv.setImageResource(R.drawable.check);
        } else if (idx == 1) {
            every_time_iv.setImageResource(R.drawable.uncheck);
            five_time4_iv.setImageResource(R.drawable.uncheck);
            five_time5_iv.setImageResource(R.drawable.uncheck);
            five_time_iv.setImageResource(R.drawable.check);
            lock_screen_iv.setImageResource(R.drawable.uncheck);
        } else if (idx == 3) {
            every_time_iv.setImageResource(R.drawable.uncheck);
            five_time4_iv.setImageResource(R.drawable.check);
            five_time5_iv.setImageResource(R.drawable.uncheck);
            five_time_iv.setImageResource(R.drawable.uncheck);
            lock_screen_iv.setImageResource(R.drawable.uncheck);
        } else if (idx == 4) {
            every_time_iv.setImageResource(R.drawable.uncheck);
            five_time4_iv.setImageResource(R.drawable.uncheck);
            five_time5_iv.setImageResource(R.drawable.check);
            five_time_iv.setImageResource(R.drawable.uncheck);
            lock_screen_iv.setImageResource(R.drawable.uncheck);
        }

        d.getWindow().setWindowAnimations(R.style.dialog_animation);
        d.getWindow().setGravity(Gravity.CENTER);
        d.show();

        try {
            resetPattern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    VacPreference.setIntruderSlot(0);
                    intrude_time.setText(summaries[0]);
                }
            });

            resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    VacPreference.setIntruderSlot(2);
                    intrude_time.setText(summaries[2]);
                }
            });
            five_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    VacPreference.setIntruderSlot(1);
                    intrude_time.setText(summaries[1]);
                }
            });
            five_time4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    VacPreference.setIntruderSlot(3);
                    intrude_time.setText(summaries[3]);
                }
            });
            five_time5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    VacPreference.setIntruderSlot(4);
                    intrude_time.setText(summaries[4]);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onIntent(Intent intent) {

    }
}
