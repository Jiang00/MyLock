package com.vactorapps.lib.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.vactorappsapi.manager.lib.ImageTools;

import java.util.Date;

/**
 * Created by song on 15/9/23.
 */
public class AnimationImageView extends ImageView {
    Animation fadeIn;
    String url;
    int fileType;
    long id;

    public AnimationImageView(Context context) {
        super(context);
        loadAnimation();
    }

    public AnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAnimation();
    }

    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAnimation();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimationImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAnimation();
    }

    public void setImageBitmap(Bitmap bm, boolean playAnimation) {
        super.setImageBitmap(bm);
        if (playAnimation) {
            startAnimation(fadeIn);
        }
    }


    private void loadAnimation() {
        fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
    }

    public void setImage(String url, long id, int fileType, Bitmap bacbitmap, Context context, String data) {
        this.id = id;
        this.fileType = fileType;
        this.url = url;
        if (url == null) {
            setImageDrawable(null);
        } else {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(url);
                setImageBitmap(bitmap, false);
                Date mydate = new Date(Long.parseLong(data));
                long time = mydate.getTime();
                Bitmap bm = ImageTools.Watermark(bitmap,255, bacbitmap, context);
                ImageTools.saveMyBitmap(time+"", bm);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }
}
