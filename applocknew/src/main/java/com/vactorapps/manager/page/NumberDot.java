package com.vactorapps.manager.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vactorapps.manager.meta.VacPref;

/**
 * Created by superjoy on 2014/10/28.
 */
public class NumberDot extends LinearLayout {
    public interface ICheckListener {
        public void match(String pass);
    }

    private int getId(String id, String type) {

        return  getResources().getIdentifier(id,type,getContext().getPackageName());
    }

    public NumberDot(Context context) {
        super(context);
    }

    Animation a;

    public NumberDot(Context context, AttributeSet attrs) {
        super(context, attrs);

        int drawable = attrs.getAttributeResourceValue(null, "drawable", getId("whitelist_security_password_dot_cre","drawable"));

        setCount(6, drawable);
        realPasswd = VacPref.getPasswd().toCharArray();
        a = AnimationUtils.loadAnimation(getContext(), getId("whitelist_security_shake_dot","anim"));
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rightReset();

                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    char[] realPasswd;

    private void setCount(int count, int drawable) {
        passwd = new char[count];
        dots = new ImageView[count];
        int size = getResources().getDimensionPixelSize(getId("d10","dimen"));
        for (int i = 0; i < count; ++i) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageResource(drawable);
            dots[i].setVisibility(GONE);
            LayoutParams llp = new LayoutParams(size, size);
            llp.setMargins(8, 8, 8, 8);
            addView(dots[i], llp);
        }
        dots[0].setVisibility(INVISIBLE);
        len = (byte) (count - 1);
    }

    char[] passwd;
    ImageView[] dots;
    byte idx = 0;
    byte len;
    boolean create;
    ICheckListener finish;
    public ErrorBiddenView errorBiddenView;

    public boolean empty(){
        return idx == 0;
    }

    public void setFlag(boolean create) {
        this.create = create;
        if (!create){
            realPasswd = new char[idx];
            System.arraycopy(passwd, 0, realPasswd, 0, idx);
        }
    }

    public void init(ICheckListener finish) {
        this.finish = finish;
    }

    boolean animating = false;


    public void setErrorBiddenView(ErrorBiddenView errorbiddenview){
        this.errorBiddenView=errorbiddenview;

    }

    public void setNumber(char num) {
        if (animating || idx > len) return;

        if (idx <= len) {
            passwd[idx] = num;
            dots[idx].setVisibility(VISIBLE);
            if (!create) {
                int length = realPasswd.length;
                if (idx == length -1) {
                    boolean match = true;
                    for (int i = 0; i < length; i++) {
                        if (realPasswd[i] != passwd[i]) {
                            match = false;
                            break;
                        }
                    }
                    if (match){
                        for (int i = 0; i < length; ++i) {
                            dots[i].setEnabled(false);
                        }
                        finish.match(new String(realPasswd));
                        return;
                    }
                }
            }
        }

        if (++idx > len && !create) {
            for (ImageView iv : dots) {
                iv.setSelected(true);
            }
            startAnimation(a);
            if (errorBiddenView != null)
                errorBiddenView.wrong();
        }
    }

    public void backSpace() {
        if (animating) return;

        if (idx > 0) {
            --idx;
        }
        if (idx == 0){
            dots[idx].setVisibility(INVISIBLE);
        } else {
            dots[idx].setVisibility(GONE);
        }
    }

    public void reset() {
        idx = 0;
        for (ImageView v : dots) {
            v.setEnabled(true);
            v.setSelected(false);
            v.setVisibility(GONE);
        }
        dots[0].setVisibility(INVISIBLE);
        if (errorBiddenView != null) {
            errorBiddenView.right();
        }
    }

    public void rightReset() {
        idx = 0;
        for (ImageView v : dots) {
            v.setEnabled(true);
            v.setSelected(false);
            v.setVisibility(GONE);
        }
        dots[0].setVisibility(INVISIBLE);

    }



}
