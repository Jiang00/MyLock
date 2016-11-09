package com.security.manager.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyRelativeLayout extends LinearLayout {
    private SlideMenu dl;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDragLayout(SlideMenu dl) {
        this.dl = dl;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(dl!=null){
            if (dl.getStatus() != SlideMenu.Status.Close) {
                return true;
            }
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(dl!=null){
            if (dl.getStatus() != SlideMenu.Status.Close) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dl.close();
                }
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

}
