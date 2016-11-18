package com.security.manager.meta;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by song on 15/10/20.
 */
public class SimpleGetTopAppUseCase extends UseCase<Void, String> {
    public boolean hasAccessUsagePermission = false;
    private ActivityManager mActivityManager;
    Field processState = null;
    protected Context context;

    public SimpleGetTopAppUseCase(Context context, ActivityManager activityManager, boolean hasAccessUsagePermission) {
        super(context);
        this.context = context;
        this.mActivityManager = activityManager;
        this.hasAccessUsagePermission = hasAccessUsagePermission;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String execute(Void... params) {
        String packageName = null;
        List<UsageStats> listpkgName;
        if (Build.VERSION.SDK_INT > 19) {
            try {
                packageName = getActivePackages();
                Log.e("mttname",packageName+"----1");

            } catch (Exception | Error e) {
                e.printStackTrace();
            }
            try {
                if (packageName == null) {

                    packageName = getTopPackage();
                    Log.e("mttname",packageName+"---2");


                }
            } catch (Exception | Error e) {
                e.printStackTrace();

            }
            try {
                if (packageName == null) {
                    packageName = getForegroundApp();
                    Log.e("mttname",packageName+"---3");

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            List<ActivityManager.RunningTaskInfo> lst = mActivityManager.getRunningTasks(1);
            if (lst != null && lst.size() > 0) {

                ActivityManager.RunningTaskInfo runningTaskInfo = lst.get(0);
                if (runningTaskInfo.numRunning > 0 && runningTaskInfo.topActivity != null) {
                    packageName = runningTaskInfo.topActivity.getPackageName();

                }
            }
        }
        return packageName;
    }

    private String getActivePackages() throws NoSuchFieldException, IllegalAccessException {
        if (processState == null) {
            processState = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            processState.setAccessible(true);
        }
        final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
//        if (processInfos != null) {
//            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
//
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    return processInfo.processName;
//                }
//                int anInt = processState.getInt(processInfo);
//                if (anInt == 2) {
//                    return processInfo.pkgList[0];
//                }
//            }
//        }


        List<ActivityManager.RunningTaskInfo> list = mActivityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
//            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) && info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {

            Log.e("pkgname",info.baseActivity.getPackageName()+"----");
            //}
        }


        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getTopPackage() {
        if (mUsageStatsManager == null) {
            mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            mRecentComp = new Comparator<UsageStats>() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public int compare(UsageStats lhs, UsageStats rhs) {
                    return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : ((lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1);
                }
            };
        }

        long ts = System.currentTimeMillis();
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 10000, ts);
        if (usageStats == null || usageStats.size() == 0) {
            return null;
        } else {
            Collections.sort(usageStats, mRecentComp);
            return usageStats.get(0).getPackageName();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStats> getTopListPackage() {
        if (mUsageStatsManager == null) {
            mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            mRecentComp = new Comparator<UsageStats>() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public int compare(UsageStats lhs, UsageStats rhs) {
                    return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : ((lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1);
                }
            };
        }

        long ts = System.currentTimeMillis();
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 10000, ts);
        if (usageStats == null || usageStats.size() == 0) {
            return null;
        } else {
            return usageStats;
        }
    }


    private UsageStatsManager mUsageStatsManager;
    private Comparator mRecentComp;

    /**
     * first app user
     */
    public static final int AID_APP = 10000;

    /**
     * offset for uid ranges for each user
     */
    public static final int AID_USER = 100000;

    static HashMap<String, Boolean> excludes = new HashMap<>();

    static {
        excludes.put("com.android.systemui", true);
        excludes.put("android.process.acore", true);
        excludes.put("android.process.media", true);
    }

    public static String getForegroundApp() {
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        String foregroundProcess = null;

        for (File file : files) {
            int pid;
            String name = file.getName();
            try {
                pid = Integer.parseInt(name);
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                String cgroup = read(String.format("/proc/%d/cgroup", pid));

                if (cgroup.contains("bg_non_interactive")) {
                    continue;
                }

                if (!cgroup.endsWith(name)) {
                    continue;
                }

                int uid = Integer.parseInt(cgroup.substring(cgroup.indexOf("uid_") + 4, cgroup.lastIndexOf("/")));
                if (uid >= 1000 && uid <= 1038) {
                    // system process
                    continue;
                }

                int appId = uid - AID_APP;
                // loop until we get the correct user id.
                // 100000 is the offset for each user.
                while (appId > AID_USER) {
                    appId -= AID_USER;
                }

                if (appId < 0) {
                    continue;
                }

                String cmdline = read(String.format("/proc/%d/cmdline", pid));
                if (excludes.containsKey(cmdline)) {
                    continue;
                }

                // u{user_id}_a{app_id} is used on API 17+ for multiple user account support.
                // String uidName = String.format("u%d_a%d", userId, appId);

                File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
                if (oomScoreAdj.canRead()) {
                    int oomAdj = Integer.parseInt(read(oomScoreAdj.getAbsolutePath()));
                    if (oomAdj != 0) {
                        continue;
                    }
                }

                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", pid)));
                if (oomscore < lowestOomScore) {
                    lowestOomScore = oomscore;
                    foregroundProcess = cmdline;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return foregroundProcess;
    }

    private static String read(String path) throws IOException {
        BufferedReader reader = null;
        try {
            StringBuilder output = new StringBuilder();
            reader = new BufferedReader(new FileReader(path));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                output.append(line);
            }
            return output.toString().trim();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
