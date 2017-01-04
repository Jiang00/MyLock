package com.security.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.security.manager.db.backgroundData;
import com.security.manager.lib.Utils;
import com.security.manager.lib.io.SafeDB;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.AppFragementSecurity;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.ShowDialogview;
import com.security.manager.page.SlideMenu;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.security.manager.page.SecurityThemeFragment.TAG_TLEF_AD;

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
