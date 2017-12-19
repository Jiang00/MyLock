package com.security.manager.page;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.ivymobi.applock.free.R;
import com.privacy.lock.aidl.IWorker;
import com.security.lib.customview.SecurityBaseFragment;
import com.security.lib.customview.SecurityloadImage;
import com.security.manager.App;
import com.security.manager.NotificationService;
import com.security.manager.SearchThread;
import com.security.manager.Tracker;
import com.security.manager.db.SecurityProfileHelper;
import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.manager.lib.io.RefreshList;
import com.security.manager.meta.MApps;
import com.security.manager.meta.SecuritProfiles;
import com.security.manager.meta.SecurityMyPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

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
    @InjectView(R.id.abs_list2)
    ListView listView2;
    @InjectView(R.id.main_scrollview)
    ScrollView main_scrollview;
    @InjectView(R.id.main_good_ad)
    FrameLayout main_good_ad;
    @InjectView(R.id.applock_fl)
    LinearLayout applock_fl;
    @InjectView(R.id.open_lock)
    TextView open_lock;

    private boolean CloseSearch = true;


    CListViewScroller scroller;
    static CListViewAdaptor adaptor;
    static CListViewAdaptor adaptor2;

    SecurityProfileHelper.ProfileEntry profileEntry;
    SQLiteDatabase db;

    static View headerView;

    SecuritySharPFive shareFive;

    boolean adShow = false;

    MenuItem menuSearch;
    MenuItem visitorState;

    private static List<SearchThread.SearchData> apps;
    private List<SearchThread.SearchData> searchResult;

    int count = 0;
    boolean hide;
    private LottieAnimationView lottie_good;
    private ArrayList<String> commons;
    private HashMap<String, Boolean> filter;
    private String[] predefinedpkgs;
    private Handler handler;
    private List<SearchThread.SearchData> list;
    private int show_applock_stop;
    private AlertDialog dialog1;
    private boolean questionFlag4;
    private boolean questionFlag3;
    private boolean questionFlag2;
    private boolean questionFlag1;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

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
        handler = new Handler();

        showAdOrFive();
        recyclerSetAdapter();
        setAdaptor();
        try {
            String flurryString = AndroidSdk.getExtraData();
            JSONObject baseJson = new JSONObject(flurryString);
            show_applock_stop = baseJson.getInt("show_applock_stop");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (show_applock_stop == 1) {
            if (!SecurityMyPref.getVisitor()) {
                applock_fl.setVisibility(View.VISIBLE);
            }
        }

        open_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applock_fl.setVisibility(View.GONE);
                visitorState.setIcon(R.drawable.security_notification_lock);
                Toast.makeText(getActivity(), getResources().getString(R.string.security_visitor_on), Toast.LENGTH_SHORT).show();
                SecurityMyPref.setVisitor(true);
                if (SecurityMyPref.getNotification()) {
                    getActivity().stopService(new Intent(getActivity(), NotificationService.class));
                    getActivity().startService(new Intent(getActivity(), NotificationService.class));
                }
            }
        });
        applock_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MApps.setWaiting(action);
        updateLocks();
    }

    void showAdOrFive() {
        preferences = getActivity().getSharedPreferences("five_", Context.MODE_PRIVATE);
        editor = preferences.edit();
        if (!shareFive.getFiveRate() || preferences.getBoolean("five_rate_close_a", false)) {
            main_good_ad.addView(headerView);
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

    private void recyclerSetAdapter() {
        Log.e("chfq", "===recyclerSetAdapter====");
        predefinedpkgs = new String[]{
                "com.sec.android.gallery3d",
                "com.android.gallery3d",
                "com.android.gallery",
                "com.facebook.katana",
                "com.facebook.orca",
                "jp.naver.line.android",
                "com.whatsapp",
                "com.kakao.talk",
                "com.instagram.android",
                "com.android.vending",
                "com.twitter.android",
                "com.android.contacts",
                "com.android.mms",
                "com.google.android.gm",
                "com.android.email",
                "com.google.android.youtube",
                "com.tencent.mm",
                "com.google.android.talk",
                "com.skype.raider",
        };
        commons = new ArrayList<>();
        filter = new HashMap<>();
        final HashMap<String, String> labels = new HashMap<>();
        for (String pkg : predefinedpkgs) {
            try {
                PackageInfo pi = getActivity().getPackageManager().getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
                labels.put(pkg, pi.applicationInfo.loadLabel(getActivity().getPackageManager()).toString());
                if (locks.containsKey(pkg)) {
                    commons.add(0, pkg);
                } else {
                    commons.add(pkg);
                }
            } catch (Exception ignore) {
            }
            filter.put(pkg, true);//是否选中的
        }

        adaptor2 = new CListViewAdaptor(scroller, R.layout.security_apps_item2) {

            private void updateUI(int position, ViewHolder h, boolean forceLoading) {
                if (commons.size() != 0) {
                    if (position >= commons.size()) return;
                    String pkgName = commons.get(position);
                    h.icon.setImageIcon(pkgName, forceLoading);
                    h.name.setText(labels.get(pkgName));
                    h.lock.setImageResource(R.drawable.security_lock_bg2);
                    h.lock.setEnabled(locks.containsKey(pkgName));
                    if (locks.containsKey(pkgName)) {
                        h.unlock_yuan2.setVisibility(View.VISIBLE);
                        h.unlock_yuan.setVisibility(View.GONE);
                    } else {
                        h.unlock_yuan2.setVisibility(View.GONE);
                        h.unlock_yuan.setVisibility(View.VISIBLE);
                    }
                    h.unlock_yuan.setScaleY(1);
                    h.unlock_yuan.setScaleX(1);
                    h.unlock_yuan2.setScaleY(1);
                    h.unlock_yuan2.setScaleX(1);
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
                return commons.size();
            }
        };

        listView2.setAdapter(adaptor2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pkgName = commons.get(position);
                final ViewHolder holder = (ViewHolder) view.getTag();
                if (locks.containsKey(pkgName)) {
                    locks.remove(pkgName);
                    unlockAnimation(holder);
                    toast = Toast.makeText(getActivity(), getActivity().getString(R.string.security_unlock_success, labels.get(pkgName)), Toast.LENGTH_SHORT);
                } else {
                    toast = Toast.makeText(getActivity(), getActivity().getString(R.string.security_lock_success, labels.get(pkgName)), Toast.LENGTH_SHORT);
                    locks.put(pkgName, true);
                    lockAnimation(holder);
                }
                toast.show();

                holder.lock.setEnabled(locks.containsKey(pkgName));
            }
        });

    }

    private void setAdaptor() {
        apps = MApps.getApps(locks);
        adaptor = new CListViewAdaptor(scroller, R.layout.security_apps_item) {

            private void updateUI(int position, ViewHolder h, boolean forceLoading) {
                if (apps.size() != 0) {
                    list = searchResult == null ? apps : searchResult;
                    if (position >= list.size()) return;
                    SearchThread.SearchData data = list.get(position);
                    String pkgName = data.pkg;
                    h.icon.setImageIcon(pkgName, forceLoading);
                    h.name.setText(data.label);

                    h.lock.setImageResource(R.drawable.security_lock_bg2);
                    h.lock.setEnabled(locks.containsKey(pkgName));
                    if (locks.containsKey(pkgName)) {
                        h.unlock_yuan2.setVisibility(View.VISIBLE);
                        h.unlock_yuan.setVisibility(View.GONE);
                    } else {
                        h.unlock_yuan2.setVisibility(View.GONE);
                        h.unlock_yuan.setVisibility(View.VISIBLE);
                    }
                    h.unlock_yuan.setScaleY(1);
                    h.unlock_yuan.setScaleX(1);
                    h.unlock_yuan2.setScaleY(1);
                    h.unlock_yuan2.setScaleX(1);
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

    private void lockAnimation(final ViewHolder holder) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(holder.unlock_yuan, "rotation", 0f, 60f);
        rotate.setDuration(150);
        rotate.start();
        rotate.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.unlock_yuan, "scaleX", 1f, 0.4f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.unlock_yuan, "scaleY", 1f, 0.4f);
                AnimatorSet animSet = new AnimatorSet();
                animSet.play(scaleX).with(scaleY);
                animSet.setDuration(200);
                animSet.start();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.unlock_yuan.setVisibility(View.GONE);
                        holder.unlock_yuan2.setVisibility(View.VISIBLE);
                        holder.lock.setEnabled(true);
                        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(holder.unlock_yuan2, "scaleX", 0.4f, 1f);
                        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(holder.unlock_yuan2, "scaleY", 0.4f, 1f);
                        AnimatorSet animSet2 = new AnimatorSet();
                        animSet2.play(scaleX2).with(scaleY2);
                        animSet2.setDuration(300);
                        animSet2.start();
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
    }

    private void unlockAnimation(final ViewHolder holder) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.unlock_yuan2, "scaleX", 1f, 0.4f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.unlock_yuan2, "scaleY", 1f, 0.4f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY);
        animSet.setDuration(200);
        animSet.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.unlock_yuan2.setVisibility(View.GONE);
                holder.unlock_yuan.setVisibility(View.VISIBLE);
                holder.lock.setEnabled(false);
                ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(holder.unlock_yuan, "scaleX", 0.4f, 1f);
                ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(holder.unlock_yuan, "scaleY", 0.4f, 1f);
                AnimatorSet animSet2 = new AnimatorSet();
                animSet2.play(scaleX2).with(scaleY2);
                animSet2.setDuration(200);
                animSet2.start();
            }
        }, 100);
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

        if (show_applock_stop == 0) {
            visitorState.setVisible(false);
        }

        if (SecurityMyPref.getshowLockAll()) {
            if (SecurityMyPref.getVisitor()) {
                visitorState.setIcon(R.drawable.security_notification_lock).setVisible(true);
            } else {
                visitorState.setIcon(R.drawable.security_notification_unlock).setVisible(true);
            }
        } else {
            if (SecurityMyPref.getVisitor()) {
                visitorState.setIcon(R.drawable.security_notification_lock).setVisible(false);
            } else {
                visitorState.setIcon(R.drawable.security_notification_unlock).setVisible(false);
            }

        }
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuSearch);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(menuSearch, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

//                menuSearch.setVisible(false);
                CloseSearch = false;
                if (!shareFive.getFiveRate()) {
//                    listView.removeHeaderView(headerView);
                    main_good_ad.removeAllViews();
                } else {
                    if (scrollView != null) {
//                        listView.removeHeaderView(scrollView);
                        main_good_ad.removeAllViews();
                    }
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                CloseSearch = true;

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
            searchResult = null;
//            refreshUI(refreshSearchResult);
            apps = MApps.getApps(locks);
            adaptor.notifyDataSetChanged();
            CloseSearch = false;
        } else {
            Log.e("CloseSearch", "CloseSearch");
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

        @InjectView(R.id.unlock_yuan)
        ImageView unlock_yuan;

        @InjectView(R.id.unlock_yuan2)
        ImageView unlock_yuan2;


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
        ViewHolder holder = (ViewHolder) view.getTag();
//        List<SearchThread.SearchData> list = searchResult == null ? apps : searchResult;
        if (which >= list.size()) return;
        SearchThread.SearchData data = list.get(which);
        dirty = true;
        if (toast != null) {
            toast.cancel();
        }
        String pkgName = data.pkg;
        Log.e("chfq", "==which==" + which + "=pkgName==" + pkgName);

        if (locks.containsKey(pkgName)) {
            locks.remove(pkgName);
            toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.security_unlock_success, data.label), Toast.LENGTH_SHORT);
            unlockAnimation(holder);
        } else {
            toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.security_lock_success, data.label), Toast.LENGTH_SHORT);
            locks.put(pkgName, true);
            lockAnimation(holder);
        }

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
        lottie_good = (LottieAnimationView) headerView.findViewById(R.id.lottie_good);
        lottie_good.setAnimation("good.json");
        lottie_good.setScale(0.3f);//相对原大小的0.2倍
        lottie_good.loop(true);
        lottie_good.playAnimation();

        headerView.findViewById(R.id.security_bad_tit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                main_good_ad.removeView(headerView);
                shareFive.setFiveRate(true);
                editor.putBoolean("five_rate_close_a", false).commit();
//                sendEmail("iebuznel@gmail.com", getActivity());
                showBadDialog();
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_PERMISSION_CANCLE, 1L);
            }
        });
        headerView.findViewById(R.id.security_good_tit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                shareFive.setFiveRate(true);
                editor.putBoolean("five_rate_close_a", false).commit();
                Utils.rate(getActivity());
                listView.removeHeaderView(headerView);
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_GOOD_RATE_GOOD, 1L);

            }
        });
        headerView.findViewById(R.id.security_rat_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_good_ad.removeView(headerView);
                shareFive.setFiveRate(true);

                if (preferences.getBoolean("five_rate_close_a", false)) {
                    editor.putBoolean("five_rate_close_a", false).commit();
                    editor.putBoolean("five_rate_close_f", false).commit();
                } else {
                    editor.putBoolean("five_rate_close_f", true).commit();
                }
//                if (shareFive.getFiveRate()) {
//                    shareFive.setFiveRate2(true);
//                } else {
//                }
                Tracker.sendEvent(Tracker.ACT_GOOD_RATE, Tracker.ACT_GOOD_RATE_GOOD, Tracker.ACT_GOOD_RATE_CLOSE, 1L);
            }
        });
    }

    //差评反馈
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showBadDialog() {
        View view = View.inflate(getActivity(), R.layout.dialog_bad, null);
        LinearLayout question1 = (LinearLayout) view.findViewById(R.id.question1);
        LinearLayout question2 = (LinearLayout) view.findViewById(R.id.question2);
        LinearLayout question3 = (LinearLayout) view.findViewById(R.id.question3);
        LinearLayout question4 = (LinearLayout) view.findViewById(R.id.question4);
        final ImageView question_v1 = (ImageView) view.findViewById(R.id.question_v1);
        final ImageView question_v2 = (ImageView) view.findViewById(R.id.question_v2);
        final ImageView question_v3 = (ImageView) view.findViewById(R.id.question_v3);
        final ImageView question_v4 = (ImageView) view.findViewById(R.id.question_v4);
        final EditText edittext = (EditText) view.findViewById(R.id.edittext);
        ImageView good_close = (ImageView) view.findViewById(R.id.good_close);
        TextView main_bad = (TextView) view.findViewById(R.id.main_bad);
        TextView main_good = (TextView) view.findViewById(R.id.main_good);

        final MyDialog d1 = new MyDialog(getActivity(), 0, 0, view, R.style.dialog);
        d1.show();

        question1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag1) {
                    questionFlag1 = false;
                    question_v1.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag1 = true;
                    question_v1.setImageResource(R.drawable.check);
                }
            }
        });
        question2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag2) {
                    questionFlag2 = false;
                    question_v2.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag2 = true;
                    question_v2.setImageResource(R.drawable.check);
                }
            }
        });
        question3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag3) {
                    questionFlag3 = false;
                    question_v3.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag3 = true;
                    question_v3.setImageResource(R.drawable.check);
                }
            }
        });
        question4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionFlag4) {
                    questionFlag4 = false;
                    question_v4.setImageResource(R.drawable.uncheck);
                } else {
                    questionFlag4 = true;
                    question_v4.setImageResource(R.drawable.check);
                }
            }
        });
        main_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d1.dismiss();
                Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "放弃点击", 1L);
            }
        });
        good_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d1.dismiss();
                Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "叉号点击", 1L);
            }
        });
        main_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = "";
                if (questionFlag1) {
                    str += "1广告";
                }
                if (questionFlag2) {
                    str += "2不锁";
                }
                if (questionFlag3) {
                    str += "3界面";
                }
                if (questionFlag4) {
                    str += "4其他";
                }
                Log.e("chfq", "===edittext.getText().toString().isEmpty()==" + edittext.getText().toString().isEmpty());
                if (str.isEmpty() && edittext.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.least_question), Toast.LENGTH_SHORT).show();
                } else {
                    d1.dismiss();
                    if (!edittext.getText().toString().isEmpty()) {
                        Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "提交点击" + edittext.getText().toString(), 1L);
                    } else {
                        Tracker.sendEvent("锁应用界面展示", "差评反馈点击", "提交点击" + str, 1L);
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuvisitor) {
            if (SecurityMyPref.getVisitor()) {
                applock_fl.setVisibility(View.VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(applock_fl, "translationX", -applock_fl.getWidth(), 0f);
                animator.setDuration(400);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
                SecurityMyPref.setVisitor(false);
                visitorState.setIcon(R.drawable.security_notification_unlock);
                Toast.makeText(getActivity(), getResources().getString(R.string.security_visitor_off), Toast.LENGTH_SHORT).show();

                Tracker.sendEvent(Tracker.ACT_MODE, Tracker.ACT_MODE_APPS, Tracker.ACT_MODE_OFF, 1L);
                if (SecurityMyPref.getNotification()) {
                    getActivity().stopService(new Intent(getActivity(), NotificationService.class));
                    getActivity().startService(new Intent(getActivity(), NotificationService.class));
                }

            } else {
                applock_fl.setVisibility(View.GONE);
                visitorState.setIcon(R.drawable.security_notification_lock);
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
        if (AndroidSdk.hasNativeAd(TAG_TOP_AD)) {
            scrollView = AndroidSdk.peekNativeAdScrollViewWithLayout("rating", AndroidSdk.HIDE_BEHAVIOR_NO_HIDE, R.layout.app_top_native_layout, new ClientNativeAd.NativeAdClickListener() {
                @Override
                public void onNativeAdClicked(ClientNativeAd clientNativeAd) {

                }
            }, new ClientNativeAd.NativeAdScrollListener() {
                @Override
                public void onNativeAdScrolled(float v) {

                }
            });
            if (scrollView != null) {
//                listView.addHeaderView(scrollView);
                main_good_ad.addView(scrollView);
            }
        }
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



