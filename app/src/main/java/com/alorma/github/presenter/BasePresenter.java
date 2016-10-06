package com.alorma.github.presenter;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

/**
 * Represents base implementation of {@link Presenter}.
 * It holds WeakReference to {@link View} object, it means that {@link #getView()} could return null
 * and before calling that method you should call {@link #isViewAttached()} which checks if there is
 * alive {@link View} object.
 */
public abstract class BasePresenter<VIEW extends View> implements Presenter<VIEW> {
    private WeakReference<VIEW> weakView;

    @UiThread
    @Override
    public void attachView(VIEW view) {
        this.weakView = new WeakReference<>(view);
    }

    @UiThread
    @Override
    public void detachView() {
        if (weakView != null) {
            this.weakView.clear();
            this.weakView = null;
        }
    }

    /**
     * Returns view that was previously attached to this presenter.
     * @return view that was attached
     */
    @UiThread
    @Nullable
    public VIEW getView(){
        return weakView == null ? null : weakView.get();
    }

    /**
     * Indicates if view is attached to this presenter.
     * @return true if view is attached, otherwise false.
     */
    @UiThread
    public boolean isViewAttached() {
        return weakView != null && weakView.get() != null;
    }
}
