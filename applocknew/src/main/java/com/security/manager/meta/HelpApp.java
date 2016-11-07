package com.security.manager.meta;

import android.widget.ImageView;
import com.security.manager.asyncmanager.ImageManager;

/**
 * Created by huale on 2015/1/7.
 */
public class HelpApp {
    public String pkgName;
    public int icon = -1;

    public void show(ImageView iv){
        if (icon > 0){
            iv.setImageResource(icon);
        } else {
            ImageManager.setImageView(iv, pkgName, true);
        }
    }
}
