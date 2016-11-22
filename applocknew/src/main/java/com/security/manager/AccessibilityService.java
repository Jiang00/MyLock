package com.security.manager;

import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by song on 16/4/6.
 */
public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null) {
            if (event.getPackageName() != null) {
                String packageName = event.getPackageName().toString();
                if (!packageName.equals(getPackageName())) {
                    SecurityService.startLock(this, packageName);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        SecurityService.stopWorking(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        SecurityService.startWorking(this);
        return super.onUnbind(intent);
    }
}
