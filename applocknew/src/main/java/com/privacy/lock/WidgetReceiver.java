package com.privacy.lock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by song on 15/9/7.
 */
public class WidgetReceiver extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, Worker.class));
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, Worker.class));
        for (int appID : appWidgetIds) {

            // 获取 example_appwidget.xml 对应的RemoteViews
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_switch);

            // 设置显示图片
            remoteView.setImageViewResource(R.id.icon, R.drawable.icon);

            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, Worker.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
