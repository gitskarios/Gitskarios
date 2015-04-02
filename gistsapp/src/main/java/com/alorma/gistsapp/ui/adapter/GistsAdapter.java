package com.alorma.gistsapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.Gist;

import java.util.List;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistsAdapter extends LazyAdapter<Gist> {
    private final LayoutInflater mInflater;

    public GistsAdapter(Context context, List<Gist> objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText("" + getItem(position).description);

        return view;
    }
}
