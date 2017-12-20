package com.batteryvactorapps.module.charge.saver.utilsvac;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by huale on 2015/2/3.
 */
public class WidgetContainer extends FrameLayout implements View.OnClickListener {
    public static final int MATCH_PARENT = WindowManager.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = WindowManager.LayoutParams.WRAP_CONTENT;

    public static final int LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public static final int PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static final int BEHIND = ActivityInfo.SCREEN_ORIENTATION_BEHIND;

    WindowManager.LayoutParams lp;
    boolean movable;
    int screenHeight;

    private WidgetContainer(Context context, Builder builder) {
        super(context);

        this.movable = builder.movable;
        int type = builder.type;
        int flag = builder.flag;
        if (!builder.blockInput) {
            flag |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
        if (!builder.blockTouch) {
            flag = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        lp = new WindowManager.LayoutParams(
                builder.width, builder.height, type, flag,
                PixelFormat.TRANSPARENT);
        lp.gravity = builder.gravity;
        lp.screenOrientation = builder.orientation;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenHeight = wm.getDefaultDisplay().getHeight();

        setOnClickListener(this);
        if (Build.VERSION.SDK_INT > 18) {
            setFitsSystemWindows(true);
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    public LayoutParams makeLayoutParams(int width, int height, int gravity) {
        return new LayoutParams(width, height, gravity);
    }

    public static class Builder {
        boolean movable = false;
        boolean blockTouch = true;
        boolean blockInput = true;
        int orientation = BEHIND;
        int gravity = Gravity.NO_GRAVITY;
        int width = MATCH_PARENT;
        int height = MATCH_PARENT;
        int type = Build.VERSION.SDK_INT >= 19 ? (Build.VERSION.SDK_INT < 24 ? WindowManager.LayoutParams.TYPE_TOAST : WindowManager.LayoutParams.TYPE_SYSTEM_ERROR) : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        int flag = Build.VERSION.SDK_INT >= 19 ?

//                (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
                (WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                : 0;

        public Builder setMovable(boolean movable) {
            this.movable = movable;
            return this;
        }

        public Builder setBlockTouch(boolean blockTouch) {
            this.blockTouch = blockTouch;
            return this;
        }

        public Builder setBlockInput(boolean blockInput) {
            this.blockInput = blockInput;
            return this;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public WidgetContainer build(Context context) {
            return new WidgetContainer(context, this);
        }
    }

//    public void setBackgroundColor(int color){
//        this.setBackgroundColor(color);
//    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick();
        }
    }

    public interface IWidgetListener {
        boolean onBackPressed();

        boolean onMenuPressed();

        void onClick();
    }

    public boolean isShowing() {
        return added;
    }

    private boolean added = false;

    public void addToWindow() {
        if (!added && wm != null) {
            try {
                wm.addView(this, lp);
                added = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFromWindow() {
        if (added && wm != null) {
            try {
                added = false;
                wm.removeView(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private IWidgetListener listener;

    public void setWidgetListener(IWidgetListener listener) {
        this.listener = listener;
    }

    private WindowManager wm;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (this.listener != null) {
                        if (this.listener.onBackPressed()) return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_MENU:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (this.listener != null) {
                        if (this.listener.onMenuPressed()) return true;
                    }
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return movable;
    }

    private float x, y;
    private boolean moving = false;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.movable) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP) {
                if (!moving && this.listener != null) {
                    listener.onClick();
                    return true;
                }
            } else if (action == MotionEvent.ACTION_DOWN) {
                x = event.getRawX();
                y = event.getRawY();
                moving = false;
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                float dx = event.getRawX() - x;
                float dy = event.getRawY() - y;
                if (Math.abs(dx) > 20 || Math.abs(dy) > 20) {
                    moving = true;
                    x = event.getRawX();
                    y = event.getRawY();
                    lp.x = (int) x - getWidth() / 2;
                    lp.y = screenHeight - (int) y - getHeight() / 2;
                    wm.updateViewLayout(this, lp);
                }
            }
            return super.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }
}
