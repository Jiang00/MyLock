package com.privacy.lock;

import android.content.Intent;

/**
 * Created by song on 15/8/18.
 */
public class IntruderPresenter {
    public static void show() {
        App.getContext().startActivity(new Intent(App.getContext(), IntruderActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
