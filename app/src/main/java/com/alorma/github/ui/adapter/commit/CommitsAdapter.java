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
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
			String message = commit.commit.message;
			if (message != null && message.length() > 80) {
				message = message.substring(0, 80);
			}
			title.setText(message);
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


				String name = "";
				if (commit.author != null && commit.author.login != null) {
					name = commit.author.login;
				} else if (commit.commit.author != null && commit.commit.author.name != null) {
					name = commit.commit.author.name;
				}

				user.setText(getTimeString(name, commit.commit.author));

			} else if (commit.author != null) {
				user.setText(commit.author.login);
			}
		}

		if (commit.sha != null) {
			sha.setText(commit.sha.substring(0, 8));
		}


		return v;
	}

	private String getTimeString(String name, User author) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

		DateTime dt = formatter.parseDateTime(author.date);
		DateTime dtNow = DateTime.now().withZone(DateTimeZone.UTC);

		Years years = Years.yearsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
		int text = R.string.commit_authored_at_years;
		int time = years.getYears();

		if (time == 0) {
			Months months = Months.monthsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
			text = R.string.commit_authored_at_days;
			time = months.getMonths();

			if (time == 0) {

				Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
				text = R.string.commit_authored_at_days;
				time = days.getDays();

				if (time == 0) {
					Hours hours = Hours.hoursBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
					time = hours.getHours();
					text = R.string.commit_authored_at_hours;

					if (time == 0) {
						Minutes minutes = Minutes.minutesBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
						time = minutes.getMinutes();
						text = R.string.commit_authored_at_minutes;
						if (time == 0) {
							Seconds seconds = Seconds.secondsBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
							time = seconds.getSeconds();
							text = R.string.commit_authored_at_seconds;
						}
					}
				}
			}
		}

		return getContext().getResources().getString(text, name, time);
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
