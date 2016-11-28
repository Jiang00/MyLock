package com.security.manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.privacy.lock.R;
import com.security.manager.meta.SecurityMyPref;

/**
 * Created by song on 16/4/21.
 */
public class Notification {
    public static final String NOTIFICATION = "notifiactions";
    Context context;
    android.app.Notification n;
    NotificationCompat.Builder mBuilder;

    public Notification(Context context) {
        this.context = context;
        n = _getNotification(context);

    }

    public void updateNotification(int id) {
        if (n == null) {
            n = _getNotification(context);
        }

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, n);
    }


    public android.app.Notification getNotification() {
        return n;
    }

    private android.app.Notification _getNotification(Context context) {
        mBuilder = new NotificationCompat.Builder(context);
        Intent notifyIntent = null;
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notifaction_view);

        if (SecurityMyPref.getVisitor()) {
            try {
                notifyIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName() + "");
                notifyIntent.putExtra(NOTIFICATION, true);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                remoteView.setImageViewResource(R.id.right_icon, R.drawable.security_visitor_on);
                CharSequence status = context.getResources().getString(R.string.security_visitor_on);
                remoteView.setTextViewText(R.id.applock_run, status);
            }catch (Exception e){
                e.printStackTrace();
            }

            mBuilder.setSmallIcon(R.drawable.security_notification_lock);


        } else {
            notifyIntent = new Intent(context, SecurityTansparent.class);
            notifyIntent.putExtra(NOTIFICATION, true);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            remoteView.setImageViewResource(R.id.right_icon, R.drawable.security_visitor_off);
            CharSequence status = context.getResources().getString(R.string.security_visitor_off);
            remoteView.setTextViewText(R.id.applock_run, status);
            mBuilder.setSmallIcon(R.drawable.security_notification_unlock);

        }


        int requestCode = (int) SystemClock.uptimeMillis();

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
