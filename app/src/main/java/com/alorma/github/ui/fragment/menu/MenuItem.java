package com.alorma.github.ui.fragment.menu;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuItem {
	public int parentId;
	public int text;
	public int color;
	public int id;
	public Iconify.IconValue icon;

	public MenuItem(int id, int parentId, @StringRes int text, @ColorRes int color, @NonNull Iconify.IconValue icon) {
		this.text = text;
		this.color = color;
		this.id = id;
		this.parentId = parentId;
		this.icon = icon;
	}
}
