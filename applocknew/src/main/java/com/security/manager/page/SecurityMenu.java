package com.security.manager.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.privacy.lock.*;
import com.security.manager.SecurityAppLock;
import com.security.manager.FakeSelectorActivitySecurity;
import com.security.manager.IntruderActivitySecurity;
import com.security.manager.SecuritySettings;
import com.security.manager.Tracker;
import com.security.manager.meta.SecurityMyPref;

import java.net.URI;
import java.net.URL;
import java.util.Locale;

/**
 * Created by huale on 2014/12/22.
 */
public class SecurityMenu {
    public static final int MENU_LOCK_APP = 0;
    public static final int MENU_LOCK_PHOTO = 1;
    public static final int MENU_LOCK_VIDEO = 2;
    public static final int MENU_PROFILE = 3;
    public static final int MENU_FAKE = 4;
    public static final int MENU_PLUGIN = 5;
    public static final int MENU_THEME = 6;
    public static final int MENU_LOCK_FILE = 7;
    public static final int MENU_HIDE_APP = 8;
    public static final int MENU_SETTING = 9;
    public static final int MENU_ABOUT = 10;
    public static final int MENU_DAILY = 11;
    public static final int MENU_FAQ = 4;
    public static final String FACEBOOK = "https://www.facebook.com/IvyAppLock";
    public static final String GOOGLE = "https://plus.google.com/u/0/communities/113134139742239607331";


    public static final String[] newidkeys = {
            "nlk", "np", "nv", "npr", "nfk", "npl", "nt", "nf", "nh", "ns", "na", "nd"
    };

    public static int icons[] = {
            R.drawable.security_intrude_infomation,
//            R.drawable.photo,
//            R.drawable.video,
//            R.drawable.profile,
            R.drawable.security_intrude_infomation,
//            R.drawable.plugin,
//            R.drawable.theme,
//            R.drawable.file,
//            R.drawable.hide,
            R.drawable.security_myfake_2,
            R.drawable.security_intrude_infomation,
//            R.drawable.daily
            R.drawable.security_intrude_infomation,
            R.drawable.security_intrude_infomation


    };

//    public static int menus[] = {
//            R.string.lock_tab, R.string.hide_pic, R.string.hide_video, R.string.profile, R.string.fake, R.string.plugin, R.string.theme, R.string.hide_file,
//            R.string.hide_app, R.string.setting_tab, R.string.daily_app
//    };

    public static int menus[] = {
            R.string.security_lock_app, R.string.security_myfake, R.string.security_new_intruder,
            R.string.security_tab_setting, R.string.security_facebook, R.string.security_google
    };

    public static byte[] seperator = {
            MENU_HIDE_APP, -1
    };

    public static int currentMenuIt = MENU_LOCK_APP;
    static int langIdx = 0;

    public static void attach(final SlideMenu menu, final View reddot) {

        Uri facebookuri = Uri.parse(FACEBOOK);
        Uri googleuri = Uri.parse(GOOGLE);

        LinearLayout menuList = (LinearLayout) menu.findViewById(R.id.menu_its);
        final Context context = menu.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        Resources resources = context.getResources();
        int iconColorSelected = resources.getColor(R.color.security_navdrawer_icon_tint_selected);
        int iconColorNormal = resources.getColor(R.color.security_navdrawer_icon_tint);
        int textColorSelected = resources.getColor(R.color.security_navdrawer_text_color_selected);
        int textColorNormal = resources.getColor(R.color.security_text_color);

        final Intent[] intents = new Intent[]{
                new Intent(context, SecurityAppLock.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
                new Intent(context, FakeSelectorActivitySecurity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
                new Intent(context, IntruderActivitySecurity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
                new Intent(context, SecuritySettings.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),

        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = (Integer) v.getTag();
                if (currentMenuIt == i) {
                    menu.close();
                } else {
                    if (intents[i] != null) {
                        context.startActivity(intents[i]);
                        ((Activity) context).overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                        currentMenuIt = i;
                        ((Activity) context).finish();

                    }

                    if(i==0){
                        Tracker.sendEvent(Tracker.ACT_LLIDE_MENU,Tracker.ACT_APPLOCK,Tracker.ACT_APPLOCK,1L);

                    }else if(i==1){
                        Tracker.sendEvent(Tracker.ACT_LLIDE_MENU,Tracker.ACT_FAKE,Tracker.ACT_FAKE,1L);


                    }else if(i==2){
                        Tracker.sendEvent(Tracker.ACT_LLIDE_MENU,Tracker.ACT_INTRUDE,Tracker.ACT_INTRUDE,1L);


                    }else if(i==3){
                        Tracker.sendEvent(Tracker.ACT_LLIDE_MENU,Tracker.ACT_SETTING_MENU,Tracker.ACT_SETTING_MENU,1L);


                    }

                }
            }
        };

        SecurityMyPref.pressMenu(SecurityMenu.MENU_LOCK_APP);
        for (int i = MENU_LOCK_APP; i < MENU_FAQ; ++i) {
            if (i == MENU_DAILY) {
                SecurityMyPref.pressMenu(SecurityMenu.MENU_DAILY);
            } else {
                View menuItem = inflater.inflate(R.layout.security_slide_menu_item, menuList, false);
                ImageView icon = (ImageView) menuItem.findViewById(R.id.icon);
                icon.setImageResource(icons[i]);
                icon.setColorFilter(currentMenuIt == i ? iconColorSelected : iconColorNormal);
                TextView text = (TextView) menuItem.findViewById(R.id.text);
                text.setText(menus[i]);
                menuItem.findViewById(R.id.blue).setVisibility(currentMenuIt == i ? View.VISIBLE : View.GONE);
//                text.setTextColor(currentMenuIt == i ? textColorSelected : textColorNormal);
                menuItem.findViewById(R.id.menu_it).setBackgroundResource(currentMenuIt==i?R.color.security_theme_change_bac:R.color.security_theme_primary);
                menuItem.setOnClickListener(listener);

                menuItem.setTag(i);
                menuList.addView(menuItem);
            }
        }


    }

}
