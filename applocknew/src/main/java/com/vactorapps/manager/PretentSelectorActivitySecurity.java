package com.vactorapps.manager;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ivymobi.applock.free.R;
import com.nineoldandroids.animation.ValueAnimator;
import com.vactorapps.manager.meta.VacPref;
import com.vactorappsapi.manager.lib.Utils;
import com.vactorappsapi.manager.lib.controller.CListViewAdaptor;
import com.vactorappsapi.manager.lib.controller.CListViewScroller;
import com.vactorapps.manager.page.MessageBox;
import com.vactorapps.manager.page.PretentPresenter;
import com.vactorapps.manager.page.VacMenu;
import com.vactorapps.manager.page.SlideMenu;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by song on 15/8/18.
 */
public class PretentSelectorActivitySecurity extends BaseAbsActivity {
    @InjectView(R.id.pretent_cover_list)
    GridView PretentCoverList;

    @InjectView(R.id.pretent_icon_list)
    GridView pretentIconList;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.facebook)
    ImageView facebook;

    @InjectView(R.id.goolge)
    ImageView google;

    @InjectView(R.id.googleplay)
    ImageView googleplay;

    @InjectView(R.id.fake_loading)
    FrameLayout fake_loading;

    @InjectView(R.id.fakes_lottie)
    LottieAnimationView fakes_lottie;
    @InjectView(R.id.fakes_lottie2)
    LottieAnimationView fakes_lottie2;

    static final int[] fakes = {
            R.string.security_pretent_none, R.drawable.security_myfake_2,
            R.string.security_pretent_fc, R.drawable.security_myfake_2,
            R.string.security_pretent_finger, R.drawable.security_myfake_2
    };

    static final int[] icons = {
            R.string.security_pretent_icon_default, R.drawable.ic_launcher,
            R.string.security_pretent_calender, R.drawable.security_myfake_1,//日历
            R.string.fakes_files, R.drawable.fakes_files,//文件
            R.string.fakes_email, R.drawable.fakes_email,//邮箱
            R.string.fakes_camera, R.drawable.fakes_camera,//相机
            R.string.fakes_compass, R.drawable.fakes_compass,//指南针
            R.string.fakes_music, R.drawable.fakes_music,//音乐
            R.string.security_pretent_icon_2, R.drawable.security_myfake_2,//计算器
    };

    @Override
    protected boolean hasHelp() {
        return false;
    }

    int currentFakeCover = PretentPresenter.PRETENT_NONE;
    int currentFakeIcon = 0;

    @Override
    public void setupView() {
        setContentView(R.layout.security_myfake_selector);
        ButterKnife.inject(this);
        setupToolbar();
        initclick();
        setup(R.string.security_myfake);
        normalTitle.setText("   " + getResources().getString(R.string.security_myfake));
        normalTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.security_back), null, null, null);

        findViewById(R.id.search_button).setVisibility(View.GONE);
        currentFakeCover = VacPref.getFakeCover(PretentPresenter.PRETENT_NONE);
        currentFakeIcon = PretentPresenter.pretentIconIdx();

        normalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        PretentCoverList.setAdapter(new CListViewAdaptor(new CListViewScroller(PretentCoverList), R.layout.security_myfake_item) {
            @Override
            protected void onUpdate(int position, Object holderObject, boolean scrolling) {
                ViewHolder holder = (ViewHolder) holderObject;
                holder.appName.setText(fakes[position << 1]);
                holder.fakeicon.setImageResource(fakes[(position << 1) + 1]);
                holder.encrypted.setVisibility(currentFakeCover == position ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            protected Object getHolder(View root) {
                return new ViewHolder(root);
            }

            @Override
            public int getCount() {
                return fakes.length >> 1;
            }
        });

        pretentIconList.setAdapter(new CListViewAdaptor(new CListViewScroller(pretentIconList), R.layout.security_myfake_item) {
            @Override
            protected void onUpdate(int position, Object holderObject, boolean scrolling) {
                FakesViewHolder holder = (FakesViewHolder) holderObject;
                holder.fakeicon.setImageResource(icons[(position << 1) + 1]);
                if (currentFakeIcon == position) {
                    holder.fake_item.setBackgroundColor(ContextCompat.getColor(PretentSelectorActivitySecurity.this, R.color.A8));
                    holder.appName.setText(getResources().getString(R.string.now));
                } else {
                    holder.fake_item.setBackgroundColor(ContextCompat.getColor(PretentSelectorActivitySecurity.this, R.color.A1));
                    holder.appName.setText(icons[position << 1]);
                }
            }

            @Override
            protected Object getHolder(View root) {
                return new FakesViewHolder(root);
            }

            @Override
            public int getCount() {
                return icons.length >> 1;
            }
        });
    }

    @OnItemClick(R.id.pretent_icon_list)
    public void switchFakeIcon(int which) {
        currentFakeIcon = which;
        PretentPresenter.switchPretentIcon(which);
        Utils.notifyDataSetChanged(pretentIconList);
        stopService(new Intent(this, VacNotificationService.class));
        startService(new Intent(this, VacNotificationService.class));
        fake_loading.setVisibility(View.VISIBLE);
        fakes_lottie.setAnimation("fakes_lottie.json");
        fakes_lottie.setScale(0.3f);//相对原大小的0.2倍
//        fakes_lottie.setSpeed(0.7f);
        fakes_lottie.loop(true);
        fakes_lottie.playAnimation();
        // 自定义速度与时长
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1500);
        animator.setRepeatCount(3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fakes_lottie.setProgress((float) animation.getAnimatedValue());
            }
        });
        animator.start();
        fakes_lottie.cancelAnimation();
        animator.addListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                fakes_lottie.setVisibility(View.GONE);
                fakes_lottie2.setAnimation("frist4.json");
                fakes_lottie2.setScale(2f);//相对原大小的0.2倍
                fakes_lottie2.loop(false);//是否循环，true循环
                fakes_lottie2.setSpeed(1f);//播放速度
                fakes_lottie2.playAnimation();
                fakes_lottie2.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fake_loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        });
    }

    @OnItemClick(R.id.pretent_cover_list)
    public void activeFakeCover(int which) {
        if (currentFakeCover == which) {
            return;
        }
        switch (which) {
            case PretentPresenter.PRETENT_NONE:
                VacPref.setFakeCover(PretentPresenter.PRETENT_NONE);
                currentFakeCover = which;
                Utils.notifyDataSetChanged(PretentCoverList);
                Toast.makeText(MyApp.getContext(), R.string.security_pretent_none, Toast.LENGTH_SHORT).show();
                break;

            case PretentPresenter.PRETENT_FC:
                AlertDialog dialog = PretentPresenter.showFC(MyApp.getContext(), R.string.security_myfake, Html.fromHtml(getString(R.string.security_pretent_setting_msg)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.security_pretent_fails, Toast.LENGTH_SHORT).show();
                            }
                        }, new MessageBox.OnLongClickListener<AlertDialog>() {
                            @Override
                            public boolean onLongClick(View v) {
                                VacPref.setFakeCover(PretentPresenter.PRETENT_FC);
                                dialog.cancel();
                                currentFakeCover = PretentPresenter.PRETENT_FC;
                                MessageBox.Data data = new MessageBox.Data();
                                data.msg = R.string.security_security_set_fake_success;
                                MessageBox.show(PretentSelectorActivitySecurity.this, data);
                                return true;
                            }
                        });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utils.notifyDataSetChanged(PretentCoverList);
                    }
                });
                break;

            case PretentPresenter.PRETENT_SCAN:
                PretentPresenter.show(MyApp.getContext(), PretentPresenter.PRETENT_SCAN_SETTING, null, new Runnable() {
                    @Override
                    public void run() {
                        VacPref.setFakeCover(PretentPresenter.PRETENT_SCAN);
                        currentFakeCover = PretentPresenter.PRETENT_SCAN;
                        PretentPresenter.hide();
                        MessageBox.Data data = new MessageBox.Data();
                        data.msg = R.string.security_security_set_fake_success;
                        MessageBox.show(PretentSelectorActivitySecurity.this, data);
                        Utils.notifyDataSetChanged(PretentCoverList);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        PretentPresenter.hide();
                    }
                });
                break;
        }


    }


    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.security_slide_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_myfake);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SlideMenu.Status status = menu.getStatus();
            if (status == SlideMenu.Status.Close)
                menu.open();
            else if (status == SlideMenu.Status.OpenRight) {
                menu.close();
            } else
                askForExit();
        }
        return true;
    }

    public void initclick() {
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(VacMenu.FACEBOOK);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_FACEBOOK, Tracker.ACT_FACEBOOK, 1L);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(VacMenu.GOOGLE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLUS, Tracker.ACT_GOOGLE_PLUS, 1L);

            }
        });

        googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(VacMenu.GOOGLEPLAY);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLAY, Tracker.ACT_GOOGLE_PLAY, 1L);


            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fakes_lottie != null) {
            fakes_lottie.cancelAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fakes_lottie != null) {
            fakes_lottie.cancelAnimation();
        }
    }
}
