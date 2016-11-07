package com.security.manager.lib.io;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;

import com.security.manager.lib.BaseApp;
import com.security.manager.lib.async.LoadingTask;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class LoadIconFromApp extends LoadingTask {
    public interface LoadingNotifiable {
        String getUrl();

        Point getSize();

        int getFileType();

        long getIdLong();

        void offer(Bitmap bitmap);
    }

    private static LoadIconFromApp instance = new LoadIconFromApp();
    public static LoadIconFromApp Instance() {
        return instance;
    }

    LinkedBlockingQueue<LoadingNotifiable> queue = new LinkedBlockingQueue<>();

    public void execute(LoadingNotifiable notifiable) {
        try {
            if (queue.contains(notifiable)) return;
            queue.put(notifiable);
            super.restart(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doInBackground() {
        try {
            while (!isCanceled() && !queue.isEmpty()) {
                LoadingNotifiable notifiable = queue.poll();
                String pkgName = notifiable.getUrl();
                if (pkgName == null) continue;
                if (ImageMaster.hasImage(pkgName)) {
                    notifiable.offer(ImageMaster.getImage(pkgName));
                } else {
                    Bitmap drawable = getBitmap(pkgName, notifiable);
                    if (drawable != null) {
                        ImageMaster.addImage(pkgName, drawable);
                    }
                    if (pkgName.equals(notifiable.getUrl())) {
                        notifiable.offer(drawable);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Bitmap getBitmap(String url, LoadingNotifiable notifiable) throws PackageManager.NameNotFoundException {
        PackageManager pm = BaseApp.getContext().getPackageManager();
        PackageInfo p = pm.getPackageInfo(url, PackageManager.GET_UNINSTALLED_PACKAGES);
        BitmapDrawable drawable = (BitmapDrawable) p.applicationInfo.loadIcon(pm);
        return drawable.getBitmap();
    }
}
