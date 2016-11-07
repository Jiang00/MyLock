package com.privacy.lock;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.privacy.lock.intf.IThemeBridge;
import com.privacy.lock.meta.Daily;
import com.privacy.lock.meta.OverflowMenu;
import com.privacy.lock.meta.Pref;
import com.privacy.lock.meta.ThemeBridge;
import com.privacy.lock.view.MessageBox;

/**
 * Created by huale on 2015/2/2.
 */
public class ThemeBridgeImpl implements IThemeBridge {
    boolean unlockSelf;
    Context context;
    Drawable icon;
    //有SYSTEM_ALERT_WINDOW的权限，小米系列默认禁止此权限
    boolean hasPermission;
    boolean unlockMe;
    String pkgName;
    CharSequence appName;
    Object[] addedToWindow = new Object[MENU_IDX_COUNT];

    static final ThemeBridgeImpl ins = new ThemeBridgeImpl();

    public static void reset(Context context, boolean unlockSelf, boolean hasPermission, String pkgName) {
        pkgName = pkgName == null ? context.getPackageName() : pkgName;
        ins.unlockSelf = unlockSelf;
        ins.context = context;
        ins.pkgName = pkgName;
        ins.icon = ins.getIcon(pkgName);
        ThemeBridge.bridge = ins;
        ins.hasPermission = hasPermission;
    }

    public Drawable getIcon(String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo pi = packageManager.getPackageInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            appName = pi.applicationInfo.loadLabel(packageManager);
            return pi.applicationInfo.loadIcon(packageManager);
        } catch (Exception e) {
            appName = context.getResources().getString(R.string.app_name);
            return context.getResources().getDrawable(R.drawable.icon);
        }
    }

    @Override
    public CharSequence appName() {
        return appName;
    }

    @Override
    public Drawable icon() {
        return icon;
    }

    @Override
    public boolean check(String passwd, boolean normal) {
        if (Pref.checkPasswd(passwd, normal)) {
            if (context instanceof Activity) {
                ((PatternActivity) context).unlockSuccess(unlockMe);
            } else {
                ((Worker) context).unlockSuccess(unlockMe);
                SharedPreferences sp = App.getSharedPreferences();
                if (sp.getInt(Pref.PREF_BRIEF_SLOT, Pref.PREF_DEFAULT) == Pref.PREF_BRIEF_AFTER_SCREEN_OFF
                        && !sp.contains("PREF_BRIEF_AFTER_SCREEN_OFF")) {
                    MessageBox.Data data = new MessageBox.Data();
                    data.alert = true;
                    data.title = R.string.brief_exit;
                    data.msg = R.string.brief_exit_screen_off;
                    MessageBox.show_(context, data);
                    sp.edit().putBoolean("PREF_BRIEF_AFTER_SCREEN_OFF", true).apply();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Resources res() {
        return context.getResources();
    }

    @Override
    public int resId(String name, String type) {
        return res().getIdentifier(name, type, context.getPackageName());
    }

    @Override
    public boolean random() {
        return App.getSharedPreferences().getBoolean("random", false);
    }

    OverflowMenu[] menus;
    public static final int MENU_IDX_ALL = -1;
    public static final int MENU_IDX_BRIEF = 0;
    public static final int MENU_IDX_UNLOCKME = 1;
//    public static final int MENU_IDX_FORGET = 2;
    public static final int MENU_IDX_THEME = 2;
    public static final int MENU_IDX_TOGGLE = 3;
    public static final int MENU_IDX_COUNT = 4;

    public void detachFromWindow(int idx) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (idx == MENU_IDX_ALL) {
            for (int i = 0; i < MENU_IDX_COUNT; ++i) {
                detatchSingleView(i, wm);
            }
        } else {
            detatchSingleView(idx, wm);
        }
    }

    public void detatchSingleView(int idx, WindowManager wm) {
        try {
            if (addedToWindow[idx] instanceof View) {
                wm.removeViewImmediate((View) addedToWindow[idx]);
            } else {
                ((AlertDialog) addedToWindow[idx]).dismiss();
            }
        } catch (Exception ignore) {

        } finally {
            addedToWindow[idx] = null;
        }
    }

    @Override
    public OverflowMenu[] menus() {
        if (menus == null) {
            /*
            OverflowMenu forget = new OverflowMenu() {
                @Override
                public OverflowMenu init() {
                    title = R.string.forgot_passwd;
                    return this;
                }

                @Override
                public void onClick(View v) {
                    MyTracker.sendEvent(MyTracker.CATE_OVERFLOW_MENU, MyTracker.ACT_OVERFLOW_MENU_FORGET_PASSWD, MyTracker.ACT_OVERFLOW_MENU_FORGET_PASSWD, 1L);
                    if (hasPermission) {
                        /*
                        View resetView = ResetPasswd.getView(context, true, ((Worker)context).handler, new ResetPasswd.IResetListener() {
                            @Override
                            public void onSuccess() {
                                ((Worker)context).hideAlertIfPossible(false);
                                detachFromWindow(MENU_IDX_FORGET);
                            }
                        });
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                detachFromWindow(MENU_IDX_FORGET);
                            }
                        };
                        resetView.findViewById(R.id.back).setOnClickListener(listener);
                        MyFrameLayout layout = new MyFrameLayout(context);
                        layout.setOnBackListener(listener);
                        layout.addView(resetView);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, PixelFormat.TRANSLUCENT);
                        lp.gravity = Gravity.CENTER;
                        lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).addView(layout, lp);
                        */
            /*
                        AlertDialog dialog = ResetPattern.showDialog(context, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActi*vity(new Intent(context, ResetPattern.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("direct-confirm", true));
                                ((Worker) context).hideAlertIfPossible(false);
                                detachFromWindow(MENU_IDX_FORGET);
                            }
                        }, null, hasPermission);
                        addedToWindow[MENU_IDX_FORGET] = dialog;
                    } else {
                        context.startActivity(new Intent(context, ResetPattern.class));
                    }
                }
            }.init();
*/
            OverflowMenu theme = new OverflowMenu() {
                @Override
                public OverflowMenu init() {
                    title = R.string.theme;
                    return this;
                }

                @Override
                public void onClick(View dummy) {
                    ThemeBridge.requestTheme = true;
                    ThemeBridge.needUpdate = false;
                    MyTracker.sendEvent(MyTracker.CATE_OVERFLOW_MENU, MyTracker.ACT_OVERFLOW_MENU_THEME, MyTracker.ACT_OVERFLOW_MENU_THEME, 1L);
//                    if (hasPermission) {
//                        View themeView = Themes.getView(context, ((Worker) context).handler, new Runnable() {
//                            @Override
//                            public void run() {
//                                ((Worker) context).hideAlertImmediate();
//                                detachFromWindow(MENU_IDX_THEME);
//                            }
//                        });
//                        View.OnClickListener listener = new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (ThemeBridge.needUpdate) {
//                                    ((Worker) context).hideAlertImmediate();
//                                }
//                                detachFromWindow(MENU_IDX_THEME);
//                            }
//                        };
//                        TextView title = (TextView) themeView.findViewById(R.id.title);
//                        title.setOnClickListener(listener);
//                        title.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_action_back_dark), null, null, null);
//                        MyFrameLayout layout = new MyFrameLayout(context);
//                        layout.setOnBackListener(listener);
//                        layout.addView(themeView);
//                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
//                                WindowManager.LayoutParams.MATCH_PARENT,
//                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, PixelFormat.TRANSLUCENT);
//                        lp.gravity = Gravity.CENTER;
//                        lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).addView(layout, lp);
//                        addedToWindow[MENU_IDX_THEME] = layout;
//                    } else {
//                        context.startActivity(new Intent(context, Themes.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    }
                }
            }.init();

            OverflowMenu brief = new OverflowMenu() {
                @Override
                public OverflowMenu init() {
                    title = R.string.overf_brief;
                    return this;
                }

                @Override
                public void onClick(View v) {
                    try {
                        int idx = App.getSharedPreferences().getInt(Pref.PREF_BRIEF_SLOT, Pref.PREF_DEFAULT);
                        final String[] stringArray = context.getResources().getStringArray(R.array.brief_slot);
                        AlertDialog d = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(context.getResources().getString(R.string.overf_brief)).setSingleChoiceItems(stringArray, idx, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                App.getSharedPreferences().edit().putInt(Pref.PREF_BRIEF_SLOT, i).commit();
                                MyTracker.sendEvent(MyTracker.CATE_OVERFLOW_MENU, MyTracker.ACT_OVERFLOW_MENU_BRIEF, stringArray[i], 1L);
                                dialogInterface.dismiss();
                            }
                        }).create();
                        Log.e("haha", "ehihei " + context);
                        if (hasPermission) {
                            d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        }
                        d.show();
                        addedToWindow[MENU_IDX_BRIEF] = d;
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            }.init();

            final OverflowMenu unlockMeMenu = new OverflowMenu() {
                @Override
                public OverflowMenu init() {
                    title = R.string.overf_unlock_me;
                    checkable = true;
                    return this;
                }

                @Override
                public void onClick(View v) {
                    checked = !checked;
                    unlockMe = checked;
                    if (checked) {
                        MessageBox.Data data = new MessageBox.Data();
                        data.title = R.string.overf_unlock_me;
                        data.msg = R.string.unlock_me;
                        data.alert = hasPermission;
                        AlertDialog d = MessageBox.show_(context, data);
                        addedToWindow[MENU_IDX_UNLOCKME] = d;
                        MyTracker.sendEvent(MyTracker.CATE_OVERFLOW_MENU, MyTracker.ACT_OVERFLOW_MENU_UNLOCK_ME, MyTracker.ACT_OVERFLOW_MENU_UNLOCK_ME, 1L);
                    }
                }
            }.init();

            menus = new OverflowMenu[]{brief, unlockMeMenu, /*forget, */theme};
        }

        if (unlockSelf) {
            return new OverflowMenu[]{/*menus[2], */menus[2]};
        } else {
            unlockMe = false;
            menus[1].checked = false;
            return menus;
        }
    }

    @Override
    public Daily daily() {
//        Daily d = new Daily();
//        SharedPreferences sp = App.getSharedPreferences();
//        d.has = sp.getBoolean(ServerData.KEY_DAILY_UNLOCK, false);
//        d.unread = sp.getBoolean(ServerData.KEY_DAILY_UNLOCK_NEW, false);
//        d.iconUrl = ServerData.KEY_DAILY_ICON_PREF_KEY;
//        d.iconPersistentUrl = ServerData.KEY_DAILY_ICON_PERSISTENT;
//        return d;

        return  null;
    }

    @Override
    public void visitDaily(boolean persistent) {
//        MyTracker.sendEvent(MyTracker.CATE_DEFAULT, MyTracker.ACT_DLY_UNLOCK, MyTracker.ACT_DLY_UNLOCK, 1L);
//        if (persistent) {
//            menus[MENU_IDX_THEME].onClick(null);
//        } else {
//            SharedPreferences sp = App.getSharedPreferences();
//            sp.edit().putBoolean(ServerData.KEY_DAILY_UNLOCK_NEW, false).commit();
//            if (hasPermission) {
//                ((Worker) context).hideAlertIfPossible(false);
////                detachFromWindow(MENU_IDX_THEME);
//            }
//            String url = sp.getString(ServerData.KEY_DAILY_UNLOCK_URL, context.getPackageName());
//            /**
//             * @design if url has https:// http:// ftp:// etc. will use MoreGameWeb.show()
//             * otherwise use google play
//             */
//            if (!url.contains("://")) {
//                Tools.openPlayStore(context, url);
//            } else {
//                MoreGame.show(context, url);
//            }
//        }
    }

    @Override
    public void toggle(boolean normal) {
        if (unlockSelf) {
            ((PatternActivity) context).toggle(normal);
        }
    }

    @Override
    public boolean hasPattern() {
        return unlockSelf && Pref.isPasswdSet(false);
    }

    @Override
    public boolean hasPasswd() {
        return unlockSelf && Pref.isPasswdSet(true);
    }

    @Override
    public void back() {
        if (!hasPermission || context instanceof Activity) {
            ((PatternActivity) context).onBackPressed();
        } else {
            ((Worker) context).backHome();
        }
    }

    @Override
    /**
     * 无用
     */
    public void switchTheme() {

    }

    @Override
    public String currentPkg() {
        return pkgName;
    }

    public static void clear() {
        ins.context = null;
        ins.icon = null;
    }
}
