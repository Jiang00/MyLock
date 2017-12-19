package com.security.manager.page;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ivymobi.applock.free.R;


public class SlideMenu extends FrameLayout {
    private GestureDetectorCompat gestureDetector;
    private ViewDragHelper dragHelper;
    private int range;
    private int width;
    private int height;
    private int mainLeft;
    private View vg_left;
    private View vg_right;
    private MyRelativeLayout vg_main;
    boolean lock;
    boolean lockRight;
    Activity act;

    public void setActivity(Activity act) {
        this.act = act;
    }

    public void setLock(boolean l) {
        lock = l;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLockRight(boolean l) {
        lockRight = l;
    }

    public boolean isLockRight() {
        return lockRight;
    }

    public SlideMenu(Context context) {
        this(context, null);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
        ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                boolean toLeft = (mainLeft + dx) < 0;
                if ((lock && !toLeft) || (lockRight && toLeft)) return 0;
                if (mainLeft + dx < -range) {
                    return -range;
                } else if (mainLeft + dx > range) {
                    return range;
                } else {
                    return left;
                }
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return scrollFinished && child == vg_main;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return width;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (mainLeft == 0) return;
                boolean showingMenu = mainLeft > 0;
                boolean open;

                if (showingMenu) {
                    if (lock) {
                        act.onBackPressed();
                        return;
                    }
                    open = xvel > 100 || (xvel > -100 && mainLeft > .3f * range);
                } else {
                    open = xvel < -100 || (xvel < 100 && mainLeft < -.3f * range);
                }

                if (open) {
                    open();
                } else {
                    close();
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                mainLeft = left;

                if (mainLeft < -range) {
                    mainLeft = -range;
                } else if (mainLeft > range) {
                    mainLeft = range;
                }

                dispatchDragEvent(mainLeft);
            }
        };
        dragHelper = ViewDragHelper.create(this, dragHelperCallback);
    }

    public void openHelp() {
        mainLeft = -1;
        open();
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            return Math.abs(dy * 3) <= Math.abs(dx);
        }
    }

    @Override
    protected void onFinishInflate() {
        vg_left = findViewById(R.id.left);
        vg_main = (MyRelativeLayout) findViewById(R.id.main);
        vg_main.setDragLayout(this);
//        vg_left.setClickable(true);
//        vg_right.setClickable(true);
//        vg_main.setClickable(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = vg_main.getMeasuredWidth();
        height = vg_main.getMeasuredHeight();
        range = (int) (width * 0.7f);
//        if (!layouted){
////            vg_right.setLayoutParams(new FrameLayout.LayoutParams(range, height));
//            layouted = true;
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
        vg_left.layout(-range, 0, 0, height);
//        try {
        vg_main.layout(mainLeft, 0, mainLeft + width, height);
//        } catch (Exception e) {
//
//        }
//        vg_right.layout(width, 0, (width + range), height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        try {
            dragHelper.processTouchEvent(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return true;
    }

    private void dispatchDragEvent(float mainLeft) {
        float percent = mainLeft > 0 ? (mainLeft / range) : (mainLeft / -range);
        animateView(percent);
    }

    private void animateView(float percent) {
        float f1 = 1 - percent * .7f;
        Log.e("chfq", "==f1===" + f1);
        vg_main.setX(0);
//        ViewHelper.setAlpha(vg_main, f1);
        vg_main.setAlpha(f1);
        View showing;
        float x;
        if (mainLeft > 0) {
            showing = vg_left;
            x = /*showing.getWidth()/2.3f * (percent - 1);*/ mainLeft - range - 1;
            showing.setX(x);
        }
//        else {
//            showing = vg_right;
//            x = mainLeft + width + 1;
//        }
//
//        showing.setX(x);
//        ViewHelper.setX(showing, x);
//        ViewHelper.setScaleX(showing, 0.5f + 0.5f * percent);
//        ViewHelper.setScaleY(showing, 0.5f + 0.5f * percent);
//        ViewHelper.setAlpha(showing, percent);
    }

    boolean scrollFinished = true;

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            scrollFinished = true;
        }
    }

    public enum Status {
        DragLeft, DragRight, OpenLeft, OpenRight, Close
    }

    public Status getStatus() {
        Status status = Status.Close;
        if (mainLeft == range) {
            status = Status.OpenLeft;
        } else if (mainLeft == -range) {
            status = Status.OpenRight;
        } else if (mainLeft < 0) {
            status = Status.DragRight;
        } else if (mainLeft > 0) {
            status = Status.DragLeft;
        }
        return status;
    }

    public void open() {
//        ViewHelper.setX(vg_left, -vg_left.getWidth());
//        ViewHelper.setX(vg_right, width + 5);
        scrollFinished = false;
        if (dragHelper.smoothSlideViewTo(vg_main, mainLeft < 0 ? -range : range, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void close() {
        scrollFinished = false;
        if (dragHelper.smoothSlideViewTo(vg_main, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

}