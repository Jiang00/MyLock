package com.batteryvactorapps.module.charge.saver.protectview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.client.AndroidSdk;
import com.batteryvactorapps.module.charge.saver.acSetADActivity;
import com.batteryvactorapps.module.charge.saver.R;
import com.batteryvactorapps.module.charge.saver.utilsvac.BatteryConstants;
import com.batteryvactorapps.module.charge.saver.utilsvac.ADRequest;
import com.batteryvactorapps.module.charge.saver.utilsvac.MyUtils;
import com.batteryvactorapps.module.charge.saver.entry.BatteryEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProtectBatteryView extends FrameLayout {

    private Context mContext;
    private GestureDetector detector;
    private float distance;
    private boolean isBindView = false;
    private boolean isRegisterTimeUpdate = false;
    private View adView;
    private ProtectBatteryView.UnlockListener listener;

    private ProtectBatteryView batteryView;
    private LinearLayout adLayout;
    private TextView title;
    private LinearLayout more;
    private LinearLayout switchLayout;
    private CheckBox saverSwitch;
    private TextView time;
    private TextView date;
    private TextView battery_now_year;
    private TextView day;
    private TextView week;
    private TextView batteryLeft;
    private QiuBubbleLayout main_bubble;

    private int halfWidth;
    private ImageView battery_icon;
    private ChargWaterView battertext;
    //    private ImageView shutter;

    public interface UnlockListener {
        void onUnlock();
    }

    public ProtectBatteryView(Context context) {
        super(context, null);
    }

    public ProtectBatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ProtectBatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUnlockListener(UnlockListener unlockListener) {
        listener = unlockListener;
    }

    private BroadcastReceiver timerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTime();
        }
    };

    private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);

    private void showNativeAD() {
        adView = new ADRequest().showCustomNativeAD(BatteryConstants.TAG_CHARGING, R.layout.native_ad, null);
        if (adLayout != null && adView != null) {
            if (adLayout.getVisibility() == View.GONE) {
                adLayout.setVisibility(VISIBLE);
            }
            if (adView.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) adView.getParent();
                viewGroup.removeAllViews();
            }
            adLayout.removeAllViews();
            adLayout.addView(adView);
        }
        adLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float startX = event.getX();
                detector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if ((event.getX() - startX) > 20 || (startX - event.getX()) > 20) {
                            if ((event.getX() - startX) > halfWidth / 2) {
                                if (listener != null) {
                                    listener.onUnlock();
                                }
                            }
                            return true;
                        } else {
                            break;
                        }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    public void updateTime() {
        if (time != null && date != null && week != null) {
            try {
                long t = System.currentTimeMillis();
                Date d = new Date(t);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String str = sdf.format(d);
                time.setText(str);

                str = new SimpleDateFormat("yy", Locale.getDefault()).format(d);
                battery_now_year.setText(str);
                str = new SimpleDateFormat("MM", Locale.getDefault()).format(d);
                date.setText(str);
                str = new SimpleDateFormat("dd", Locale.getDefault()).format(d);
                day.setText(str);
                str = new SimpleDateFormat("EEEE").format(d);
                week.setText(str);
            } catch (Exception e) {
            }
        }
    }

    private int progress = 0;

    public void bind(BatteryEntry entry) {
        if (entry == null) {
            return;
        }
        final int curLevel = entry.getLevel();
        final int le = curLevel % 100;
        Log.e("chfq", "==curLevel==" + curLevel + "==" + le);

        battertext.start();
        battertext.upDate(curLevel);
        battertext.setFloatWaterListener(new ChargWaterView.FloatWaterListener() {

            @Override
            public void success() {
                battertext.stop();
            }

            @Override
            public void update(int jindu) {

            }
        });
        main_bubble.reStart();


        int leftChargeTime = entry.getLeftTime();
        if (batteryLeft != null) {
            String str;
            if (entry.isCharging()) {
                str = getResources().getString(R.string.charging_on_left2);
                if (curLevel == 100) {
                    str = getResources().getString(R.string.charging_on_left3);
                }
            } else {
                str = getResources().getString(R.string.charging_use_left);
            }
            String result = String.format(str, entry.extractHours(leftChargeTime), entry.extractMinutes(leftChargeTime));
            batteryLeft.setText(result);
        }
    }

    protected int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setCharing(boolean isVisible) {
        if (isVisible) {
        } else {
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isBindView) {
            initViews();
            isBindView = true;
            updateTime();
//            showNativeAD();

            halfWidth = (int) (((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 1.3f);
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (switchLayout != null && switchLayout.getVisibility() == View.VISIBLE) {
                        more.setVisibility(View.VISIBLE);
                        switchLayout.setVisibility(GONE);
                    }
                    detector.onTouchEvent(event);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (distance > halfWidth) {
                            if (listener != null) {
//                                batteryView.setAlpha(1.0f);
                                listener.onUnlock();
                            }
                        } else {
                            if (batteryView != null) {
//                                batteryView.setAlpha(1.0f);
                            }
                        }
                    }
                    return true;
                }
            });
            detector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (listener != null) {
//                        batteryView.setAlpha(1.0f);
                        listener.onUnlock();
                    }
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (e2.getX() - e1.getX() == distance) {
                        return true;
                    }
                    distance = e2.getX() - e1.getX();
                    if (batteryView != null) {
//                        batteryView.setTranslationX(distance);
//                        batteryView.setAlpha(1 - distance / halfWidth + .2f);
                    }
                    return true;
                }
            });

            String titleTxt = (String) MyUtils.readData(mContext, BatteryConstants.CHARGE_SAVER_TITLE, "Cleaner");
            title.setText(titleTxt);
            int titleIcon = (int) MyUtils.readData(mContext, BatteryConstants.CHARGE_SAVER_ICON, R.mipmap.battery_check);
            battery_icon.setImageResource(titleIcon);

            more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent("charging.intent.setting").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("from", "charging"));
//                    if (switchLayout != null) {
////                        more.setVisibility(View.GONE);
//                        switchLayout.setVisibility(VISIBLE);
//                    }
                }
            });

            if ((Boolean) MyUtils.readData(mContext, BatteryConstants.CHARGE_SAVER_SWITCH, true)) {
                if (saverSwitch != null) {
                    saverSwitch.setChecked(true);
                }
            } else {
                if (saverSwitch != null) {
                    saverSwitch.setChecked(false);
                }
            }
            saverSwitch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent("charging.intent.setting").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("from", "charging"));
//                    if ((Boolean) MyUtils.readData(mContext, BatteryConstants.CHARGE_SAVER_SWITCH, true)) {
//                        if (saverSwitch != null) {
//                            saverSwitch.setChecked(false);
//                            MyUtils.writeData(mContext, BatteryConstants.CHARGE_SAVER_SWITCH, false);
//                        }
//                    } else {
//                        if (saverSwitch != null) {
//                            saverSwitch.setChecked(true);
//                            MyUtils.writeData(mContext, BatteryConstants.CHARGE_SAVER_SWITCH, true);
//                        }
//                    }
                }
            });
        }
    }

    private void initViews() {
        main_bubble = (QiuBubbleLayout) findViewById(R.id.main_bubble);
        batteryView = (ProtectBatteryView) findViewById(R.id.battery_charge_save);
        switchLayout = (LinearLayout) findViewById(R.id.battery_switch);
        saverSwitch = (CheckBox) findViewById(R.id.battery_switch_check);
        adLayout = (LinearLayout) findViewById(R.id.battery_ad_layout);
        title = (TextView) findViewById(R.id.battery_title);
        battery_icon = (ImageView) findViewById(R.id.battery_icon);
        more = (LinearLayout) findViewById(R.id.battery_more);
        time = (TextView) findViewById(R.id.battery_now_time);
        date = (TextView) findViewById(R.id.battery_now_date);
        battery_now_year = (TextView) findViewById(R.id.battery_now_year);
        day = (TextView) findViewById(R.id.battery_now_day);
        week = (TextView) findViewById(R.id.battery_now_week);
        batteryLeft = (TextView) findViewById(R.id.battery_now_battery_left);
        battertext = (ChargWaterView) findViewById(R.id.battertext);

    }

    public void pauseBubble() {
        if (main_bubble != null) {
            main_bubble.pause();
        }
    }

    public void reStartBubble() {
        if (main_bubble != null) {
            main_bubble.reStart();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isRegisterTimeUpdate) {
            registerTimeUpdateReceiver();
        }
        if (!isBindView) {
            isBindView = true;
        }
    }

    boolean onDetachedFromWindow = false;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDetachedFromWindow = true;
        try {
            JSONObject object = new JSONObject(AndroidSdk.getExtraData());
            int state = analysisJson(object);
            if (state == 1) {
                mContext.startActivity(new Intent(mContext, acSetADActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isRegisterTimeUpdate) {
            unregisterTimeUpdateReceiver();
        }
        if (main_bubble != null) {
            main_bubble.destroy();
        }
        if (isBindView) {
            isBindView = false;
        }
    }

    private int analysisJson(JSONObject object) throws JSONException {
        int state = 0;
        if (object != null) {
            state = object.optInt("charging", 0);
        }
        return state;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            unregisterTimeUpdateReceiver();
        } else {
            updateTime();
            registerTimeUpdateReceiver();
        }
    }

    public void registerTimeUpdateReceiver() {
        mContext.registerReceiver(timerUpdateReceiver, mIntentFilter);
        isRegisterTimeUpdate = true;
    }

    public void unregisterTimeUpdateReceiver() {
        mContext.unregisterReceiver(timerUpdateReceiver);
        isRegisterTimeUpdate = false;
    }
}
