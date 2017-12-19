package com.security.manager.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.client.AndroidSdk;
import com.android.theme.internal.data.Theme;
import com.ivy.ivyshop.ShopMaster;
import com.ivymobi.applock.free.R;
import com.security.manager.App;
import com.security.manager.IntruderActivitySecurity;
import com.security.manager.PretentSelectorActivitySecurity;
import com.security.manager.SecurityAppLock;
import com.security.manager.SecuritySettings;
import com.security.manager.Tracker;
import com.security.manager.meta.SecurityMyPref;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huale on 2014/12/22.
 */
public class SecurityMenu {
    public static final int MENU_LOCK_APP = 0;
    public static final int MENU_SETTING = 9;
    public static final String FACEBOOK = "https://www.facebook.com/IvyAppLock";
    public static final String GOOGLE = "https://plus.google.com/u/0/communities/113134139742239607331";
    public static final String GOOGLEPLAY = "https://play.google.com/store/apps/developer?id=IVYMOBILE";


    public static final String[] newidkeys = {
            "nlk", "np", "nv", "npr", "nfk", "npl", "nt", "nf", "nh", "ns", "na", "nd"
    };

    public static int currentMenuIt = MENU_LOCK_APP;
    private static Context context;
    private static int show_theme;
    private static int show_fakecover;
    private static int show_intruder;
    private static int show_vault;
    static SlideMenu menu1;

    public static void attach(final SlideMenu menu, final View reddot) {
        menu1 = menu;
        context = menu.getContext();
        FrameLayout side_applcok = (FrameLayout) menu.findViewById(R.id.side_applcok);
        FrameLayout side_shop = (FrameLayout) menu.findViewById(R.id.side_shop);
        FrameLayout side_fakes = (FrameLayout) menu.findViewById(R.id.side_fakes);
        FrameLayout side_intruder = (FrameLayout) menu.findViewById(R.id.side_intruder);
        FrameLayout side_picture = (FrameLayout) menu.findViewById(R.id.side_picture);
        FrameLayout side_video = (FrameLayout) menu.findViewById(R.id.side_video);
        FrameLayout side_files = (FrameLayout) menu.findViewById(R.id.side_files);
        FrameLayout side_setting = (FrameLayout) menu.findViewById(R.id.side_setting);

        ImageView side_applcok_select = (ImageView) menu.findViewById(R.id.side_applcok_select);
        ImageView side_fakes_select = (ImageView) menu.findViewById(R.id.side_fakes_select);
        ImageView side_intruder_select = (ImageView) menu.findViewById(R.id.side_intruder_select);
        ImageView side_picture_select = (ImageView) menu.findViewById(R.id.side_picture_select);
        ImageView side_video_select = (ImageView) menu.findViewById(R.id.side_video_select);
        ImageView side_files_select = (ImageView) menu.findViewById(R.id.side_files_select);
        ImageView side_setting_select = (ImageView) menu.findViewById(R.id.side_setting_select);

        try {
            JSONObject jsonObject = new JSONObject(AndroidSdk.getExtraData());
            show_theme = jsonObject.getInt("show_theme");//主题商店
            show_fakecover = jsonObject.getInt("show_fakecover");//应用伪装
            show_intruder = jsonObject.getInt("show_intruder");//入侵者
            show_vault = jsonObject.getInt("show_vault");//保险箱
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (show_theme == 0) {
            side_shop.setVisibility(View.GONE);
        }
        if (show_fakecover == 0) {
            side_fakes.setVisibility(View.GONE);
        }
        if (show_intruder == 0) {
            side_intruder.setVisibility(View.GONE);
        }
        if (show_vault == 0) {
            side_picture.setVisibility(View.GONE);
            side_video.setVisibility(View.GONE);
            side_files.setVisibility(View.GONE);
        }
        switch (currentMenuIt) {
            case 0:
                side_applcok_select.setVisibility(View.VISIBLE);
                side_applcok.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
            case 2:
                side_fakes_select.setVisibility(View.VISIBLE);
                side_fakes.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
            case 3:
                side_intruder_select.setVisibility(View.VISIBLE);
                side_intruder.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
            case 4:
                side_picture_select.setVisibility(View.VISIBLE);
                side_picture.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
            case 5:
                side_video_select.setVisibility(View.VISIBLE);
                side_video.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
            case 6:
                side_files_select.setVisibility(View.VISIBLE);
                side_files.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
            case 7:
                side_setting_select.setVisibility(View.VISIBLE);
                side_setting.setBackgroundColor(context.getResources().getColor(R.color.A8));
                break;
        }

        side_applcok.setOnClickListener(onClickListener);
        side_shop.setOnClickListener(onClickListener);
        side_fakes.setOnClickListener(onClickListener);
        side_intruder.setOnClickListener(onClickListener);
        side_picture.setOnClickListener(onClickListener);
        side_video.setOnClickListener(onClickListener);
        side_files.setOnClickListener(onClickListener);
        side_setting.setOnClickListener(onClickListener);

        SecurityMyPref.pressMenu(SecurityMenu.MENU_LOCK_APP);
    }

    static View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_applcok:
                    if (currentMenuIt == 0) {
                        menu1.close();
                        return;
                    }
                    context.startActivity(new Intent(context, SecurityAppLock.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                    ((Activity) context).overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                    currentMenuIt = 0;
                    ((Activity) context).finish();
                    Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_APPLOCK, Tracker.ACT_APPLOCK, 1L);
                    break;
                case R.id.side_shop:
                    ShopMaster.launch(App.getContext(),
                            new Theme(R.raw.theme_preview, App.getContext().getPackageName()),
                            new Theme(R.raw.theme_preview_two, "theme_preview_two"));
                    Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.CATE_ACTION_OPEN_THEME, Tracker.CATE_ACTION_OPEN_THEME, 1L);
                    ((Activity) context).overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                    break;
                case R.id.side_fakes:
                    if (currentMenuIt == 2) {
                        menu1.close();
                        return;
                    }
                    Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_FAKE, Tracker.ACT_FAKE, 1L);
                    context.startActivity(new Intent(context, PretentSelectorActivitySecurity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                    ((Activity) context).overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                    currentMenuIt = 2;
                    ((Activity) context).finish();
                    break;
                case R.id.side_intruder:
                    if (currentMenuIt == 3) {
                        menu1.close();
                        return;
                    }
                    Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_INTRUDE, Tracker.ACT_INTRUDE, 1L);
                    context.startActivity(new Intent(context, IntruderActivitySecurity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                    ((Activity) context).overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                    currentMenuIt = 3;
                    ((Activity) context).finish();
                    break;
                case R.id.side_picture:
                    if (currentMenuIt == 4) {
                        menu1.close();
                        return;
                    }
                    currentMenuIt = 4;
                    break;
                case R.id.side_video:
                    if (currentMenuIt == 5) {
                        menu1.close();
                        return;
                    }
                    currentMenuIt = 5;
                    break;
                case R.id.side_files:
                    if (currentMenuIt == 6) {
                        menu1.close();
                        return;
                    }
                    currentMenuIt = 6;
                    break;
                case R.id.side_setting:
                    if (currentMenuIt == 7) {
                        menu1.close();
                        return;
                    }
                    context.startActivity(new Intent(context, SecuritySettings.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                    ((Activity) context).overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
                    currentMenuIt = 7;
                    ((Activity) context).finish();
                    Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_SETTING_MENU, Tracker.ACT_SETTING_MENU, 1L);
                    break;
            }
        }
    };
}
