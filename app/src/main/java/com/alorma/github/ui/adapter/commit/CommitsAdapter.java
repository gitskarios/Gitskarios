package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsAdapter extends LazyAdapter<Commit> implements StickyListHeadersAdapter {

	public CommitsAdapter(Context context, List<Commit> objects) {
		super(context, 0, objects);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflate(R.layout.commit_row, parent, false);

		TextView title = (TextView) v.findViewById(R.id.title);
		TextView user = (TextView) v.findViewById(R.id.user);
		TextView sha = (TextView) v.findViewById(R.id.sha);
		ImageView avatar = (ImageView) v.findViewById(R.id.avatarAuthor);

		Commit commit = getItem(position);

		if (commit.commit != null) {
			User author = commit.author;
			title.setText(commit.commit.message);
			if (author == null) {
				author = commit.commit.author;
			}

			if (author != null) {
				if (author.avatar_url != null) {
					ImageLoader.getInstance().displayImage(author.avatar_url, avatar);
				} else {
					GithubIconDrawable iconDrawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_octoface);
					iconDrawable.color(AttributesUtils.getSecondaryTextColor(getContext(), R.style.AppTheme_Repos));
					iconDrawable.sizeDp(36);
					iconDrawable.setAlpha(128);
					avatar.setImageDrawable(iconDrawable);
				}
			}

			if (commit.commit.author != null && commit.commit.author.date != null) {
				DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
				DateTime dt = formatter.parseDateTime(commit.commit.author.date);

				Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay());

				String name = "";
				if (commit.author != null && commit.author.login != null) {
					name = commit.author.login;
				} else if (commit.commit.author != null && commit.commit.author.name != null) {
					name = commit.commit.author.name;
				}

				String userDate = getContext().getResources().getString(R.string.commit_authored_at, name, days.getDays());
				user.setText(userDate);
				
			} else if (commit.author != null) {
				user.setText(commit.author.login);
			}
		}

		if (commit.sha != null) {
			sha.setText(commit.sha.substring(0, 8));
		}


		return v;
	}

	@Override
	public View getHeaderView(int i, View view, ViewGroup viewGroup) {
		View v = inflate(R.layout.commit_row_header, viewGroup, false);
		TextView tv = (TextView) v.findViewById(android.R.id.text1);

		Commit commit = getItem(i);

		if (commit.commit != null && commit.commit.author != null && commit.commit.author.date != null) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
			DateTime dt = formatter.parseDateTime(commit.commit.author.date);

			String text = dt.toString("dd MMM yyyy");

			tv.setText(text);
		}
		return tv;
	}

	@Override
	public long getHeaderId(int i) {
		return getItem(i).days;
	}

}
