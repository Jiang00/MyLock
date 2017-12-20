package com.android.theme.internal.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.async.AsyncExecutor;
import com.android.common.SdkCache;
import com.android.common.SdkEnv;
import com.android.network.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by song on 2017/3/7.
 */

public class BitmapCache {

    public interface LoadingListener {
        void onStart();

        void onSuccess();

        void onFails();
    }


    public static HashMap<String, ImageView> pendingImageViews = new HashMap<>();

    private static final AsyncExecutor executor = new AsyncExecutor(5);

    public static void setImageView(final String url, ImageView v, int width, int height) {
        width = SdkEnv.dp2px(width);
        height = SdkEnv.dp2px(height);
        final Bitmap cached = cached(url);
        if (cached != null) {
            v.setImageBitmap(cached);
        } else {
            if (!isLoading(url, v)) {
                if (!loadFromDisk(url, v, width, height, null)) {
                    loadFromNetwork(url, v, width, height, null);
                }
            }
        }
    }

    public static void setImageView(final String url, final String placeholderUrl, ImageView v, int width, int height, LoadingListener listener) {
        width = SdkEnv.dp2px(width);
        height = SdkEnv.dp2px(height);
        final Bitmap cached = cached(url);
        if (cached == null) {
            final Bitmap placeholder = cached(placeholderUrl);
            if (placeholder != null) {
                v.setImageBitmap(placeholder);
            }
            if (!isLoading(url, v)) {
                if (!loadFromDisk(url, v, width, height, listener)) {
                    loadFromNetwork(url, v, width, height, listener);
                }
            }
        } else {
            v.setImageBitmap(cached);
            listener.onSuccess();
        }
    }

    private static boolean isLoading(String url, ImageView v) {
        if (pendingImageViews.containsKey(url)) {
            return true;
        } else {
            pendingImageViews.put(url, v);
            return false;
        }
    }

    private static Bitmap cached(String name) {
        return null;
    }

    private static boolean loadFromDisk(final String url, ImageView v, final int width, final int height, final LoadingListener listener) {
        if (SdkCache.cache().has(url, true)) {
            if (listener != null) {
                listener.onStart();
            } else {
                v.setImageBitmap(null);
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapReader.readBitmap(url, width, height, true);
                    if (bitmap != null) {
//                        bitmaps.put(url, bitmap);
                        SdkEnv.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    pendingImageViews.remove(url).setImageBitmap(bitmap);
                                    if (listener != null) {
                                        listener.onSuccess();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (listener != null) {
                                        listener.onFails();
                                    }
                                }
                            }
                        });
                    } else {
                        if (listener != null) {
                            listener.onFails();
                        }
                    }
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private static void loadFromNetwork(final String url, ImageView v, final int width, final int height, final LoadingListener listener) {
        if (listener != null) {
            listener.onStart();
        } else {
            v.setImageBitmap(null);
        }
        executor.execute(new Request(url) {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection) throws Exception {
                boolean success = SdkCache.cache().cacheInputStream(url, httpURLConnection.getInputStream(), true);
                if (success) {
                    final Bitmap bitmap = BitmapReader.readBitmap(url, width, height, true);
//                    bitmaps.put(url, bitmap);
                    SdkEnv.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                pendingImageViews.remove(url).setImageBitmap(bitmap);
                                if (listener != null) {
                                    listener.onSuccess();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (listener != null) {
                                    listener.onFails();
                                }
                            }
                        }
                    });
                } else {
                    throw new RuntimeException("download fails");
                }
            }

            @Override
            public void onFailure(int code, String exception) {
                if (listener != null) {
                    SdkEnv.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFails();
                        }
                    });
                }
                super.onFailure(code, exception);
                pendingImageViews.remove(url);
            }
        });
    }

    public static void setImageView(ImageView iv, String url) {
        Glide.with(iv.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
    }
}
