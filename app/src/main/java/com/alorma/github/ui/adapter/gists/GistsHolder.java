package com.alorma.github.ui.adapter.gists;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by Bernat on 12/07/2014.
 */
public class GistsHolder extends RecyclerView.ViewHolder {
    public TextView text1;
    public TextView text2;
    public GistsHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
            text2 = (TextView) itemView.findViewById(android.R.id.text2);
        }
    }
}
