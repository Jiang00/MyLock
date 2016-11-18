package com.security.manager.meta;

import android.graphics.Bitmap;


import com.security.lib.customview.AnimationImageView;
import com.security.manager.ImageManager;
import com.security.manager.meta.File;


import java.util.HashMap;

/**
 * Created by song on 15/12/22.
 */
public class LoadImagePresente {
    private final HashMap<String, AnimationImageView> loadingIconViews = new HashMap<>();



    public void requestThumbnail(String file, AnimationImageView imageView, boolean loading) {
        Bitmap bitmap = ImageManager.get(file);
        if (bitmap != null && !bitmap.isRecycled()) {
            imageView.setImageBitmap(bitmap, false);
        } else {
            imageView.setImageDrawable(null);
//            if (loading) {
//                imageView.setTag(file.filePath);
//                loadingIconViews.put(file.filePath, imageView);
////                api.requestThumbnail(file);
//            }
        }
    }



}
