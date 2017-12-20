package com.batteryvactorapps.module.charge.saver.protectview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.batteryvactorapps.module.charge.saver.R;

/**
 * Created by  on 2016/12/2.
 */
public class ChargWaterView extends View {
    public int width;
    public int height;
    private Paint firstPaint;
    private Paint beijingPaint;
    private String text = "50";
    private String text_2 = "%";
    private int pratent = 50;
    Context context;
    private int textSize;
    private float height_t;
    private int textSize_b, textSize_s;

    public ChargWaterView(Context context) {
        this(context, null);
    }

    public ChargWaterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChargWaterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
        initPaints();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        int d = (width >= height) ? height : width;
        setMeasuredDimension(width, height);

    }

    private void initPaints() {
        firstPaint = new Paint();
        firstPaint.setAntiAlias(true);
        firstPaint.setStyle(Paint.Style.FILL);
        textSize_b = getResources().getDimensionPixelSize(R.dimen.s100);
        textSize_s = getResources().getDimensionPixelSize(R.dimen.s38);
//        firstPaint.setStrokeJoin(Paint.Join.ROUND);
        firstPaint.setTextSize(textSize_b);
//        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "charging_num.TTF");
//        firstPaint.setTypeface(tf);
        Paint.FontMetrics metrics = firstPaint.getFontMetrics();
        height_t = metrics.ascent;
        beijingPaint = new Paint();
        beijingPaint.setAntiAlias(true);

    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        firstPaint.setColor(Color.parseColor("#ffffffff"));
        int src = canvas.saveLayer(0, 0, width, height, firstPaint, Canvas.ALL_SAVE_FLAG);
        firstPaint.setColor(Color.parseColor("#6f7886"));//上面的颜色
        firstPaint.setTextSize(textSize_b);
        float textWidth = firstPaint.measureText(text);
        float x = width / 2 - textWidth / 2;
        Paint.FontMetrics metrics = firstPaint.getFontMetrics();
        float dy = -(metrics.descent + metrics.ascent) / 2;
        float y = dy + height / 2;
        canvas.drawText(text, x, y, firstPaint);
        firstPaint.setTextSize(textSize_s);
        canvas.drawText(text_2, x + textWidth, y, firstPaint);

        firstPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        firstPaint.setColor(Color.parseColor("#ffffffff"));
        canvas.drawPath(firstPath(), firstPaint);
        firstPaint.setXfermode(null);
        canvas.restoreToCount(src);


        sin_offset += sin_offset_increment_value;
        sin_offset2 += sin_offset_increment_value2;
    }

    private float sin_offset_increment_value = 0.2f;//初项递增值，表示波浪的快慢
    private float sin_offset_increment_value2 = 0.23f;//初项递增值，表示波浪的快慢
    private Path firstPath = new Path();
    private int sin_amplitude = 5;//振幅 ，10到100之间
    private float sin_cycle = 0.05f;//周期 ， 0.01f左右
    float sin_offset = 0.0f;//初项，偏移量
    float sin_offset2 = 5.0f;//初项，偏移量

    private Path firstPath() {
        firstPath.reset();
        firstPath.moveTo(0, height / 2 - height_t / 2);// 移动到左下角的点
        Log.e("text_size2", height / 2 - height_t / 2 + "==");
        for (float x = 0; x <= width; x++) {
            if (pratent != 100) {
                float y = (float) (sin_amplitude * Math.sin(sin_cycle * x + sin_offset)) + (height / 2 - height_t / 2) * (100 - pratent) / 100;
                firstPath.lineTo(x, y);
            } else {
                firstPath.lineTo(x, 0);
            }
        }
        firstPath.lineTo(width, height / 2 - height_t / 2);
        firstPath.lineTo(0, height / 2 - height_t / 2);
        firstPath.close();
        return firstPath;
    }

    UpThread upThread;
    boolean isup = false;

    public void setPratent(int pratent) {
        if (!isup) {
            isup = true;
            upThread = new UpThread(pratent);
            upThread.start();
        }
    }

    public void upDate(int pratent) {
        this.pratent = pratent;
        this.text = pratent + "";
//        postInvalidate();
    }

    RunThread runThread;
    boolean isStart = false;

    public void start() {
        if (!isStart) {
            isStart = true;
            runThread = new RunThread();
            runThread.start();
        }
    }

    public void stop() {
        isStart = false;
    }

    public class RunThread extends Thread {
        @Override
        public void run() {
            while (isStart) {
                try {
                    Thread.sleep(100);
                    postInvalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class UpThread extends Thread {
        int p;

        public UpThread(int p) {
            this.p = p;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                upDate(i);
                if (listener != null) {
                    listener.update(i);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 100; i >= p; i--) {
                upDate(i);
                if (listener != null) {
                    listener.update(i);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.success();
            }
            isup = false;
        }
    }

    FloatWaterListener listener;

    public void setFloatWaterListener(FloatWaterListener listener) {
        this.listener = listener;
    }

    public interface FloatWaterListener {
        void success();

        void update(int jindu);
    }

    private void init() {
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= 11) {
//                setLayerType(LAYER_TYPE_SOFTWARE, null);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}