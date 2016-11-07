package com.security.manager.lib.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class AsyncExecutor {
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 8, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(72));

    public static void execute(Runnable runnable){
        executor.execute(runnable);
    }

    public static boolean remove(Runnable runnable){
        return executor.remove(runnable);
    }
}
