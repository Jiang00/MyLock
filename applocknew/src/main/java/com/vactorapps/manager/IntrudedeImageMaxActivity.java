package com.vactorapps.manager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivymobi.applock.free.R;
import com.vactorapps.lib.customview.AnimationImageView;
import com.vactorappsapi.manager.IntruderApi;
import com.vactorappsapi.manager.lib.BaseActivity;
import com.vactorapps_model.mymodule.FileType;
import com.vactorapps_model.mymodule.IntruderEntry;
import com.vactorappsapi.manager.lib.LoadManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by song on 16/1/4.
 */
public class IntrudedeImageMaxActivity extends BaseActivity {

    @InjectView(R.id.security_invade_peple)
    AnimationImageView blockImage;

    @InjectView(R.id.max_delect)
    LinearLayout delete;

    @InjectView(R.id.share_id)
    LinearLayout shareImg;

    @InjectView(R.id.title_back)
    ImageView title_back;
    @InjectView(R.id.max_button)
    FrameLayout max_button;
    @InjectView(R.id.max_title)
    FrameLayout max_title;

    @InjectView(R.id.share_image)
    FrameLayout share_image;
    @InjectView(R.id.share_icon)
    ImageView share_icon;
    @InjectView(R.id.share_name)
    TextView share_name;
    @InjectView(R.id.share_time)
    TextView share_time;

    public Bitmap bacbitmap;

    private static final String EXTRA_KEY_URL = "url";
    private static final String EXTRA_KEY_DATE = "date";
    private static final String EXTRA_KEY_PKG = "pkg";
    private static final String EXTRA_KEY_POSITION = "position";

    private String url;
    public String date;
    public String pkg;
    private int position;
    private boolean onClickFlag;
    private float height;
    private ArrayList<IntruderEntry> intruderEntries;

    @Override
    protected void onIntent(Intent intent) {
        url = intent.getStringExtra(EXTRA_KEY_URL);
        date = intent.getStringExtra(EXTRA_KEY_DATE);
        pkg = intent.getStringExtra(EXTRA_KEY_PKG);
        position = intent.getIntExtra(EXTRA_KEY_POSITION, -1);
    }

    public static void launch(Context context, IntruderEntry entity) {
        Intent i = new Intent(context, IntrudedeImageMaxActivity.class);
        i.putExtra(EXTRA_KEY_URL, entity.url);
        i.putExtra(EXTRA_KEY_DATE, entity.simdate);
        i.putExtra(EXTRA_KEY_PKG, entity.pkg);
        context.startActivity(i);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        url = savedInstanceState.getString(EXTRA_KEY_URL);
        date = savedInstanceState.getString(EXTRA_KEY_DATE);
        pkg = savedInstanceState.getString(EXTRA_KEY_PKG);
        position = savedInstanceState.getInt(EXTRA_KEY_POSITION, -1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.security_invade_view);
        ButterKnife.inject(this);
        share_icon.setImageDrawable(LoadManager.getInstance(this).getAppIcon(pkg));
        share_time.setText(date);
        share_name.setText(getString(R.string.share_title, LoadManager.getInstance(this).getAppLabel(pkg)));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        intruderEntries = IntruderApi.getIntruders();

        bacbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testbac);

        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Date mydate = new Date(date);
//                long newdate = mydate.getTime();
//                ImageTools.SharePhoto(newdate + "", IntrudedeImageMaxActivity.this);
//                share("", url);
                share_image.buildDrawingCache();
                saveShare(share_image.getDrawingCache());
                Tracker.sendEvent(Tracker.ACT_INTRUDE, Tracker.ACT_INTRUDE_SHARE, Tracker.ACT_INTRUDE_SHARE, 1L);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in = new Intent();
//                in.putExtra("position", position);
//                setResult(1, in);
                IntruderEntry intruderEntry = intruderEntries.remove(position);
                IntruderApi.deleteIntruder(intruderEntry);
                finish();
            }
        });
        blockImage.setImage(url, 0L, FileType.TYPE_PIC, bacbitmap, IntrudedeImageMaxActivity.this, date);
        onClickFlag = false;
        height = getResources().getDimensionPixelSize(R.dimen.d80);
        blockImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickFlag) {
                    onClickFlag = false;
                    titleButtonAni(-height, 0f, height, 0f);
                } else {
                    onClickFlag = true;
                    titleButtonAni(0f, -height, 0f, height);
                }
            }
        });
    }

    private void share(String content, String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (url != null) {
            //uri 是图片的地址
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(url)));
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定义选择框的标题
//        startActivity(Intent.createChooser(shareIntent, "邀请好友"));
        //系统默认标题
        startActivity(shareIntent);
    }

    public void saveShare(Bitmap bitmap) {

        File filename;
        try {
            String path1 = android.os.Environment.getExternalStorageDirectory()
                    .toString();
            //Log.i("in save()", "after mkdir");
            File file = new File(path1 + "/" + getString(R.string.app_name));
            if (!file.exists())
                file.mkdirs();
//            String qianzhui = date.replaceAll("\\s", "_");
            String DEFAULT_IMAGE_NAME = "" + "_" + "share";

            filename = new File(file.getAbsolutePath() + "/" + DEFAULT_IMAGE_NAME
                    + ".jpg");
            FileOutputStream out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Uri result = Uri.fromFile(filename);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpg");
            share.putExtra(Intent.EXTRA_STREAM, result);
            startActivity(Intent.createChooser(share, getString(R.string.max_share)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void titleButtonAni(float startheight, float endheight, float startheightW, float endheightW) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(max_title, "translationY", startheight, endheight);
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(max_button, "translationY", startheightW, endheightW);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(rotate).with(moveIn);
        animSet.setDuration(500);
        animSet.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //本类将不会调用 presenter.stop
//        presenter.start(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        FileType file = new FileType();
        file.filePath = url;
        file.fileType = FileType.TYPE_PIC;
//        ImageTManager.setImageView(blockImage,url,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.security_intruder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.inder_two_set) {
            Intent in = new Intent();
            in.putExtra("position", position);
            deleteIntruder();
            setResult(1, in);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void deleteIntruder() {
        IntruderApi.deleteIntruder(url);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
