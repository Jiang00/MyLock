package com.security.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.ivymobi.applock.free.R;
import com.security.lib.customview.AnimationImageView;
import com.security.manager.lib.BaseActivity;

import com.security.manager.lib.ImageTools;
import com.security.mymodule.FileType;
import com.security.mymodule.IntruderEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by song on 16/1/4.
 */
public class IntrudedeImageActivity extends BaseActivity {
    @InjectView(R.id.security_invade_shang_ic)
    ImageView blockIcon;

    @InjectView(R.id.security_invade_peple)
    AnimationImageView blockImage;


    @InjectView(R.id.security_xia_ic)
    ImageView dateIcon;
    @InjectView(R.id.security_invade_data)
    TextView dateView;

    @InjectView(R.id.security_invade_tishi)
    TextView messageView;
    @InjectView(R.id.security_title_bar_te)
    TextView title;
    @InjectView(R.id.security_et_m)
    ImageButton edit_mode;
    @InjectView(R.id.security_set_bt)
    ImageButton delete;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.share_id)
    ImageView shareImg;

    public Bitmap sharetop;
    public Bitmap sharebootom;
    public Bitmap bacbitmap;


//    LoadImagePresenter presenter;

    private static final String EXTRA_KEY_URL = "url";
    private static final String EXTRA_KEY_DATE = "date";
    private static final String EXTRA_KEY_PKG = "pkg";
    private static final String EXTRA_KEY_POSITION = "position";

    private String url;
    public String date;
    private String pkg;
    private int position;

    @Override
    protected void onIntent(Intent intent) {
        url = intent.getStringExtra(EXTRA_KEY_URL);
        date = intent.getStringExtra(EXTRA_KEY_DATE);
        pkg = intent.getStringExtra(EXTRA_KEY_PKG);
        position = intent.getIntExtra(EXTRA_KEY_POSITION, -1);
    }

    public static void launch(Context context, IntruderEntry entity) {
        Intent i = new Intent(context, IntrudedeImageActivity.class);
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
        setupToolbar();

        bacbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testbac);

        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date mydate = new Date(date);
                long newdate = mydate.getTime();
                ImageTools.SharePhoto(newdate + "", IntrudedeImageActivity.this);
                Tracker.sendEvent(Tracker.ACT_INTRUDE, Tracker.ACT_INTRUDE_SHARE, Tracker.ACT_INTRUDE_SHARE, 1L);

            }
        });

//       设置 actionbar
//        setSupportActionBar(lockscreen_toolbar);
//        ActionBar bar = getSupportActionBar();
//        if (bar != null) {
//            bar.setDisplayHomeAsUpEnabled(true);
//            bar.setTitle(R.string.intruder_detail);
//        }
//
//        presenter = LoadImagePresenter.getPresenter();
        //设置自定义标题
        title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.security_back), null, null, null);
        title.setText("      " + getString(R.string.security_new_intruder));
        edit_mode.setVisibility(View.GONE);
        delete.setImageResource(R.drawable.security_incade_de);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.putExtra("position", position);
//                deleteIntruder();
                setResult(1, in);
                finish();
            }
        });
        Drawable icon = null;
        CharSequence label = null;

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(pkg, 0);
            icon = packageInfo.applicationInfo.loadIcon(getPackageManager());

            label = packageInfo.applicationInfo.loadLabel(getPackageManager());
            messageView.setText(getResources().getString(R.string.security_intru_f_app, label == null ? getResources().getString(R.string.app_name) : label));
        } catch (PackageManager.NameNotFoundException e) {
            messageView.setText(getResources().getString(R.string.security_intru_f_app, label == null ? getResources().getString(R.string.app_name) : label));

        }
        blockIcon.setBackgroundDrawable(icon == null ? getResources().getDrawable(R.drawable.ic_launcher) : icon);
        dateIcon.setBackgroundDrawable(icon == null ? getResources().getDrawable(R.drawable.ic_launcher) : icon);
        blockImage.setImage(url, 0L, FileType.TYPE_PIC, bacbitmap, IntrudedeImageActivity.this, date);
//        messageView.setText(getResources().getString(R.string.block_intruder_for_app, label));

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        dateView.setText(df.format(new Date(Long.parseLong(date))));

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

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_new_intruder);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.security_setting_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
