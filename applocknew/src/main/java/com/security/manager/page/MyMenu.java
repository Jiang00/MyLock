package com.security.manager.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.privacy.lock.*;
import com.security.manager.AppLock;
import com.security.manager.FakeSelectorActivity;
import com.security.manager.IntruderActivity;
import com.security.manager.meta.Pref;
import com.security.manager.Settings;

import java.util.Locale;

/**
 * Created by huale on 2014/12/22.
 */
public class MyMenu {
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
            R.drawable.security_intrude_infomation
//            R.drawable.daily
    };

//    public static int menus[] = {
//            R.string.lock_tab, R.string.hide_pic, R.string.hide_video, R.string.profile, R.string.fake, R.string.plugin, R.string.theme, R.string.hide_file,
//            R.string.hide_app, R.string.setting_tab, R.string.daily_app
//    };

    public static int menus[] = {
            R.string.lock_tab, R.string.fake,R.string.intruder,
             R.string.setting_tab
    };

    public static byte[] seperator = {
            MENU_HIDE_APP, -1
    };

    public static int currentMenuIt = MENU_LOCK_APP;
    static int langIdx = 0;

    public static void attach(final DragLayout menu, final View reddot) {
        LinearLayout menuList = (LinearLayout) menu.findViewById(R.id.menu_its);
        final Context context = menu.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        Resources resources = context.getResources();
        int iconColorSelected = resources.getColor(R.color.navdrawer_icon_tint_selected);
        int iconColorNormal = resources.getColor(R.color.navdrawer_icon_tint);
        int textColorSelected = resources.getColor(R.color.navdrawer_text_color_selected);
        int textColorNormal = resources.getColor(R.color.navdrawer_text_color);

        final Intent[] intents = new Intent[]{
                new Intent(context, AppLock.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS).putExtra("hide", false),
//                new Intent(context, ExploreFolder.class).putExtra("normal", false).putExtra("video", false).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
//                new Intent(context, ExploreFolder.class).putExtra("normal", false).putExtra("video", true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
//                new Intent(context, Profiles.class),
                new Intent(context, FakeSelectorActivity.class),
                new Intent(context, IntruderActivity.class),


//                new Intent(context, PluginList.class),
//                new Intent(context, Themes.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
//                new Intent(context, ExploreCommonFolder.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
//                new Intent(context, AppLock.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS).putExtra("hide", true),
                new Intent(context, Settings.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
//                new Intent(context, About.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
                null
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = (Integer) v.getTag();
                if (currentMenuIt == i) {
                    menu.close();
                } else {
                    Context context = v.getContext();
                    if (!Pref.isMenuPressed(i)) {
                        Pref.pressMenu(i);
                        v.findViewById(R.id.red).setVisibility(View.GONE);
                        if (!Pref.hasReddot()) {
                            reddot.setVisibility(View.GONE);
                        }
                    }
                    if (i == MENU_THEME) {
                        context.startActivity(intents[i]);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else if (i == MENU_DAILY) {
//                        Utils.openPlayStore(context, App.getSharedPreferences().getString(ServerData.KEY_DAILY_MENU_URL, context.getString(R.string.website)));
                    } else {
                        if (intents[i] != null) {
                            context.startActivity(intents[i]);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            currentMenuIt = i;
                            ((Activity) context).finish();
                        }
                    }
//                    MyTracker.sendEvent(MyTracker.CATE_MENU, acts[i], acts[i], 1L);
                }
            }
        };

        int j = 0;
        Pref.pressMenu(MyMenu.MENU_LOCK_APP);
        for (int i = MENU_LOCK_APP; i < MENU_FAQ; ++i) {
            if (i == MENU_DAILY) {
                Pref.pressMenu(MyMenu.MENU_DAILY);
            } else {
                View menuItem = inflater.inflate(R.layout.security_slide_menu_item, menuList, false);
                ImageView icon = (ImageView) menuItem.findViewById(R.id.icon);
                icon.setImageResource(icons[i]);
                icon.setColorFilter(currentMenuIt == i ? iconColorSelected : iconColorNormal);
                TextView text = (TextView) menuItem.findViewById(R.id.text);
                text.setText(menus[i]);
//                text.setTextColor(currentMenuIt == i ? textColorSelected : textColorNormal);
                menuItem.setOnClickListener(listener);
                if (Pref.isMenuPressed(i)) {
                    menuItem.findViewById(R.id.red).setVisibility(View.GONE);
                }
                menuItem.setTag(i);
                menuList.addView(menuItem);
            }

            if (i == seperator[j]) {
                View sep = inflater.inflate(R.layout.security_slide_menu_line, menuList, false);
                menuList.addView(sep);
                ++j;
            }
        }

//        if (Pref.isAdvanceEnabled()) {
//            View adv = inflater.inflate(R.layout.mat_menu_it, menuList, false);
//            setUninstallMenu(adv);
//            menuList.addView(adv);
//            advanceView = adv;
//        }

//        View faq = menu.findViewById(R.id.faq);
//        if (Pref.isMenuPressed(MENU_FAQ)) {
//            faq.findViewById(R.id.red).setVisibility(View.GONE);
//        }
//        faq.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!Pref.isMenuPressed(MyMenu.MENU_FAQ)) {
//                    Pref.pressMenu(MyMenu.MENU_FAQ);
//                    v.findViewById(R.id.red).setVisibility(View.INVISIBLE);
//                    if (!Pref.hasReddot()) {
//                        reddot.setVisibility(View.GONE);
//                    }
//                }
//                Context context = v.getContext();
//                MyTracker.sendEvent(MyTracker.CATE_MENU, MyTracker.ACT_FAQ_MENU, MyTracker.ACT_FAQ_MENU, 1L);
//                View vv = LayoutInflater.from(context).inflate(R.layout.faq_layout, null, false);
//                new AlertDialog.Builder(context).setTitle(R.string.help_help).setView(vv).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).create().show();
//            }
//        });

//        Spinner langspinner = (Spinner) menu.findViewById(R.id.langs);
//        langspinner.setAdapter(new ArrayAdapter<>(context, R.layout.lang_it, new String[]{context.getResources().getString(R.string.system), "English"}));
//        langspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                /**
//                 * @design Well, English's position is 1, System's position is 0, I defined it
//                 */
//                if (Pref.isEnglish()) {
//                    if (position == 1) {
//                        return;
//                    }
//                } else {
//                    if (position == 0) return;
//                }
//                selectLanguage((Activity) context, position == 1);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        langspinner.setSelection(Pref.isEnglish() ? 1 : 0);
    }


    public static void selectLanguage(Activity context, boolean english) {
        Configuration cfg = context.getResources().getConfiguration();
        if (english)
            cfg.locale = Locale.ENGLISH;
        else
            cfg.locale = Locale.getDefault();
        Pref.selectLanguage(english);
        context.getResources().updateConfiguration(cfg, context.getResources().getDisplayMetrics());

        context.recreate();
    }

    static View advanceView;


    public static void removeUninstallMenu(DragLayout menu) {
        LinearLayout menuList = (LinearLayout) menu.findViewById(R.id.menu_its);
        if (advanceView != null)
            menuList.removeView(advanceView);
    }


}
