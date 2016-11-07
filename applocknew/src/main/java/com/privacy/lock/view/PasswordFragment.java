package com.privacy.lock.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import com.privacy.lock.App;
import com.privacy.lock.R;
import com.privacy.lock.Tools;
import com.privacy.lock.intf.IThemeBridge;
import com.privacy.lock.meta.CustomTheme;
import com.privacy.lock.meta.ThemeBridge;

/**
 * Created by huale on 2014/11/19.
 */
public class PasswordFragment extends ThemeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return passwd = getView(inflater, container, ctrl, new ICheckResult() {
            @Override
            public void onSuccess() {
                getActivity().finish();
            }
        });
    }

    public View passwd;
    public static View getView(LayoutInflater inflater, ViewGroup container, OverflowCtrl ctrl, final ICheckResult callback) {
        final IThemeBridge bridge = ThemeBridge.bridge;
        inflater = ThemeBridge.themeContext == null ? inflater : LayoutInflater.from(ThemeBridge.themeContext);
        View v = inflater.inflate(R.layout.passwd, container, false);
        ((MyFrameLayout) v).setOverflowCtrl(ctrl);

        if (App.getSharedPreferences().getString("theme", "").equals("custom")) {
            Bitmap bitmap = CustomTheme.getBitmap();
            if (bitmap != null)
            {
                v.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }     }

        final PasswdDot dot = (PasswdDot) v.findViewById(R.id.passwd_dot_id);
        dot.init(new PasswdDot.ICheckListener() {
            @Override
            public void match(String passwd) {
                bridge.check(passwd, true);
                if (callback != null) {
                    Log.e("mtt","right");
                    callback.onSuccess();
                }
            }
        });
        dot.reset();
        ViewStub forbidden = new ViewStub(App.getContext(), R.layout.security_myforbidden);
        dot.forbiddenView = new ForbiddenView(forbidden);
        dot.forbiddenView.init();
        ((MyFrameLayout) v).addView(forbidden);

        v.findViewById(R.id.passwd_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bridge.back();
            }
        });

        v.findViewById(R.id.use_pattern).setVisibility(View.GONE);
        /*
        View usep = v.findViewById(R.id.use_pattern);
        if (bridge.hasPattern()){
            usep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bridge.toggle(false);
                }
            });
        } else {
            usep.setVisibility(View.INVISIBLE);
        }
        */

        v.findViewById(R.id.backspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dot.backSpace();
            }
        });
        int[] buttons = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
        };
        Tools.RandomNumpad(bridge, v, buttons);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dot.setNumber(((Button) v).getText().charAt(0));
            }
        };
        for(int btn : buttons){
            v.findViewById(btn).setOnClickListener(clickListener);
        }

        v.setOnClickListener(ctrl.hideOverflow);

        return v;
    }

    @Override
    public void onDestroyView() {
        if (passwd != null){
            ViewGroup group = (ViewGroup) getView();
            if (group != null) {
                group.removeView(passwd);
            }
            passwd = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (passwd != null){
            ((PasswdDot)passwd.findViewById(R.id.passwd_dot_id)).reset();
        }
    }
}
