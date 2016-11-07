package com.privacy.lock;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.security.manager.lib.Utils;
import com.security.manager.lib.controller.CListViewAdaptor;
import com.security.manager.lib.controller.CListViewScroller;
import com.security.manager.lib.io.SafeDB;
import com.privacy.lock.io.ProfileDBHelper;
import com.privacy.lock.meta.MProfiles;
import com.privacy.lock.meta.Pref;
import com.privacy.lock.view.AppsFragment;

import java.util.*;

/**
 * Created by SongHualin on 3/24/2015.
 */
public class Profiles extends ClientActivity {
    public static final int ADD_PROFILE = REQ_CODE_USER + 1;
    public static final int EDIT_PROFILE = REQ_CODE_USER + 2;

    @Override
    protected boolean hasHelp() {
        return false;
    }

    @OnClick(R.id.add_profile)
    public void onButtonClicked() {
        if (editing){
            List<ProfileDBHelper.ProfileEntry> entries = MProfiles.getEntries();
            boolean deleted = false;
            for(int i=selected.length - 1; i>=0; --i){
                if (selected[i]){
                    ProfileDBHelper.ProfileEntry.deleteProfile(MProfiles.getDB(), entries.remove(i).id);
                    deleted = true;
                }
            }
            if (deleted){
                MProfiles.updateProfiles();
                Toast.makeText(this, R.string.del_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.noting_deleted, Toast.LENGTH_SHORT).show();
            }
            exitEditMode();
        } else {
            startActivityForResult(new Intent(this, AddProfile.class), ADD_PROFILE);
        }
    }

    boolean hasUpdate = false;
    @Override
    protected void onReceiveActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (requestCode == ADD_PROFILE){
            MProfiles.waiting(waitingForProfile);
        } else if (requestCode == EDIT_PROFILE) {
            Utils.LOGER("here here");
            try {
                server.notifyApplockUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                hasUpdate = true;
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        if (hasUpdate) {
            hasUpdate = false;
            try {
                server.notifyApplockUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @InjectView(R.id.add_profile)
    Button actionButton;

    @InjectView(R.id.abs_list)
    ListView listView;

    @OnItemClick(R.id.abs_list)
    public void onItemClick(View view, int position) {
        if (editing){
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.box.setChecked(!selected[position]);
        } else {
            ProfileDBHelper.ProfileEntry entry = MProfiles.getEntries().get(position);
            startActivityForResult(new Intent(this, AddProfile.class).putExtra(AppsFragment.PROFILE_NAME_KEY, entry.name).putExtra(AppsFragment.PROFILE_ID_KEY, entry.id), EDIT_PROFILE);
        }
    }

    @InjectView(R.id.edit_mode)
    ImageButton enterEdit;

    boolean editing;

    public void enterEditMode() {
        if (editing){
            exitEditMode();
        } else {
            enterEdit.setSelected(true);
            editing = true;
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
            actionButton.setText(R.string.del_title);
        }
    }

    @Override
    protected void exitEditMode(){
        editing = false;
        waitingForProfile.run();
        Utils.notifyDataSetChanged(listView);
        enterEdit.setSelected(false);
        actionButton.setText(R.string.float_action_add_profile);
    }

    ViewHolder lastHolder;
    String profileName;
    Toast toast;

    @Override
    public void setupView() {
        setContentView(R.layout.profile_manager);
        ButterKnife.inject(this);

        setup(R.string.profile);

        findViewById(R.id.search_container).setVisibility(View.GONE);
        findViewById(R.id.search_button).setVisibility(View.GONE);

        enterEdit.setVisibility(View.VISIBLE);

        profileName = SafeDB.defaultDB().getString(Pref.PREF_ACTIVE_PROFILE, Pref.PREF_DEFAULT_LOCK);

        listView.setAdapter(new CListViewAdaptor(new CListViewScroller(listView), R.layout.profile_it) {

            @Override
            protected void onUpdate(int position, Object holderObject, boolean scrolling) {
                ViewHolder holder = (ViewHolder) holderObject;
                String profile = MProfiles.getProfiles()[position];
                holder.idx = position;
                holder.appName.setText(profile);
                if (profile.equals(profileName)) {
                    if (editing) {
                        holder.box.setEnabled(false);
                        holder.box.setChecked(true);
                    } else {
                        lastHolder = holder;
                        holder.box.setEnabled(true);
                        holder.box.setChecked(true);
                    }
                } else {
                    if (editing) {
                        holder.box.setChecked(selected[position]);
                    } else {
                        holder.box.setEnabled(true);
                        holder.box.setChecked(false);
                    }
                }
            }

            @Override
            protected Object getHolder(View root) {
                final ViewHolder holder = new ViewHolder(root);
                holder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (editing) {
                            selected[holder.idx] = isChecked;
                        } else {
                            if (lastHolder == holder) {
                                if (!isChecked) {
                                    holder.box.setChecked(true);
                                }
                                return;
                            } else if (!isChecked) return;

                            ProfileDBHelper.ProfileEntry entry = MProfiles.getEntries().get(holder.idx);
                            profileName = entry.name;
                            MProfiles.switchProfile(entry, server);
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(Profiles.this, getString(R.string.profile_switch_done, profileName), Toast.LENGTH_SHORT);
                            toast.show();

                            ViewHolder holder2 = lastHolder;
                            lastHolder = holder;
                            if (holder2 != null) {
                                holder2.box.setChecked(false);
                            }
                        }
                    }
                });

                return holder;
            }

            @Override
            public int getCount() {
                return count;
            }
        });

        MProfiles.waiting(waitingForProfile);
    }

    boolean selected[];
    int count;

    private Runnable waitingForProfile = new Runnable() {
        @Override
        public void run() {
            int length = MProfiles.getProfiles().length;
            selected = new boolean[length];
            count = length;
            Utils.notifyDataSetChanged(listView);
        }
    };
}