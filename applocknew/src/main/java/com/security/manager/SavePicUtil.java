package com.security.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.android.common.SdkCache;
import com.ivymobi.applock.free.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wangqi on 17/2/8.
 */

public class SavePicUtil {


    /**
     * 将资源ID转化为Drawable
     *
     * @param id
     * @return
     */
    public static Drawable idToDrawable(int id) {
        return App.getContext().getResources().getDrawable(R.drawable.theme_preview_two);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        return ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * 将Bitmap以指定格式保存到指定路径
     */
//    public void saveBitmap(Bitmap bitmap, String name, Bitmap.CompressFormat format) throws PackageManager.NameNotFoundException {
//        // 创建一个位于SD卡上的文件
//        Context context =App.getContext();
//        File file = new File(Environment.getExternalStorageDirectory() + "/.android/.themestore/."
//                + context.getPackageName()
//                + context.getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0).versionCode + "/" + "applock_theme_preview", name);
//        FileOutputStream out = null;
//        try{
//            // 打开指定文件输出流
//            out = new FileOutputStream(file);
//            // 将位图输出到指定文件
//            bitmap.compress(format, 100, out);
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static void saveMyBitmap(String bitName, Bitmap mBitmap) throws PackageManager.NameNotFoundException {
        File f = new File(Environment.getExternalStorageDirectory()
                + "/.android/.themestore/."
                + App.getContext().getPackageName()
                + App.getContext().getPackageManager().getPackageInfo(
                App.getContext().getPackageName(), 0).versionCode + "/" + "applock_theme_preview" + bitName);

        try {
            if (!f.exists()) {
                f.mkdir();
            }
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception","我的异常－－－－"+e.getMessage()+"---------");
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

    public static void SavePicInLocal(Bitmap bitmap,String filename,String path) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ByteArrayOutputStream baos = null; // 字节数组输出流
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
            String picName = filename;


            File PHOTO_DIR = new File(path);
            if(!PHOTO_DIR.exists()){
                PHOTO_DIR.mkdirs();
            }
            File file = new File(PHOTO_DIR, picName);
            //如果存在就删掉
            if (file.exists()){
                file.delete();
            }
            // 将字节数组写入到刚创建的图片文件中
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
