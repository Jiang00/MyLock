package com.security.manager;

/**
 * Created by superjoy on 2014/11/5.
 */
public class UnlockApp extends SecurityPatternActivity {
//    @Override
//    protected void onStop() {
//        super.onStop();
//        finish();
//    }

    @Override
    public void setupView() {

//        if (PretentPresenter.isFakeCover()) {
//            try {
//                CharSequence label = getPackageManager().getApplicationInfo(pkg, 0).loadLabel(getPackageManager());
//                PretentPresenter.show(this, SecurityMyPref.getFakeCover(PretentPresenter.PRETENT_NONE), label, new Runnable() {
//                    @Override
//                    public void run() {
//                        UnlockApp.super.setupView();
//                        PretentPresenter.hide();
//                    }
//                }, new Runnable() {
//                    @Override
//                    public void run() {
//                        onBackPressed();
//                        PretentPresenter.hide();
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
            super.setupView();
       // }
        Tracker.sendEvent(Tracker.CATE_DEFAULT, Tracker.ACT_UNLOCK, Tracker.ACT_UNLOCK, 1L);
    }
}