package com.security.manager.page;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import com.ivymobi.applock.free.R;
import com.security.lib.customview.MyWidgetContainer;


/**
 * Created by wangqi on 16/10/26.
 */

public class showDialog {

    public static void showPermission(final Context c) {
        final View alertDialogView = View.inflate(c, R.layout.security_show_permission, null);
        final MyDialog d = new MyDialog(c, 0, 0, alertDialogView, R.style.dialog);
        d.show();

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


