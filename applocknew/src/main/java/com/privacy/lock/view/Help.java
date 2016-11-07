package com.privacy.lock.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.privacy.lock.*;
import com.privacy.lock.async.ImageManager;
import com.privacy.lock.meta.HelpApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by huale on 2015/1/6.
 */
public class Help {
    static String dailyApp = "";
    public static final String HELP_PREFIX = "help.";

    static final View.OnClickListener share_bar_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            switch (v.getId()){
                case R.id.share:
                    Resources resources = context.getResources();
                    String title = resources.getString(R.string.share_title);
                    String msg = resources.getString(R.string.share_msg, Share.GOOGLE_PALY_URL + context.getPackageName());
                    Share.share(context, title, msg);
                    MyTracker.sendEvent(MyTracker.CATE_HELP, MyTracker.ACT_SHARE, MyTracker.ACT_SHARE, 1L);
                    break;

                case R.id.rate:
                    Share.rate(context);
                    MyTracker.sendEvent(MyTracker.CATE_HELP, MyTracker.ACT_RATE, MyTracker.ACT_RATE, 1L);
                    break;

                case R.id.support:
                    Uri uri = Uri.parse("mailto:" + context.getString(R.string.support_email));
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri);
                    try {
                        context.startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    MyTracker.sendEvent(MyTracker.CATE_HELP, MyTracker.ACT_SUPPORT_EMAIL, MyTracker.ACT_SUPPORT_EMAIL, 1L);
                    break;

                case R.id.daily_app:
                    Tools.openPlayStore(context, dailyApp);
                    MyTracker.sendEvent(MyTracker.CATE_HELP, MyTracker.ACT_DAILY_APP, dailyApp, 1L);
                    break;
            }
        }
    };

    static final ArrayList<HelpApp> apps = new ArrayList<HelpApp>();
    static int lastInitTime;
    public static void init(Context context){
        if (System.currentTimeMillis()/1000L - lastInitTime > 1800){
            lastInitTime = (int) (System.currentTimeMillis()/1000L);
            apps.clear();
            HelpApp defapp = new HelpApp();
            defapp.pkgName = "help.com.privacylock.christmas";
            defapp.icon = R.drawable.icon;
            apps.add(defapp);

//            Set<String> appset = App.getSharedPreferences().getStringSet(ServerData.KEY_HELP, null);
            Set<String> appset=null;
            if (appset != null){
                for(String app : appset){
                    if (ImageManager.isImageExists(app)){
                        HelpApp ha = new HelpApp();
                        ha.pkgName = app;
                        apps.add(ha);
                    }
                }
            }

            int size = apps.size();
            Collections.shuffle(apps);
            randomIdx = (byte) (Math.random() * size);
        }
    }


    static byte randomIdx = 0;

    /**
     *
     * @param sharebar
     * @param help
     * @param ids icon, need color filter, description, ...
     */
    public static void attach(LinearLayout sharebar, LinearLayout help, int ... ids){
        Context context = help.getContext();

        sharebar.findViewById(R.id.support).setOnClickListener(share_bar_listener);
        sharebar.findViewById(R.id.share).setOnClickListener(share_bar_listener);
        sharebar.findViewById(R.id.rate).setOnClickListener(share_bar_listener);
        View daily = help.findViewById(R.id.daily_app);
        daily.setOnClickListener(share_bar_listener);
        ImageView app_icon = (ImageView) daily.findViewById(R.id.app_icon);
        int size = apps.size();
        if (++randomIdx >= size){
            randomIdx = 0;
        }
        HelpApp helpApp = apps.get(randomIdx);
        helpApp.show(app_icon);
        /**
         * @design
         *  because the help package name has a "help." prefix to distinguish the package name that used by the theme
         *
         *  therefore the real package name will strip the prefix
         */
        dailyApp = helpApp.pkgName.substring(HELP_PREFIX.length());

        LayoutInflater inflater = LayoutInflater.from(context);

        int color = context.getResources().getColor(R.color.navdrawer_icon_tint_selected);

        int i=0;
        while(true){
            View view = inflater.inflate(R.layout.help_it, help, false);
            TextView tv = (TextView) view.findViewById(R.id.text);
            tv.setText(ids[i+2]);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(ids[i]);
            if (ids[i+1] == 1){
                icon.setColorFilter(color);
            }
            help.addView(view);
            i += 3;
            if (i<ids.length){
                View sep = inflater.inflate(R.layout.mat_menu_seperator, help, false);
                help.addView(sep);
            } else {
                break;
            }
        }
    }
}
