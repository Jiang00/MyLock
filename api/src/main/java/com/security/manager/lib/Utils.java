package com.security.manager.lib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.Toast;

import com.privacy.api.BuildConfig;

import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by SongHualin on 4/20/2015.
 */
public class Utils {
    public static final String TAG = "utils";

    public static void stopAsyncTask(AsyncTask task) {
        if (task != null) {
            final AsyncTask.Status status = task.getStatus();
            if (status == AsyncTask.Status.RUNNING || status == AsyncTask.Status.PENDING) {
                task.cancel(true);
            }
        }
    }

    public static void share(Context context, String title, String text, Bitmap stream) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("text/plain");
        it.putExtra(Intent.EXTRA_SUBJECT, title);
        it.putExtra(Intent.EXTRA_TEXT, text);
        if (stream != null) {
            it.putExtra(Intent.EXTRA_STREAM, stream);
        }
        context.startActivity(Intent.createChooser(it, title));
    }

    public static void rate(Context context) {
        Utils.openPlayStore(context, context.getPackageName());
    }

    public static void showSoftKeyboard(Activity activity, View view, boolean show) {
        if (show) {
            if (view != null && view.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View viewById = activity.findViewById(android.R.id.content);
            if (viewById != null)
                imm.hideSoftInputFromWindow(viewById.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static final String GOOGLE_PALY_URL = "https://play.google.com/store/apps/details?id=";

    public static void openPlayStore(Context context, String pkg) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        String url = GOOGLE_PALY_URL;

        if (pkg.startsWith("http")) {
            pkg = pkg.replace(url, "");
            if (pkg.startsWith("http")) {
                launchApp(context, pkg, i);
                return;
            }
        }

        try {
            int playStoreEnabled = context.getPackageManager().getApplicationEnabledSetting("com.android.vending");
            if (playStoreEnabled == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT || playStoreEnabled == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                url = "market://details?id=";
                i.setPackage("com.android.vending");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        launchApp(context, url + pkg, i);
    }

    public static void launchApp(Context context, String url, Intent i) {
        i.setData(Uri.parse(url));
        try {
            if (context instanceof Activity) {
                context.startActivity(i);
            } else {
                context.startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100;
    private static final int NETWORK_TYPE_WIFI = -101;

    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /**
     * Unknown network class.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    private static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    private static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    private static final int NETWORK_CLASS_4_G = 3;

    public static boolean hasCheapNetwork(Context context) {
        int network = getNetworkClass(context);
        return network == NETWORK_CLASS_3_G || network == NETWORK_CLASS_4_G || network == NETWORK_CLASS_WIFI;
    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static int getNetworkClass(Context context) {
        int networkType = NETWORK_TYPE_UNAVAILABLE;
        try {
            final NetworkInfo network = ((ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager)
                            context.getSystemService(
                                    Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);
    }

    public static String getUUID(Context c) {
        final TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(c.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 13) {
            wm.getDefaultDisplay().getSize(size);
        } else {
            size.x = wm.getDefaultDisplay().getWidth();
            size.y = wm.getDefaultDisplay().getHeight();
        }
        return size;
    }

    public static int getDimens(Context context, int size) {
        return (int) (context.getResources().getDisplayMetrics().density * size);
    }

    public static void notifyDataSetChanged(AbsListView listView) {
        if (listView.getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter listAdapter = (HeaderViewListAdapter) listView.getAdapter();
            BaseAdapter baseadapter = (BaseAdapter) listAdapter.getWrappedAdapter();
            baseadapter.notifyDataSetChanged();
        } else {
            BaseAdapter ba = (BaseAdapter) listView.getAdapter();
            ba.notifyDataSetChanged();
        }

    }

    public static void attachToWindow(Context context, View view) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_FULLSCREEN, PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.CENTER;
        lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.addView(view, lp);
    }

    public static void removeFromWindow(Context context, View view) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.removeViewImmediate(view);
    }

    /** @hide No operation specified. */
    public static final int OP_NONE = -1;
    /** @hide Access to coarse location information. */
    public static final int OP_COARSE_LOCATION = 0;
    /** @hide Access to fine location information. */
    public static final int OP_FINE_LOCATION = 1;
    /** @hide Causing GPS to run. */
    public static final int OP_GPS = 2;
    /** @hide */
    public static final int OP_VIBRATE = 3;
    /** @hide */
    public static final int OP_READ_CONTACTS = 4;
    /** @hide */
    public static final int OP_WRITE_CONTACTS = 5;
    /** @hide */
    public static final int OP_READ_CALL_LOG = 6;
    /** @hide */
    public static final int OP_WRITE_CALL_LOG = 7;
    /** @hide */
    public static final int OP_READ_CALENDAR = 8;
    /** @hide */
    public static final int OP_WRITE_CALENDAR = 9;
    /** @hide */
    public static final int OP_WIFI_SCAN = 10;
    /** @hide */
    public static final int OP_POST_NOTIFICATION = 11;
    /** @hide */
    public static final int OP_NEIGHBORING_CELLS = 12;
    /** @hide */
    public static final int OP_CALL_PHONE = 13;
    /** @hide */
    public static final int OP_READ_SMS = 14;
    /** @hide */
    public static final int OP_WRITE_SMS = 15;
    /** @hide */
    public static final int OP_RECEIVE_SMS = 16;
    /** @hide */
    public static final int OP_RECEIVE_EMERGECY_SMS = 17;
    /** @hide */
    public static final int OP_RECEIVE_MMS = 18;
    /** @hide */
    public static final int OP_RECEIVE_WAP_PUSH = 19;
    /** @hide */
    public static final int OP_SEND_SMS = 20;
    /** @hide */
    public static final int OP_READ_ICC_SMS = 21;
    /** @hide */
    public static final int OP_WRITE_ICC_SMS = 22;
    /** @hide */
    public static final int OP_WRITE_SETTINGS = 23;
    /** @hide */
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    /** @hide */
    public static final int OP_ACCESS_NOTIFICATIONS = 25;
    /** @hide */
    public static final int OP_CAMERA = 26;
    /** @hide */
    public static final int OP_RECORD_AUDIO = 27;
    /** @hide */
    public static final int OP_PLAY_AUDIO = 28;
    /** @hide */
    public static final int OP_READ_CLIPBOARD = 29;
    /** @hide */
    public static final int OP_WRITE_CLIPBOARD = 30;
    /** @hide */
    public static final int OP_TAKE_MEDIA_BUTTONS = 31;
    /** @hide */
    public static final int OP_TAKE_AUDIO_FOCUS = 32;
    /** @hide */
    public static final int OP_AUDIO_MASTER_VOLUME = 33;
    /** @hide */
    public static final int OP_AUDIO_VOICE_VOLUME = 34;
    /** @hide */
    public static final int OP_AUDIO_RING_VOLUME = 35;
    /** @hide */
    public static final int OP_AUDIO_MEDIA_VOLUME = 36;
    /** @hide */
    public static final int OP_AUDIO_ALARM_VOLUME = 37;
    /** @hide */
    public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
    /** @hide */
    public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
    /** @hide */
    public static final int OP_WAKE_LOCK = 40;
    /** @hide Continually monitoring location data. */
    public static final int OP_MONITOR_LOCATION = 41;
    /** @hide Continually monitoring location data with a relatively high power request. */
    public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    /** @hide Retrieve current usage stats via {@link UsageStatsManager}. */
    public static final int OP_GET_USAGE_STATS = 43;
    /** @hide */
    public static final int OP_MUTE_MICROPHONE = 44;
    /** @hide */
    public static final int OP_TOAST_WINDOW = 45;
    /** @hide Capture the device's display contents and/or audio */
    public static final int OP_PROJECT_MEDIA = 46;
    /** @hide Activate a VPN connection without user intervention. */
    public static final int OP_ACTIVATE_VPN = 47;
    /** @hide */
    public static final int _NUM_OP = 48;

    public static boolean xiaomi = false;
    public static boolean huawei = false;

    public static void init() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        xiaomi = (manufacturer.contains("xiaomi"));
        huawei = manufacturer.contains("huawei");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean requireCheckAccessPermission(Context context) {
        int result = checkPermissionIsGrant(context, OP_GET_USAGE_STATS);
        return result != -1 && result != AppOpsManager.MODE_ALLOWED;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasSystemAlertPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 19 && !huawei) {
            int result = checkPermissionIsGrant(context, OP_SYSTEM_ALERT_WINDOW);
            return result == AppOpsManager.MODE_ALLOWED;
        } else {
            return !xiaomi;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int checkPermissionIsGrant(Context context, int permission) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            method.setAccessible(true);
            return (Integer)method.invoke(appOpsManager, permission, applicationInfo.uid, applicationInfo.packageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void showDialog(Dialog dialog, boolean alert) {
        if (alert) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
    }

    public static void LOGE(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void LOGW(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void LOGI(String TAG, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void LOGE(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void LOGER(String msg) {
        Log.e(TAG, msg);
    }

    public static void LOGW(String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void LOGI(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void printStackTrace() {
        if (BuildConfig.DEBUG) {
            try {
                throw new RuntimeException("print stack trace");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printStackTraceR() {
        try {
            throw new RuntimeException("print stack trace");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static boolean runCommandWithRoot(String... cmds) {
        if (cmds.length == 0) return false;
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            for (String cmd : cmds) {
                dos.writeBytes(cmd + "\n");
            }
            dos.writeBytes("\nexit\n");
            dos.flush();
            return p.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
