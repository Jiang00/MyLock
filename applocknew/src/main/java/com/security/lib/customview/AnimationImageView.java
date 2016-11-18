package com.security.lib.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.security.manager.App;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.lib.io.LoadNormalThumbnail;

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


    public void setImage(String url, long id, int fileType, boolean forceLoading) {
        this.id = id;
        this.fileType = fileType;
        this.url = url;
        if (url == null) {
            setImageDrawable(null);
        } else {
//            Bitmap bitmap = ImageMaster.getImage(url);
            Bitmap bitmap= BitmapFactory.decodeFile(url);
            Log.e("mtt",url+"------");
            setImageBitmap(bitmap,false);

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
