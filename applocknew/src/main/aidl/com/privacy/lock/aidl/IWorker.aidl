package com.privacy.lock.aidl;

/**
 * Created by superjoy on 2014/8/26.
 */
interface IWorker {
    void notifyApplockUpdate();
    void toggleProtectStatus();
    void updateProtectStatus();
    void showNotification(boolean yes);
    boolean unlockApp(String pkg);
    boolean unlockLastApp(boolean unlockAlways);
    boolean homeDisplayed();
    void notifyShowWidget();
}
