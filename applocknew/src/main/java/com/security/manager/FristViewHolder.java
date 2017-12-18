package com.security.manager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivymobi.applock.free.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by superjoy on 2014/8/29.
 */
public class FristViewHolder {
    @Optional
    @InjectView(R.id.security_ima)
    ImageView icon;

    @Optional
    @InjectView(R.id.name)
    TextView appName;

    @Optional
    @InjectView(R.id.lock)
    ImageView lock;
    @Optional
    @InjectView(R.id.unlock_yuan)
    ImageView unlock_yuan;
    @Optional
    @InjectView(R.id.unlock_yuan2)
    ImageView unlock_yuan2;

    long id = 0;


    public FristViewHolder() {
    }

    public FristViewHolder(View root) {
        ButterKnife.inject(this, root);
        root.setTag(this);
    }
}
