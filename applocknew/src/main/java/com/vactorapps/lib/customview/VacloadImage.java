package com.vactorapps.lib.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ivymobi.applock.free.R;
import com.vactorapps.manager.MyApp;
import com.vactorappsapi.manager.lib.io.ImageMaster;
import com.vactorappsapi.manager.lib.io.LoadIconFromApp;
import com.vactorappsapi.manager.lib.io.LoadNormalThumbnail;
import com.vactorappsapi.manager.lib.io.LoadSafeThumbnail;

/**
 * Created by SongHualin on 6/12/2015.
 */
public class VacloadImage extends ImageView
        implements LoadIconFromApp.LoadingNotifiable{
    Animation animation;
    String url;
    int fileType;
    long id;


    private Paint paint;
    /**
     * 个人理解是
     * <p>
     * 这两个都是画圆的半径
     */
    private int roundWidth;
    private int roundHeight;
    private Paint paint2;

    public VacloadImage(Context context) {
        super(context);
        loadAnimation();

        init(context, null);
    }

    public VacloadImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAnimation();
        init(context, attrs);
    }

    public VacloadImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAnimation();
        roundWidth = getResources().getDimensionPixelOffset(R.dimen.d14);
        roundHeight = getResources().getDimensionPixelOffset(R.dimen.d14);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
            roundWidth = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, roundWidth);
            roundHeight = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, roundHeight);
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            roundWidth = (int) (roundWidth * density);
            roundHeight = (int) (roundHeight * density);
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    private void loadAnimation() {
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_in);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VacloadImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        loadAnimation();
    }

    public void setImage(String url, long id, int fileType, boolean forceLoading) {
        this.id = id;
        this.fileType = fileType;
        this.url = url;
        if (url == null) {
            setImageDrawable(null);
        } else {
            Bitmap bitmap = ImageMaster.getImage(url);

//              setImageBitmap(Bitmap.createBitmap(urlDrawable.getBitmap(),10,100,100,500));
//            BitmapDrawable bitmapDrawable=(BitmapDrawable)imageview1.getDrawable();
//            imageview2.setImageBitmap(Bitmap.createBitmap(bitmapDrawable.getBitmap(),x,y,width,height));

            setImageBitmap(bitmap);
            if (bitmap == null && forceLoading) {
                LoadNormalThumbnail loadingTask = LoadNormalThumbnail.Instance();
                loadingTask.execute(this);
            }
        }
    }

    public void setImageThumbnail(String url, boolean forceLoading) {
        this.url = url;
        if (url == null) {
            setImageDrawable(null);
        } else {
            Bitmap bitmap = ImageMaster.getImage(url);
            setImageBitmap(bitmap);
            if (bitmap == null && forceLoading) {
                LoadSafeThumbnail task = LoadSafeThumbnail.Instance();
                task.execute(this);
            }
        }
    }

    public void setImageIcon(String packageName, boolean forceLoading) {
        this.url = packageName;
        Bitmap bitmap = ImageMaster.getImage(packageName);
        setImageBitmap(bitmap);
        if (bitmap == null && forceLoading) {
            LoadIconFromApp task = LoadIconFromApp.Instance();
            task.execute(this);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Point getSize() {
        return new Point(114, 96);
    }

    @Override
    public int getFileType() {
        return fileType;
    }

    @Override
    public void offer(final Bitmap bitmap) {
        MyApp.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    if (!bitmap.isRecycled()) {
                        setImageBitmap(bitmap);
                    }
                }
                startAnimation(animation);
            }
        });
    }

    @Override
    public long getIdLong() {
        return id;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        drawLiftUp(canvas2);
        drawLiftDown(canvas2);
        drawRightUp(canvas2);
        drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();
    }
    private void drawLiftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(0, getHeight() - roundHeight * 2, roundWidth * 2, getHeight()), 90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - roundHeight);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, getHeight() - roundHeight * 2, getWidth(), getHeight()), -0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - roundWidth, 0);
        path.arcTo(new RectF(getWidth() - roundWidth * 2, 0, getWidth(), 0 + roundHeight * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }
}
