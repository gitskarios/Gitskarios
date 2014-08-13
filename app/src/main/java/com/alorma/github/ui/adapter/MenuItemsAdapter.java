package com.alorma.github.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.fragment.menu.MenuItem;
import com.joanzapata.android.iconify.IconDrawable;

import java.util.List;

/**
 * Created by Bernat on 13/08/2014.
 */
public class MenuItemsAdapter extends ArrayAdapter<MenuItem>{
    private final LayoutInflater mInflater;

    public MenuItemsAdapter(Context context, List<MenuItem> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mInflater.inflate(R.layout.row_menu, parent, false);

        ImageView image = (ImageView) v.findViewById(android.R.id.icon);
        TextView text = (TextView) v.findViewById(android.R.id.text1);

        MenuItem item = getItem(position);

        image.setImageDrawable(new IconDrawable(getContext(), item.icon).color(item.color));
        text.setText(item.text);

        return v;
    }
}
