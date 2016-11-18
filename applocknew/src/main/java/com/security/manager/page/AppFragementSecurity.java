package com.security.manager.page;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.security.lib.customview.MyWidgetContainer;
import com.security.lib.customview.SecurityBaseFrag;
import com.security.manager.App;
import com.security.manager.SecurityAppLock;
import com.security.manager.Tracker;
import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.lib.customview.SecurityloadImage;
import com.privacy.lock.R;
import com.security.manager.SearchThread;
import com.security.manager.Tools;
import com.privacy.lock.aidl.IWorker;
import com.security.manager.db.SecurityProfileHelper;
import com.security.manager.lib.io.RefreshList;
import com.security.manager.meta.MApps;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.meta.SimpleGetTopAppUseCase;

import java.util.*;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.security.manager.page.SecurityThemeFragment.TAG_TLEF_AD;
import static com.security.manager.page.SecurityThemeFragment.TAG_TOP_AD;

/**
 * Created by SongHualin on 6/24/2015.
 */
public class AppFragementSecurity extends SecurityBaseFrag implements RefreshList {
    public static final String PROFILE_ID_KEY = "profile_id";
    public static final String PROFILE_NAME_KEY = "profile_name";
    public static final String PROFILE_HIDE = "hide";

    @InjectView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.abs_list)
     ListView listView;

    CListViewScroller scroller;
    static CListViewAdaptor adaptor;

    SecurityProfileHelper.ProfileEntry profileEntry;
    SQLiteDatabase db;

    static View headerView;

    SecuritySharPFive shareFive;

    boolean adShow = false;

    SimpleGetTopAppUseCase topUseCase;


    public static final Object searchLock = new Object();

    private static List<SearchThread.SearchData> apps;
    private List<SearchThread.SearchData> searchResult;
    int count = 0;
    boolean hide;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROFILE_NAME_KEY, profileEntry.name);
        outState.putLong(PROFILE_ID_KEY, profileEntry.id);
        outState.putBoolean(PROFILE_HIDE, hide);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        onArguments(savedInstanceState);

    }

    @Override
    protected void onArguments(Bundle arguments) {
        profileEntry = new SecurityProfileHelper.ProfileEntry();
        profileEntry.id = arguments.getLong(PROFILE_ID_KEY);
        profileEntry.name = arguments.getString(PROFILE_NAME_KEY);
        hide = arguments.getBoolean(PROFILE_HIDE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
        updateLocks();


    }

    private void updateLocks() {
        db = SecurityProfileHelper.singleton(getActivity()).getWritableDatabase();
        if (profileEntry.name != null) {
            try {
                locks = SecurityProfileHelper.ProfileEntry.getLockedApps(db, profileEntry.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.security_myapp_list, container, false);
        ButterKnife.inject(this, v);
        shareFive = new SecuritySharPFive(getActivity());
        headerView = inflater.inflate(R.layout.security_main_title_rate, null);
        refreshLayout.setColorSchemeResources(R.color.security_theme_accent_2, R.color.security_theme_accent_1);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MApps.setWaiting(action);
            }
        });
        scroller = new CListViewScroller(listView);
        if (!shareFive.getFiveRate()) {
            listView.addHeaderView(headerView);
            headerClick(headerView);
        } else {
            ininShowAD();
        }
        setAdaptor();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MApps.setWaiting(action);
        updateLocks();



    }

    public void saveOrCreateProfile(String profileName, IWorker server) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(locks.keySet());
        try {
            if (profileEntry.name == null) {
                profileEntry.id = SecurityProfileHelper.ProfileEntry.createProfile(db, profileName, list);
                profileEntry.name = profileName;
                SecuritProfiles.addProfile(profileEntry);
            } else {
                SecurityProfileHelper.ProfileEntry.updateProfile(db, profileEntry.id, list);
                if (server != null) {
                    server.notifyApplockUpdate();
                }
            }
            dirty = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        db = null;
        adShow = false;
        super.onDestroy();
    }


    private void setAdaptor() {
        adaptor = new CListViewAdaptor(scroller, R.layout.security_apps_item) {

            private void updateUI(int position, ViewHolder h, boolean forceLoading) {
                apps = MApps.getApps(locks);
                if (apps.size() != 0) {
                    //                List<SearchThread.SearchData> list = searchResult == null ? apps : searchResult;
                    if (position >= apps.size()) return;
                    SearchThread.SearchData data = apps.get(position);
                    String pkgName = data.pkg;
                    h.icon.setImageIcon(pkgName, forceLoading);
                    h.name.setText(data.label);

                    if (SecurityMyPref.getVisitor()) {
                        h.lock.setImageResource(R.drawable.security_lock_bg);

                    } else {
                        h.lock.setImageResource(R.drawable.security_lock_bg_two);
                    }
                    h.lock.setImageResource(R.drawable.security_lock_bg);
                    h.lock.setEnabled(locks.containsKey(pkgName));
                }

            }

            @Override
            protected void onUpdate(int position, Object holder, boolean scrolling) {
                ViewHolder h = (ViewHolder) holder;
                updateUI(position, h, !scrolling);
            }

            @Override
            protected Object getHolder(View root) {
                return new ViewHolder(root);
            }

            @Override
            public int getCount() {
                if (searchResult == null) {
                    return hide ? count : (count
                    );
                } else {
                    return searchResult.size();
                }
            }
        };
        listView.setAdapter(adaptor);

    }

    @Override
    public void refresh() {
        if(adaptor!=null){
            adaptor.notifyDataSetChanged();
        }

    }


    class ViewHolder {
        @InjectView(R.id.icon)
        public SecurityloadImage icon;

        @InjectView(R.id.name)
        public TextView name;

        @InjectView(R.id.lock)
        public ImageView lock;

        public ViewHolder(View root) {
            ButterKnife.inject(this, root);
        }
    }

    Map<String, Boolean> locks = new HashMap<>();
    boolean dirty = false;

    Toast toast;

    @OnItemClick(R.id.abs_list)
    public void onItemClick(View view, int which) {
        int count = listView.getHeaderViewsCount();
        if (count > 0) {
            which--;
        }
        List<SearchThread.SearchData> list = apps;
        if (which >= list.size()) return;
        SearchThread.SearchData data = list.get(which);
        dirty = true;
        if (toast != null) {
            toast.cancel();
        }
        String pkgName = data.pkg;

        if (locks.containsKey(pkgName)) {
            locks.remove(pkgName);
            toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.security_unlock_success, data.label), Toast.LENGTH_SHORT);
            Tracker.sendEvent(Tracker.ACT_APPLOCK,Tracker.ACT_APPLOCK_UNLOCK,data.label+"",1L);

        } else {

            toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.security_lock_success, data.label), Toast.LENGTH_SHORT);
            locks.put(pkgName, true);
            Tracker.sendEvent(Tracker.ACT_APPLOCK,Tracker.ACT_APPLOCK_LOCK,data.label+"",1L);

        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.lock.setEnabled(locks.containsKey(pkgName));
        toast.show();
    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            apps = hide ? MApps.getHiddenApps(locks) : MApps.getApps(locks);
            count = apps.size();
            listView.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
            Utils.notifyDataSetChanged(listView);
        }
    };

    public static void refreshUI(Runnable action) {
        App.runOnUiThread(action);
    }


    private void headerClick(final View headerView) {

        headerView.findViewById(R.id.security_bad_tit).setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(final View v) {
                listView.removeHeaderView(headerView);
                shareFive.setFiveRate(true);
                getActivity().finish();
                Intent intent = new Intent(getActivity(), SecurityAppLock.class);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE,Tracker.ACT_GOOD_RATE_GOOD,Tracker.ACT_PERMISSION_CANCLE,1L);


            }
        });


        headerView.findViewById(R.id.security_good_tit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                shareFive.setFiveRate(true);
                Utils.rate(getActivity());

                if (!Utils.isEMUI()) {

                    View alertDialogView = View.inflate(v.getContext(), R.layout.suo_rate_result, null);

                    final MyWidgetContainer w = new MyWidgetContainer(getActivity(), MyWidgetContainer.MATCH_PARENT, MyWidgetContainer.MATCH_PARENT, MyWidgetContainer.PORTRAIT);
                    w.addView(alertDialogView);
                    w.addToWindow();

                    w.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            w.removeAllViews();
                            w.removeFromWindow();

                        }
                    });
                }


                listView.removeHeaderView(headerView);
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE,Tracker.ACT_GOOD_RATE_GOOD,Tracker.ACT_GOOD_RATE_GOOD,1L);

            }
        });


        headerView.findViewById(R.id.security_rat_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.removeHeaderView(headerView);
                shareFive.setFiveRate(true);
                getActivity().finish();
                Intent intent = new Intent(getActivity(), SecurityAppLock.class);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE,Tracker.ACT_GOOD_RATE_GOOD,Tracker.ACT_GOOD_RATE_CLOSE,1L);

            }
        });


    }


    void ininShowAD() {
        if (AndroidSdk.hasNativeAd(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {

            View scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.app_native_layout, new ClientNativeAd.NativeAdClickListener() {
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
                listView.addHeaderView(scrollView);
            }
        }
//
//        if (AndroidSdk.hasNativeAd(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
//            View scrollView = AndroidSdk.peekNativeAdViewWithLayout(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, R.layout.top_app_native_layout, null);
//            if (scrollView != null) {
//                App.getWatcher().watch(scrollView);
//                listView.addHeaderView(scrollView);
//            }
//        }


    }

    public static void refreshlist(){
        adaptor.notifyDataSetChanged();
        Log.e("mtt","refresh");

    }


}



