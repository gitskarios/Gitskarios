package com.alorma.github.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.ui.fragment.menu.MenuItem;

import java.util.List;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuItemsAdapter extends ArrayAdapter<MenuItem>{
    public MenuItemsAdapter(Context context, List<MenuItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
