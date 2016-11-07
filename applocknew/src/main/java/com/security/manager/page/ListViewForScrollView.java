package com.security.manager.page;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by wangqi on 16/11/4.
 */

public class ListViewForScrollView extends ListView {

    public ListViewForScrollView(Context context) {

        super(context);

    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    @Override

/**

 * 重写该方法，达到使ListView适应ScrollView的效果

 */

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//适配ListView的高度与ScrollView高度【具体算法由google官方2013年的案例中提供】

//目前google官方强烈要求使用RecyclerView，并且不在更新ListView

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}

