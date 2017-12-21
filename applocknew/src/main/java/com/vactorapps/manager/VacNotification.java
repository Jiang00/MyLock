package com.vactorapps.manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.ivymobi.applock.free.R;
import com.vactorapps.manager.meta.VacPref;

/**
 * Created by song on 16/4/21.
 */
public class VacNotification {
    public static final String NOTIFICATION = "notifiactions";
    Context context;
    android.app.Notification n;
    NotificationCompat.Builder mBuilder;

    public VacNotification(Context context) {
        this.context = context;
        n = _getNotification(context);
    }

    public android.app.Notification getNotification() {
        return n;
    }

    public void updateNotification(int id) {
        if (n == null) {
            n = _getNotification(context);
        }

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, n);
    }

    public static int getInstallNum(Context context) {
        PackageManager packageManager = context.getPackageManager();
        long firstInstallTime = System.currentTimeMillis();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            firstInstallTime = packageInfo.firstInstallTime;//应用第一次安装的时间
//           int versionCode=packageInfo.versionCode;//应用现在的版本号
//           String versionName=packageInfo.versionName;//应用现在的版本名称
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time_ = firstInstallTime % 24 * 60 * 60 * 1000;
        long installTime = System.currentTimeMillis() - firstInstallTime + time_;
        if (firstInstallTime == 0) {
            return -1;
        } else {
            return millTransFate(installTime) + 1;
        }

    }

    //多少天
    public static int millTransFate(long millisecond) {
        int day = (int) Math.floor(millisecond / 86400000);

        return day;
    }

    private android.app.Notification _getNotification(Context context) {
        mBuilder = new NotificationCompat.Builder(context);
        Intent notifyIntent = null;
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notifaction_view);

        if (VacPref.getVisitor()) {
            try {
                notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName() + "");
                notifyIntent.putExtra(NOTIFICATION, true);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                remoteView.setImageViewResource(R.id.notice_fl, R.drawable.notice_bj1);
                remoteView.setImageViewResource(R.id.notice_bj_circle1, R.drawable.notice_bj_circle);
                remoteView.setImageViewResource(R.id.notice_bj_circle2, R.drawable.notice_bj_circle);
                remoteView.setImageViewResource(R.id.notice_lock, R.drawable.notice_iv);
                int day = getInstallNum(context);
                CharSequence status = context.getString(R.string.security_visitor_on2, day + "");
                status = context.getString(R.string.app_name) +" "+ status;
                remoteView.setTextViewText(R.id.applock_run, status);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mBuilder.setSmallIcon(R.drawable.security_notification_lock);
        } else {
            notifyIntent = new Intent(context, VacTansparent.class);
            notifyIntent.putExtra(NOTIFICATION, true);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            remoteView.setImageViewResource(R.id.notice_fl, R.drawable.notice_bj2);
            remoteView.setImageViewResource(R.id.notice_bj_circle1, R.drawable.notice_bj_circle2);
            remoteView.setImageViewResource(R.id.notice_bj_circle2, R.drawable.notice_bj_circle2);
            CharSequence status = context.getResources().getString(R.string.security_visitor_off2);
            remoteView.setTextViewText(R.id.applock_run, status);
            remoteView.setImageViewResource(R.id.notice_lock, R.drawable.notice_lock);
            mBuilder.setSmallIcon(R.drawable.security_notification_unlock);
        }


        int requestCode = (int) System.currentTimeMillis();

        PendingIntent pendIntent = PendingIntent.getActivity(context, requestCode,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteView.setOnClickPendingIntent(R.id.notification, pendIntent);

        mBuilder.setContent(remoteView);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mBuilder.setWhen(System.currentTimeMillis());

        android.app.Notification notification = mBuilder.build();
        notification.flags = android.app.Notification.FLAG_NO_CLEAR;
        return notification;
    }
}
