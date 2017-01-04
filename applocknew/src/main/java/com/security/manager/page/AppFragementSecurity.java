package com.security.manager.page;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.ivymobi.applock.free.R;
import com.security.lib.customview.MyWidgetContainer;
import com.security.lib.customview.SecurityBaseFragment;
import com.security.manager.App;
import com.security.manager.NotificationService;
import com.security.manager.SecurityAppLock;
import com.security.manager.Tracker;
import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.lib.customview.SecurityloadImage;
import com.security.manager.SearchThread;
import com.privacy.lock.aidl.IWorker;
import com.security.manager.db.SecurityProfileHelper;
import com.security.manager.lib.io.RefreshList;
import com.security.manager.meta.MApps;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;

import java.util.*;

import static com.security.manager.page.SecurityThemeFragment.TAG_TOP_AD;

/**
 * Created by SongHualin on 6/24/2015.
 */
public class AppFragementSecurity extends SecurityBaseFragment implements RefreshList, SearchView.OnQueryTextListener {
    public static final String PROFILE_ID_KEY = "profile_id";
    public static final String PROFILE_NAME_KEY = "profile_name";
    public static final String PROFILE_HIDE = "hide";
    View scrollView;

    @InjectView(R.id.refresh)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.abs_list)
    ListView listView;

    private boolean CloseSearch = true;


    CListViewScroller scroller;
    static CListViewAdaptor adaptor;

    SecurityProfileHelper.ProfileEntry profileEntry;
    SQLiteDatabase db;

    static View headerView;

    SecuritySharPFive shareFive;

    boolean adShow = false;

    MenuItem menuSearch;
    MenuItem visitorState;


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
        setHasOptionsMenu(true);
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

        showAdOrFive();
        setAdaptor();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MApps.setWaiting(action);
        updateLocks();
    }

    void showAdOrFive() {
        if (!shareFive.getFiveRate()) {
            listView.addHeaderView(headerView);
            headerClick(headerView);
        } else {
            ininShowAD();
        }
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
        try {
            AndroidSdk.destroyNativeAdView(TAG_TOP_AD, scrollView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setAdaptor() {

        adaptor = new CListViewAdaptor(scroller, R.layout.security_apps_item) {

            private void updateUI(int position, ViewHolder h, boolean forceLoading) {
                apps = MApps.getApps(locks);
                if (apps.size() != 0) {
                    List<SearchThread.SearchData> list = searchResult == null ? apps : searchResult;
                    if (position >= list.size()) return;
                    SearchThread.SearchData data = list.get(position);
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
                    return hide ? count : (count);
                } else {
                    return searchResult.size();
                }
            }
        };

        listView.setAdapter(adaptor);

    }

    @Override
    public void refresh() {
        if (adaptor != null) {
            adaptor.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.security_applock_menu, menu);
        menuSearch = menu.findItem(R.id.action_search);
        visitorState = menu.findItem(R.id.menuvisitor);

        if (SecurityMyPref.getshowLockAll()) {
            if (SecurityMyPref.getVisitor()) {
                visitorState.setIcon(R.drawable.security_app_visitor_on).setVisible(true);
            } else {
                visitorState.setIcon(R.drawable.security_app_visitor_off).setVisible(true);
            }
        } else {
            if (SecurityMyPref.getVisitor()) {
                visitorState.setIcon(R.drawable.security_app_visitor_on).setVisible(false);
            } else {
                visitorState.setIcon(R.drawable.security_app_visitor_off).setVisible(false);
            }

        }
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuSearch);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(menuSearch, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                menuSearch.setVisible(false);
                CloseSearch = false;
                if (!shareFive.getFiveRate()) {
                    listView.removeHeaderView(headerView);

                } else {
                    if (scrollView != null) {
                        listView.removeHeaderView(scrollView);
                    }
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                CloseSearch = true;
                searchResult = null;
                menuSearch.setVisible(true);
                if (SecurityMyPref.getshowLockAll()) {
                    visitorState.setVisible(true);
                } else {
                    visitorState.setVisible(false);

                }

                showAdOrFive();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (CloseSearch) {
            refreshUI(refreshSearchResult);
            apps = MApps.getApps(locks);
            CloseSearch = false;
        } else {
            CloseSearch = false;
            searchResult = filter((ArrayList<SearchThread.SearchData>) MApps.getApps(locks), newText);
            refreshUI(refreshSearchResult);
        }
        return true;
    }

    Runnable refreshSearchResult = new Runnable() {
        @Override
        public void run() {
            Utils.notifyDataSetChanged(listView);
        }
    };


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
        List<SearchThread.SearchData> list = searchResult == null ? apps : searchResult;
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
            Tracker.sendEvent(Tracker.ACT_APPLOCK, Tracker.ACT_APPLOCK_UNLOCK, data.label + "", 1L);

        } else {

            toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.security_lock_success, data.label), Toast.LENGTH_SHORT);
            locks.put(pkgName, true);
            Tracker.sendEvent(Tracker.ACT_APPLOCK, Tracker.ACT_APPLOCK_LOCK, data.label + "", 1L);

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
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_PERMISSION_CANCLE, 1L);


            }
        });


        headerView.findViewById(R.id.security_good_tit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                shareFive.setFiveRate(true);
                Utils.rate(getActivity());

                if (!Utils.isEMUI()) {
                    View alertDialogView = View.inflate(v.getContext(), R.layout.security_rate_result, null);
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
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_GOOD_RATE_GOOD, 1L);

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
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_GOOD_RATE_CLOSE, 1L);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuvisitor) {
            if (SecurityMyPref.getVisitor()) {
                final View alertDialogView = View.inflate(getActivity(), R.layout.security_stop_applock, null);
                final AlertDialog d = new AlertDialog.Builder(getActivity(), R.style.dialog).create();
                d.setView(alertDialogView);
                d.setCanceledOnTouchOutside(false);
                d.show();

                alertDialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.cancel();
                        SecurityMyPref.setVisitor(false);
                        visitorState.setIcon(R.drawable.security_app_visitor_off);
                        Toast.makeText(getActivity(), getResources().getString(R.string.security_visitor_off), Toast.LENGTH_SHORT).show();

                        Tracker.sendEvent(Tracker.ACT_MODE, Tracker.ACT_MODE_APPS, Tracker.ACT_MODE_OFF, 1L);
                        if (SecurityMyPref.getNotification()) {
                            getActivity().stopService(new Intent(getActivity(), NotificationService.class));
                            getActivity().startService(new Intent(getActivity(), NotificationService.class));
                        }
                    }
                });

                alertDialogView.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        d.cancel();
                    }
                });
//                visitorState.setIcon(R.drawable.security_app_visitor_off);


            } else {
                visitorState.setIcon(R.drawable.security_app_visitor_on);
                Toast.makeText(getActivity(), getResources().getString(R.string.security_visitor_on), Toast.LENGTH_SHORT).show();
                SecurityMyPref.setVisitor(true);
                if (SecurityMyPref.getNotification()) {
                    getActivity().stopService(new Intent(getActivity(), NotificationService.class));
                    getActivity().startService(new Intent(getActivity(), NotificationService.class));
                }
                Tracker.sendEvent(Tracker.ACT_MODE, Tracker.ACT_MODE_APPS, Tracker.ACT_MODE_ON, 1L);

            }

        }

        return super.onOptionsItemSelected(item);

    }

    void ininShowAD() {
        if (AndroidSdk.hasNativeAd(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL)) {

            scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout(TAG_TOP_AD, AndroidSdk.NATIVE_AD_TYPE_ALL, AndroidSdk.HIDE_BEHAVIOR_AUTO_HIDE, R.layout.app_top_native_layout, new ClientNativeAd.NativeAdClickListener() {
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


    public ArrayList<SearchThread.SearchData> filter(ArrayList<SearchThread.SearchData> models, String query) {
        query = query.toLowerCase();
        ArrayList<SearchThread.SearchData> oneEntity = null;
        for (int i = 0; i < models.size(); i++) {
            oneEntity = new ArrayList<SearchThread.SearchData>();
            ArrayList<SearchThread.SearchData> app = (ArrayList<SearchThread.SearchData>) MApps.getApps(locks);
            for (SearchThread.SearchData enity : app) {
                final String text = enity.label.toLowerCase();
                if (text.contains(query)) {
                    oneEntity.add(enity);
                }
            }
            searchResult = oneEntity;
        }

        return oneEntity;
    }
}



