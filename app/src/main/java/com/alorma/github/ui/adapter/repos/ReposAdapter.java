package com.alorma.github.ui.adapter.repos;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListRepos;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

		reposHolder.textTitle.setText(repo.name);

		reposHolder.textTitle.setTextColor(getContext().getResources().getColor(R.color.accent));

		reposHolder.textDescription.setText(repo.description);

		String starText = getContext().getResources().getString(R.string.star_icon_text, repo.stargazers_count);
		reposHolder.textStarts.setText(starText);

		String forkText = getContext().getResources().getString(R.string.fork_icon_text, repo.forks_count);
		reposHolder.textForks.setText(forkText);

		Iconify.addIcons(reposHolder.textStarts, reposHolder.textForks);

		reposHolder.textDescription.setText(repo.description);

		if (repo.language != null && !TextUtils.isEmpty(repo.language)) {
			reposHolder.textLanguage.setText(repo.language);
		} else {
			reposHolder.textLanguage.setVisibility(View.GONE);
		}

		reposHolder.repoPrivate.setVisibility(repo.isPrivate ? View.VISIBLE : View.INVISIBLE);

		ImageLoader.getInstance().displayImage(repo.owner.avatar_url, reposHolder.authorAvatar);

		reposHolder.authorName.setText(repo.owner.name != null ? repo.owner.name : repo.owner.login);

		reposHolder.authorDate.setText(getDate(repo.created_at));

		return v;
	}

	private String getDate(Date created_at) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return sdf.format(created_at);
	}

	public void addAll(Collection<? extends Repo> collection, boolean refreshing) {
		if (refreshing) {
			clear();
		}
		super.addAll(collection);
	}
}