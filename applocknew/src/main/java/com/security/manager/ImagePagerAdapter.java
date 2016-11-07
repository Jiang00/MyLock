/*
 Copyright (c) 2013 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.security.manager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.security.gallery.view.TileBitmapDrawable;
import com.security.gallery.view.TouchImageView;
import com.privacy.lock.R;
import com.security.manager.meta.FileData;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<FileData> images;
    boolean encrypted;
    ArrayList<Boolean> selected;
    View.OnClickListener listener;
    ArrayList<ViewGroup> cachedView = new ArrayList<>();

    public ImagePagerAdapter(Context context, ArrayList<FileData> imgs, boolean encrypted, View.OnClickListener listener) {
        this.context = context;
        images = imgs;
        this.encrypted = encrypted;
        this.listener = listener;
        if (encrypted) {
            selected = new ArrayList<>();
            for (int i = imgs.size() - 1; i >= 0; --i) {
                selected.add(false);
            }
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup tmp = null;
        if (cachedView.size() > 0) {
            tmp = cachedView.remove(0);
        }

        if (tmp == null) {
            tmp = (ViewGroup) LayoutInflater.from(container.getContext()).inflate(R.layout.security_gallary_item, container, false);
        }

        if (encrypted) {
            selected.set(position, true);
        }
        String path = encrypted ? AppsCore.p(images.get(position).url, true) : images.get(position).url;
        final View progress = tmp.findViewById(R.id.gallery_view_pager_sample_item_progress);

        ImageView image = (ImageView) tmp.findViewById(R.id.gallery_view_pager_sample_item_image);
        image.setOnClickListener(listener);
        TileBitmapDrawable.attachTileBitmapDrawable(image, path, null, new TileBitmapDrawable.OnInitializeListener() {

            @Override
            public void onStartInitialization() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEndInitialization() {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception ex) {
                progress.setVisibility(View.GONE);
            }
        });

        container.addView(tmp, 0);
        return tmp;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (encrypted) {
            if (position < images.size()) {
                AppsCore.p(images.get(position).url, false);
                selected.set(position, false);
            }
        }
        ViewGroup v = (ViewGroup) object;
        container.removeView(v);
        cachedView.add(v);
        ((TouchImageView) v.getChildAt(0)).setImageDrawable(null);
    }

    public void onDestroy() {
        if (encrypted) {
            for (int i = 0; i < selected.size(); ++i) {
                if (selected.get(i)) {
                    AppsCore.p(images.get(i).url, false);
                }
            }
            selected.clear();
        }
        for (ViewGroup vg : cachedView) {
            vg.removeAllViews();
        }
        cachedView.clear();
        images = null;
        selected = null;
    }

    public void remove(int position) {
        if (encrypted) {
            selected.remove(position);
        }
        images.remove(position);
        notifyDataSetChanged();
    }
}