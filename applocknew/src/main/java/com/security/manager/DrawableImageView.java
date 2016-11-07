package com.security.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by SongHualin on 5/28/2015.
 */
public class DrawableImageView extends ImageView {
    public DrawableImageView(Context context) {
        super(context);
    }

    public DrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
