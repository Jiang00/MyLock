package com.vactorapps.manager;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivymobi.applock.free.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by superjoy on 2014/8/29.
 */
public class FakesViewHolder {
    @Optional
    @InjectView(R.id.fake_item)
    LinearLayout fake_item;

    @Optional
    @InjectView(R.id.icon)
    ImageView fakeicon;

    @Optional
    @InjectView(R.id.name)
    TextView appName;

    long id = 0;

    public FakesViewHolder() {
    }

    public FakesViewHolder(View root) {
        ButterKnife.inject(this, root);
        root.setTag(this);
    }
}
