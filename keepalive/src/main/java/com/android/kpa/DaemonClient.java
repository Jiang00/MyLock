package com.android.kpa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author renqingyou
 */
public class DaemonClient implements IDaemonClient {
    private DaemonConfigurations mConfigurations;

    public DaemonClient(Context context, DaemonConfigurations.DaemonListener daemonListener) {
        mConfigurations = createDaemonConfigurations(context, daemonListener);
    }

    public DaemonClient(DaemonConfigurations configurations) {
        this.mConfigurations = configurations;
    }

    @Override
    public void onAttachBaseContext(Context base) {
        if (mConfigurations != null) {
            initDaemon(base);
            base.startService(new Intent(base, PersistService.class));
        }
    }


    private final String DAEMON_PERMITTING_SP_FILENAME = "d_permit";
    private final String DAEMON_PERMITTING_SP_KEY = "permitted";


    private BufferedReader mBufferedReader;//release later to save time


    private DaemonConfigurations createDaemonConfigurations(Context context, DaemonConfigurations.DaemonListener daemonListener) {
        DaemonConfigurations.DaemonConfiguration persistentConfig = new DaemonConfigurations.DaemonConfiguration(
                context.getPackageName() + context.getString(com.android.kpa.R.string.com_android_kpa_nativ_process_name), PersistService.class, PersistReceiver.class);
        return new DaemonConfigurations(context, persistentConfig, daemonListener);
    }


    /**
     * do some thing about daemon
     *
     * @param base
     */
    private void initDaemon(Context base) {
        if (!isDaemonPermitting(base) || mConfigurations == null) {
            return;
        }
        String processName = getProcessName();
        String packageName = base.getPackageName();

        Log.e("rqy", "processName=" + processName);
        Log.e("rqy", "PERSISTENT_CONFIG=" + mConfigurations.PERSISTENT_CONFIG.PROCESS_NAME);
        Log.e("rqy", "--DAEMON_ASSISTANT_CONFIG=" + mConfigurations.DAEMON_ASSISTANT_CONFIG.PROCESS_NAME);
        Log.e("rqy", "--packageName=" + packageName);
        if (processName.startsWith(mConfigurations.PERSISTENT_CONFIG.PROCESS_NAME)) {
            Log.e("rqy", "--startWith= PERSISTENT_CONFIG");
            IDaemonStrategy.Fetcher.fetchStrategy().onPersistentCreate(base, mConfigurations);
        } else if (processName.startsWith(mConfigurations.DAEMON_ASSISTANT_CONFIG.PROCESS_NAME)) {
            Log.e("rqy", "--startWith= DAEMON_ASSISTANT_CONFIG");
            IDaemonStrategy.Fetcher.fetchStrategy().onDaemonAssistantCreate(base, mConfigurations);
        } else if (processName.startsWith(packageName)) {
            Log.e("rqy", "--startWith= packageName");
            IDaemonStrategy.Fetcher.fetchStrategy().onInitialization(base);
        }

        releaseIO();
    }

	
	/* spend too much time !! 60+ms
    private String getProcessName(){
		ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		int pid = android.os.Process.myPid();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (int i = 0; i < infos.size(); i++) {
			RunningAppProcessInfo info = infos.get(i);
			if(pid == info.pid){
				return info.processName;
			}
		}
		return null;
	}
	*/

    private String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            mBufferedReader = new BufferedReader(new FileReader(file));
            return mBufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * release reader IO
     */
    private void releaseIO() {
        if (mBufferedReader != null) {
            try {
                mBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBufferedReader = null;
        }
    }

    private boolean isDaemonPermitting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME, Context.MODE_PRIVATE);
        return sp.getBoolean(DAEMON_PERMITTING_SP_KEY, true);
    }

    protected boolean setDaemonPermiiting(Context context, boolean isPermitting) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(DAEMON_PERMITTING_SP_KEY, isPermitting);
        return editor.commit();
    }

}
