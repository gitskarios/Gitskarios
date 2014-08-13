package com.alorma.github.ui.fragment.menu;

import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuItem {
    public int text;
    public int color;
    public int id;
    public Iconify.IconValue icon;

    public MenuItem(int id, int text, int color, Iconify.IconValue icon) {
        this.text = text;
        this.color = color;
        this.id = id;
        this.icon = icon;
    }
}
