package com.android.theme.internal.data;

import com.android.async.AsyncExecutor;
import com.android.common.SdkCache;
import com.android.common.SdkEnv;
import com.android.network.Request;

import java.net.HttpURLConnection;

/**
 * Created by song on 2017/3/3.
 */

public class Server {
    private static final String VERSION_FILE = "shop/version.json";
    public AsyncExecutor executor;
    private boolean requireReload;
    private static final Server SERVER = new Server();
    private String url;

    public static void execute(Runnable runnable) {
        SERVER.executor.execute(runnable);
    }

    public Server() {
        executor = new AsyncExecutor(3);
    }

    public static void load() {
        SERVER.load_();
    }

    public static void setUrl(String url) {
        SERVER.url = url;
    }

    private void load_() {
        executor.execute(new Request(baseUrl_() + "/version.json") {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection) throws Exception {
                String s = SdkCache.readText(httpURLConnection.getInputStream());
                if (s != null && !s.equals("null")) {
                    final String s1 = SdkCache.cache().readText(VERSION_FILE, false, false);
                    boolean requireLoad;
                    if (s1 == null) {
                        requireLoad = true;
                    } else {
                        try {
                            requireLoad = (Integer.parseInt(s1) < Integer.parseInt(s));
                        } catch (Exception e) {
                            e.printStackTrace();
                            requireLoad = true;
                        }
                    }
                    if (requireLoad) {
                        SdkCache.cache().cache(VERSION_FILE, s.getBytes(), false);
                        executor.execute(new Request(baseUrl_() + "/tags.json") {
                            @Override
                            public void onSuccess(HttpURLConnection httpURLConnection) throws Exception {
                                Local.save(httpURLConnection.getInputStream());
                                onComplete(true);
                            }

                            @Override
                            public void onFailure(int code, String exception) {
                                super.onFailure(code, exception);
                                onComplete(true);
                            }
                        });
                    } else {
                        onComplete(false);
                    }
                } else {
                    onComplete(false);
                }
            }

            @Override
            public void onFailure(int code, String exception) {
                super.onFailure(code, exception);
                onComplete(false);
            }

            private void onComplete(boolean requireLoad) {
                requireReload = requireLoad;
                final String data = Local.load();
                ShopLoadEvent event = new ShopLoadEvent(data);
                SdkEnv.sendEvent(ShopLoadEvent.EVENT_LOAD_COMPLETE, event);
            }
        });
    }

    public static boolean requireReload() {
        return SERVER.requireReload;
    }

    public static String baseUrl() {
        return SERVER.url;
    }

    private String baseUrl_() {
        return url;
    }
}
