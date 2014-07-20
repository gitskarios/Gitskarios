package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;

import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */
public class RepoContentAdapter extends ArrayAdapter<Content> {

    private final LayoutInflater inflater;

    public RepoContentAdapter(Context context, List<Content> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.row_content, parent, false);

        TextView textName = (TextView) v.findViewById(R.id.name);

        Content item = getItem(position);

        textName.setText(item.name);

        return v;
    }
}
