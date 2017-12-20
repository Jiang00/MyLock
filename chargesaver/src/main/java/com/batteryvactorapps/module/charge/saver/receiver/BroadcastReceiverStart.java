package com.batteryvactorapps.module.charge.saver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.batteryvactorapps.module.charge.saver.protectservicevac.ServiceBattery;


/**
 * Created by on 2016/12/14.
 */

public class BroadcastReceiverStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ServiceBattery.class));
    }
}
