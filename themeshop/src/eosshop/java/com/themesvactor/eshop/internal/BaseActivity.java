package com.themesvactor.eshop.internal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by song on 2017/3/9.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            onReceiveIntent(getIntent());
        } else {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    protected void onReceiveIntent(Intent intent) {

    }
}
