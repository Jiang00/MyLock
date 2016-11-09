package com.security.manager;

import android.content.Intent;

/**
 * Created by song on 15/8/18.
 */
public class SecurityIntruderPresenter {
    public static void show() {
        App.getContext().startActivity(new Intent(App.getContext(), IntruderActivitySecurity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
