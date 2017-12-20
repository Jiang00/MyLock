package com.vactorapps.manager;

import android.content.Intent;


/**
 * Created by song on 15/8/18.
 */
public class IntruderPresenter {
    public static void show() {
        MyApp.getContext().startActivity(new Intent(MyApp.getContext(), VacIntruderActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }
}
