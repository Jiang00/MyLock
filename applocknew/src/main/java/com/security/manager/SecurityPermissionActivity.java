package com.security.manager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.privacy.lock.R;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class SecurityPermissionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.permission_translate);
        this.findViewById(R.id.onclick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


}
