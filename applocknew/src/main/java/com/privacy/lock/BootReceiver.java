package com.privacy.lock;

import android.content.*;

import com.privacy.lock.meta.MApps;
import com.privacy.lock.meta.Pref;
import com.security.manager.lib.Utils;

/**
 * Created by superjoy on 2014/8/25.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String act = intent.getAction();
        Utils.LOGER("on receive " + intent.toString());
        switch (act) {
            case Intent.ACTION_BOOT_COMPLETED:
                context.startService(new Intent(context, Worker.class));
                break;
            case Intent.ACTION_PACKAGE_REMOVED:{
                if (intent.getDataString() == null || intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return;
                final String pkg = intent.getDataString().substring("package:".length());
                if (pkg.startsWith(context.getPackageName())) {
                    //do nothing for our skins and ourselves
                    return;
                }
                MApps.removed(pkg);
            }
                break;
            case Intent.ACTION_PACKAGE_ADDED:{
                if (intent.getDataString() == null || intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) return;
                final String pkg = intent.getDataString().substring("package:".length());
                if (pkg.startsWith(context.getPackageName())) {
                    //do nothing for our skins and ourselves
                    return;
                }
                MApps.add(context, pkg);
                SharedPreferences sp = App.getSharedPreferences();
                if (sp.getBoolean(Pref.LOCK_NEW, Pref.LOCK_DEFAULT)){
                    context.startService(new Intent(context, Worker.class).putExtra(Worker.WORK_EXTRA_KEY, Worker.WORK_LOCK_NEW).putExtra("pkg", pkg));
                }
            }
                break;
            case Intent.ACTION_USER_PRESENT:
                context.startService(new Intent(context, Worker.class).putExtra("on", true));
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                context.startService(new Intent(context, Worker.class));
                break;
        }
    }
}
