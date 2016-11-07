package com.security.manager.lib.controller;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by SongHualin on 6/12/2015.
 */
public abstract class CListViewAdaptor extends BaseAdapter{
    int layoutId;
    CListViewScroller scroller;

    public CListViewAdaptor(@Nullable CListViewScroller scroller, @LayoutRes int layoutId) {
        this.layoutId = layoutId;
        this.scroller = scroller;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Object holder;

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            holder = getHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }

        onUpdate(position, holder, this.scroller != null && this.scroller.isScrolling());

        return convertView;
    }

    protected abstract void onUpdate(int position, Object holderObject, boolean scrolling);
    protected abstract Object getHolder(View root);
}
