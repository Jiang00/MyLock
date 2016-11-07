package com.security.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.security.manager.lib.Utils;

/**
 * Created by song on 15/9/6.
 */
public class KeepAliveApi {
    public static boolean isKeepAliveByRoot(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public static boolean keepAliveByRoot() {
        String[] commands = {
                "mount -o remount, rw /system",
                "cat /data/app/com.privacy.lock-1.apk > /system/app/applock.apk",
                "cat /data/app/com.privacy.lock-2.apk >> /system/app/applock.apk",
                "cat /data/data/com.privacy.lock/lib/libcore.so > /system/lib/libcore.so",
                "cat /data/data/com.privacy.lock/lib/libdog.so > /system/lib/libdog.so",
                "chmod 644 /system/app/applock.apk",
                "mount -o remount, ro /system",
                "reboot"
        };
        return Utils.runCommandWithRoot(commands);
    }
}
