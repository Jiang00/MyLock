package com.android.theme.internal;

import java.util.List;

/**
 * Created by song on 2017/3/3.
 */

public interface UIController<T> {
    void showLoading();

    void hideLoading();

    void onReceiveNone();

    void onReceiveOne(T one);

    void onReceiveSome(List<T> data);

    void onReceiveMany(List<T> data);

    void onCorrect(Object data);

    void onError(Object data);

    void done(Object data);

    void nothing();
}
