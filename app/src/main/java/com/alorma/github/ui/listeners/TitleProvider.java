package com.alorma.github.ui.listeners;

import android.support.annotation.StringRes;

import com.mikepenz.iconics.typeface.IIcon;

/**
 * Created by Bernat on 10/12/2014.
 */
public interface TitleProvider {
    @StringRes
    int getTitle();

    IIcon getTitleIcon();
}
