package com.android.kpa;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;

/**
 * the configurations of Daemon SDK, contains two process configuration.
 *
 * @author renqingyou
 */
public class DaemonConfigurations {

    public DaemonConfiguration PERSISTENT_CONFIG = null;
    public DaemonConfiguration DAEMON_ASSISTANT_CONFIG = null;
    public DaemonListener LISTENER = null;


    public DaemonConfigurations(DaemonConfiguration persistentConfig, DaemonConfiguration daemonAssistantConfig) {
        this(persistentConfig, daemonAssistantConfig, null);
    }

    public DaemonConfigurations(DaemonConfiguration persistentConfig, DaemonConfiguration daemonAssistantConfig, DaemonListener listener) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = daemonAssistantConfig;
        this.LISTENER = listener;
    }

    public DaemonConfigurations(Context context, DaemonConfiguration persistentConfig, DaemonListener listener) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = new DaemonConfigurations.DaemonConfiguration(
                context.getPackageName() + ":daemon", DaemonService.class, DaemonReceiver.class);
        this.LISTENER = listener;
    }


    /**
     * the configuration of a daemon process, contains process name, service name and receiver name if Android 6.0
     *
     * @author renqingyou
     */
    public static class DaemonConfiguration {

        public final String PROCESS_NAME;
        public final String SERVICE_NAME;
        public final String RECEIVER_NAME;

        public DaemonConfiguration(String processName, Class<? extends Service> service, Class<? extends BroadcastReceiver> receiver) {
            this.PROCESS_NAME = processName;
            this.SERVICE_NAME = service.getCanonicalName();
            this.RECEIVER_NAME = receiver.getCanonicalName();
        }
    }

    /**
     * listener of daemon for external
     *
     * @author renqingyou
     */
    public interface DaemonListener {
        void onPersistentStart(Context context);

        void onDaemonAssistantStart(Context context);

        void onWatchDaemonDead();
    }
}
