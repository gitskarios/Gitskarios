package com.alorma.github.ui.cards.profile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Bernat on 23/11/2014.
 */
public class GithubDataCard extends Card implements View.OnClickListener {

	private GithubDataCardListener githubDataCardListener;

	private User user;
	private int avatarColor;

	public GithubDataCard(Context context, User user, int avatarColor) {
		super(context, R.layout.card_github_data_layout);
		this.user = user;
		this.avatarColor = avatarColor;
		CardHeader header = new CardHeader(context);
		header.setTitle(context.getString(R.string.public_data));
		addCardHeader(header);
		setCardElevation(4f);
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		super.setupInnerViewElements(parent, view);

		setUpRepos(view);
		setUpGists(view);
	}

	private void setUpRepos(View view) {
		if (user.public_repos > 0) {
			ImageView icon = (ImageView) view.findViewById(R.id.iconRepositories);

			GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_repo);

			icon.setImageDrawable(githubIconDrawable);

			TextView text = (TextView) view.findViewById(R.id.textRepositories);

			text.setText(view.getContext().getString(R.string.repos_num, user.public_repos));

			view.findViewById(R.id.repositories).setOnClickListener(this);
		} else {
			view.findViewById(R.id.repositories).setVisibility(View.GONE);
			view.findViewById(R.id.dividerRepositories).setVisibility(View.GONE);
		}
	}

	private void setUpGists(View view) {
		if (user.public_gists > 0) {
			ImageView icon = (ImageView) view.findViewById(R.id.iconGists);

			GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_gist);

			icon.setImageDrawable(githubIconDrawable);

			TextView text = (TextView) view.findViewById(R.id.textGists);

			text.setText(view.getContext().getString(R.string.gists_num, user.public_gists));

			view.findViewById(R.id.gists).setOnClickListener(this);
		} else {
			view.findViewById(R.id.gists).setVisibility(View.GONE);
		}
	}

	private GithubIconDrawable drawable(Context context, GithubIconify.IconValue icon) {
		GithubIconDrawable githubIconDrawable = new GithubIconDrawable(context, icon);

		githubIconDrawable.sizeDp(30);
		githubIconDrawable.color(avatarColor);

		return githubIconDrawable;
	}

	@Override
	public void onClick(View v) {
		if (githubDataCardListener != null) {
			switch (v.getId()) {
				case R.id.repositories:
					githubDataCardListener.onRepositoriesRequest(user.login);
					break;
			}
		}
	}

	public void setGithubDataCardListener(GithubDataCardListener githubDataCardListener) {
		this.githubDataCardListener = githubDataCardListener;
	}

	public interface GithubDataCardListener {
		void onRepositoriesRequest(String username);
	}
}
