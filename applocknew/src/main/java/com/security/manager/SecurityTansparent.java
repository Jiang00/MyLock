package com.security.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ivymobi.applock.free.R;
import com.security.manager.meta.SecurityMyPref;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class SecurityTansparent extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.finish();
        stopService(new Intent(this, NotificationService.class));
        startService(new Intent(this, NotificationService.class));
        SecurityMyPref.setVisitor(true);
        Toast.makeText(this, R.string.security_visitor_on,Toast.LENGTH_LONG).show();
        Tracker.sendEvent(Tracker.ACT_MODE,Tracker.ACT_MODE_NOTIFICATION,Tracker.ACT_MODE_ON,1L);


    }


}
