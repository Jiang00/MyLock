package com.security.manager.page;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;

import com.security.lib.customview.MyWidgetContainer;
import com.privacy.lock.R;


/**
 * Created by wangqi on 16/4/11.
 */
public class ShowDialogview {
    public static final String FIVE_STARED = "five_sta_ed";


    public static void showDialog(final Context context, final String style, final ListView listview) {
        final View alertDialogView = View.inflate(context, R.layout.security_main_five_rate_new, null);
        final AlertDialog d = new AlertDialog.Builder(context,R.style.security_show_dialog).create();
//        Utils.addAlertAttribute(d.getWindow());
        d.setView(alertDialogView);
        d.show();
        alertDialogView.findViewById(R.id.security_good_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharPFive sh = new SharPFive(context);
                sh.setFiveRate(true);
                if(listview!=null){
                    if(AppsFragment.headerView!=null){
                        listview.removeHeaderView(AppsFragment.headerView);
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
}
