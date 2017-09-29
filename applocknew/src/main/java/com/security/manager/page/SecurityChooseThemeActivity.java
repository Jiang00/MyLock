package com.security.manager.page;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ivy.ivyshop.ShopMaster;
import com.ivymobi.applock.free.R;
import com.security.manager.App;
import com.security.manager.SavePicUtil;
import com.security.manager.SecurityAppLock;
import com.security.manager.meta.SecurityMyPref;

/**
 * Created by superjoy on 2014/9/4.
 */
public class SecurityChooseThemeActivity extends AppCompatActivity {


    FrameLayout defaultTheme;
    FrameLayout chooseTheme;
    ImageView defalutImageview;
    ImageView chooseImageView;
    int chooseValue = 1;
    Bitmap savepic;

    String cacheDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_theme);
        defaultTheme = (FrameLayout) this.findViewById(R.id.default_theme);
//        chooseTheme = (FrameLayout) this.findViewById(R.id.choose_theme);
        defalutImageview = (ImageView) this.findViewById(R.id.default_theme_choose);
        chooseImageView = (ImageView) this.findViewById(R.id.choose_theme_check);
        savepic = getBitmap(SavePicUtil.idToDrawable(R.drawable.theme_preview_two));
        try {
            cacheDir = Environment.getExternalStorageDirectory()
                    + "/.android/.themestore/."
                    + this.getPackageName()
                    + this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        defaultTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defalutImageview.setVisibility(View.VISIBLE);
                chooseImageView.setVisibility(View.GONE);
                chooseValue = 1;
            }
        });
        chooseTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defalutImageview.setVisibility(View.GONE);
                chooseImageView.setVisibility(View.VISIBLE);
                chooseValue = 2;
            }
        });

        this.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {

                SavePicUtil.SavePicInLocal(savepic, "applock_theme_previewtheme_preview_two", cacheDir);
//                    SavePicUtil.saveMyBitmap("theme_preview_two", savepic);
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }

                if (chooseValue == 2) {
                    ShopMaster.applyTheme(App.getContext(), "theme_preview_two", true);
                }
                SecurityMyPref.launchNow();
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), SecurityAppLock.class.getName());
                intent.putExtra("hide", false);
                intent.putExtra("launch", true);
                startActivity(intent);

                finish();


            }
        });
    }

    private Bitmap getBitmap(Drawable drawable) {
        // TODO Auto-generated method stub
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

}