package com.security.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.WrapperListAdapter;

import com.android.launcher3.theme.ThemeManager;
import com.privacy.lock.R;
import com.security.manager.db.SecurityProfileHelper;
import com.security.manager.lib.Utils;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SecurityTheBridge;
import com.security.manager.page.PasswordFragmentSecurity;
import com.security.manager.page.PatternFragmentSecurity;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.meta.SecuritProfiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by superjoy on 2014/8/25.
 */
public class SecurityPatternActivity extends SecuritySetPattern {
    public static final int ACTION_UNLOCK_SELF = 0;
    public static final int ACTION_UNLOCK_OTHER = 1;
    public static final int ACTION_SWITCH_PROFILE = 2;
    public static final int ACTION_TOGGLE_PROTECT = 3;
    int action = ACTION_UNLOCK_SELF;

    PatternFragmentSecurity patternFrag;
    PasswordFragmentSecurity passFrag;
    boolean normal = false;
    Intent notiIntent;

    public void toggle(boolean normal) {
        if (normal) {
            if (passFrag == null) {
                passFrag = new PasswordFragmentSecurity();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, passFrag).commitAllowingStateLoss();
        } else {
            if (patternFrag == null) {
                patternFrag = new PatternFragmentSecurity();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, patternFrag).commitAllowingStateLoss();
        }
        this.normal = normal;
    }

    @Override
    protected void onStart() {
        resetThemeBridgeImpl();
        super.onStart();
        if (!toggled && (passFrag != null || patternFrag != null)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, normal ? passFrag : patternFrag).commitAllowingStateLoss();
        }
    }

    protected void resetThemeBridgeImpl() {
//        SecurityBridgeImpl.reset(this, true, false, "");
        SecurityBridgeImpl.reset(this, action == ACTION_UNLOCK_SELF, false, pkg);
    }

    @Override
    public void onResume() {
        switchTheme();
//        selectOperation();
        notiIntent = getIntent();


        super.onResume();
    }

    @Override
    protected void onStop() {
        if (passFrag != null || patternFrag != null) {
            getSupportFragmentManager().beginTransaction().remove(normal ? passFrag : patternFrag).commitAllowingStateLoss();
        }
        toggled = false;
        super.onStop();
    }


    public void unlockSuccess(boolean unlockMe) {
        switch (action) {
            case ACTION_SWITCH_PROFILE:
                for (SecurityProfileHelper.ProfileEntry entry : SecuritProfiles.getEntries()) {
                    if (entry.name.equals(profileName)) {
                        SecuritProfiles.switchProfile(entry, server);
                        break;
                    }
                }
                finish();
                break;
            case ACTION_TOGGLE_PROTECT:
                try {
                    server.toggleProtectStatus();
                    finish();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_UNLOCK_OTHER:
                try {
                    server.unlockLastApp(unlockMe);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                break;

            case ACTION_UNLOCK_SELF:

                startListApp();
                if (notiIntent.hasExtra(Notification.NOTIFICATION)) {
                    SecurityMyPref.setVisitor(false);
                    stopService(new Intent(this, NotificationService.class));
                    startService(new Intent(this, NotificationService.class));
                    Toast.makeText(this, R.string.security_visitor_off, Toast.LENGTH_LONG).show();
                    Tracker.sendEvent(Tracker.ACT_MODE, Tracker.ACT_MODE_NOTIFICATION, Tracker.ACT_MODE_OFF, 1L);

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (unlockApp || setting == SET_EMPTY) {
            backHome();
        }
        finish();
    }

    boolean firstLaunchShowResult = false;

    @Override
    public void startListApp() {
        if (firstLaunchShowResult) {
            firstTimeLaunch();
            firstLaunchShowResult = false;
            return;
        }
        SecurityMyPref.launchNow();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), SecurityAppLock.class.getName());
        intent.putExtra("hide", false);
        intent.putExtra("launch", true);
        startActivity(intent);
        finish();
    }

    public void switchTheme() {
        if (SecurityTheBridge.requestTheme && SecurityTheBridge.needUpdate) {
            SecurityTheBridge.needUpdate = false;
            SecurityTheBridge.requestTheme = false;
            destroyThemeContext();
            selectOperation();
        }
    }

    public void destroyThemeContext() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (passFrag != null) {
            fragmentTransaction.remove(passFrag);
        }
        if (patternFrag != null) {
            fragmentTransaction.remove(patternFrag);
        }
        fragmentTransaction.commit();
        passFrag = null;
        patternFrag = null;
        SecurityTheBridge.themeContext = null;
        System.gc();
    }

    public boolean unlockApp = false;
    public byte setting;

    @Override
    public void setupView() {
        Log.e("mynewname", getPackageName() + "-------");

        if (SecurityMyPref.isANewDay()) {
            Tracker.sendEvent(Tracker.CATE_ACTION, Tracker.ACT_DAILY_USE, Tracker.ACT_DAILY_USE, 1L);
        }
        Tracker.sendEvent(Tracker.CATE_ACTION_OPEN_APP, Tracker.CATE_ACTION_OPEN_APP_TIME, Tracker.CATE_ACTION_OPEN_APP_TIME, 1L);
        SecurityMyPref.upgrade();

        Intent intent = getIntent();
        if (intent.hasExtra("theme_package_name")) {
            String theme = intent.getStringExtra("theme_package_name");
//            App.getSharedPreferences().edit().putString("theme_package_name", theme).putBoolean("theme-switched", true).apply();
//            SecurityTheBridge.needUpdate = true;
//            SecurityTheBridge.requestTheme = true;
//            switchTheme();
            selectOperation();
            ThemeManager.applyTheme(this, theme);
        } else {
            selectOperation();
        }
    }

    ArrayList<String> firstLaunchList;
    HashMap<String, String> firstLaunchLabels;
    HashMap<String, Boolean> firstLaunchLocked;
    HashMap<String, Boolean> firstLaunchFilter;

    void loadPackages() {
        if (firstLaunchList != null && firstLaunchList.size() > 0) return;
        PackageManager packageManager = getPackageManager();
        String[] predefinedpkgs = new String[]{
                "com.sec.android.gallery3d",
                "com.android.gallery3d",
                "com.android.gallery",
                "com.android.contacts",
                "com.android.mms",
//                "com.android.phone",
//                    "com.android.packageinstaller",
                "com.facebook.katana",
                "com.google.android.gm",
                "com.android.email",
                "com.android.vending",
                "com.twitter.android",
                "com.instagram.android",
                "com.google.android.youtube",
                "jp.naver.security_invade_li.android",
                "com.whatsapp",
                "com.facebook.orca",
                "com.tencent.mm",
                "com.google.android.talk",
                "com.skype.raider",
                "com.kakao.talk"
        };
        ArrayList<String> commons = new ArrayList<>();
        HashMap<String, Boolean> filter = new HashMap<>();
        HashMap<String, String> labels = new HashMap<>();
        for (String pkg : predefinedpkgs) {
            try {
                PackageInfo pi = packageManager.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
                labels.put(pkg, pi.applicationInfo.loadLabel(packageManager).toString());
                commons.add(pkg);
            } catch (Exception ignore) {
            }
            filter.put(pkg, true);
        }

        firstLaunchList = commons;
        firstLaunchLabels = labels;
        firstLaunchLocked = new HashMap<>();
        for (int i = 0; i < commons.size(); ++i) {
//            if (i > 10) break; //推荐默认枷锁前面多少个
            firstLaunchLocked.put(commons.get(i), true);
        }
        firstLaunchFilter = filter;
    }

    public void firstTimeLaunch() {
        setContentView(R.layout.security_first);

        loadPackages();

        final Button next = (Button) findViewById(R.id.next);
        final ListView lv = (ListView) findViewById(R.id.abs_list);

        View header = getLayoutInflater().inflate(R.layout.security_first_header, null, false);
        lv.addHeaderView(header, null, false);

        final boolean clickable = !firstLaunchShowResult;

        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return firstLaunchList.size();
            }

            @Override
            public boolean isEnabled(int position) {
                return clickable;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return clickable;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = LayoutInflater.from(SecurityPatternActivity.this).inflate(R.layout.security_locked_apps, parent, false);
                    holder = new ViewHolder();
                    holder.icon = (android.widget.ImageView) convertView.findViewById(R.id.icon);
                    holder.appName = (TextView) convertView.findViewById(R.id.name);
                    holder.encrypted = convertView.findViewById(R.id.bg_sel);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                String pkg = firstLaunchList.get(position);
                Bitmap icon = ImageMaster.getImage(pkg);
                if (icon == null) {
                    try {
                        BitmapDrawable bd = (BitmapDrawable) getPackageManager().getPackageInfo(pkg, 0).applicationInfo.loadIcon(getPackageManager());
                        icon = bd.getBitmap();
                        ImageMaster.addImage(pkg, icon);
                    } catch (OutOfMemoryError | Exception error) {
                        error.printStackTrace();
                    }
                }
                holder.icon.setImageBitmap(icon);
                holder.appName.setText(firstLaunchLabels.get(pkg));
                holder.encrypted.setEnabled(firstLaunchLocked.containsKey(pkg));
                if (!clickable) {
                    holder.encrypted.setSelected(false);
                } else {
                    holder.encrypted.setSelected(true);
                }

                return convertView;
            }
        });

        if (!firstLaunchShowResult) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) return;
                    --position;
                    String pkg = firstLaunchList.get(position);
                    boolean locked = firstLaunchLocked.containsKey(pkg);
                    if (locked) {
                        firstLaunchLocked.remove(pkg);
                    } else {
                        firstLaunchLocked.put(pkg, true);
                    }
                    if (firstLaunchLocked.size() > 0) {
                        next.setEnabled(true);
                    } else {
                        next.setEnabled(false);
                    }
                    ((BaseAdapter) ((WrapperListAdapter) lv.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                }
            });
            new Thread() {
                @Override
                public void run() {
                    PackageManager packageManager = getPackageManager();
                    final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> pkgs = packageManager.queryIntentActivities(mainIntent, 0);

                    String pkgname = getPackageName();

                    HashMap<String, String> labels = new HashMap<>();
                    ArrayList<String> apps = new ArrayList<>();
                    for (int i = 0; i < pkgs.size(); ++i) {
                        ResolveInfo pkg = pkgs.get(i);
                        String pkgName = pkg.activityInfo.packageName;
                        if (pkgName.equals(pkgname)) {
                            pkgs.remove(i);
                            --i;
                            continue;
                        }
                        String pn = pkgName.toLowerCase();
                        if ((pkg.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                            pkgs.remove(i);
                            --i;
                        } else if (firstLaunchFilter.containsKey(pn)) {
                            pkgs.remove(i);
                            --i;
                        } else {
                            labels.put(pkgName, pkg.loadLabel(packageManager).toString());
                            apps.add(pkgName);
                            if (labels.size() == 10 || i == pkgs.size() - 1) {
                                final HashMap<String, String> labels_ = (HashMap<String, String>) labels.clone();
                                final ArrayList<String> apps_ = (ArrayList<String>) apps.clone();
                                labels.clear();
                                apps.clear();
                                SecurityPatternActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        firstLaunchList.addAll(apps_);
                                        apps_.clear();
                                        firstLaunchLabels.putAll(labels_);
                                        labels_.clear();
                                        ((BaseAdapter) ((WrapperListAdapter) lv.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                }
            }.start();
        } else {
            next.setEnabled(true);
            firstLaunchList.clear();
            firstLaunchList.addAll(firstLaunchLocked.keySet());
        }
        if (firstLaunchShowResult) {
            TextView title = (TextView) header.findViewById(R.id.title);
            TextView desc = (TextView) header.findViewById(R.id.desc);
            ImageView icon = (ImageView) header.findViewById(R.id.select_app_status);
            icon.setBackgroundResource(R.drawable.security_select_apps_successful);
            title.setText(R.string.security_app_pro_successful);
            desc.setText(R.string.security_select_apps_locked);
            next.setText(R.string.security_done);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startListApp();
                    Tracker.sendEvent(Tracker.ACT_LEADER, Tracker.ACT_LEDADER_OK, Tracker.ACT_LEDADER_OK, 1L);
                }
            });
        } else {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstSetup = true;
                    firstLaunchShowResult = true;
                    setGraphView();
                    Tracker.sendEvent(Tracker.ACT_LEADER, Tracker.ACT_LEADER_SETPASSWORD, Tracker.ACT_LEADER_SETPASSWORD, 1L);

                }
            });
        }
    }

    @Override
    public void setEmail() {
        if (firstSetup) {
            if (firstLaunchLocked != null && firstLaunchLocked.size() > 0) {
                try {
                    SQLiteDatabase db = SecurityProfileHelper.singleton(getApplicationContext()).getWritableDatabase();
                    long profileId = SecurityProfileHelper.ProfileEntry.createProfile(db, SecurityMyPref.PREF_DEFAULT_LOCK, new ArrayList<>(firstLaunchLocked.keySet()));
                    SecurityProfileHelper.ProfileEntry entry = new SecurityProfileHelper.ProfileEntry();
                    entry.id = profileId;
                    entry.name = SecurityMyPref.PREF_DEFAULT_LOCK;
                    SecuritProfiles.addProfile(entry);
                    SecuritProfiles.switchProfile(entry, server);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.setEmail();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("action", action);
        outState.putString("pkg", pkg);
        outState.putInt("setting", setting);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        action = savedInstanceState.getInt("action");
        pkg = savedInstanceState.getString("pkg");
        setting = (byte) savedInstanceState.getInt("setting");
    }

    String pkg;

    @Override
    protected void onIntent(Intent intent) {
        action = intent.getIntExtra("action", intent.hasExtra("pkg") ? ACTION_UNLOCK_OTHER : ACTION_UNLOCK_SELF);
        pkg = intent.getStringExtra("pkg");
        setting = intent.getByteExtra("set", SET_EMPTY);
        profileName = intent.getStringExtra("profileName");
    }

    boolean toggled = false;
    String profileName = null;

    public void selectOperation() {
        try {
            switch (setting) {
                case SET_EMPTY:
                    SecurityBridgeImpl.reset(this, action == ACTION_UNLOCK_SELF, false, pkg);
                    if (SecurityMyPref.isPasswdSet(true) || SecurityMyPref.isPasswdSet(false)) {
                        unlockApp = true;
                        setContentView(R.layout.security_password_container);
//                        createThemeContextIfNecessary(this);
                        toggle(SecurityMyPref.isUseNormalPasswd());
                        toggled = true;
                    } else {
                        firstTimeLaunch();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onDestroy() {
        SecurityTheBridge.themeContext = null;
        SecurityBridgeImpl.clear();
        super.onDestroy();
    }

//    public static void createThemeContextIfNecessary(Context context) {
//
//        context = context.getApplicationContext();
//        SharedPreferences sp = App.getSharedPreferences();
//        String themePkg = sp.getString("theme_package_name", null);
//        if (themePkg != null && themePkg.equals("custom")) {
//            SecurityTheBridge.themeContext = context;
//        } else {
//            if (themePkg != null) {
//                try {
//                    context.getPackageManager().getPackageInfo(themePkg, 0);
//                } catch (PackageManager.NameNotFoundException e) {
//                    sp.edit().remove("theme_package_name").apply();
//                    SecurityTheBridge.themeContext = null;
//                    e.printStackTrace();
//                }
//            }
//            if (SecurityTheBridge.themeContext != null) return;
//            try {
//                Context themeContext = null;
//                if (themePkg != null) {
//                    try {
//                        themeContext = context.createPackageContext(themePkg, CONTEXT_IGNORE_SECURITY);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        sp.edit().remove("theme_package_name").commit();
////                        MyTracker.sendEvent(MyTracker.CATE_EXCEPTION, MyTracker.ACT_CRASH, Tools.getExceptionMessage(e), 0L);
//                    }
//                }
//                if (themeContext == null)
//                    themeContext = context;
//                else {
//                    Class c = ClassLoader.class;
//                    Field parent = c.getDeclaredField("parent");
//                    parent.setAccessible(true);
//                    parent.set(themeContext.getClassLoader(), context.getApplicationContext().getClassLoader());
//                }
//
//                SecurityTheBridge.themeContext = themeContext;
//            } catch (Exception e) {
//                SecurityTheBridge.themeContext = context;
////                MyTracker.sendEvent(MyTracker.CATE_EXCEPTION, Tools.getExceptionMessage(e), "theme context", 0L);
//                e.printStackTrace();
//            }
//        }
//    }

}