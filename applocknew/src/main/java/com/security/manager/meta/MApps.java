package com.security.manager.meta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.security.manager.App;
import com.security.manager.lib.async.LoadingTask;
import com.security.manager.SearchThread;

import java.util.*;

/**
 * Created by SongHualin on 6/26/2015.
 */
public class MApps {
    public static void init(){
        loadingTask.start();
    }

    public static void setWaiting(Runnable waiting){
        loadingTask.waiting(waiting);
    }

    public static void add(Context context, String pkg){
        if (loadingTask.isFinished()){
            SearchThread.SearchData data = new SearchThread.SearchData();
            try {
                data.pkg = pkg;
                ApplicationInfo applicationInfo = context.getPackageManager().getPackageInfo(pkg, 0).applicationInfo;
                data.label = applicationInfo.loadLabel(context.getPackageManager()).toString();
                data.system = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                if (data.system){
                    systems.add(data);
                    Collections.sort(systems, comparator);
                } else{
                    thirdparties.add(data);
                    Collections.sort(thirdparties, comparator);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            init();
        }
    }

    private static boolean remove(List<SearchThread.SearchData> pool, String pkg){
        for(int i=pool.size()-1; i>=0; --i){
            if (pool.get(i).pkg.equals(pkg)){
                pool.remove(i);
                return true;
            }
        }
        return false;
    }

    public static void removed(String pkg){
        if (loadingTask.isFinished()){
            if (remove(thirdparties, pkg)) return;
            if (remove(predefined, pkg)) return;
            remove(systems, pkg);
        } else {
            init();
        }
    }

    public static void show(SearchThread.SearchData data){
        hiddens.remove(data);
        if (data.predefined){
            predefined.add(data);
            Collections.sort(predefined, comparator);
        } else if (data.system){
            systems.add(data);
            Collections.sort(systems, comparator);
        } else {
            thirdparties.add(data);
            Collections.sort(thirdparties, comparator);
        }
    }

    public static void hide(SearchThread.SearchData data){
        hiddens.add(data);
        Collections.sort(hiddens, comparator);
        if (data.predefined){
            predefined.remove(data);
        } else if (data.system){
            systems.remove(data);
        } else {
            thirdparties.remove(data);
        }
    }

    public static List<SearchThread.SearchData> getHiddenApps(Map<String, Boolean> outHiddens){
        List<SearchThread.SearchData> apps = new ArrayList<>();
        outHiddens.clear();
        for(SearchThread.SearchData data : hiddens){
            outHiddens.put(data.pkg, true);
        }
        apps.addAll(hiddens);
        apps.addAll(predefined);
        apps.addAll(thirdparties);
        apps.addAll(systems);
        return apps;
    }

    /**
     *
     * @param filter locked apps
     * @return
     */
    public static List<SearchThread.SearchData> getApps(Map<String, Boolean> filter){
        List<SearchThread.SearchData> apps = new ArrayList<>();
        List<SearchThread.SearchData> left = new ArrayList<>();
        apps.addAll(top);
        filter(filter, predefined, apps, left);
        filter(filter, thirdparties, apps, left);
        filter(filter, systems, apps, left);
        apps.addAll(left);
        return apps;
    }

    private static void filter(Map<String, Boolean> filter, List<SearchThread.SearchData> pool, List<SearchThread.SearchData> filtered, List<SearchThread.SearchData> left){
        if (filter.size() == 0){
            left.addAll(pool);
        } else {
            for(SearchThread.SearchData data : pool){
                if (filter.containsKey(data.pkg)){
                    filtered.add(data);
                } else {
                    left.add(data);
                }
            }
        }
    }

    public static final ArrayList<SearchThread.SearchData> top = new ArrayList<>(4);
    private static final ArrayList<SearchThread.SearchData> hiddens = new ArrayList<>();
    private static final ArrayList<SearchThread.SearchData> predefined = new ArrayList<>();
    private static final ArrayList<SearchThread.SearchData> thirdparties = new ArrayList<>();
    private static final ArrayList<SearchThread.SearchData> systems = new ArrayList<>();
    static final Comparator<SearchThread.SearchData> comparator = new Comparator<SearchThread.SearchData>() {
        @Override
        public int compare(SearchThread.SearchData lhs, SearchThread.SearchData rhs) {
            return lhs.label.compareTo(rhs.label);
        }
    };

    static LoadingTask loadingTask = new LoadingTask() {

        private List<ResolveInfo> getDesktopApps() {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            return App.getContext().getPackageManager().queryIntentActivities(mainIntent, 0);
        }

        @Override
        protected void doInBackground() {
//            SearchThread.SearchData incoming = new SearchThread.SearchData();
//            incoming.label = App.getContext().getString(R.string.incoming_call);
//            incoming.pkg = "com.android.phone";
//            top.add(incoming);
//
//            SearchThread.SearchData recents = new SearchThread.SearchData();
//            recents.pkg = "com.android.systemui";
//            recents.label = App.getContext().getString(R.string.recent_apps);
//            top.add(recents);

            ArrayList<SearchThread.SearchData> hiddens_ = new ArrayList<>();

            PackageManager packageManager = App.getContext().getPackageManager();
            List<PackageInfo> ps = packageManager.getInstalledPackages(0);
            for (PackageInfo p : ps) {
                if (!p.applicationInfo.enabled) {
                    SearchThread.SearchData data = new SearchThread.SearchData();
                    data.label = p.applicationInfo.loadLabel(packageManager).toString();
                    data.pkg = p.packageName;
                    data.system = (p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                    hiddens_.add(data);
                }
            }

            HashMap<String, Boolean> filters = new HashMap<>();
            String[] predefineds = {
                    "com.sec.android.gallery3d",
                    "com.android.gallery3d",
                    "com.android.gallery",
                    "com.android.contacts",
                    "com.android.mms",
//                    "com.android.phone",
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

            Map<String, Boolean> excludes = new HashMap<>();
            excludes.put(App.getContext().getPackageName(), true);

            List<ResolveInfo> apps_ = getDesktopApps();
            ArrayList<SearchThread.SearchData> systems_ = new ArrayList<>();
            ArrayList<SearchThread.SearchData> thirdParties_ = new ArrayList<>();
            ArrayList<SearchThread.SearchData> predefinedData_ = new ArrayList<>();

            for (String pkg : predefineds) {
                try {
                    PackageInfo pi = packageManager.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
                    SearchThread.SearchData data = new SearchThread.SearchData();
                    data.pkg = pkg;
                    data.label = pi.applicationInfo.loadLabel(packageManager).toString();
                    data.predefined = true;
                    filters.put(pkg, true);
                    if (!pkg.equals("com.android.phone")) {
                        predefinedData_.add(data);
                    }
                } catch (Exception ignore) {}
            }

            for (ResolveInfo app : apps_) {
                String pkg = app.activityInfo.packageName;
                if (excludes.containsKey(pkg) || filters.containsKey(pkg)) continue;
                String label = app.loadLabel(packageManager).toString();
                SearchThread.SearchData data = new SearchThread.SearchData();
                data.label = label;
                data.pkg = pkg;

                //锁应用列表界面去除掉了设置实现
                // 引导界面需要添加设置需要去相关patternactivity设置
//                if(!pkg.equals("com.android.settings")){
                    if ((app.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        data.system = true;
                        systems_.add(data);
                    } else {
                        data.system = false;
                        thirdParties_.add(data);
                    }
               // }


            }

            Collections.sort(hiddens_, comparator);
            Collections.sort(thirdParties_, comparator);
            Collections.sort(systems_, comparator);

            synchronized (this) {
                hiddens.clear();
                hiddens.addAll(hiddens_);
                systems.clear();
                systems.addAll(systems_);
                thirdparties.clear();
                thirdparties.addAll(thirdParties_);
                predefined.clear();
                predefined.addAll(predefinedData_);
            }
        }
    };
}
