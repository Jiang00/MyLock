package com.security.manager;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.privacy.lock.R;
import com.security.manager.db.SecurityPreference;
import com.security.manager.lib.BaseActivity;
import com.security.manager.meta.SecurityMyPref;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ma on 15/12/25.
 */
public class SecurityIntruderSetting extends BaseActivity {

//    @InjectView(R.id.suo_title_bar_te)
//    TextView title;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.security_intrude_set);
        ButterKnife.inject(this);
        setupToolbar();


//        标题
//        title.setText("   "+getString(R.string.suo_intru_set));
//        title.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.suo_back), null, null, null);
//        findViewById(R.id.suo_up).setVisibility(View.GONE);
//        findViewById(R.id.suo_title_bt).setVisibility(View.GONE);
//        findViewById(R.id.suo_set_bt).setVisibility(View.GONE);
//        title.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        SettingFragment fragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(R.id.suo_frag_co, fragment).commitAllowingStateLoss();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.security_intruder_setting_new);
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
    protected void onIntent(Intent intent) {

    }

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        public static final String SETTING_CATCH_INTRUDER = "invade_set_kaiguan";
        public static final String SETTING_CATCH_INTRUDER_SLOT = "invade_set_n";
        public static final String SETTING_INTRUDER_SHUTTER = "invade_set_shengyin";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.security_intrude_set);

            CheckBoxPreference intruder = (CheckBoxPreference) findPreference(SETTING_CATCH_INTRUDER);
            intruder.setChecked(SecurityMyPref.fetchIntruder());
            intruder.setOnPreferenceChangeListener(this);

            Preference preference = findPreference(SETTING_CATCH_INTRUDER_SLOT);
            String[] summaries = getResources().getStringArray(R.array.suo_ruqinzhe_slot);
            int slot = SecurityPreference.getIntruderSlot();
            preference.setSummary(summaries[slot]);

            preference.setOnPreferenceChangeListener(this);

            CheckBoxPreference shutter = (CheckBoxPreference) findPreference(SETTING_INTRUDER_SHUTTER);
            shutter.setChecked(SecurityPreference.isShutterSoundEnabled());
            shutter.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()) {
                case SETTING_CATCH_INTRUDER_SLOT: {
                    ListPreference lp = (ListPreference) preference;
                    String value = (String) newValue;
                    int slot = Integer.parseInt(value);
                    lp.setValue(value);
                    SecurityPreference.setIntruderSlot(slot);
                    lp.setSummary(lp.getEntry());
                }
                break;

                case SETTING_CATCH_INTRUDER: {
                    boolean yes = (boolean) newValue;
                    SecurityMyPref.setFetchIntruder(yes);
//                    com.lockscreen.lock.SecurityPreference.setCatchIntruderEnable(yes);
                }
                break;

                case SETTING_INTRUDER_SHUTTER: {
                    boolean yes = (boolean) newValue;
                    SecurityPreference.setShutterSoundEnabled(yes);
                }
                break;
            }
            return true;
        }


    }
}
