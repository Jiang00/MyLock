package com.security.manager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.privacy.lock.R;
import com.security.manager.lib.Utils;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.ListViewForScrollView;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.lib.customview.SecurityloadImage;
import com.security.manager.page.SlideMenu;
import com.security.mymodule.FileType;
import com.security.mymodule.IntruderEntry;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by song on 15/10/8.
 */
public class IntruderActivitySecurity extends ClientActivitySecurity {
    @InjectView(R.id.abs_list)
    ListViewForScrollView listView;

    @InjectView(R.id.tip)
    TextView tip;

    CListViewAdaptor adapter;
    ArrayList<IntruderEntry> intruderEntries;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onIntent(Intent intent) {

    }

    @Override
    protected boolean hasHelp() {
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void setupView() {
        setContentView(R.layout.security_intruder_container);
        ButterKnife.inject(this);

        setupToolbar();

        if (Utils.isMIUI()) {
            if (!SecurityMyPref.getintruderCamer() ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                SecurityMyPref.setintruderCamer(true);
            }
        }

        setup(R.string.security_intrude_five);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            int position = data.getIntExtra("position", -1);
            if (position >= 0) {
                IntruderEntry intruder = intruderEntries.remove(position);
                IntruderApi.deleteIntruder(intruder);
                adapter.notifyDataSetChanged();
                if (intruderEntries.size() == 0) {
                    listView.setVisibility(View.GONE);
                    tip.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        intruderEntries = IntruderApi.getIntruders();
        if (intruderEntries.size() == 0) {
            listView.setVisibility(View.GONE);
            tip.setVisibility(View.VISIBLE);
        } else {
            tip.setVisibility(View.GONE);
            ListView lv = listView;
            adapter = new CListViewAdaptor(new CListViewScroller(lv), R.layout.security_intruder) {
                @Override
                protected void onUpdate(final int position, Object holderObject, boolean scroll) {
                    ViewHolder holder = (ViewHolder) holderObject;
                    if (position >= intruderEntries.size()) return;
                    final IntruderEntry entry = intruderEntries.get(position);
                    holder.idx = position;
                    holder.appName.setText(entry.date);
                    ((SecurityloadImage) holder.icon).setImage(entry.url, 0L, FileType.TYPE_PIC, !scroll);
                    holder.icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(IntruderActivitySecurity.this, IntrudedeImageActivity.class);
                            intent.putExtra("url", entry.url);
                            intent.putExtra("date", entry.date);
                            intent.putExtra("pkg", entry.pkg);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, 1);


                        }
                    });

                    try {
                        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(entry.pkg, 0);
                        Drawable icon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                        holder.lockIcon.setBackgroundDrawable(icon);

                    } catch (PackageManager.NameNotFoundException e) {
                        holder.lockIcon.setBackgroundResource(R.drawable.ic_launcher);

                    }

                }

                @Override
                protected Object getHolder(View root) {
                    final ViewHolder viewHolder = new ViewHolder(root);
                    viewHolder.encrypted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntruderEntry intruder = intruderEntries.remove(viewHolder.idx);
                            IntruderApi.deleteIntruder(intruder);
                            notifyDataSetChanged();
                            if (intruderEntries.size() == 0) {
                                listView.setVisibility(View.GONE);
//                                tip.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    return viewHolder;
                }

                @Override
                public int getCount() {
                    return intruderEntries.size();
                }
            };

            lv.setAdapter(adapter);
        }
    }


    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.security_slide_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_new_intruder);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.security_setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.intrude_set) {
            Intent i = new Intent(this, SecurityIntruderSetting.class);
            startActivity(i);

        } else if (item.getItemId() == android.R.id.home) {
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
}
