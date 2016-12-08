package com.security.manager.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wangqi on 16/12/5.
 */

public class ImageTools {

    public static String photoUrl = "/sdcard/mybitmap";

    /**
     * 水印
     *
     * @param intruderpic 添加水印的图
     * @param watermark   水印图
     * @param alpha       水印的透明度
     * @return
     */
    public static Bitmap Watermark(Bitmap intruderpic, int alpha, Bitmap bacBitmap, Context context) {
        if (intruderpic == null) {
            return null;
        }

        Paint paint = new Paint();
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(intruderpic, Utils.dip2px(context, 265), Utils.dip2px(context, 285));
        Bitmap newb = Bitmap.createBitmap(bacBitmap.getWidth(), bacBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(bacBitmap, matrix, null);
        Matrix intrude = new Matrix();
        intrude.postTranslate(Utils.dip2px(context, 6), Utils.dip2px(context, 6));
        cv.drawBitmap(resizeBmp, intrude, paint);
        cv.save(Canvas.MATRIX_SAVE_FLAG);
        cv.restore();

        return newb;
    }


    public static void saveMyBitmap(String bitName, Bitmap mBitmap) {

        File f = new File(photoUrl + bitName + ".png");
        if (f.exists()) {
            return;
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 分享照片
    public static void SharePhoto(String photoUri, final Context activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(photoUrl + photoUri + ".png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/jpeg");
        activity.startActivity(Intent.createChooser(shareIntent, ""));
    }


    /**
     * 获得屏幕高度
     *
     * @param context
     * @return by Hankkin at:2015-10-07 21:15:59
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return by Hankkin at:2015-10-07 21:16:13
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


}
