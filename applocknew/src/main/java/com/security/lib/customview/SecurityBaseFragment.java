package com.security.lib.customview;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by SongHualin on 6/24/2015.
 */
public class SecurityBaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState);
        } else {
            if (getArguments() != null){
                onArguments(getArguments());
            }
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){

    }

    protected void onArguments(Bundle arguments){

    }
}
