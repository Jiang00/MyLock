package com.security.manager;

import android.util.Log;
import android.widget.Toast;

import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.FakePresenter;

/**
 * Created by superjoy on 2014/11/5.
 */
public class UnlockApp extends SecurityPatternActivity {

    @Override
    public void setupView() {

        Log.e("unlocksussful","解锁成功");

        if (FakePresenter.isFakeCover()) {
            try {
                CharSequence label = getPackageManager().getApplicationInfo(pkg, 0).loadLabel(getPackageManager());
                FakePresenter.show(this, SecurityMyPref.getFakeCover(FakePresenter.FAKE_NONE), label, new Runnable() {
                    @Override
                    public void run() {
                        UnlockApp.super.setupView();
                        FakePresenter.hide();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                        FakePresenter.hide();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.setupView();
        }
        Tracker.sendEvent(Tracker.CATE_DEFAULT, Tracker.ACT_UNLOCK, Tracker.ACT_UNLOCK, 1L);
    }
}