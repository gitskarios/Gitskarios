package com.alorma.github.ui.adapter.detail.repo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListBranches;

/**
 * Created by Bernat on 09/08/2014.
 */
public class BranchesSpinnerAdapter extends ArrayAdapter<Branch> {
    public BranchesSpinnerAdapter(Context context, ListBranches branches) {
        super(context, 0, 0, branches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(getContext());
        textView.setText(getItem(position).name);
        return textView;
    }
}
