package com.security.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.security.manager.meta.SecurityMyPref;


/**
 * Created by superjoy on 2014/8/25.
 */
public class NotificationService extends Service {

    @Override
    public void onCreate() {
        if (SecurityMyPref.getNotification()) {
            try {
                Notification n = new Notification(this);
                startForeground(101, n.getNotification());
                n.updateNotification(101);
            } catch (Exception|Error e) {
                e.printStackTrace();
            }

        }
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

