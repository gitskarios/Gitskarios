package com.alorma.github.ui.actions;

/**
 * Created by Bernat on 12/10/2015.
 */
public abstract class Action<T> {
    private ActionCallback<T> callback;

    public abstract Action<T> execute();

    public Action<T> setCallback(ActionCallback<T> callback) {
        this.callback = callback;
        return this;
    }

    public ActionCallback<T> getCallback() {
        return callback;
    }
}
