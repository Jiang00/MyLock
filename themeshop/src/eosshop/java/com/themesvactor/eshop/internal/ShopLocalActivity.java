package com.themesvactor.eshop.internal;

import com.android.theme.internal.data.EventHook;

public class ShopLocalActivity extends ShopActivity {
    private boolean requireResume;

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (tags != null) {
            refreshThemeSelector(tags);
            if (requireResume) {
                requireResume = false;
                presenter.loadLocal(this, tags, localThemes);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        requireResume = true;
    }

    @Override
    protected void setupPresenter() {
        presenter = new ShopPresenter(controller);
        presenter.start();
        presenter.loadLocal(this, tags, localThemes);
    }

    @Override
    public void onBackPressed() {
        if (!EventHook.hooked(EventHook.EVENT_BACK_PRESSED, this)) {
            super.onBackPressed();
        }
    }
}
