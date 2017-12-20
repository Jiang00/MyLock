package com.vactorapps.manager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.ivymobi.applock.free.R;


/**
 * Created by song on 15/9/7.
 */
public class WidgetReceiver extends AppWidgetProvider {
    private RemoteViews remoteView;

    //在Widget使用中，会多次调用该方法
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, WorksService.class));
        if (TextUtils.equals("my.appwidget.action.wiget",intent.getAction())){
            if (remoteView == null) {
                remoteView = new RemoteViews(context.getPackageName(), R.layout.security_mywidget_switch);
            }
            Intent intent1 = new Intent(context, FristActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
            ComponentName cn = new ComponentName(context.getApplicationContext(), FristActivity.class);
            appWidgetManager.updateAppWidget(cn, remoteView);

        }
        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, WorksService.class));
//        for (int appID : appWidgetIds) {

        // 获取 example_appwidget.xml 对应的RemoteViews
        if (remoteView == null) {
            remoteView = new RemoteViews(context.getPackageName(), R.layout.security_mywidget_switch);
        }
        // 设置显示图片
        Intent startActivityIntent = new Intent(context, FristActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent processInfoIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.ll_processinfo, processInfoIntent);

        // 更新 widget
        appWidgetManager.updateAppWidget(appWidgetIds, remoteView);
//        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    //当Widget第一次创建的时候，该方法调用，然后启动后台的服务
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, WorksService.class));
    }

    //当把桌面上的Widget全部都删掉的时候，调用该方法
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent stopUpdateIntent = new Intent(context, WorksService.class);
        context.stopService(stopUpdateIntent);
    }
}
