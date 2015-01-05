package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReposAdapter extends ArrayAdapter<Repo> {

	private final LayoutInflater mInflater;
	private final int accentColor;
	private final int primaryDark;
	private final int secondaryTextColor;

	public ReposAdapter(Context context, List<Repo> repos) {
		super(context, 0, 0, repos);
		this.mInflater = LayoutInflater.from(context);

		this.accentColor = AttributesUtils.getAccentColor(context, R.style.AppTheme_Repos);
		this.primaryDark = AttributesUtils.getPrimaryDarkColor(context, R.style.AppTheme_Repos);
		this.secondaryTextColor = AttributesUtils.getPrimaryDarkColor(context, R.style.AppTheme_Repos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = mInflater.inflate(R.layout.row_repo, viewGroup, false);
		ReposHolder reposHolder = new ReposHolder(v);

		Repo repo = getItem(position);

		reposHolder.textTitle.setText(repo.name);

		reposHolder.textTitle.setTextColor(primaryDark);

		reposHolder.textDescription.setText(repo.description);

		String starText = getContext().getResources().getString(R.string.star_icon_text, repo.stargazers_count);
		applyIcon(reposHolder.textStarts, GithubIconify.IconValue.octicon_star);
		reposHolder.textStarts.setText(starText);

		String forkText = getContext().getResources().getString(R.string.fork_icon_text, repo.forks_count);
		applyIcon(reposHolder.textForks, GithubIconify.IconValue.octicon_repo_forked);
		reposHolder.textForks.setText(forkText);


		reposHolder.textDescription.setText(repo.description);

		if (repo.language != null && !TextUtils.isEmpty(repo.language)) {
			reposHolder.textLanguage.setText(repo.language);
		} else {
			reposHolder.textLanguage.setText(R.string.unknown_language);
		}

		reposHolder.repoPrivate.setVisibility(repo.isPrivate ? View.VISIBLE : View.INVISIBLE);

		ImageLoader.getInstance().displayImage(repo.owner.avatar_url, reposHolder.authorAvatar);

		reposHolder.authorName.setText(repo.owner.name != null ? repo.owner.name : repo.owner.login);

		reposHolder.authorDate.setText(getDate(repo.created_at));

		return v;
	}

	private void applyIcon(TextView textView, GithubIconify.IconValue value) {
		GithubIconDrawable drawableForks = new GithubIconDrawable(getContext(), value);
		drawableForks.color(secondaryTextColor);
		drawableForks.sizeRes(R.dimen.textSizeSmall);
		textView.setCompoundDrawables(drawableForks, null, null, null);
		int offset = getContext().getResources().getDimensionPixelOffset(R.dimen.textSizeSmall);
		textView.setCompoundDrawablePadding(offset);
	}

	private String getDate(Date created_at) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return sdf.format(created_at);
	}

	public void addAll(Collection<? extends Repo> collection, boolean paging) {
		if (!paging) {
			clear();
		}
		super.addAll(collection);
	}

}