package com.alorma.github.ui.fragment.menu;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 13/08/2014.
 */
public class CategoryMenuItem extends MenuItem {
    public CategoryMenuItem(int id, @StringRes int text, @ColorRes int color, @NonNull Iconify.IconValue icon) {
        super(id, text, color, icon);
    }
}
