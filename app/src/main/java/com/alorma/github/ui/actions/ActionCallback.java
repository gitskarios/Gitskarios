package com.alorma.github.ui.actions;

/**
 * Created by Bernat on 12/10/2015.
 */
public interface ActionCallback<T> {
    void onResult(T t);
}
