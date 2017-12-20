package com.vactorapps.manager;

import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by song on 16/4/6.
 */
public class VacAccessibilityService extends android.accessibilityservice.AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null) {
            if (event.getPackageName() != null) {
                String packageName = event.getPackageName().toString();
                if (!packageName.equals(getPackageName())) {
                    WorksService.startLock(this, packageName);
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
        WorksService.stopWorking(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        WorksService.startWorking(this);
        return super.onUnbind(intent);
    }
}
