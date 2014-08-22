package com.alorma.github.ui.adapter.issues;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListIssues;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesAdapter extends ArrayAdapter<Issue>{
    private final LayoutInflater mInflater;

    public IssuesAdapter(Context context, ListIssues issues) {
        super(context, 0, issues);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.row_issue, parent, false);

        TextView tv = (TextView) v.findViewById(android.R.id.text1);

        tv.setText(getItem(position).title);

        return v;
    }
}
