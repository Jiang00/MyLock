package com.security.manager.page;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.privacy.lock.R;
import com.security.lib.customview.MyWidgetContainer;
import com.security.manager.SecuritySetPattern;
import com.security.manager.Tracker;


/**
 * Created by wangqi on 16/10/26.
 */

public class showDialog {

    public static void showPermission(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_show_permission, null);


        final MyDialog d = new MyDialog(c, 0, 0, alertDialogView, R.style.dialog);
        d.show();


        alertDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                c.startActivity(intent);

                final View submitDialogView = View.inflate(v.getContext(), R.layout.security_permission_setting, null);
                final MyWidgetContainer w = new MyWidgetContainer(c, MyWidgetContainer.MATCH_PARENT, MyWidgetContainer.MATCH_PARENT, MyWidgetContainer.PORTRAIT);
                w.addView(submitDialogView);
                w.addToWindow();


                submitDialogView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        w.removeFromWindow();

                    }
                });

            }
        });

        alertDialogView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
            }
        });

    }


    public static void showResetPasswordDialog(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_show_reset_password, null);
        final MyDialog d = new MyDialog(c, 0, 0, alertDialogView, R.style.dialog);

        LinearLayout resetPattern = (LinearLayout) alertDialogView.findViewById(R.id.pattern);
        LinearLayout resetPassword = (LinearLayout) alertDialogView.findViewById(R.id.digital);

        ImageView resetTop = (ImageView) alertDialogView.findViewById(R.id.new_reset_top);
//
        d.getWindow().setWindowAnimations(R.style.dialog_animation);
        d.getWindow().setGravity(Gravity.BOTTOM);
        d.show();

   resetPattern.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           d.cancel();

           Intent intent = new Intent(c,SecuritySetPattern.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.putExtra("set", SecuritySetPattern.SET_GRAPH_PASSWD);
           c.startActivity(intent);
           Tracker.sendEvent(Tracker.ACT_SETTING_MENU,Tracker.ACT_LEADER_SETTINGPASS,Tracker.ACT_LEADER_SETTINGPASS,1L);


       }
   });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();

                Intent intent = new Intent(c,SecuritySetPattern.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("set", SecuritySetPattern.SET_NORMAL_PASSWD);
                c.startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_SETTING_MENU,Tracker.ACT_LEADER_SETTINGPASS_PASSWORD,Tracker.ACT_LEADER_SETTINGPASS_PASSWORD,1L);



            }
        });

    }


    }


