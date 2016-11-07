package com.privacy.lock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.privacy.lock.view.AppsFragment;
import com.privacy.lock.view.MessageBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongHualin on 3/20/2015.
 */
public class AddProfile extends AbsActivity{
    AppsFragment fragment;
    String profileName;

    @Override
    public List<SearchThread.SearchData> getSearchList() {
        return fragment.getSearchData();
    }

    protected void onSearchExit() {
        fragment.onResult(null);
    }

    public void onResult(ArrayList<SearchThread.SearchData> list) {
        fragment.onResult(list);
    }

    @OnClick(R.id.save)
    public void onSave() {
        if (profileName != null){
            save();
        } else {
            final EditText profile = new EditText(this);
            new AlertDialog.Builder(this).setTitle(getString(R.string.set_profile_title)).setView(profile).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String profileName = profile.getText().toString();
                    if (profileName.length() == 0) {
                        MessageBox.Data data = new MessageBox.Data();
                        data.msg = R.string.profile_name_is_empty;
                        data.title = R.string.profile_title_empty;
                        MessageBox.show(AddProfile.this, data);
                        return;
                    } else if (App.getSharedPreferences().contains(profileName)) {
                        MessageBox.Data data = new MessageBox.Data();
                        data.msg = R.string.profile_name_already_exists;
                        data.title = R.string.profile_title_exists;
                        MessageBox.show(AddProfile.this, data);
                        return;
                    }
                    AbsActivity.showSoftKeyboard(AddProfile.this, null, false);
                    dialog.dismiss();
                    AddProfile.this.profileName = profileName;
                    save();
                }
            }).setNegativeButton(android.R.string.cancel, null).create().show();
            App.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AbsActivity.showSoftKeyboard(AddProfile.this, profile, true);
                }
            }, 200);
        }
    }

    public void save() {
        fragment.saveOrCreateProfile(profileName, null);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected boolean hasHelp() {
        return false;
    }

    protected int getBackImage() {
        return R.drawable.icon;
    }

    long profileId;
    @Override
    protected void onIntent(Intent intent) {
        profileName = intent.getStringExtra(AppsFragment.PROFILE_NAME_KEY);
        profileId = intent.getLongExtra(AppsFragment.PROFILE_ID_KEY, 0L);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppsFragment.PROFILE_NAME_KEY, profileName);
        outState.putLong(AppsFragment.PROFILE_ID_KEY, profileId);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        profileId = savedInstanceState.getLong(AppsFragment.PROFILE_ID_KEY);
        profileName = savedInstanceState.getString(AppsFragment.PROFILE_NAME_KEY);
    }

    @Override
    public void setupView() {
        setContentView(R.layout.profile_new);
        ButterKnife.inject(this);

        fragment = (AppsFragment) getFragmentManager().findFragmentByTag("fragment");
        if (fragment == null) {
            fragment = new AppsFragment();
            Bundle args = new Bundle();
            args.putLong(AppsFragment.PROFILE_ID_KEY, profileId);
            args.putString(AppsFragment.PROFILE_NAME_KEY, profileName);
            fragment.setArguments(args);
        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "fragment").commit();

        setup(R.string.float_action_add_profile);
    }
}
