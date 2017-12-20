package com.android.kpa;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

/**
 * DO NOT do anything in this Service!<br/>
 * <p>
 * Created by renqingyou .
 */
public class PersistService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        keepAlive();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            /**被关闭时再启动服务，确保不被杀死*/
            @Override
            public void run() {
                try {
                    PersistService.this.startService(new Intent(PersistService.this, PersistService.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100);
        super.onDestroy();
    }

    private void keepAlive() {
        try {
            Notification notification = new Notification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(0, notification); // 设置为前台服务避免kill，Android4.3及以上需要设置id为0时通知栏才不显示该通知；
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
