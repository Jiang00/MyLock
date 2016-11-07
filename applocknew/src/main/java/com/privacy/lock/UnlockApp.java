package com.privacy.lock;

import com.privacy.lock.meta.Pref;
import com.privacy.lock.view.FakePresenter;

/**
 * Created by superjoy on 2014/11/5.
 */
public class UnlockApp extends PatternActivity {

    @Override
    public void setupView() {
        if (FakePresenter.isFakeCover()) {
            try {
                CharSequence label = getPackageManager().getApplicationInfo(pkg, 0).loadLabel(getPackageManager());
                FakePresenter.show(this, Pref.getFakeCover(FakePresenter.FAKE_NONE), label, new Runnable() {
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
        MyTracker.sendEvent(MyTracker.CATE_DEFAULT, MyTracker.ACT_UNLOCK, MyTracker.ACT_UNLOCK, 1L);
    }
}