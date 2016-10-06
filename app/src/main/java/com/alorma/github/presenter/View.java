package com.alorma.github.presenter;

import android.support.annotation.UiThread;

/**
 * Represents view a layer that displays data and reacts to user actions.
 * All of its methods should be called from UI thread.
 */
public interface View<MODEL> {

    /**
     * Indicates that some data is loaded.
     * This method should be called on UI thread.
     */
    @UiThread
    void showLoading();

    /**
     * Indicates that some data loading is finished.
     * This method should be called on UI thread.
     */
    @UiThread
    void hideLoading();

    /**
     * Indicates that data is received and should be processed.
     * This method should be called on UI thread.
     * @param data some data
     * @param isFromPaginated true if this method received response from paginated request
     */
    @UiThread
    void onDataReceived(MODEL data, boolean isFromPaginated);

    /**
     * Indicates that some error happens when receiving data.
     * This method should be called on UI thread.
     * @param throwable error
     */
    @UiThread
    void showError(Throwable throwable);
}
