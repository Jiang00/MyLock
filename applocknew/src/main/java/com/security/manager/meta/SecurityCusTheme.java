package com.security.manager.meta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.privacy.lock.R;
import com.security.manager.App;
import com.security.manager.lib.BaseActivity;
import com.security.manager.lib.io.ImageMaster;
import com.security.manager.lib.io.LoadNormalThumbnail;
import com.security.manager.asyncmanager.SecurityImgManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by song on 15/7/21.
 */
public class SecurityCusTheme {
    public static final String root = SecurityImgManager.CACHE_ROOT;
    public static final String defaultUrl = root + "custom_theme";
    public static final String CACHE_KEY = "_custom_theme_cache_";
    public static final int REQ_CODE_PICK = BaseActivity.REQ_CODE_USER + 1;

    public static void chooseImage(Activity activity) {
        Intent pick = new Intent(Intent.ACTION_PICK);
//        pick.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pick.setType("image/*");
        activity.startActivityForResult(pick, REQ_CODE_PICK);
    }

    public static void chooseComplete(Intent data) {
        Uri uri = data.getData();
        save(uri);
    }

    public static void save(Uri uri) {
        try {
            File file = new File(defaultUrl);
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = App.getContext().getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[40960];
            int count;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            is.close();
            fos.close();
            ImageMaster.remove(CACHE_KEY);
            App.getSharedPreferences().edit().putString("theme", "custom").apply();
            Toast.makeText(App.getContext(), R.string.security_use_theme_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap() {
        Bitmap image = ImageMaster.getImage(CACHE_KEY);
        if (image == null) {
            try {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(defaultUrl, opt);
                opt.inSampleSize = LoadNormalThumbnail.calcSampleSize(opt.outWidth, opt.outHeight, 480, 854);
                opt.inJustDecodeBounds = false;
                image = BitmapFactory.decodeFile(defaultUrl, opt);
                ImageMaster.addImage(CACHE_KEY, image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }


}
