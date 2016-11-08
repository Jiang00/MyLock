package com.security.manager;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.privacy.lock.R;
import com.security.manager.meta.Pref;
import com.security.manager.page.AppsFragment;
import com.security.manager.page.DragLayout;
import com.security.manager.page.MyMenu;
import com.security.manager.page.ShowDialogview;
import com.security.manager.lib.Utils;
import com.security.manager.lib.io.SafeDB;
import com.security.manager.meta.MProfiles;
import com.security.manager.page.MessageBox;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.security.manager.page.ThemeFragment.TAG_TLEF_AD;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class AppLock extends ClientActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResult(ArrayList<SearchThread.SearchData> list) {
        if (fragment != null) {
            fragment.onResult(list);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSearchExit() {
        if (fragment != null) {
            fragment.onResult(null);
        }
    }

    @Override
    public List<SearchThread.SearchData> getSearchList() {
        return fragment == null ? super.getSearchList() : fragment.getSearchData();
    }

//    @InjectView(R.id.float_action_menu)

    AppsFragment fragment;

    private String profileName;

    boolean hide;

    @InjectView(R.id.slide_menu_ad)
    FrameLayout ADView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requirePermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Utils.requireCheckAccessPermission(this)) {
                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    if (Utils.isEMUI()) {
                        new android.app.AlertDialog.Builder(this).setTitle(R.string.security_show_permission)
                                .setMessage(R.string.security_permission_msg)
                                .setPositiveButton(R.string.security_permission_grand, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(intent);
                                    }
                                }).setNegativeButton(android.R.string.cancel, null).create().show();

                    } else {
                        ShowDialogview.showPermission(this);

                    }
                }
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
        if (MProfiles.requireUpdateServerStatus()) {
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

        if (hide) {

        } else {
            MyMenu.currentMenuIt = MyMenu.MENU_LOCK_APP;
        }
        setup(R.string.security_lock_app);

        profileName = SafeDB.defaultDB().getString(Pref.PREF_ACTIVE_PROFILE, Pref.PREF_DEFAULT_LOCK);
        long profileId = SafeDB.defaultDB().getLong(Pref.PREF_ACTIVE_PROFILE_ID, 1);

        fragment = (AppsFragment) getFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new AppsFragment();
            Bundle args = new Bundle();
            args.putLong(AppsFragment.PROFILE_ID_KEY, profileId);
            args.putString(AppsFragment.PROFILE_NAME_KEY, profileName);
            args.putBoolean(AppsFragment.PROFILE_HIDE, hide);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "fragment").commit();
        }

//        if (!hide) {
////            setupFloatingActionButtons();
//        } else {
////            float_action_menu.setVisibility(View.GONE);
//        }

        if (Pref.hasNewVersion()) {
            MessageBox.Data data = new MessageBox.Data();
            data.button = MessageBox.BUTTON_YES_NO;
            data.style = R.style.MessageBox;
            data.title = R.string.security_update_title;
            data.yes = R.string.security_update;
            data.no = R.string.security_later_;
            data.messages = Html.fromHtml(Pref.getNewVersionDesc());
            data.onyes = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utils.openPlayStore(App.getContext(), getPackageName());
                }
            };
            MessageBox.show_(this, data);
        } else if (Pref.tip4Rate()) {

            //之前的评价机制

        }
//        } else if (!Pref.isAdvanceEnabled()) {
////            if (Pref.tip4Security()) {
////                showAdvanceSecurity();
////                Pref.tip4SecurityComplete();
////            }
//            try {
//                if (getIntent().getExtras().containsKey("launch")) {
//                    MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_ADVANCE, MyTracker.ACT_ADVANCE, 1L);
//                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(context, DeviceAdmin.class));
//                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.dev_admin_desc));
//                    startActivityForResult(intent, Setting.REQ_CODE_ADVANCE);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        requirePermission();


//        ininShowAD();

    }

//    public void showAdvanceSecurity() {
//        MessageBox.Data data = new MessageBox.Data();
//        data.button = MessageBox.BUTTON_YES_CANCEL;
//        data.yes = R.string.enable;
//        data.cancel = R.string.cancel;
//        data.msg = R.string.protect_uninstall;
//        data.title = R.string.protect_title;
//        data.onyes = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_ADVANCE, MyTracker.ACT_ADVANCE, 1L);
//                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(context, DeviceAdmin.class));
//                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.dev_admin_desc));
//                startActivityForResult(intent, Setting.REQ_CODE_ADVANCE);
//            }
//        };
//        MessageBox.show(this, data);
//    }

//     @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


//    @InjectViews({R.id.add_profile, R.id.switch_profile, R.id.switch_widget})
//    FloatingActionButton[] fabs;

//    public static final int REQ_ADD_PROFILE = 2;


    @Override
    protected void onPause() {
        if (!hide) {
            fragment.saveOrCreateProfile(profileName, server);
        }
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String pn = SafeDB.defaultDB().getString(Pref.PREF_ACTIVE_PROFILE, Pref.PREF_DEFAULT_LOCK);
        if (!pn.equals(profileName)) {
            fragment.switchProfile(MProfiles.getEntries().get(MProfiles.getActiveProfileIdx(pn)), server);
            profileName = pn;
        }
    }

    SharedPreferences getSharedPreferences() {
        return getSharedPreferences("cf", MODE_MULTI_PROCESS);
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Setting.REQ_CODE_ADVANCE) {
//            DevicePolicyManager p = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//            boolean b = p.isAdminActive(new ComponentName(context, DeviceAdmin.class));
//            if (b) {
//                MessageBox.Data d = new MessageBox.Data();
//                d.title = R.string.advanced_security;
//                d.msg = R.string.dev_admin_actived;
//                MessageBox.show(this, d);
//                MyTracker.sendEvent(MyTracker.CATE_SETTING, MyTracker.ACT_ADVANCE, MyTracker.LABEL_ADVANCE, 1);
//            } else {
//                Toast.makeText(context, R.string.dev_admin_canceled, Toast.LENGTH_SHORT).show();
//            }
//            Pref.enableAdvance(b);
//            if (b) {
//                if (!Pref.isOptionPressed(Pref.OPT_ADVANCE_REDDOT)) {
//                    Pref.pressOption(Pref.OPT_ADVANCE_REDDOT);
//                }
//                if (menu != null)
//                    MyMenu.addUninstallMenu(menu);
//            } else {
//                if (menu != null)
//                    MyMenu.removeUninstallMenu(menu);
//            }
//        }
//    }
//
//
    void ininShowAD() {
        if (AndroidSdk.hasNativeAd(TAG_TLEF_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {


            View scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_TLEF_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.app_slide_native_layout, new ClientNativeAd.NativeAdClickListener() {
                @Override
                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {

                }
            }, new ClientNativeAd.NativeAdScrollListener() {
                @Override
                public void onNativeAdScrolled(float v) {

                }
            });
            if (scrollView != null) {
                App.getWatcher().watch(scrollView);
                ADView.addView(scrollView);
            }
        }

    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.security_slide_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DragLayout.Status status = menu.getStatus();
            if (status == DragLayout.Status.Close)
                menu.open();
            else if (status == DragLayout.Status.OpenRight) {
                menu.close();
            } else
                askForExit();
        }
        return true;
    }


}
