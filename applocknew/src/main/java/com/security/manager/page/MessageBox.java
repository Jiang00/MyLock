package com.security.manager.page;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import com.privacy.lock.R;


/**
 * Created by huale on 2015/3/10.
 */
public class MessageBox {
    public static final byte BUTTON_YES = 1;
    public static final byte BUTTON_YES_NO = 2;
    public static final byte BUTTON_YES_CANCEL = 3;
    public static final byte BUTTON_YES_NO_CANCEL = 4;
    public static final int NO_TITLE = -1;

    public static class Data {
        public byte button = BUTTON_YES;
        public boolean alert = false;
        public int yes = android.R.string.yes;
        public int no = android.R.string.no;
        public int cancel = android.R.string.cancel;
        public DialogInterface.OnClickListener onyes, onno, oncancel;
        public OnLongClickListener onyeslong, onnolong, oncancelong;
        public DialogInterface.OnDismissListener ondismiss;
        public CharSequence messages;
        public CharSequence titles;
        public View view;
        public int msg = 0;
        public int title = 0;
        public boolean cancelable;
        public int icon = R.drawable.ic_launcher;
        public int style = R.style.Theme_AppCompat_Light_Dialog_Alert;
    }

    public abstract static class OnLongClickListener<T extends Dialog> implements View.OnLongClickListener {
        public T dialog;
    }

    public static void show(final Context context, final Data data){
        if (context instanceof Activity){
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    show_(context, data);
                }
            });
        } else {
            show_(context, data);
        }
    }

    public static android.app.AlertDialog showLegacy(Context context, Data data) {
        android.app.AlertDialog.Builder builder;
        if (data.style == NO_TITLE) {
            builder = new android.app.AlertDialog.Builder(context);
        } else {
            builder = new android.app.AlertDialog.Builder(context, data.style);
        }
        if (data.icon != NO_TITLE) {
            builder.setIcon(data.icon);
        }
        builder.setCancelable(data.cancelable);
        if (data.title != NO_TITLE) {
            if (data.title == 0) {
                builder.setTitle(data.titles);
            } else {
                builder.setTitle(data.title);
            }
        }

        if (data.view != null) {
            builder.setView(data.view);
        } else {
            if (data.msg == 0) {
                builder.setMessage(data.messages);
            } else {
                builder.setMessage(data.msg);
            }
        }

        builder.setPositiveButton(data.yes, data.onyes);
        switch (data.button){
            case BUTTON_YES_CANCEL:
                builder.setNeutralButton(data.cancel, data.oncancel);
                break;

            case BUTTON_YES_NO:
                builder.setNegativeButton(data.no, data.onno);
                break;

            case BUTTON_YES_NO_CANCEL:
                builder.setNegativeButton(data.no, data.onno).setNeutralButton(data.cancel, data.oncancel);
                break;
        }
        android.app.AlertDialog dialog = builder.create();
        if (data.ondismiss != null){
            dialog.setOnDismissListener(data.ondismiss);
        }

        if (data.alert){
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();

        if (data.onyeslong != null) {
            data.onyeslong.dialog = dialog;
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnLongClickListener(data.onyeslong);
        }

        if (data.onnolong != null) {
            data.onnolong.dialog = dialog;
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnLongClickListener(data.onnolong);
        }

        if (data.oncancelong != null) {
            data.oncancelong.dialog = dialog;
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnLongClickListener(data.oncancelong);
        }

        return dialog;
    }

    public static AlertDialog show_(Context context, Data data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, data.style).setCancelable(data.cancelable);
        if (data.icon != NO_TITLE) {
            builder.setIcon(data.icon);
        }
        if (data.title != NO_TITLE) {
            if (data.title == 0) {
                builder.setTitle(data.titles);
            } else {
                builder.setTitle(data.title);
            }
        }

        if (data.view != null) {
            builder.setView(data.view);
        } else {
            if (data.msg == 0) {
                builder.setMessage(data.messages);
            } else {
                builder.setMessage(data.msg);
            }
        }

        builder.setPositiveButton(data.yes, data.onyes);
        switch (data.button){
            case BUTTON_YES_CANCEL:
                builder.setNeutralButton(data.cancel, data.oncancel);
                break;

            case BUTTON_YES_NO:
                builder.setNegativeButton(data.no, data.onno);
                break;

            case BUTTON_YES_NO_CANCEL:
                builder.setNegativeButton(data.no, data.onno).setNeutralButton(data.cancel, data.oncancel);
                break;
        }
        AlertDialog dialog = builder.create();
        if (data.ondismiss != null){
            dialog.setOnDismissListener(data.ondismiss);
        }

        if (data.alert){
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();

        if (data.onyeslong != null) {
            data.onyeslong.dialog = dialog;
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnLongClickListener(data.onyeslong);
        }

        if (data.onnolong != null) {
            data.onnolong.dialog = dialog;
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnLongClickListener(data.onnolong);
        }

        if (data.oncancelong != null) {
            data.oncancelong.dialog = dialog;
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnLongClickListener(data.oncancelong);
        }

        return dialog;
    }
}
