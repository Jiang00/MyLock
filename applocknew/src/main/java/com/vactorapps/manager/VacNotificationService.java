package com.vactorapps.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vactorapps.manager.meta.VacPref;


/**
 * Created by superjoy on 2014/8/25.
 */
public class VacNotificationService extends Service {

    @Override
    public void onCreate() {
        if (VacPref.getNotification()) {
            try {
                VacNotification n = new VacNotification(this);
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

