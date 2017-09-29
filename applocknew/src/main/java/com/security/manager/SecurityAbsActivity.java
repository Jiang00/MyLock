package com.security.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.client.AndroidSdk;
import com.ivymobi.applock.free.R;
import com.security.manager.lib.BaseActivity;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.SlideMenu;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

//import com.android.client.AndroidSdk;


/**
 * Created by superjoy on 2014/9/11.
 */
public abstract class SecurityAbsActivity extends BaseActivity implements SearchThread.OnSearchResult {

    public SecurityAbsActivity context;
    boolean invisible;
    private AnimatorListenerAdapter invis2visAnimatorListener;


    @Override
    protected void onPause() {
        super.onPause();
        invisible = true;
    }

    protected void onIntent(Intent intent) {

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        invisible = false;

    }

    protected void tips() {
        if (SecurityMyPref.hasIntruder()) {
            this.finish();
            SecurityMenu.currentMenuIt = 2;
            SecurityIntruderPresenter.show();
            overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
            SecurityMyPref.setHasIntruder(false);
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
        outState.putInt("menu", SecurityMenu.currentMenuIt);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        SecurityMenu.currentMenuIt = savedInstanceState.getInt("menu", SecurityMenu.MENU_LOCK_APP);
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
//                File image = new File(SecurityImgManager.CACHE_ROOT + AdConfig.DAILY_IMAGE_CACHE_KEY);
//                if (image.exists()) {
//                    upgrade.setImageURI(Uri.fromFile(new File(SecurityImgManager.CACHE_ROOT + AdConfig.DAILY_ICON_CACHE_KEY)));
//                }
//            }
//        }
        TextView title = (TextView) findViewById(R.id.title);
        LinearLayout imageicon = (LinearLayout) findViewById(R.id.title_icon);
        title.setText(titleId);
//        redDot = findViewById(R.id.reddot);
        int backImage = getBackImage();
        if (SecurityMyPref.hasReddot() && backImage == R.drawable.security_slide_menu) {
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
                        searchThread.waittingForSearch(editable.toString(), getSearchList(), SecurityAbsActivity.this);
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

            SecurityMenu.attach(menu, null);
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

    protected void askForExit() {
        AndroidSdk.showFullAd(AndroidSdk.FULL_TAG_PAUSE, null);
        super.onBackPressed();

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
//        if (!search && SecurityMyPref.hasReddot()) {
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
            showSoftKeyboard(SecurityAbsActivity.this, null, false);
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
                    if (SecurityMyPref.hasReddot()) {
                        if (invis2visAnimatorListener == null) {
                            invis2visAnimatorListener = new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
//                                    if (SecurityMyPref.hasReddot() && getBackImage() == R.drawable.ic_top_bar_category) {
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
            Log.e("value","close1");
            SlideMenu.Status status = menu.getStatus();
            if (status == SlideMenu.Status.Close) menu.open();

            else if (status == SlideMenu.Status.OpenRight) {
                menu.close();
                Log.e("value","close2");

            } else
                askForExit();
            Log.e("value","close3");

        } else
            super.onBackPressed();
        Log.e("value","close4");

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