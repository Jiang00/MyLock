package com.privacy.lock;

import android.os.AsyncTask;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by superjoy on 2014/8/29.
 */
public class ViewHolder {
    int idx = -1;

    @Optional @InjectView(R.id.security_ima)
    ImageView icon;

    @Optional @InjectView(R.id.icon)
    ImageView fakeicon;

    @Optional @InjectView(R.id.name)
    TextView appName;

    @Optional @InjectView(R.id.bg_sel)
    View encrypted;

    @Optional @InjectView(R.id.icon_left)
    ImageView iconL;

    @Optional @InjectView(R.id.intrude_new_i)
    View intrudenewicon;

    @Optional @InjectView(R.id.security_invade_data)
    TextView simName;


    @Optional @InjectView(R.id.security_invade_ic)
    ImageView lockIcon;







    long id = 0;
    String iconUrl;
    String iconLUrl;

    AsyncTask task;

    @Optional @InjectView(R.id.checkBox)
    CheckBox box;

    public ViewHolder() {
    }

    public ViewHolder(View root) {
        ButterKnife.inject(this, root);
        root.setTag(this);
    }
}
