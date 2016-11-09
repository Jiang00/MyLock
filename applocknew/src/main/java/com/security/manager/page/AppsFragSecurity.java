package com.security.manager.page;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
//
//import com.android.client.AndroidSdk;
//import com.android.client.ClientNativeAd;
import com.security.lib.customview.MyWidgetContainer;
import com.security.lib.customview.SecurityBaseFrag;
import com.security.manager.App;
import com.security.manager.SecurityAppLock;
import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.lib.customview.SecurityloadImage;
import com.privacy.lock.R;
import com.security.manager.SearchThread;
import com.security.manager.Tools;
import com.privacy.lock.aidl.IWorker;
import com.security.manager.db.SecurityProfileHelper;
import com.security.manager.meta.MApps;
import com.security.manager.meta.SecuritProfiles;

import java.util.*;

/**
 * Created by SongHualin on 6/24/2015.
 */
public class AppsFragSecurity extends SecurityBaseFrag implements SearchThread.OnSearchResult {
    public static final String PROFILE_ID_KEY = "profile_id";
    public static final String PROFILE_NAME_KEY = "profile_name";
    public static final String PROFILE_HIDE = "hide";

    @InjectView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.abs_list)
    ListView listView;

    CListViewScroller scroller;
    CListViewAdaptor adaptor;

    SecurityProfileHelper.ProfileEntry profileEntry;
    SQLiteDatabase db;

    static View headerView;

    SecuritySharPFive shareFive;

    boolean adShow = false;


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
        if (!hide) {
            db = SecurityProfileHelper.singleton(getActivity()).getWritableDatabase();
            if (profileEntry.name != null) {
                try {
                    locks = SecurityProfileHelper.ProfileEntry.getLockedApps(db, profileEntry.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.security_myapp_list, container, false);
        ButterKnife.inject(this, v);

        shareFive = new SecuritySharPFive(getActivity());
//        showDialogFive();


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
//            ininShowAD();
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

    public void switchProfile(SecurityProfileHelper.ProfileEntry entry, IWorker server) {
        if (dirty) {
            saveOrCreateProfile(profileEntry.name, server);
        }
        refreshLayout.setRefreshing(true);
        profileEntry = entry;
        locks = SecurityProfileHelper.ProfileEntry.getLockedApps(db, entry.id);
        SecuritProfiles.switchProfile(entry, server);
        MApps.setWaiting(action);
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
//                if (requireCheckHeader()) {
//                    if (position == 0) {
//                        h.icon.setImageResource(R.drawable.fake_none);
//                        h.name.setText(R.string.intruder);
//                        h.lock.setImageResource(R.drawable.ic_action_next_item);
//                        return;
//                    } else {
//                        --position;
//                    }
//                }

                apps = MApps.getApps(locks);
                if (apps.size() != 0) {
                    //                List<SearchThread.SearchData> list = searchResult == null ? apps : searchResult;
                    if (position >= apps.size()) return;
                    SearchThread.SearchData data = apps.get(position);
                    String pkgName = data.pkg;
                    h.icon.setImageIcon(pkgName, forceLoading);
                    h.name.setText(data.label);
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

    Runnable refreshSearchResult = new Runnable() {
        @Override
        public void run() {
            Utils.notifyDataSetChanged(listView);
        }
    };
    boolean searching = false;

    public List<SearchThread.SearchData> getSearchData() {
        searching = true;
        return apps;
    }

    @Override
    public void onResult(ArrayList<SearchThread.SearchData> list) {
        synchronized (searchLock) {
            if (searching) {
                searchResult = list;
                if (list == null) {
                    searching = false;
                    MApps.setWaiting(action);
                } else {
                    refreshUI(refreshSearchResult);
                }
            }
        }
    }

    public void refreshUI(Runnable action) {
        App.runOnUiThread(action);
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

    private boolean requireCheckHeader() {
        return !hide && searchResult == null;
    }

    @OnItemClick(R.id.abs_list)
    public void onItemClick(View view, int which) {
//        if (requireCheckHeader()) {
//            if (which == 0) {
//                SecurityIntruderPresenter.show();
//                return;
//            } else {
//                --which;
//            }
//        }
        int count = listView.getHeaderViewsCount();
        if(count>0){
            which--;
        }

        Log.e("mtt", "which" + which);
        List<SearchThread.SearchData> list = apps;
        if (which >= list.size()) return;
        SearchThread.SearchData data = list.get(which);
        dirty = true;
        if (toast != null) {
            toast.cancel();
        }
        String pkgName = data.pkg;

        Log.e("mtt",pkgName+"--");
        Context context = view.getContext().getApplicationContext();
        if (locks.containsKey(pkgName)) {
            if (hide) {
                if (!Tools.showApp(pkgName)) {
                } else {
                    toast = Toast.makeText(context, getString(R.string.security_show_successful, data.label), Toast.LENGTH_SHORT);
                    locks.remove(pkgName);
                    MApps.show(data);
                }
                toast.show();
            } else {
                locks.remove(pkgName);
                toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.security_unlock_success, data.label), Toast.LENGTH_SHORT);
            }
        } else {
            if (hide) {
                if (!Tools.hideApp(pkgName)) {
//                    toast = Toast.makeText(context, null, data.label), Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(context, getString(R.string.security_hide_successful, data.label), Toast.LENGTH_SHORT);
                    locks.put(pkgName, true);
                    MApps.hide(data);
                }
            } else {
                toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.security_lock_success, data.label), Toast.LENGTH_SHORT);
                locks.put(pkgName, true);
            }
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.lock.setEnabled(locks.containsKey(pkgName));

        toast.show();
    }

    private Runnable action = new Runnable() {
        @Override
        public void run() {
            apps = hide ? MApps.getHiddenApps(locks) : MApps.getApps(locks);
            count = apps.size();
            listView.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(false);
            Utils.notifyDataSetChanged(listView);
        }
    };


    private void headerClick(final View headerView) {

        headerView.findViewById(R.id.security_bad_tit).setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(final View v) {
                listView.removeHeaderView(headerView);
                shareFive.setFiveRate(true);
                getActivity().finish();
                Log.e("mtt", "headview");
                Intent intent = new Intent(getActivity(), SecurityAppLock.class);
                startActivity(intent);

            }
        });


        headerView.findViewById(R.id.security_good_tit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                shareFive.setFiveRate(true);

//                if (Utils.hasPlayStore(getActivity())) {
//
//                }

                Utils.rate(getActivity());

                if(!Utils.isEMUI()){

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
            }
        });


    }

    public void showDialogFive() {

        int time = shareFive.getEnterAppsTimes();
        time++;
        shareFive.setEnterAppsTimes(time);

        if (time == 2) {
            SecuritySharPFive sh = new SecuritySharPFive(getActivity());
            if (!sh.getFiveRate()) {
                ShowDialogview.showDialog(getActivity(), null, listView);
            }


        }
    }
//
//    void ininShowAD() {
//        if (AndroidSdk.hasNativeAd(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {
//
//            View scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.app_native_layout, new ClientNativeAd.NativeAdClickListener() {
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
//                listView.addHeaderView(scrollView);
//            }
//        }
//
//    }


}
