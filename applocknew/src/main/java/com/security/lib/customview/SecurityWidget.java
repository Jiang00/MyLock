package com.security.lib.customview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.security.manager.lib.Utils;

/**
 * Created by huale on 2015/2/3.
 */
public class SecurityWidget extends FrameLayout implements View.OnClickListener {
    WindowManager.LayoutParams lp;
    boolean modal;
    int screenHeight;

    public SecurityWidget(Context context, int gravity, int width, int height, boolean modal) {
        super(context);
        this.modal = modal;
        int type = Build.VERSION.SDK_INT >= 19 ? WindowManager.LayoutParams.TYPE_TOAST : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        lp = new WindowManager.LayoutParams(width, height, type,
                modal ? WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD:
                        (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN),
                PixelFormat.TRANSLUCENT);
        lp.gravity = gravity;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = Utils.getScreenSize(context);
        screenHeight = size.y;
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick();
        }
    }

    public interface IWidgetListener {
        boolean onBackPressed();
        boolean onMenuPressed();
        void onClick();
    }

    boolean added = false;
    public void addToWindow(){
        if (!added && wm != null){
            try{
                wm.addView(this, lp);
                added = true;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void removeFromWindow(){
        if (added && wm != null){
            try{
                added = false;
                wm.removeViewImmediate(this);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public IWidgetListener listener;

    public void setWidgetListener(IWidgetListener listener){
        this.listener = listener;
    }

    WindowManager wm;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP){
                    if (this.listener != null){
                        if (this.listener.onBackPressed()) return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_MENU:
                if (event.getAction() == KeyEvent.ACTION_UP){
                    if (this.listener != null){
                        if(this.listener.onMenuPressed()) return true;
                    }
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !modal;
    }

    float x, y;
    boolean moving = false;
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (this.modal){
            return super.onTouchEvent(event);
        } else {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP){
                if (!moving && this.listener != null){
                    listener.onClick();
                    return true;
                }
            } else if (action == MotionEvent.ACTION_DOWN){
                x = event.getRawX();
                y = event.getRawY();
                moving = false;
                return true;
            } else if (action == MotionEvent.ACTION_MOVE){
                float dx = event.getRawX() - x;
                float dy = event.getRawY() - y;
                if (Math.abs(dx) > 20 || Math.abs(dy) > 20){
                    moving = true;
                    x = event.getRawX();
                    y = event.getRawY();
                    lp.x = (int) x - getWidth()/2;
                    lp.y = screenHeight - (int) y  - getHeight()/2;
                    wm.updateViewLayout(this, lp);
                }
            }
            return super.onTouchEvent(event);
        }
    }
}
