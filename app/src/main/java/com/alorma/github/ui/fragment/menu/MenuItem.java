package com.alorma.github.ui.fragment.menu;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuItem {
	public int parentId;
	public int text;
	public int id;
	public GithubIconify.IconValue icon;
	public int color = -1;

	public MenuItem(int id, int parentId, @StringRes int text, @NonNull GithubIconify.IconValue icon) {
		this.text = text;
		this.id = id;
		this.parentId = parentId;
		this.icon = icon;
	}
	
	public MenuItem(int id, int parentId, @StringRes int text, @NonNull GithubIconify.IconValue icon, int color) {
		this(id, parentId, text, icon);
		this.color = color;
	}
}
