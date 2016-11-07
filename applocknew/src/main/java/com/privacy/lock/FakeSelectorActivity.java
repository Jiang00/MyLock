package com.privacy.lock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.privacy.lock.meta.Pref;
import com.privacy.lock.view.FakePresenter;
import com.privacy.lock.view.MessageBox;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by song on 15/8/18.
 */
public class FakeSelectorActivity extends AbsActivity {
    @InjectView(R.id.fake_cover_list)
    GridView fakeCoverList;

    @InjectView(R.id.fake_icon_list)
    GridView fakeIconList;

//    @InjectView(R.id.new_normal_bar)
//    LinearLayout fakeReturn;

    @InjectView(R.id.normal_title_name)
    TextView normalTitle;

    static final int[] sections = {
            R.string.fake,
            R.string.fake_selector
    };

    static final int[] fakes = {
            R.string.fake_none, R.drawable.fake_icon_2,
            R.string.fake_fc, R.drawable.fake_icon_2,
            R.string.fake_finger, R.drawable.fake_icon_2
    };

    static final int[] icons = {
            R.string.fake_icon_default, R.drawable.fake_default,
            R.string.fake_icon_1, R.drawable.fake_icon_1,
            R.string.fake_icon_2, R.drawable.fake_icon_2,
            R.string.fake_icon_3, R.drawable.fake_icon_3,
            R.string.fake_calender, R.drawable.fake_icon_4,
            R.string.fake_notepad, R.drawable.fake_icon_5


    };

    @Override
    protected boolean hasHelp() {
        return false;
    }

    int currentFakeCover = FakePresenter.FAKE_NONE;
    int currentFakeIcon = 0;

    @Override
    public void setupView() {
        setContentView(R.layout.security_myfake_selector);
        ButterKnife.inject(this);
        setup(R.string.fake);
        normalTitle.setText("   "+getResources().getString(R.string.fake));
        normalTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.security_back), null, null, null);

        findViewById(R.id.search_button).setVisibility(View.GONE);
        currentFakeCover = Pref.getFakeCover(FakePresenter.FAKE_NONE);
        currentFakeIcon = FakePresenter.fakeIconIdx();

        normalTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });

        fakeCoverList.setAdapter(new CListViewAdaptor(new CListViewScroller(fakeCoverList), R.layout.security_myfake_item) {
            @Override
            protected void onUpdate(int position, Object holderObject, boolean scrolling) {
                com.privacy.lock.ViewHolder holder = (com.privacy.lock.ViewHolder) holderObject;
                holder.appName.setText(fakes[position << 1]);
                holder.fakeicon.setImageResource(fakes[(position << 1) + 1]);
                holder.encrypted.setVisibility(currentFakeCover == position ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            protected Object getHolder(View root) {
                return new com.privacy.lock.ViewHolder(root);
            }

            @Override
            public int getCount() {
                return fakes.length >> 1;
            }
        });

        fakeIconList.setAdapter(new CListViewAdaptor(new CListViewScroller(fakeIconList), R.layout.security_myfake_item) {
            @Override
            protected void onUpdate(int position, Object holderObject, boolean scrolling) {
                com.privacy.lock.ViewHolder holder = (com.privacy.lock.ViewHolder) holderObject;
                holder.appName.setText(icons[position << 1]);
                holder.fakeicon.setImageResource(icons[(position << 1) + 1]);
                holder.encrypted.setVisibility(currentFakeIcon == position ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            protected Object getHolder(View root) {
                return new com.privacy.lock.ViewHolder(root);
            }

            @Override
            public int getCount() {
                return icons.length >> 1;
            }
        });
    }

    @OnItemClick(R.id.fake_icon_list)
    public void switchFakeIcon(int which) {
        currentFakeIcon = which;
        FakePresenter.switchFakeIcon(which);
        Utils.notifyDataSetChanged(fakeIconList);
    }

    @OnItemClick(R.id.fake_cover_list)
    public void activeFakeCover(int which) {
        if (currentFakeCover == which) {
            return;
        }
        switch (which) {
            case FakePresenter.FAKE_NONE:
                Pref.setFakeCover(FakePresenter.FAKE_NONE);
                currentFakeCover = which;
                Utils.notifyDataSetChanged(fakeCoverList);
                Toast.makeText(App.getContext(), R.string.fake_none, Toast.LENGTH_SHORT).show();
                break;

            case FakePresenter.FAKE_FC:
                AlertDialog dialog = FakePresenter.showFC(App.getContext(), R.string.fake, Html.fromHtml(getString(R.string.fake_setting_msg)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.set_fake_fails, Toast.LENGTH_SHORT).show();
                            }
                        }, new MessageBox.OnLongClickListener<AlertDialog>() {
                            @Override
                            public boolean onLongClick(View v) {
                                Pref.setFakeCover(FakePresenter.FAKE_FC);
                                dialog.cancel();
                                currentFakeCover = FakePresenter.FAKE_FC;
                                MessageBox.Data data = new MessageBox.Data();
                                data.msg = R.string.set_fake_success;
                                MessageBox.show(FakeSelectorActivity.this, data);
                                return true;
                            }
                        });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utils.notifyDataSetChanged(fakeCoverList);
                    }
                });
                break;

            case FakePresenter.FAKE_SCAN:
                FakePresenter.show(App.getContext(), FakePresenter.FAKE_SCAN_SETTING, null, new Runnable() {
                    @Override
                    public void run() {
                        Pref.setFakeCover(FakePresenter.FAKE_SCAN);
                        currentFakeCover = FakePresenter.FAKE_SCAN;
                        FakePresenter.hide();
                        MessageBox.Data data = new MessageBox.Data();
                        data.msg = R.string.set_fake_success;
                        MessageBox.show(FakeSelectorActivity.this, data);
                        Utils.notifyDataSetChanged(fakeCoverList);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        FakePresenter.hide();
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        Intent intent = new Intent(this, AppLock.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        super.onBackPressed();
    }
}
