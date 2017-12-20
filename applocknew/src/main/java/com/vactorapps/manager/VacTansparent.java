package com.vactorapps.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ivymobi.applock.free.R;
import com.vactorapps.manager.meta.VacPref;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class VacTansparent extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.finish();
        stopService(new Intent(this, VacNotificationService.class));
        startService(new Intent(this, VacNotificationService.class));
        VacPref.setVisitor(true);
        Toast.makeText(this, R.string.security_visitor_on,Toast.LENGTH_LONG).show();
        Tracker.sendEvent(Tracker.ACT_MODE,Tracker.ACT_MODE_NOTIFICATION,Tracker.ACT_MODE_ON,1L);


    }


}
