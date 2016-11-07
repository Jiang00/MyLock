package com.privacy.lock;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.view.Menu;
import android.widget.FrameLayout;

import com.security.manager.lib.Utils;
import com.security.manager.lib.io.SafeDB;
import com.privacy.lock.meta.MProfiles;
import com.privacy.lock.meta.Pref;
import com.privacy.lock.view.AppsFragment;
import com.privacy.lock.view.MessageBox;
import com.privacy.lock.view.MyMenu;
import com.privacy.lock.view.ShowDialogview;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void requirePermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Utils.requireCheckAccessPermission(this)) {
                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                if (getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
//                    new android.app.AlertDialog.Builder(this).setTitle(R.string.permission_title)
//                            .setMessage(R.string.permission_msg)
//                            .setPositiveButton(R.string.permission_grant, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    startActivity(intent);
//                                }
//                            }).setNegativeButton(android.R.string.cancel, null).create().show();

//                    if (getAppOps(this)) {
                        ShowDialogview.showPermission(this);
                   // }


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
//        if (hide) {
//            Help.attach(shareBar, helpScrollView,
//                    R.drawable.locked, 0, R.string.help_show,
//                    R.drawable.unlock, 1, R.string.help_hide,
//                    R.drawable.ic_menu_search, 1, R.string.help_search_app);
//        } else {
//            Help.attach(shareBar, helpScrollView,
//                    R.drawable.locked, 0, R.string.help_unlock,
//                    R.drawable.unlock, 1, R.string.help_lock,
//                    R.drawable.ic_menu_search, 1, R.string.help_search_app);
//        }
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
        setContentView(R.layout.mat_list_v_newest);
        ButterKnife.inject(this);

        if (hide) {
            MyMenu.currentMenuIt = MyMenu.MENU_HIDE_APP;
            new Thread() {
                @Override
                public void run() {
                    if (!Tools.isMyPhoneRooted()) {
                        MessageBox.Data data = new MessageBox.Data();
                        data.alert = false;
                        data.title = MessageBox.NO_TITLE;
                        data.msg = R.string.phone_not_root;
                        MessageBox.show(AppLock.this, data);
                    }
                }
            }.start();
        } else {
            MyMenu.currentMenuIt = MyMenu.MENU_LOCK_APP;
        }
        setup(hide ? R.string.hide_app : R.string.lock_tab);

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
            data.title = R.string.update_title;
            data.yes = R.string.update;
            data.no = R.string.later;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


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
    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }

//    void ininShowAD() {
//        if (AndroidSdk.hasNativeAd(TAG_TLEF_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
//
//
//            View scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_TLEF_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.app_native_layout, new ClientNativeAd.NativeAdClickListener() {
//                @Override
//                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {
//
//                }
//            }, new ClientNativeAd.NativeAdScrollListener() {
//                @Override
//                public void onNativeAdScrolled(float v) {
//
//                }
//            });
//            if (scrollView != null) {
//                App.getWatcher().watch(scrollView);
//                ADView.addView(scrollView);
//            }
//        }
//
//    }


}
