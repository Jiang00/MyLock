package com.vactorapps.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.mydb.PreData;
import com.vactorapps.manager.page.SlideMenu;
import com.vactorapps.manager.page.VacMenu;
import com.vactorappsapi.manager.lib.BaseActivity;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

//import com.android.client.AndroidSdk;


/**
 * Created by superjoy on 2014/9/11.
 */
public abstract class BaseAbsActivity extends BaseActivity implements SearchThread.OnSearchResult {

    public BaseAbsActivity context;
    boolean invisible;
    private AnimatorListenerAdapter invis2visAnimatorListener;
    private AlertDialog dialog1;


    @Override
    protected void onPause() {
        super.onPause();
        invisible = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        invisible = false;

    }

    protected void onIntent(Intent intent) {

    }

    protected void tips() {
        if (VacPref.hasIntruder()) {
            this.finish();
            VacMenu.currentMenuIt = 2;
            IntruderPresenter.show();
            overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
            VacPref.setHasIntruder(false);
        }
    }


    static class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        boolean crashing = false;

        String versionName;

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            try {
                if (crashing) return;
                crashing = true;
                StringWriter sw = new StringWriter();
                sw.append(thread.toString());
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
//                Tracker.sendEvent(Tracker.CATE_EXCEPTION, Tracker.ACT_CRASH + versionName, sw.toString(), 0L);
                defaultHandler.uncaughtException(thread, ex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private MyUncaughtExceptionHandler exceptionHandler = new MyUncaughtExceptionHandler();

    public static void showSoftKeyboard(Activity activity, View view, boolean show) {
        if (show) {
            if (view != null && view.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View viewById = activity.findViewById(android.R.id.content);
            if (viewById != null)
                imm.hideSoftInputFromWindow(viewById.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    protected abstract boolean hasHelp();

    public static final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        try {
            exceptionHandler.setVersionName(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        try {
            initNow();
        } catch (Error e) {
            e.printStackTrace();
        }
//        Sketch.with(this).getConfiguration().setMobileNetworkGlobalPauseDownload(false).setDefaultImageDisplayer(new TransitionImageDisplayer());
//        Sketch.setDebugMode(true);


    }

    @Override
    protected void onDestroy() {
        context = null;
        super.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("menu", VacMenu.currentMenuIt);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        VacMenu.currentMenuIt = savedInstanceState.getInt("menu", VacMenu.MENU_LOCK_APP);
    }

    protected void initNow() {
        AndroidSdk.onCreate(this);
        setupView();

    }

    @Override
    protected void onResume() {
        try {
            AndroidSdk.onResumeWithoutTransition(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    protected int getBackImage() {
        return R.drawable.security_slide_menu;
    }

    SlideMenu menu;
//    View redDot;

    protected void setup(int titleId) {
        ButterKnife.inject(this);
//
//        if (upgrade != null) {
//            if (AdConfig.hasDaily.getValue()) {
//                upgrade.setVisibility(View.VISIBLE);
//                File image = new File(VacImgManager.CACHE_ROOT + AdConfig.DAILY_IMAGE_CACHE_KEY);
//                if (image.exists()) {
//                    upgrade.setImageURI(Uri.fromFile(new File(VacImgManager.CACHE_ROOT + AdConfig.DAILY_ICON_CACHE_KEY)));
//                }
//            }
//        }
        TextView title = (TextView) findViewById(R.id.title);
        LinearLayout imageicon = (LinearLayout) findViewById(R.id.title_icon);
        title.setText(titleId);
//        redDot = findViewById(R.id.reddot);
        int backImage = getBackImage();
        if (VacPref.hasReddot() && backImage == R.drawable.security_slide_menu) {
//            redDot.setVisibility(View.VISIBLE);
        }
        imageicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menu != null && !menu.isLock()) {
                    if (menu.getStatus() == SlideMenu.Status.Close)
                        menu.open();
                    else
                        menu.close();
                } else
                    onBackPressed();
            }
        });
//        title.setBackgroundResource(R.drawable.ic_top_bar_category);
//        title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(backImage), null, null, null);

        EditText searchEditor = (EditText) findViewById(R.id.search_edit_text);
        if (searchEditor != null) {
            searchEditor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (search)
                        searchThread.waittingForSearch(editable.toString(), getSearchList(), BaseAbsActivity.this);
                }
            });
            findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleEditAnimation();
                }
            });
            findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        findViewById(R.id.edit_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterEditMode();
            }
        });
        menu = (SlideMenu) findViewById(R.id.menu);
        if (menu != null) {
            if (!hasHelp()) {
                menu.setLockRight(true);
                findViewById(R.id.help).setVisibility(View.GONE);
            } else {
                findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menu.openHelp();
                    }
                });
            }

            VacMenu.attach(menu, null);
        } else {
            findViewById(R.id.help).setVisibility(View.GONE);
        }

    }


    public List<SearchThread.SearchData> getSearchList() {
        return new ArrayList<>();
    }

    @Override
    public void onResult(ArrayList<SearchThread.SearchData> list) {

    }

    public void enterEditMode() {
        View view = findViewById(R.id.edit_mode);
        if (!edit) {
            edit = true;
            findViewById(R.id.bottom_action_bar).setVisibility(View.VISIBLE);
            view.setSelected(true);
            onEditMode(true);
        } else
            onBackPressed();
    }

    public void backHome() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(setIntent);
    }

    //退出
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showExitDialog() {
        View view = View.inflate(BaseAbsActivity.this, R.layout.dialog_exit2, null);
        TextView main_bad = (TextView) view.findViewById(R.id.main_bad);
        TextView main_good = (TextView) view.findViewById(R.id.main_good);
        main_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                finish();
            }
        });
        main_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        dialog1 = new AlertDialog.Builder(this, R.style.dialog).create();

        dialog1.getWindow().setWindowAnimations(R.style.dialog_animation);
        dialog1.getWindow().setGravity(Gravity.CENTER);
        dialog1.show();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        WindowManager.LayoutParams lp = dialog1.getWindow().getAttributes();
        lp.width = dm.widthPixels; //设置宽度
        lp.height = dm.heightPixels; //设置高度
        int uiOptions =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        dialog1.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        dialog1.getWindow().setAttributes(lp);
        dialog1.getWindow().setContentView(view);
    }

    protected void askForExit() {
        showExitDialog();
        String tag = "ad_interval_minute2";
        long lastPopAdTime = PreData.getDB(this, tag, 0l);
        if (lastPopAdTime == -1) {
            PreData.putDB(this, tag, 0l);
            return;
        }
        long l = System.currentTimeMillis();
        if (lastPopAdTime == 0) {
            Log.e("chfq", "====lastPopAdTime=0====");
            AndroidSdk.showFullAd("exit_full", null);
            PreData.putDB(this, tag, l);
            return;
        }
        try {
            long minute = new JSONObject(AndroidSdk.getExtraData()).optLong("show_interval_time");
            Log.e("chfq", "minute=" + minute);
            if (l - lastPopAdTime >= minute * 60 * 1000) {
                Log.e("chfq", "====l - lastPopAdTime=====");
                AndroidSdk.showFullAd("exit_full", null);
                PreData.putDB(this, tag, l);
            }
        } catch (Exception e) {
        }

//        super.onBackPressed();

    }


    boolean search = false;
    public boolean edit = false;
    public static SearchThread searchThread = new SearchThread();

    static {
        searchThread.start();
    }

    protected void onEditMode(boolean show) {
    }

    protected void onSearchExit() {
    }

    static final Interpolator acc = new AccelerateInterpolator();
    static final Interpolator dec = new DecelerateInterpolator();

    void toggleEditAnimation() {
//        if (!search && VacPref.hasReddot()) {
//            redDot.setVisibility(View.GONE);
//        }

        final View searchView = findViewById(R.id.search_container);
        View normalView = findViewById(R.id.normal_bar);

        final View visibleView, invisibleView;
        if (searchView.getVisibility() == View.GONE) {
            visibleView = normalView;
            invisibleView = searchView;
        } else {
            visibleView = searchView;
            invisibleView = normalView;
            showSoftKeyboard(BaseAbsActivity.this, null, false);
        }

        final ObjectAnimator invis2vis = ObjectAnimator.ofFloat(invisibleView, "rotationY", -90, 0);
        invis2vis.setDuration(500);
        invis2vis.setInterpolator(dec);
        ObjectAnimator vis2invis = ObjectAnimator.ofFloat(visibleView, "rotationY", 0, 90);
        vis2invis.setDuration(500);
        vis2invis.setInterpolator(acc);

        vis2invis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                visibleView.setVisibility(View.GONE);
                invisibleView.setVisibility(View.VISIBLE);

                if (search) {
                    if (VacPref.hasReddot()) {
                        if (invis2visAnimatorListener == null) {
                            invis2visAnimatorListener = new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
//                                    if (VacPref.hasReddot() && getBackImage() == R.drawable.ic_top_bar_category) {
//                                        redDot.setVisibility(View.VISIBLE);
//                                    }
                                }
                            };
                        }
                        invis2vis.addListener(invis2visAnimatorListener);
                    }
                    onSearchExit();
                    search = false;
                } else {
                    EditText search_text = (EditText) findViewById(R.id.search_edit_text);
                    search_text.setText("");
                    showSoftKeyboard(context, search_text, true);
                    search = true;
                }

                invis2vis.start();
            }
        });
        vis2invis.start();
    }

    @Override
    public void onBackPressed() {
        if (search) {
            toggleEditAnimation();
        } else if (edit) {
            exitEditMode();
        } else if (menu != null && !menu.isLock()) {
            Log.e("value", "close1");
            SlideMenu.Status status = menu.getStatus();
            if (status == SlideMenu.Status.Close) menu.open();

            else if (status == SlideMenu.Status.OpenRight) {
                menu.close();
                Log.e("value", "close2");

            } else
                askForExit();
            Log.e("value", "close3");

        } else
            super.onBackPressed();
        Log.e("value", "close4");

    }

    protected void exitEditMode() {
        findViewById(R.id.edit_mode).setSelected(false);
        findViewById(R.id.bottom_action_bar).setVisibility(View.GONE);
        edit = false;
        onEditMode(false);
    }

    protected void setViewVisible(int type, int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(type);
        }
    }


    public abstract void setupView();
}