package com.security.manager.page;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ivymobi.applock.free.R;
import com.security.lib.customview.MyWidgetContainer;
import com.security.manager.App;
import com.security.manager.SecurityPermissionActivity;
import com.security.manager.Tracker;
import com.security.manager.lib.Utils;
import com.security.manager.meta.SecurityMyPref;


/**
 * Created by wangqi on 16/4/11.
 */
public class ShowDialogview {
    public static final String FIVE_STARED = "five_sta_ed";


    public static void showDialog(final Context context, final String style, final ListView listview) {
        final View alertDialogView = View.inflate(context, R.layout.security_main_five_rate_new, null);
        final AlertDialog d = new AlertDialog.Builder(context, R.style.security_show_dialog).create();
//        Utils.addAlertAttribute(d.getWindow());
        d.setView(alertDialogView);
        d.show();
        alertDialogView.findViewById(R.id.security_good_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecuritySharPFive sh = new SecuritySharPFive(context);
                sh.setFiveRate(true);
                if (listview != null) {
                    if (AppFragementSecurity.headerView != null) {
                        listview.removeHeaderView(AppFragementSecurity.headerView);
                    }
                }
                d.dismiss();

//                Utils.rateUs(v.getContext());
            }
        });
        alertDialogView.findViewById(R.id.security_bad_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
//                showComplainDialog(context);
            }
        });

    }

    public static void showPermission(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_show_permission, null);
        final AlertDialog d = new AlertDialog.Builder(c, R.style.dialog).create();
        d.setView(alertDialogView);
        d.setCanceledOnTouchOutside(false);
        d.show();
//        Utils.addAlertAttribute(d.getWindow());
        alertDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
                try {
                    final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    c.startActivity(intent);
                    new Thread().sleep(1500);
                } catch (Exception|Error e) {
                    e.printStackTrace();
                }

                Intent transintent = new Intent(c, SecurityPermissionActivity.class);
                c.startActivity(transintent);

//                View submitDialogView = View.inflate(v.getContext(), R.layout.security_permission_setting, null);
//                final MyWidgetContainer w = new MyWidgetContainer(ApplockC, MyWidgetContainer.MATCH_PARENT, MyWidgetContainer.MATCH_PARENT, MyWidgetContainer.PORTRAIT);
//                w.addView(submitDialogView);
//                w.addToWindow();
//
//
//                submitDialogView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        w.removeFromWindow();
//
//                    }
//                });
                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_OK, 1L);

//                 WindowManager wm = (WindowManager) ApplockC.getSystemService(Context.WINDOW_SERVICE);
//
//                attachToWindow(wm, submitDialogView);
            }
        });

        alertDialogView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_CANCLE, 1L);

                d.cancel();
            }
        });
    }

    public static void showNewVersion(final Context context) {
        try {
            final View alertDialogView = View.inflate(context, R.layout.security_show_newversion, null);


            final MyDialog d = new MyDialog(context, 0, 0, alertDialogView, R.style.dialog);


            d.show();

            alertDialogView.findViewById(R.id.security_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.rate(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void showSaveMode(Context context) {
        try {
            final Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
            new Thread().sleep(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent transintent = new Intent(context, SecurityPermissionActivity.class);
        context.startActivity(transintent);
    }

    public static void showSettingPermission50(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            context.startActivity(intent);

            new Thread().sleep(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent transintent = new Intent(context, SecurityPermissionActivity.class);
        context.startActivity(transintent);

    }


    public static void stopApplock(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_stop_applock, null);
        final AlertDialog d = new AlertDialog.Builder(c, R.style.dialog).create();
        d.setView(alertDialogView);
        d.setCanceledOnTouchOutside(false);
        d.show();

//        Utils.addAlertAttribute(d.getWindow());
        alertDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
                SecurityMyPref.setVisitor(false);

//                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_OK, 1L);

            }
        });

        alertDialogView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_CANCLE, 1L);
                d.cancel();
            }
        });
    }








}
