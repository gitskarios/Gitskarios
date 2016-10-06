package com.alorma.github.presenter;

import android.support.annotation.UiThread;

/**
 * Presenter interface represents connection between view and presenter implementation.
 * All of its methods should be called from UI thread.
 */
public interface Presenter<VIEW extends View> {
    /**
     * Attaches view to presenter.
     * In order to receive callbacks from presenter view should be attached to it.
     * This method should be called on UI thread.
     * @param view view to be attached to presenter
     */
    @UiThread
    void attachView(VIEW view);

    /**
     * Detaches view from presenter. This operation should be done on UI thread.
     * View will not receive any of callbacks from presenter.
     * This method should be called on UI thread.
     */
    @UiThread
    void detachView();
}
