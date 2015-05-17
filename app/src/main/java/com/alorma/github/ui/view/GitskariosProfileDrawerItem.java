package com.alorma.github.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

/**
 * Created by Bernat on 09/05/2015.
 */
public class GitskariosProfileDrawerItem extends ProfileDrawerItem {

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View v = super.convertView(inflater, convertView, parent);

        TextView text = (TextView) v.findViewById(com.mikepenz.materialdrawer.R.id.name);
        TextView mail = (TextView) v.findViewById(com.mikepenz.materialdrawer.R.id.email);

        if (text != null && mail != null) {
            text.setVisibility(View.VISIBLE);
            mail.setVisibility(View.VISIBLE);
            text.setText(this.getName());
            mail.setText(this.getEmail());
        }

        return v;
    }
}
