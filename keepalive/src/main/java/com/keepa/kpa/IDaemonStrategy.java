package com.keepa.kpa;

import com.keepa.kpa.strategy.DaemonStrategy21;
import com.keepa.kpa.strategy.DaemonStrategy22;
import com.keepa.kpa.strategy.DaemonStrategy23;
import com.keepa.kpa.strategy.DaemonStrategyUnder21;
import com.keepa.kpa.strategy.DaemonStrategyXiaomi;

import android.content.Context;
import android.os.Build;

/**
 * define strategy method
 *
 * @author renqingyou
 */
public interface IDaemonStrategy {
    /**
     * Initialization some files or other when 1st time
     *
     * @param context
     * @return
     */
    boolean onInitialization(Context context);

    /**
     * when Persistent process create
     *
     * @param context
     * @param configs
     */
    void onPersistentCreate(Context context, DaemonConfigurations configs);

    /**
     * when DaemonAssistant process create
     *
     * @param context
     * @param configs
     */
    void onDaemonAssistantCreate(Context context, DaemonConfigurations configs);

    /**
     * when watches the process dead which it watched
     */
    void onDaemonDead();


    /**
     * all about strategy on different device here
     *
     * @author renqingyou
     */
    class Fetcher {

        private static IDaemonStrategy mDaemonStrategy;

        /**
         * fetch the strategy for this device
         *
         * @return the daemon strategy for this device
         */
        static IDaemonStrategy fetchStrategy() {
            if (mDaemonStrategy != null) {
                return mDaemonStrategy;
            }
            int sdk = Build.VERSION.SDK_INT;
            switch (sdk) {
                case Build.VERSION_CODES.M:
                    mDaemonStrategy = new DaemonStrategy23();
                    break;

                case Build.VERSION_CODES.LOLLIPOP_MR1:
                    mDaemonStrategy = new DaemonStrategy22();
                    break;

                case Build.VERSION_CODES.LOLLIPOP:
                    if ("MX4 Pro".equalsIgnoreCase(Build.MODEL)) {
                        mDaemonStrategy = new DaemonStrategyUnder21();
                    } else {
                        mDaemonStrategy = new DaemonStrategy21();
                    }
                    break;

                default:
                    if (Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("mi")) {
                        mDaemonStrategy = new DaemonStrategyXiaomi();
                    } else if (Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("a31")) {
                        mDaemonStrategy = new DaemonStrategy21();
                    } else {
                        mDaemonStrategy = new DaemonStrategyUnder21();
                    }
                    break;
            }
            return mDaemonStrategy;
        }
    }
}
