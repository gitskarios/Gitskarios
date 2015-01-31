package com.alorma.github.ui.cards.profile;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 23/11/2014.
 */
public class GithubDataCard implements View.OnClickListener {

	private GithubDataCardListener githubDataCardListener;

	private User user;
	private int avatarColor;

	public GithubDataCard(User user, View view, int avatarColor) {
		this.user = user;
		this.avatarColor = avatarColor;
		setupInnerViewElements(view);
	}

	public void setupInnerViewElements(View view) {

		setUpRepos(view);
		setUpGists(view);

		GitskariosSettings gitskariosSettings = new GitskariosSettings(view.getContext());
		String authUser = gitskariosSettings.getAuthUser(null);
		if (authUser != null && authUser.equals(user.login)) {
			setUpPrivateGists(view);
		} else {
			hidePrivateGists(view);
		}
	}

	private void setUpRepos(View view) {
		ImageView icon = (ImageView) view.findViewById(R.id.iconRepositories);

		GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_repo);

		icon.setImageDrawable(githubIconDrawable);

		TextView text = (TextView) view.findViewById(R.id.textRepositories);

		text.setText(view.getContext().getString(R.string.repos_num, user.public_repos));

		view.findViewById(R.id.repositories).setOnClickListener(this);
	}

	private void setUpGists(View view) {
		ImageView icon = (ImageView) view.findViewById(R.id.iconGists);

		GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_gist);

		icon.setImageDrawable(githubIconDrawable);

		TextView text = (TextView) view.findViewById(R.id.textGists);

		text.setText(view.getContext().getString(R.string.gists_num, user.public_gists));

		view.findViewById(R.id.gists).setOnClickListener(this);
	}

	private void setUpPrivateGists(View view) {
		ImageView icon = (ImageView) view.findViewById(R.id.iconPrivateGists);

		GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_gist);

		icon.setImageDrawable(githubIconDrawable);

		TextView text = (TextView) view.findViewById(R.id.textPrivateGists);

		text.setText(view.getContext().getString(R.string.private_gists_num, user.private_gists));

		view.findViewById(R.id.gists).setOnClickListener(this);
	}

	private void hidePrivateGists(View view) {
		view.findViewById(R.id.dividerGists).setVisibility(View.GONE);
		view.findViewById(R.id.privateGists).setVisibility(View.GONE);
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
