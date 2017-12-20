package com.vactorapps.manager;

/**
 * Created by superjoy on 2014/11/5.
 */
public class UnlockApp extends FristActivity {
//    @Override
//    protected void onStop() {
//        super.onStop();
//        finish();
//    }

    @Override
    public void setupView() {
        context = this;

            super.setupView();
        Tracker.sendEvent(Tracker.CATE_DEFAULT, Tracker.ACT_UNLOCK, Tracker.ACT_UNLOCK, 1L);
    }
}