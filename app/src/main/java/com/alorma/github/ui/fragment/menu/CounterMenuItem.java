package com.alorma.github.ui.fragment.menu;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 18/02/2015.
 */
public class CounterMenuItem extends MenuItem {
	
	private int value = 0;
	
	public CounterMenuItem(int id, int parentId, @StringRes int text, @NonNull GithubIconify.IconValue icon, int value) {
		super(id, parentId, text, icon);
		this.value = value;
	}

	public CounterMenuItem(int id, int parentId, @StringRes int text, @NonNull GithubIconify.IconValue icon, int color, int value) {
		super(id, parentId, text, icon, color);
		this.value = value;
	}


	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
