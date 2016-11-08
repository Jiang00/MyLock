package com.security.manager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.privacy.lock.R;
import com.security.manager.page.ListViewForScrollView;
import com.security.manager.meta.Pref;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.lib.customview.ReloadableImageView;
import com.security.mymodule.FileType;
import com.security.mymodule.IntruderEntry;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by song on 15/10/8.
 */
public class IntruderActivity extends AbsActivity {
    @InjectView(R.id.abs_list)
    ListViewForScrollView listView;

    @InjectView(R.id.tip)
    TextView tip;

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

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

        setContentView(R.layout.security_intruder_container);
        ButterKnife.inject(this);
        setupToolbar();

        normalTitle.setText("   " + getResources().getString(R.string.security_new_intruder));
        normalTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.security_back), null, null, null);

        normalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        title.setText(R.string.intruder);
//        title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_action_back_dark), null, null, null);
//        title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(v.getContext(),AppLock.class);
//                startActivity(intent);
//            }
//        });


    }

    @Override
    public void setupView() {

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
//                    tip.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        findViewById(R.id.help).setVisibility(View.GONE);
        findViewById(R.id.search_button).setVisibility(View.GONE);

        CheckBox checkBox = (CheckBox) findViewById(R.id.intruder_switch);
        checkBox.setChecked(Pref.fetchIntruder());


        intruderEntries = IntruderApi.getIntruders();
        if (intruderEntries.size() == 0) {
            listView.setVisibility(View.GONE);
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


                    ((ReloadableImageView) holder.icon).setImage(entry.url, 0L, FileType.TYPE_PIC, !scroll);
                    holder.icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(IntruderActivity.this, IntrudedeImageActivity.class);
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

                        CharSequence label = packageInfo.applicationInfo.loadLabel(context.getPackageManager());
//                        holder.blockmessage.setText(getResources().getString(R.string.block_intruder_for_app, label));
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

    @Override
    public void onBackPressed() {
        this.finish();
        Log.e("mtt","back");
        Intent intent = new Intent(this, AppLock.class);
        startActivity(intent);
        overridePendingTransition(R.anim.security_slide_in_left, R.anim.security_slide_right);
        super.onBackPressed();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.security_setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.intrude_set) {
            Intent i=new Intent(this,InvadeSetActivity.class);
            startActivity(i);

        }
        return true;
    }




}
