package com.auroras.module.charge.saver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.auroras.module.charge.saver.aurorasprotectservice.ServiceBattery;


/**
 * Created by on 2016/12/14.
 */

public class BroadcastReceiverStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ServiceBattery.class));
    }
}
