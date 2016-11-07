package com.security.manager;

import android.media.MediaScannerConnection;
import android.net.Uri;

/**
 * Created by song on 15/8/26.
 */
public class ScannerCallback implements MediaScannerConnection.OnScanCompletedListener {
    public int totalCount;
    public int currentCount;
    public boolean needStop;

    public interface ScannerListener {
        void updateProgress();

        void onComplete();
    }

    ScannerListener listener;

    public ScannerCallback(ScannerListener listener) {
        this.listener = listener;
        totalCount = 0;
        needStop = false;
        currentCount = 0;
    }

    public void addCount(int count) {
        totalCount += count;
    }

    public void requireStop() {
        needStop = true;
        if (currentCount >= totalCount) {
            listener.onComplete();
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        ++currentCount;
        if (needStop) {
            if (currentCount >= totalCount) {
                listener.onComplete();
            } else {
                listener.updateProgress();
            }
        }
    }
}
