package com.vactorapps.lib.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.ivymobi.applock.free.R;


/**
 * Created by on 2016/10/27.
 */

public class CustomerTypefaceTextView extends android.support.v7.widget.AppCompatTextView {
    String typeface = null;

    Context mContext;

    public CustomerTypefaceTextView(Context context) {
        super(context, null);
    }

    private void obtainAttributes(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CustomerTypefaceTextView);
        typeface = ta.getString(R.styleable.CustomerTypefaceTextView_typeface);
        ta.recycle();
        if (!TextUtils.isEmpty(typeface)) {
            Typeface tf = Typeface.createFromAsset(mContext.getAssets(), typeface);
            setTypeface(tf);
        }
    }

    public CustomerTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        obtainAttributes(attrs);
    }

    public void setTypeface(String typeface) {
        if (TextUtils.isEmpty(typeface)) {
            return;
        }
        this.typeface = typeface;
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), typeface);
        setTypeface(tf);
    }
}
