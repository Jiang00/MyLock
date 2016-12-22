package com.security.manager;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author IceStar
 */
public class ApplockC extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public static Notification applockA(Context paramContext) {
        try  {
            Class localClass = Class.forName("android.app.Notification$Builder");
            Constructor localConstructor = localClass.getDeclaredConstructor(new Class[] { Context.class });
            localConstructor.setAccessible(true);
            Object localObject = localConstructor.newInstance(new Object[] { paramContext });
            Class[] arrayOfClass1 = new Class[1];
            arrayOfClass1[0] = Integer.TYPE;
            Method localMethod1 = localClass.getDeclaredMethod("setSmallIcon", arrayOfClass1);
            localClass.getDeclaredMethod("setTicker", new Class[] { CharSequence.class });
            Class[] arrayOfClass2 = new Class[1];
            arrayOfClass2[0] = Boolean.TYPE;
            Method localMethod2 = localClass.getDeclaredMethod("setAutoCancel", arrayOfClass2);
            Method localMethod3 = localClass.getDeclaredMethod("getNotification", new Class[0]);
            Class[] arrayOfClass3 = new Class[1];
            arrayOfClass3[0] = Integer.TYPE;
            Method localMethod4 = localClass.getDeclaredMethod("setPriority", arrayOfClass3);
            Class[] arrayOfClass4 = new Class[1];
            arrayOfClass4[0] = Long.TYPE;
            Method localMethod5 = localClass.getDeclaredMethod("setWhen", arrayOfClass4);
            Object[] arrayOfObject1 = new Object[1];
            arrayOfObject1[0] = Integer.valueOf(android.R.drawable.star_on);
            localMethod1.invoke(localObject, arrayOfObject1);
            Object[] arrayOfObject2 = new Object[1];
            arrayOfObject2[0] = Boolean.valueOf(true);
            localMethod2.invoke(localObject, arrayOfObject2);
            Object[] arrayOfObject3 = new Object[1];
            arrayOfObject3[0] = Integer.valueOf(-2);
            localMethod4.invoke(localObject, arrayOfObject3);
            Object[] arrayOfObject4 = new Object[1];
            arrayOfObject4[0] = Integer.valueOf(0);
            localMethod5.invoke(localObject, arrayOfObject4);
            Notification localNotification = (Notification)localMethod3.invoke(localObject, new Object[0]);
            localNotification.flags = 16;
            return localNotification;
        } catch (Exception e) {}
        return null;
    }

    public static void applockA(Service service) {
        if(service == null)return;
        try {
            Notification localNotification = applockA(service.getApplicationContext());
            if (localNotification != null)
                service.startForeground(1220, localNotification);
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applockA(this);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
