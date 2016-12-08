package com.security.manager;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.client.AndroidSdk;
import com.privacy.lock.R;
import com.security.manager.db.backgroundData;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.AppFragementSecurity;
import com.security.manager.page.SlideMenu;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.ShowDialogview;
import com.security.manager.lib.Utils;
import com.security.manager.lib.io.SafeDB;
import com.security.manager.meta.SecuritProfiles;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by SongHualin on 6/12/2015.
 */
public class SecurityAppLock extends ClientActivitySecurity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        tips();
    }

    AppFragementSecurity fragment;

    private String profileName;

    boolean hide;

//    @InjectView(R.id.slide_menu_ad)
//    FrameLayout ADView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.facebook)
    ImageView facebook;

    @InjectView(R.id.goolge)
    ImageView google;

    @InjectView(R.id.googleplay)
    ImageView googleplay;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requirePermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Utils.requireCheckAccessPermission(this)) {
                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
//                    startActivity(intent);
                    ShowDialogview.showPermission(this);
//                    if (Utils.isEMUI() || inf.contains("ZTE")) {
//                        new android.app.AlertDialog.Builder(this).setTitle(R.string.security_show_permission)
//                                .setMessage(R.string.security_permission_msg)
//                                .setPositiveButton(R.string.security_permission_grand, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OK, Tracker.ACT_PERMISSION_OK, 1L);
//
//                                    }
//                                }).setNegativeButton(android.R.string.cancel, null).create().show();
//
//                    } else {
//                    }
                }
            }

        } else {
            if (!SecurityMyPref.getOpenPermission()) {
                Tracker.sendEvent(Tracker.ACT_PERMISSION, Tracker.ACT_PERMISSION_OPEN, Tracker.ACT_PERMISSION_OPEN, 1L);
                SecurityMyPref.setOpenPermission(true);
            }
        }
    }

    @Override
    protected void onIntent(Intent intent) {
        hide = intent.getBooleanExtra("hide", false);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        hide = savedInstanceState.getBoolean("hide");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hide", hide);
    }

    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (SecuritProfiles.requireUpdateServerStatus()) {
            try {
                server.notifyApplockUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void setupView() {
        setContentView(R.layout.security_slidemenu_data);
        ButterKnife.inject(this);
        setupToolbar();
        SecurityMenu.currentMenuIt = SecurityMenu.MENU_LOCK_APP;
        setup(R.string.security_lock_app);

        profileName = SafeDB.defaultDB().getString(SecurityMyPref.PREF_ACTIVE_PROFILE, SecurityMyPref.PREF_DEFAULT_LOCK);
        long profileId = SafeDB.defaultDB().getLong(SecurityMyPref.PREF_ACTIVE_PROFILE_ID, 1);

        fragment = (AppFragementSecurity) getFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new AppFragementSecurity();
            Bundle args = new Bundle();
            args.putLong(AppFragementSecurity.PROFILE_ID_KEY, profileId);
            args.putString(AppFragementSecurity.PROFILE_NAME_KEY, profileName);
            args.putBoolean(AppFragementSecurity.PROFILE_HIDE, hide);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "fragment").commit();
        }
        requirePermission();

        //侧边栏广告取消
//        ininShowAD();
        initclick();

        initgetData();


    }

    @Override
    protected void onPause() {
        fragment.saveOrCreateProfile(profileName, server);
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


    }

//    void ininShowAD() {
//        if (AndroidSdk.hasNativeAd(TAG_TLEF_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
//            View scrollView = AndroidSdk.peekNativeAdViewWithLayout(TAG_TLEF_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, R.layout.app_slide_native_layout, null);
//            if (scrollView != null) {
//                ADView.addView(scrollView);
//            }
//        }
//
//    }

    private void setupToolbar() {
        if (SecurityMyPref.hasIntruder()) {
            toolbar.setNavigationIcon(R.drawable.security_slide_menu_red);
        } else {
            toolbar.setNavigationIcon(R.drawable.security_slide_menu);
        }

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SlideMenu.Status status = menu.getStatus();
            if (status == SlideMenu.Status.Close)
                menu.open();
            else if (status == SlideMenu.Status.OpenRight) {
                menu.close();
            } else
                askForExit();
        }
        return false;
    }

    private void initgetData() {
        String data = AndroidSdk.getExtraData();
        if (data != null) {
            backgroundData.onReceiveData(this, data);

        }
    }
    public void initclick() {
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(SecurityMenu.FACEBOOK);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_FACEBOOK, Tracker.ACT_FACEBOOK, 1L);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(SecurityMenu.GOOGLE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLUS, Tracker.ACT_GOOGLE_PLUS, 1L);

            }
        });

        googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(SecurityMenu.GOOGLEPLAY);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLAY, Tracker.ACT_GOOGLE_PLAY, 1L);


            }
        });

    }

}
