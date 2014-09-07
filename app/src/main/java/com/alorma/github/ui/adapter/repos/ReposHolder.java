package com.alorma.github.ui.adapter.repos;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by Bernat on 12/07/2014.
 */
public class ReposHolder {

	public ImageView authorAvatar;
	public TextView textTitle;
	public TextView authorName;
	public TextView authorDate;
	public TextView repoPrivate;
	public TextView textDescription;
	public TextView textForks;
	public TextView textStarts;
	public TextView textLanguage;

	public ReposHolder(View itemView) {
		if (itemView != null) {
			authorAvatar = (ImageView) itemView.findViewById(R.id.authorAvatar);
			textTitle = (TextView) itemView.findViewById(R.id.repoName);
			authorName = (TextView) itemView.findViewById(R.id.authorName);
			authorDate = (TextView) itemView.findViewById(R.id.authorDate);
			repoPrivate = (TextView) itemView.findViewById(R.id.repoPrivate);
			textDescription = (TextView) itemView.findViewById(R.id.descriptionText);
			textStarts = (TextView) itemView.findViewById(R.id.starsIcon);
			textForks = (TextView) itemView.findViewById(R.id.forksIcon);
			textLanguage = (TextView) itemView.findViewById(R.id.languageText);
		}
	}
}
