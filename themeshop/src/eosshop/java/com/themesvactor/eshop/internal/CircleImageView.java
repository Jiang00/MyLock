package com.themesvactor.eshop.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.android.themeshop.R;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.SquaringDrawable;

/**
 * Created by song on 2017/3/8.
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint paint;
    private int circle;

    public CircleImageView(Context context) {
        super(context);
        paint = new Paint();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        paint.setAntiAlias(true);
        paint.setColor(getContext().getResources().getColor(R.color.shop_loading_bg));
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, paint);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm != null) {
            Shader shader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            postInvalidate();
        } else {
            paint.setColor(getContext().getResources().getColor(R.color.shop_loading_bg));
            postInvalidate();
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof SquaringDrawable) {
            drawable = drawable.getCurrent();
        }
        if (drawable instanceof BitmapDrawable) {
            setImageBitmap(((BitmapDrawable) drawable).getBitmap());
        } else if (drawable instanceof GlideBitmapDrawable) {
            setImageBitmap(((GlideBitmapDrawable) drawable).getBitmap());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        circle = getWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        final int circle = this.circle;
        canvas.drawCircle(circle, circle, circle, paint);
    }
}
