package com.alorma.github.ui.listeners;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 24/07/2014.
 */
public interface RefreshListener {
    void showRefresh();
    void cancelRefresh();
    void onError(RetrofitError error);
}
