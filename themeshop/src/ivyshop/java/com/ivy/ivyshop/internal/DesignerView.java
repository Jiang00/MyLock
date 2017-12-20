package com.ivy.ivyshop.internal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.theme.internal.data.ThemeContainer;
import com.android.theme.internal.data.ThemeSegment;

import java.util.ArrayList;

/**
 * Created by song on 2017/3/8.
 */

public class DesignerView {
    public static View create(Context context, final String shopTag, ViewGroup parent, final ThemeSegment designer) {
//        final LayoutInflater inflater = LayoutInflater.from(context);
//        final View view = inflater.inflate(R.layout.shop_designer_list, parent, false);
//        final ViewGroup designerList = (ViewGroup) view.findViewById(R.id.shop_designer_list);
//        for (final ThemeContainer themeContainer : designer) {
//            View v = inflater.inflate(R.layout.shop_designer, designerList, false);
//            Glide.with(context).load(themeContainer.icon).into((ImageView) v.findViewById(R.id.shop_icon));
//            ((TextView)v.findViewById(R.id.shop_title)).setText(themeContainer.tag);
//            designerList.addView(v);
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DesignerActivity.launch(v.getContext(), shopTag, themeContainer);
//                }
//            });
//        }
//        return view;
        return new View(context);
    }
}
