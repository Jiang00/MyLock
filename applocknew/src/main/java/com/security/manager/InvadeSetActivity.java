package com.security.manager;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.privacy.lock.R;
import com.security.manager.lib.BaseActivity;
import com.security.manager.meta.Pref;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by song on 15/12/25.
 */
public class InvadeSetActivity extends BaseActivity {

//    @InjectView(R.id.suo_title_bar_te)
//    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.suo_invade_set);
        ButterKnife.inject(this);


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

            addPreferencesFromResource(R.xml.suo_invade_set);

            CheckBoxPreference intruder = (CheckBoxPreference) findPreference(SETTING_CATCH_INTRUDER);
            intruder.setChecked(Pref.fetchIntruder());//com.lockscreen.lock.Preference.isFetchIntruderEnabled()
            intruder.setOnPreferenceChangeListener(this);

            Preference preference = findPreference(SETTING_CATCH_INTRUDER_SLOT);
            String[] summaries = getResources().getStringArray(R.array.suo_ruqinzhe_slot);
            int slot = com.security.manager.db.Preference.getIntruderSlot();
            preference.setSummary(summaries[slot]);
            preference.setOnPreferenceChangeListener(this);

            CheckBoxPreference shutter = (CheckBoxPreference) findPreference(SETTING_INTRUDER_SHUTTER);
            shutter.setChecked(com.security.manager.db.Preference.isShutterSoundEnabled());
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
                    com.security.manager.db.Preference.setIntruderSlot(slot);
                    Log.i("aaaaaa","--------------"+slot);
                    lp.setSummary(lp.getEntry());
                }
                break;

                case SETTING_CATCH_INTRUDER: {
                    boolean yes = (boolean) newValue;
                    Pref.setFetchIntruder(yes);
//                    com.lockscreen.lock.Preference.setCatchIntruderEnable(yes);
                }
                break;

                case SETTING_INTRUDER_SHUTTER: {
                    boolean yes = (boolean) newValue;
                    com.security.manager.db.Preference.setShutterSoundEnabled(yes);
                }
                break;
            }
            return true;
        }

    }

//    public void showTutorial() {
//        final View view = getLayoutInflater().inflate(R.layout.suo_tut_dial, null, false);
//        final View bg = view.findViewById(R.id.suo_backg_tt);
//        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//        final int shutterSoundId = soundPool.load(InvadeSetActivity.this, R.raw.shutter, 0);
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    soundPool.play(shutterSoundId, 1, 1, 1, 0, 1);
//                    bg.startAnimation(AnimationUtils.loadAnimation(InvadeSetActivity.this, R.anim.activi_in));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 200);
//        bg.setBackgroundResource(R.drawable.suo_invade_se);
//        ((TextView) view.findViewById(R.id.suo_tt_tishi)).setText(R.string.suo_intru_tishi);
//        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.invade_dia).setView(view).create();
//        view.findViewById(R.id.suo_invad_got).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                soundPool.release();
//                dialog.cancel();
//            }
//        });
//        dialog.show();
//    }
}
