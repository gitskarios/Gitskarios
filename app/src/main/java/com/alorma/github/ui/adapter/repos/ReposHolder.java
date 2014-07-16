package com.alorma.github.ui.adapter.repos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by Bernat on 12/07/2014.
 */
public class ReposHolder extends RecyclerView.ViewHolder {

    public ImageView imageRepoType;
    public TextView textTitle;
    public TextView textDescription;
    public TextView textForks;
    public TextView textStarts;
    public TextView textLanguage;

    public ReposHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            imageRepoType = (ImageView) itemView.findViewById(R.id.repoTypeIcon);
            textTitle = (TextView) itemView.findViewById(R.id.repoName);
            textDescription = (TextView) itemView.findViewById(R.id.descriptionText);
            textStarts = (TextView) itemView.findViewById(R.id.starsIcon);
            textForks = (TextView) itemView.findViewById(R.id.forksIcon);
            textLanguage = (TextView) itemView.findViewById(R.id.languageText);
        }
    }
}
