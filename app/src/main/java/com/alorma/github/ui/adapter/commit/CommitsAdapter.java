package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.ui.adapter.LazyAdapter;

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

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsAdapter extends LazyAdapter<Commit> {

	public CommitsAdapter(Context context, List<Commit> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflate(R.layout.commit_row, parent, false);

		TextView title = (TextView) v.findViewById(R.id.title);
		TextView user = (TextView) v.findViewById(R.id.user);
		TextView sha = (TextView) v.findViewById(R.id.sha);

		Commit commit = getItem(position);

		title.setText(commit.commit.message);
		sha.setText(commit.sha.substring(0, 8));

		if (commit.commit.author.date != null) {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
			DateTime dt = formatter.parseDateTime(commit.commit.author.date);

			Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay());

			if (commit.committer != null && commit.committer.login != null) {
				String userDate = getContext().getResources().getString(R.string.commit_authored_at, commit.committer.login, days.getDays());
				user.setText(userDate);
			}
		} else {
			user.setText(commit.committer.login);
		}
		
		return v;
	}
}
