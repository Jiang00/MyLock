package com.privacy.lock;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by superjoy on 2014/10/9.
 */
public class DeviceAdmin extends DeviceAdminReceiver {
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        intent.putExtra("android.app.extra.DISABLE_WARNING", "Do you really want to disable this security helper?");
        return "Do you really want to disable this security helper?";
    }
}
