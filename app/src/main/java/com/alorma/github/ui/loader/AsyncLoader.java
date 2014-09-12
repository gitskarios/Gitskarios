package com.alorma.github.ui.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

abstract class AsyncLoader<T> extends AsyncTaskLoader<T> {

    private T mData;

    private boolean mIsRunningLoading;

    AsyncLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the
            // data.
            releaseResources(data);
            return;
        }
        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        T oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }

        mIsRunningLoading = false;
    }

    @Override
    protected void onStartLoading() {
        mIsRunningLoading = true;

        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged() on the
            // Loader, which will cause the next call to takeContentChanged() to return true. If
            // this is ever the case (or if the current data is null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).

        cancelLoad();
    }

    public boolean isRunningLoading() {
        return mIsRunningLoading;
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(T data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    protected abstract void releaseResources(T data);
}