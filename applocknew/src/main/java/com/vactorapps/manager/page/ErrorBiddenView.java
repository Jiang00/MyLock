package com.vactorapps.manager.page;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivymobi.applock.free.R;
import com.vactorapps.lib.customview.SecuritySurface;

import com.vactorapps.manager.meta.TheBridgeVac;
import com.vactorapps.manager.meta.VacPref;
import com.vactorapps.manager.mydb.VacPreference;
import com.vactorappsapi.manager.lib.BaseApp;

/**
 * Created by SongHualin on 4/2/2015.
 */
public class ErrorBiddenView {
    private int wrong_time;
    private ViewStub forbidden;
    private View wrongView;
    private ValueAnimator colorAnim;
    private boolean catchIntruder = true;
    private int wrongSum = 0;
    private int countdown = 0;
    private LinearLayout.LayoutParams layoutParams;
    public void right() {
        wrong_time = 0;
        catchIntruder = true;
    }

    public ErrorBiddenView(ViewStub forbidden) {
        this.forbidden = forbidden;
    }

    public void wrong() {
        if (wrongView == null) {
            return;
        }
        wrong_time += 1;
        if (wrong_time == VacPreference.getIntruderSlot()+1) {
            if (catchIntruder) {
                String currentApp = TheBridgeVac.bridge.currentPkg();
                ((SecuritySurface) wrongView.findViewById(R.id.surface)).catchIntruder(currentApp);
                catchIntruder = false;
            }
        }
        if (wrong_time > 4) {
            wrong_time = 0;
            wrongSum++;

            switch (wrongSum) {

                case 1:
                    countdown = 5000;
                    break;
                case 2:
                    countdown = 10000;
                    break;
                case 3:
                    countdown = 15000;
                    break;
                case 4:
                    countdown = 20000;
                    break;
                case 5:
                    countdown = 25000;
                    break;
                default:
                    countdown = 25000;
                    break;
            }
            layoutParams = (LinearLayout.LayoutParams) ((LinearLayout) wrongView.findViewById(R.id.error_lin)).getLayoutParams();
            if(VacPref.isUseNormalPasswd()){
                layoutParams.setMargins(0,0,0,130);//4个参数按顺序分别是左上右下
            }else{
                layoutParams.setMargins(0,0,0,230);
            }

            new CountDownTimer(countdown, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    ((LinearLayout) wrongView.findViewById(R.id.error_lin)).setLayoutParams(layoutParams); //mView是控件
                    ((TextView) wrongView.findViewById(R.id.count_down)).setVisibility(View.VISIBLE);
                    ((TextView) wrongView.findViewById(R.id.try_later)).setText(BaseApp.getContext().getResources().getString(R.string.security_try_later)+":"+millisUntilFinished / 1000+"");
                }

                @Override
                public void onFinish() {
                    hideTips();
                }
            }.start();
            colorAnim.start();
//            showTips();
        }
        // }
    }


    public void init() {
        if (forbidden.getParent() == null) {
            return;
        }
        wrongView = forbidden.inflate();
        colorAnim = ObjectAnimator.ofInt(wrongView, "backgroundColor", 0x00000000, 0x00000000);
        colorAnim.setDuration(5000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        hideTips();
    }

    public void hideTips() {
        ((TextView) wrongView.findViewById(R.id.count_down)).setVisibility(View.GONE);
        wrongView.findViewById(R.id.try_later).setVisibility(View.GONE);
        wrongView.setClickable(false);
        wrongView.setBackgroundDrawable(null);

    }

    public void showTips() {
        wrongView.findViewById(R.id.try_later).setVisibility(View.VISIBLE);
        wrongView.setClickable(true);
    }
}
