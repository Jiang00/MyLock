package com.security.manager.meta;

import android.view.View;

/**
 * Created by huale on 2014/11/20.
 */
public abstract class SecurityFlowMenu implements View.OnClickListener{
    public boolean checkable;
    public boolean checked;
    public int title;

    public abstract SecurityFlowMenu init();
}
