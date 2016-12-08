package com.security.lib.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.security.manager.lib.ImageTools;

import java.text.SimpleDateFormat;
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

    private void loadAnimation() {
        fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
    }

    public void setImageBitmap(Bitmap bm, boolean playAnimation) {
        super.setImageBitmap(bm);
        if (playAnimation) {
            startAnimation(fadeIn);
        }
    }


    public void setImage(String url, long id, int fileType, Bitmap bacbitmap, Context context, String data) {
        this.id = id;
        this.fileType = fileType;
        this.url = url;
        if (url == null) {
            setImageDrawable(null);
        } else {
//            Bitmap bitmap = ImageMaster.getImage(url);
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            Date mydate = new Date(data);
            mydate.toString();
            long time = mydate.getTime();
            try {
                Bitmap bm = ImageTools.Watermark(bitmap,255, bacbitmap, context);
                ImageTools.saveMyBitmap(time+"", bm);
            }catch (Exception e){
                e.printStackTrace();
            }
            setImageBitmap(bitmap, false);
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
