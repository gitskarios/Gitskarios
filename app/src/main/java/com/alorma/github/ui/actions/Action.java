package com.alorma.github.ui.actions;

import rx.Subscriber;

/**
 * Created by Bernat on 12/10/2015.
 */
public abstract class Action<T> extends Subscriber<T> {
    private ActionCallback<T> callback;

    public abstract Action<T> execute();

    public Action<T> setCallback(ActionCallback<T> callback) {
        this.callback = callback;
        return this;
    }

    public ActionCallback<T> getCallback() {
        return callback;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }
}
