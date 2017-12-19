package com.security.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.security.manager.myinterface.ISecurityBridge;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by superjoy on 2014/9/4.
 */
public class Tools {

    public static boolean showApp(String pkg) {
        return Tools.runActionWithRoot("pm enable " + pkg);
    }

    public static boolean hideApp(String pkg) {
        return Tools.runActionWithRoot("pm disable " + pkg);
    }

    public static int process(String... cmd) {
        if (cmd.length <= 0) return -1;
        try {
            Process localProcess;
            localProcess = new ProcessBuilder(new String[0])
                    .command(new String[]{"sh"}).redirectErrorStream(true)
                    .start();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    localProcess.getOutputStream());
            for (int i = 0; i < cmd.length; i++) {
                localDataOutputStream.writeBytes(cmd[i] + "\n");
            }
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            return localProcess.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String runCommands(String... cmds) {
        if (cmds.length <= 0) return null;
        try {
            Process localProcess;
            localProcess = new ProcessBuilder(new String[0])
                    .command(new String[]{"sh"}).redirectErrorStream(true)
                    .start();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    localProcess.getOutputStream());
            for (int i = 0; i < cmds.length; i++) {
                localDataOutputStream.writeBytes(cmds[i] + "\n");
            }
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            StreamReader r = new StreamReader(localProcess.getInputStream());
            r.start(localProcess);
            localProcess.waitFor();
            return r.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String type = networkInfo.getTypeName();

                if (type.equalsIgnoreCase("WIFI")) {
                    return true;
                } else if (type.equalsIgnoreCase("MOBILE")) {
                    int network = getNetworkClass(context);
                    return network == NETWORK_CLASS_3_G || network == NETWORK_CLASS_4_G;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 适配低版本手机
    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;


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


    /**
     * 获取网络类型
     *
     * @return
     */
    public static String getCurrentNetworkType(Context context) {
        int networkClass = getNetworkClass(context);
        String type = "未知";
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                type = "无";
                break;
            case NETWORK_CLASS_WIFI:
                type = "Wi-Fi";
                break;
            case NETWORK_CLASS_2_G:
                type = "2G";
                break;
            case NETWORK_CLASS_3_G:
                type = "3G";
                break;
            case NETWORK_CLASS_4_G:
                type = "4G";
                break;
            case NETWORK_CLASS_UNKNOWN:
                type = "未知";
                break;
        }
        return type;
    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    private static int getNetworkClass(Context context) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager)
                    context
                            .getSystemService(Context.CONNECTIVITY_SERVICE))
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

    public static boolean runActionWithRoot(String cmd) {
        try {
            /*
            try{
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                dos.writeBytes("\nexit\n");
                dos.flush();
                if (p.waitFor() != 0)
                {
                    return false;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            */
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd);
            dos.writeBytes("\nexit\n");
            dos.flush();
            return p.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isMyPhoneRooted() {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
//            dos.writeBytes("su");
            dos.writeBytes("\nexit\n");
            dos.flush();
            return p.waitFor() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getExceptionMessage(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        if (e.getCause() != null) {
            e.getCause().printStackTrace(pw);
        }
        return sw.toString();
    }

    public static String runCommandWithRoot(String... cmds) {
        if (cmds.length == 0) return null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            for (int i = 0; i < cmds.length; i++) {
                dos.writeBytes(cmds[i] + "\n");
            }
            dos.writeBytes("\nexit\n");
            dos.flush();
            StreamReader r = new StreamReader(p.getInputStream());
            r.start(p);
            p.waitFor();
            return r.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void openPlayStore(Context context, String pkg) {
        Intent i = new Intent(Intent.ACTION_VIEW);

        String url = "https://play.google.com/store/apps/details?id=";

        if (pkg.startsWith("http")) {
            pkg = pkg.replace(url, "");
            if (pkg.startsWith("http")) {
                launchApp(context, pkg, i);
                return;
            }
        }

        try {
            if (context.getPackageManager().getApplicationEnabledSetting("com.android.vending") == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
                url = "market://details?id=";
                i.setPackage("com.android.vending");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        launchApp(context, url + pkg, i);
    }

    public static void openPlayStore(Context context, String pkg, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);

        if (pkg.startsWith("http")) {
            pkg = pkg.replace(url, "");
            if (pkg.startsWith("http")) {
                launchApp(context, pkg, i);
                return;
            }
        }

        try {
            if (context.getPackageManager().getApplicationEnabledSetting("com.android.vending") == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
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

    static class IndexedDrawable {
        int idx;
        Drawable drawable;
        public IndexedDrawable(int idx, Drawable drawable) {
            this.idx = idx;
            this.drawable = drawable;
        }

        public void recycle() {
            drawable = null;
        }
    }

    public static void RandomNumpad(ISecurityBridge bridge, View root, String[] buttons) {
        if (!bridge.random()) return;

        ArrayList<IndexedDrawable> buttonBgs = new ArrayList<>();
        Button[] numpads = new Button[10];
        for (int i = 0; i < buttons.length; ++i) {
            Button btn = (Button) root.findViewWithTag(buttons[i]);
            Drawable background = btn.getBackground();
            buttonBgs.add(new IndexedDrawable(i, background));
            numpads[i] = btn;
        }

        Collections.shuffle(buttonBgs);

        for (int i = 0; i < numpads.length; ++i) {
            Button button = numpads[i];
            numpads[i] = null;
            IndexedDrawable id = buttonBgs.get(i);
            button.setText(id.idx + "");
            button.setBackgroundDrawable(id.drawable);
            id.recycle();
        }

        buttonBgs.clear();
    }

    public static int getRandomInt(int a, int b) {
        // 下面两种形式等价
        // return a + (int) (new Random().nextDouble() * (b - a + 1));
        return a + (int) (Math.random() * (b - a + 1));
    }
}
