package com.security.manager.page;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by huale on 2015/2/4.
 */
public class OverflowCtrl {
    protected LinearLayout overflowStub;
    protected ImageButton ovf;

    public void pressOverflowMenu() {
        if (ovf != null)
            ovf.performClick();
    }

    public boolean overflowVisible() {
        return overflowStub != null && overflowStub.getVisibility() == View.VISIBLE;
    }

    View.OnClickListener hideOverflow = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (overflowVisible())
                pressOverflowMenu();
        }
    };
}
