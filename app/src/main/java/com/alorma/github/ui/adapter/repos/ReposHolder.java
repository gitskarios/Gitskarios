package com.alorma.github.ui.adapter.repos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Bernat on 12/07/2014.
 */
public class ReposHolder extends RecyclerView.ViewHolder {
    public TextView text1;
    public TextView text2;
    public ReposHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            text2 = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }
}
