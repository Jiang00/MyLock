package com.vactorapps.manager;

import android.content.*;
import android.util.Log;

import com.vactorapps.manager.meta.MApps;
import com.vactorapps.manager.meta.VacPref;
import com.vactorappsapi.manager.lib.Utils;

/**
 * Created by superjoy on 2014/8/25.
 */
public class VacBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String act = intent.getAction();
        Utils.LOGER("myreceive " + intent.toString());
        switch (act) {
            case Intent.ACTION_BOOT_COMPLETED:
                Log.e("myreceive","------");
                context.startService(new Intent(context, WorksService.class));
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
                SharedPreferences sp = MyApp.getSharedPreferences();
                if (sp.getBoolean(VacPref.LOCK_NEW, VacPref.LOCK_DEFAULT)){
                    context.startService(new Intent(context, WorksService.class).putExtra(WorksService.WORK_EXTRA_KEY, WorksService.WORK_LOCK_NEW).putExtra("pkg", pkg));
                }
            }
                break;
            case Intent.ACTION_USER_PRESENT:
                context.startService(new Intent(context, WorksService.class).putExtra("on", true));
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                context.startService(new Intent(context, WorksService.class));
                break;
        }
    }
}
