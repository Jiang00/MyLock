package com.security.manager;

import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import com.privacy.lock.R;
import com.security.manager.db.ProfileDBHelper;
import com.security.manager.meta.Pref;
import com.security.manager.page.PasswordFragment;
import com.security.manager.page.PatternFragment;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.meta.MProfiles;
import com.security.manager.meta.ThemeBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by superjoy on 2014/8/25.
 */
public class PatternActivity extends SetupPattern {
    public static final int ACTION_UNLOCK_SELF = 0;
    public static final int ACTION_UNLOCK_OTHER = 1;
    public static final int ACTION_SWITCH_PROFILE = 2;
    public static final int ACTION_TOGGLE_PROTECT = 3;
    int action = ACTION_UNLOCK_SELF;

    PatternFragment patternFrag;
    PasswordFragment passFrag;
    boolean normal = false;

    public void toggle(boolean normal) {
        if (normal) {
            if (passFrag == null) {
                passFrag = new PasswordFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, passFrag).commitAllowingStateLoss();
        } else {
            if (patternFrag == null) {
                patternFrag = new PatternFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, patternFrag).commitAllowingStateLoss();
        }
        this.normal = normal;
    }

    @Override
    protected void onStart() {
        resetThemeBridgeImpl();
        super.onStart();
        if (!toggled && (passFrag != null || patternFrag != null)){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, normal ? passFrag : patternFrag).commitAllowingStateLoss();
        }
    }

    protected void resetThemeBridgeImpl() {
//        ThemeBridgeImpl.reset(this, true, false, "");
        ThemeBridgeImpl.reset(this, action == ACTION_UNLOCK_SELF, false, pkg);
    }

    @Override
    public void onResume() {
        switchTheme();
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (passFrag != null || patternFrag != null){
            getSupportFragmentManager().beginTransaction().remove(normal ? passFrag : patternFrag).commitAllowingStateLoss();
        }
        toggled = false;
        super.onStop();
    }

    public void unlockSuccess(boolean unlockMe) {
        switch (action){
            case ACTION_SWITCH_PROFILE:
                for(ProfileDBHelper.ProfileEntry entry : MProfiles.getEntries()){
                    if (entry.name.equals(profileName)){
                        MProfiles.switchProfile(entry, server);
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
        if (firstLaunchShowResult){
            firstTimeLaunch();
            firstLaunchShowResult = false;
            return;
        }
        Pref.launchNow();
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), AppLock.class.getName());
        intent.putExtra("hide", false);
        intent.putExtra("launch", true);
        startActivity(intent);
        finish();
    }

    public void switchTheme() {
        if (ThemeBridge.requestTheme && ThemeBridge.needUpdate) {
            ThemeBridge.needUpdate = false;
            ThemeBridge.requestTheme = false;
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
        ThemeBridge.themeContext = null;
        System.gc();
    }

    public boolean unlockApp = false;
    public byte setting;

    @Override
    public void setupView() {
        if (Pref.isANewDay()) {
            MyTracker.sendEvent(MyTracker.CATE_ACTION, MyTracker.ACT_DAILY_USE, MyTracker.ACT_DAILY_USE, 1L);
        }
        Pref.upgrade();

        Intent intent = getIntent();
        if (intent.hasExtra("theme")) {
            String theme = intent.getStringExtra("theme");
            App.getSharedPreferences().edit().putString("theme", theme).putBoolean("theme-switched", true).apply();
            ThemeBridge.needUpdate = true;
            ThemeBridge.requestTheme = true;
            switchTheme();
        } else {
            selectOperation();
        }
    }

    ArrayList<String> firstLaunchList;
    HashMap<String, String> firstLaunchLabels;
    HashMap<String, Boolean> firstLaunchLocked;
    HashMap<String, Boolean> firstLaunchFilter;
    void loadPackages(){
        if (firstLaunchList != null && firstLaunchList.size() > 0) return;
        PackageManager packageManager = getPackageManager();
        String[] predefinedpkgs = new String[]{
                "com.badoo.mobile",
                "com.tencent.mobileqq",
                "com.tencent.mobileqqi",
                "com.skype.raider",
                "com.skype.rover",
                "com.jnj.mocospace.android",
                "com.instagram.android",
                "mingle.android.mingle",
                "com.taggedapp",
                "com.igg.android.im",
                "com.snapchat.android",
                "com.myyearbook.m",
                "co.vine.android",
                "com.tumblr",
                "com.skout.android",
                "com.tinder",
                "com.twitter.android",
                "com.pinterest",
                "com.viber.voip","com.imo.android.imoim","com.facebook.orca","com.facebook.lite","com.whatsapp",
                "com.askfm",
                "com.oovoo",
                "com.singlesaroundme.android",
                "com.facebook.katana",
                "sh.whisper", "com.sgiggle.production", "kik.android", "com.pof.android", "com.hotornot.app", "com.tencent.mm", "com.skype.raider", "com.mico", "com.waplog.social", "com.minus.android", "com.jaumo", "com.linkedin.android", "com.hi5.app", "com.unearby.sayhi", "com.choiceoflove.dating", "com.vkontakte.android", "com.keek", "ru.mamba.client", "com.playcorp.peepapp", "jp.naver.line.android", "com.instanza.cocovoice", "com.airg.hookt", "com.path", "com.weheartit", "chat.meet.date.me", "im.twogo.godroid", "com.tencent.mobileqqi", "com.tencent.qqlite", "kr.woot0pia.talkreply", "com.kakao.talk", "com.google.android.apps.plus", "com.immomo.momo", "com.paypal.android.p2pmobile", "com.google.android.apps.gmoney", "com.google.android.apps.walletnfcrel", "ru.mw", "com.paypal.here", "com.ns.paypalpp", "com.venmo", "com.eg.android.AlipayGphone", "com.alipay.android.client.pad", "com.moneybookers.skrillpayments", "com.payoneer.android", "com.squareup.cash", "com.transferwise.android", "com.dwolla.dwolla", "com.scvngr.levelup.app", "de.schildbach.wallet", "com.infonow.bofa", "com.htsu.hsbcpersonalbanking", "com.abnamro.nl.mobile.payments", "com.citi.citimobile", "com.db.mm.deutschebank", "fr.creditagricole.androidapp", "com.rbs.mobile.android.iom", "net.bnpparibas.mescomptes", "com.bnpp.easybanking", "com.americanexpress.android.acctsvcs.us", "com.wf.wellsfargomobile", "com.wf.wellsfargomobile.tablet", "com.chase.sig.android", "com.creditkarma.mobile", "com.konylabs.capitalone", "com.geico.mobile", "com.ifs.banking.fiid1460", "com.westernunion.android.mtapp", "com.netspend.mobileapp.westernunion", "com.mint", "com.netgate", "com.chasepay.sig.android", "com.firstdata.moneynetwork", "com.xoom.android.app", "com.worldremit.android", "com.discoverfinancial.mobile", "com.discoverfinancial.tablet", "com.usaa.mobile.android.usaa", "com.usbank.mobilebanking", "com.usb.cps.axol.usbc", "com.navyfederal.android", "com.brisk.jpay", "com.pnc.ecommerce.mobile", "com.yahoo.mobile.client.android.finance", "com.capitalone.mobile.wallet", "com.konylabs.capitalone", "com.tdbank", "com.tdbank.retail.tablet", "com.netspend.product.android", "com.netspend.product.android.ace", "com.netspend.mobileapp.heb", "com.netspend.aa.product.android", "com.netspend.product.android.bet", "com.netspend.mobileapp.skylight", "com.mfoundry.mb.android.mb_15000001", "com.cardinalcommerce.greendot", "com.netspend.mobileapp.brinks", "com.rushcard.android", "com.ace.ingo.android", "com.card.cardcom", "com.microsoft.amp.apps.bingfinance", "com.investorvista.stockspyod", "com.taguru.ChartPro", "com.investorvista.stockspyphone", "org.yccheok.jstock.gui", "com.acorns.android", "com.experian", "com.myfico", "com.regions.mobbanking", "com.statefarm.pocketagent", "com.domesticcat.chimpchange", "com.ally.MobileBanking", "com.tmcc.click2pay.mytfs", "com.unionbank.ecommerce.mobile.android"

        };
        ArrayList<String> commons = new ArrayList<>();
        HashMap<String, Boolean> filter = new HashMap<>();
        HashMap<String, String> labels = new HashMap<>();
        for (String pkg : predefinedpkgs) {
            try {
                PackageInfo pi = packageManager.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
                labels.put(pkg, pi.applicationInfo.loadLabel(packageManager).toString());
                commons.add(pkg);
            } catch (Exception ignore) {}
            filter.put(pkg, true);
        }

        firstLaunchList = commons;
        firstLaunchLabels = labels;
        firstLaunchLocked = new HashMap<>();
        for(int i=0; i<commons.size(); ++i){
            if (i > 2) break;
            firstLaunchLocked.put(commons.get(i), true);
        }
        firstLaunchFilter = filter;
    }

    public void firstTimeLaunch(){
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

                if (convertView == null){
                    convertView = LayoutInflater.from(PatternActivity.this).inflate(R.layout.security_locked_apps, parent, false);
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
                if (!clickable){
                    holder.encrypted.setSelected(false);
                } else {
                    holder.encrypted.setSelected(true);
                }

                return convertView;
            }
        });

        if (!firstLaunchShowResult){
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) return;
                    --position;
                    String pkg = firstLaunchList.get(position);
                    boolean locked = firstLaunchLocked.containsKey(pkg);
                    if (locked){
                        firstLaunchLocked.remove(pkg);
                    } else {
                        firstLaunchLocked.put(pkg, true);
                    }
                    if (firstLaunchLocked.size() > 0){
                        next.setEnabled(true);
                    } else {
                        next.setEnabled(false);
                    }
                    ((BaseAdapter)((WrapperListAdapter)lv.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                }
            });
            new Thread(){
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
                            if (labels.size() == 10 || i == pkgs.size()-1){
                                final HashMap<String, String> labels_ = (HashMap<String, String>) labels.clone();
                                final ArrayList<String> apps_ = (ArrayList<String>) apps.clone();
                                labels.clear();
                                apps.clear();
                                PatternActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        firstLaunchList.addAll(apps_);
                                        apps_.clear();
                                        firstLaunchLabels.putAll(labels_);
                                        labels_.clear();
                                        ((BaseAdapter)((WrapperListAdapter)lv.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
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

        if (firstLaunchShowResult){
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
                }
            });
        } else {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstSetup = true;
                    firstLaunchShowResult = true;
                    setGraphView();
                }
            });
        }
    }

    @Override
    public void setEmail() {
        if (firstSetup){
            if (firstLaunchLocked != null && firstLaunchLocked.size() > 0) {
                try {
                    SQLiteDatabase db = ProfileDBHelper.singleton(getApplicationContext()).getWritableDatabase();
                    long profileId = ProfileDBHelper.ProfileEntry.createProfile(db, Pref.PREF_DEFAULT_LOCK, new ArrayList<>(firstLaunchLocked.keySet()));
                    ProfileDBHelper.ProfileEntry entry = new ProfileDBHelper.ProfileEntry();
                    entry.id = profileId;
                    entry.name = Pref.PREF_DEFAULT_LOCK;
                    MProfiles.addProfile(entry);
                    MProfiles.switchProfile(entry, server);
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
                    ThemeBridgeImpl.reset(this, action == ACTION_UNLOCK_SELF, false, pkg);
                    if (Pref.isPasswdSet(true) || Pref.isPasswdSet(false)) {
                        unlockApp = true;
                        setContentView(R.layout.security_password_container);
                        toggle(Pref.isUseNormalPasswd());
                        toggled = true;
                    } else {
                        firstTimeLaunch();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        ThemeBridge.themeContext = null;
        ThemeBridgeImpl.clear();
        super.onDestroy();
    }
}