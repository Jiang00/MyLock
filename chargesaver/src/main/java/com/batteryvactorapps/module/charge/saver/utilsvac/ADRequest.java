package com.batteryvactorapps.module.charge.saver.utilsvac;

import android.view.View;
import android.view.ViewGroup;

import com.android.client.AndroidSdk;
import com.android.client.ClientNativeAd;
import com.batteryvactorapps.module.charge.saver.R;

/**
 * Created by on 2016/11/9.
 */
public class ADRequest {

    public static void showFullAD() {
        AndroidSdk.showFullAd(AndroidSdk.FULL_TAG_PAUSE);
    }

    public View showCustomNativeAD(String adTag, int layoutID, final ICustomNativeADClicked adClick) {
        View adView = null;
        if (AndroidSdk.hasNativeAd(adTag)) {
            adView = AndroidSdk.peekNativeAdViewWithLayout(adTag,
                    R.layout.native_ad,
                    new ClientNativeAd.NativeAdClickListener() {
                        @Override
                        public void onNativeAdClicked(ClientNativeAd clientNativeAd) {
                            if (adClick != null) {
                                adClick.onNativeADClicked(clientNativeAd);
                            }
                        }
                    });
            if (adView != null) {
                View ad_image = adView.findViewWithTag("ad_image");
                ad_image.setClickable(false);
                ad_image.setOnClickListener(null);
                if (ad_image instanceof ViewGroup) {
                    final int childCount = ((ViewGroup) ad_image).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        ((ViewGroup) ad_image).getChildAt(i).setClickable(false);
                    }
                }
                View ad_title = adView.findViewWithTag("ad_title");
                ad_title.setClickable(false);
                ad_title.setOnClickListener(null);
                if (ad_title instanceof ViewGroup) {
                    final int childCount = ((ViewGroup) ad_title).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        ((ViewGroup) ad_title).getChildAt(i).setClickable(false);
                    }
                }
                View ad_subtitle = adView.findViewWithTag("ad_subtitle");
                ad_subtitle.setClickable(false);
                ad_subtitle.setOnClickListener(null);
                if (ad_subtitle instanceof ViewGroup) {
                    final int childCount = ((ViewGroup) ad_subtitle).getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        ((ViewGroup) ad_subtitle).getChildAt(i).setClickable(false);
                    }
                }
            }
        }
        return adView;
    }

    public interface ICustomNativeADClicked {
        void onNativeADClicked(ClientNativeAd clientNativeAd);
    }

}
