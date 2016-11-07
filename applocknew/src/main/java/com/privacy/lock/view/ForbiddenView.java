package com.privacy.lock.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.privacy.lib.view.MySurfaceView;
import com.privacy.lock.R;
import com.privacy.lock.meta.ThemeBridge;

/**
 * Created by SongHualin on 4/2/2015.
 */
public class ForbiddenView {
    private int wrong_time;
    private ViewStub forbidden;
    private View wrongView;
    private ValueAnimator colorAnim;
    private boolean catchIntruder = true;

    public ForbiddenView(ViewStub forbidden) {
        this.forbidden = forbidden;
    }

    public void right() {
        wrong_time = 0;
        catchIntruder = true;
    }

    public void wrong() {
        if (wrongView == null) {
            return;
        }
        wrong_time += 1;

        if (wrong_time == 1) {
            if (catchIntruder) {
                String currentApp = ThemeBridge.bridge.currentPkg();
                ((MySurfaceView) wrongView.findViewById(R.id.surface)).catchIntruder(currentApp);
                catchIntruder = false;
            }
        }

        if (wrong_time > 4) {
            wrong_time = 0;
            new CountDownTimer(6200, 1000) {
                byte count = 6;

                @Override
                public void onTick(long millisUntilFinished) {
                    --count;
                    ((TextView) wrongView.findViewById(R.id.count_down)).setText("00:0" + count);
                }

                @Override
                public void onFinish() {
                    hideTips();
                }
            }.start();
            colorAnim.start();
            showTips();
        }
    }

    public void init() {
        if (forbidden.getParent() == null) {
            return;
        }
        wrongView = forbidden.inflate();
        colorAnim = ObjectAnimator.ofInt(wrongView, "backgroundColor", 0xffff2020, 0xff8080ff);
        colorAnim.setDuration(5000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        hideTips();
    }

    public void hideTips() {
        wrongView.findViewById(R.id.count_down).setVisibility(View.GONE);
        wrongView.findViewById(R.id.try_later).setVisibility(View.GONE);
        wrongView.setClickable(false);
        wrongView.setBackgroundDrawable(null);
    }

    public void showTips() {
        wrongView.findViewById(R.id.count_down).setVisibility(View.VISIBLE);
        wrongView.findViewById(R.id.try_later).setVisibility(View.VISIBLE);
        wrongView.setClickable(true);
    }
}
