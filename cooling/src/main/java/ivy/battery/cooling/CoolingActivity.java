package ivy.battery.cooling;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class CoolingActivity extends AppCompatActivity {


    private Random random;
    private int cool = 0;
    private FlakeView flakeView;
    private static final int FLAKE_NUM = 5;
    public static CoolingActivity coolingActivity = new CoolingActivity();

    public static CoolingActivity.BatteryCool batterycool;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setFinishResult(0);
        }
        return super.onKeyDown(keyCode, event);
    }


    public void setCoolLisenter(BatteryCool batteryCoolLis) {
        this.batterycool = batteryCoolLis;

    }

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (flakeView != null) {
                flakeView.addFlakes(FLAKE_NUM);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooling);
        initViews();
        startCoolingAni();
        if (complete != null) {
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFinishResult(cool);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        flakeView = new FlakeView(this);
        flakeContent.addView(flakeView);
        mHandler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSnow();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null && runnable != null) {
            mHandler.removeCallbacks(runnable);
        }
        cancelAni();
        super.onDestroy();
    }

    private FrameLayout coolingView;
    private ImageView fan;
    private LinearLayout endView;
    private FrameLayout complete;
    private TextView state;
    private LinearLayout flakeContent;
    private FrameLayout content;
    private TextView temp;

    private void initViews() {
        coolingView = (FrameLayout) findViewById(R.id.ivy_battery_cooling_view);
        fan = (ImageView) findViewById(R.id.ivy_battery_cooling_fan);
        endView = (LinearLayout) findViewById(R.id.ivy_battery_cooling_end);
        complete = (FrameLayout) findViewById(R.id.ivy_battery_cooling_complete);
        state = (TextView) findViewById(R.id.ivy_battery_cooling_state);
        flakeContent = (LinearLayout) findViewById(R.id.ivy_battery_cooling_flake_content);
        content = (FrameLayout) findViewById(R.id.ivy_battery_activity_cooling);
        temp = (TextView) findViewById(R.id.ivy_battery_cooling_temp);
    }

    private void startCoolingAni() {
        state.setText(R.string.ivy_battery_cooling_on);
        random = new Random();
        int times = random.nextInt(10);
        if (times <= 2) {
            times = 3;
        }
        final int lastDegree = times * 1080;
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, lastDegree);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (fan != null) {
                    fan.setRotation(value);
                }
                if (value == lastDegree) {
                    startEndAni();
                    hideSnow();
                }
            }
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.start();
    }

    private void hideSnow() {
        if (flakeView != null) {
            flakeView.subtractFlakes(FLAKE_NUM);
            flakeView.pause();
            flakeView = null;
            mHandler.removeCallbacks(runnable);
        }
        if (flakeContent != null) {
            flakeContent.removeAllViews();
            flakeContent.setVisibility(View.GONE);
        }
    }

    private void startEndAni() {
        ObjectAnimator fanAni = ObjectAnimator.ofFloat(coolingView, View.ALPHA, 1.0f, 0.0f);
        ObjectAnimator fanScaleX = ObjectAnimator.ofFloat(coolingView, View.SCALE_X, 1.0f, 0.0f);
        ObjectAnimator fanScaleY = ObjectAnimator.ofFloat(coolingView, View.SCALE_Y, 1.0f, 0.0f);
        ObjectAnimator endAni = ObjectAnimator.ofFloat(endView, View.ALPHA, 0.0f, 1.0f);
        ObjectAnimator endScaleX = ObjectAnimator.ofFloat(endView, View.SCALE_X, 0.0f, 1.0f);
        ObjectAnimator endScaleY = ObjectAnimator.ofFloat(endView, View.SCALE_Y, 0.0f, 1.0f);
        ObjectAnimator completeScaleX = ObjectAnimator.ofFloat(complete, View.SCALE_X, 0.0f, 0.0f);

        AnimatorSet endSet = new AnimatorSet();
        endSet.play(fanAni).with(fanScaleX).with(fanScaleY).with(endAni).with(endScaleX).with(endScaleY).with(completeScaleX);
        endSet.setDuration(600);

        endSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                try {
                    new Thread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();


                if (batterycool != null) {
                    batterycool.coolFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (endView != null) {
            endView.setVisibility(View.VISIBLE);
        }
        if (complete != null) {
            complete.setVisibility(View.VISIBLE);
        }
        if (coolingView != null && endView != null && complete != null && endSet != null) {
            endSet.start();
        }
        if (temp != null) {
            if (random == null) {
                random = new Random();
            }
            cool = random.nextInt(4);
            if (cool == 0) {
                cool = 1;
            }
            temp.setText(cool + "");
        }
        if (state != null) {
            state.setText(R.string.ivy_battery_cooling_end);
        }
    }

    private void setFinishResult(int temp) {
//        Intent intent = new Intent(CoolingActivity.this, MainActivity.class);
//        intent.putExtra(Constants.COOLING_RESULT, temp);
//        setResult(Constants.START_COOLING_ACTIVITY_REQUEST_CODE, intent);
        CoolingActivity.this.finish();
    }

    private void cancelAni() {
        try {
            if (fan != null) {
                fan.clearAnimation();
            }
            if (endView != null) {
                endView.clearAnimation();
            }
            if (coolingView != null) {
                coolingView.clearAnimation();
            }
            if (complete != null) {
                complete.clearAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface BatteryCool {

        public void coolFinish();

    }

    public static CoolingActivity getCool() {
        return coolingActivity;

    }

}
