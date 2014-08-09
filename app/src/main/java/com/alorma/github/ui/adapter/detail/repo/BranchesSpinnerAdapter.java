package com.alorma.github.ui.adapter.detail.repo;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListBranches;

/**
 * Created by Bernat on 09/08/2014.
 */
public class BranchesSpinnerAdapter extends ArrayAdapter<Branch> {

    private LayoutInflater inflater;

    public BranchesSpinnerAdapter(Context context, ListBranches branches) {
        super(context, android.R.layout.simple_spinner_dropdown_item, branches);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_branch, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(getItem(position).name);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            textView.setTextColor(Color.BLACK);
        }
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
