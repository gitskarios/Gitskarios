package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.Collection;
import java.util.List;

public class ReposAdapter extends ArrayAdapter<Repo> {

	private final LayoutInflater mInflater;

	public ReposAdapter(Context context, List<Repo> repos) {
		super(context, 0, 0, repos);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = mInflater.inflate(R.layout.row_repo, viewGroup, false);
		ReposHolder reposHolder = new ReposHolder(v);

		Repo repo = getItem(position);

		int iconsColor = R.color.gray_github_medium;

		reposHolder.textTitle.setText(repo.name);

		reposHolder.textTitle.setTextColor(getContext().getResources().getColor(R.color.accent));

		IconDrawable typeDrawable = new IconDrawable(getContext(), Iconify.IconValue.fa_book);
		if (repo.fork) {
			typeDrawable = new IconDrawable(getContext(), Iconify.IconValue.fa_code_fork);
		}

		typeDrawable.sizeDp(40);

		typeDrawable.colorRes(iconsColor);
		reposHolder.imageRepoType.setImageDrawable(typeDrawable);

		reposHolder.textDescription.setText(repo.description);

		String starText = getContext().getResources().getString(R.string.star_icon_text, repo.stargazers_count);
		reposHolder.textStarts.setText(starText);

		String forkText = getContext().getResources().getString(R.string.fork_icon_text, repo.forks_count);
		reposHolder.textForks.setText(forkText);

		reposHolder.textDescription.setText(repo.description);

		reposHolder.textLanguage.setText(repo.language);

		reposHolder.repoPrivate.setVisibility(repo.isPrivate ? View.VISIBLE : View.GONE);

		return v;
	}

	public void addAll(Collection<? extends Repo> collection, boolean paging) {
		if (!paging) {
			clear();
		}
		super.addAll(collection);
	}
}