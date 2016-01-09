package com.alorma.github.ui.adapter.repos;

import android.view.View;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by Bernat on 12/07/2014.
 */
public class ReposHolder {

    public TextView textTitle;
    public TextView repoPrivate;
    public TextView textDescription;
    public TextView textForks;
    public TextView textStarts;
    public TextView textForkOf;

    public ReposHolder(View itemView) {
        if (itemView != null) {
            textTitle = (TextView) itemView.findViewById(R.id.repoName);
            repoPrivate = (TextView) itemView.findViewById(R.id.repoPrivate);
            textDescription = (TextView) itemView.findViewById(R.id.descriptionText);
            textStarts = (TextView) itemView.findViewById(R.id.textStarts);
            textForks = (TextView) itemView.findViewById(R.id.textForks);
        }
    }
}
