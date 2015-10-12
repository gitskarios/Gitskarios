package com.alorma.github.ui.actions;

/**
 * Created by Bernat on 12/10/2015.
 */
public abstract class Action<K, T> {
    private ActionCallback<T> callback;
    protected K item;

    public Action<K, T> setUp(K k) {
        this.item = k;
        return this;
    }

    public abstract Action<K, T> execute();

    public Action<K, T> setCallback(ActionCallback<T> callback) {
        this.callback = callback;
        return this;
    }

    public ActionCallback<T> getCallback() {
        return callback;
    }
}
