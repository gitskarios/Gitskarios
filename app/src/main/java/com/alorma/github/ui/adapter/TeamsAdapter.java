package com.alorma.github.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Team;

import java.util.List;

/**
 * Created by Bernat on 14/07/2014.
 */
public class TeamsAdapter extends LazyAdapter<Team> {

    public TeamsAdapter(Context context, List<Team> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = inflate(R.layout.row_team, viewGroup, false);
        Team team = getItem(position);

        TextView textView = (TextView) v.findViewById(R.id.textName);

        textView.setText(team.name);

        View divider = v.findViewById(R.id.divider);

        if (position == getCount()) {
            divider.setVisibility(View.GONE);
        }

        return v;
    }
}
