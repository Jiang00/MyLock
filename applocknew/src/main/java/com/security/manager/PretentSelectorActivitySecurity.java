package com.security.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ivymobi.applock.free.R;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.PretentPresenter;
import com.security.manager.page.MessageBox;
import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.manager.page.SecurityMenu;
import com.security.manager.page.SlideMenu;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by song on 15/8/18.
 */
public class PretentSelectorActivitySecurity extends SecurityAbsActivity {
    @InjectView(R.id.pretent_cover_list)
    GridView PretentCoverList;

    @InjectView(R.id.pretent_icon_list)
    GridView pretentIconList;

//    @InjectView(R.id.new_normal_bar)
//    LinearLayout fakeReturn;

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

    static final int[] sections = {
            R.string.security_myfake,
            R.string.security_pretent_selector
    };

    static final int[] fakes = {
            R.string.security_pretent_none, R.drawable.security_myfake_2,
            R.string.security_pretent_fc, R.drawable.security_myfake_2,
            R.string.security_pretent_finger, R.drawable.security_myfake_2
    };

    static final int[] icons = {
            R.string.security_pretent_icon_default, R.drawable.security_myfake_default,
            R.string.security_pretent_icon_1, R.drawable.security_myfake_1,
            R.string.security_pretent_icon_2, R.drawable.security_myfake_2,
            R.string.security_pretent_icon_3, R.drawable.security_myfake_3,
            R.string.security_pretent_calender, R.drawable.security_myfake_4,
            R.string.security_pretent_notepad, R.drawable.security_myfake_5


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
        currentFakeCover = SecurityMyPref.getFakeCover(PretentPresenter.PRETENT_NONE);
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
                ViewHolder holder = (ViewHolder) holderObject;
                holder.appName.setText(icons[position << 1]);
                holder.fakeicon.setImageResource(icons[(position << 1) + 1]);
                holder.encrypted.setVisibility(currentFakeIcon == position ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            protected Object getHolder(View root) {
                return new ViewHolder(root);
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
        stopService(new Intent(this, NotificationService.class));
        startService(new Intent(this, NotificationService.class));
    }

    @OnItemClick(R.id.pretent_cover_list)
    public void activeFakeCover(int which) {
        if (currentFakeCover == which) {
            return;
        }
        switch (which) {
            case PretentPresenter.PRETENT_NONE:
                SecurityMyPref.setFakeCover(PretentPresenter.PRETENT_NONE);
                currentFakeCover = which;
                Utils.notifyDataSetChanged(PretentCoverList);
                Toast.makeText(App.getContext(), R.string.security_pretent_none, Toast.LENGTH_SHORT).show();
                break;

            case PretentPresenter.PRETENT_FC:
                AlertDialog dialog = PretentPresenter.showFC(App.getContext(), R.string.security_myfake, Html.fromHtml(getString(R.string.security_pretent_setting_msg)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.security_pretent_fails, Toast.LENGTH_SHORT).show();
                            }
                        }, new MessageBox.OnLongClickListener<AlertDialog>() {
                            @Override
                            public boolean onLongClick(View v) {
                                SecurityMyPref.setFakeCover(PretentPresenter.PRETENT_FC);
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
                PretentPresenter.show(App.getContext(), PretentPresenter.PRETENT_SCAN_SETTING, null, new Runnable() {
                    @Override
                    public void run() {
                        SecurityMyPref.setFakeCover(PretentPresenter.PRETENT_SCAN);
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
                Uri uri = Uri.parse(SecurityMenu.FACEBOOK);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_FACEBOOK, Tracker.ACT_FACEBOOK, 1L);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(SecurityMenu.GOOGLE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLUS, Tracker.ACT_GOOGLE_PLUS, 1L);

            }
        });

        googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(SecurityMenu.GOOGLEPLAY);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Tracker.sendEvent(Tracker.ACT_LLIDE_MENU, Tracker.ACT_GOOGLE_PLAY, Tracker.ACT_GOOGLE_PLAY, 1L);


            }
        });

    }


}
