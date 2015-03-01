package com.alorma.github.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.bean.ClearNotification;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.squareup.otto.Bus;

import java.util.List;

import javax.inject.Inject;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> implements StickyListHeadersAdapter {

	private final LayoutInflater mInflater;
	private final GithubIconDrawable iconDrawable;
	
	@Inject
	Bus bus;

	public NotificationsAdapter(Context context, List<Notification> notifications) {
		super(context, 0, notifications);
		mInflater = LayoutInflater.from(context);

		iconDrawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_check);
		iconDrawable.sizeRes(R.dimen.gapLarge);
		iconDrawable.color(AttributesUtils.getSecondaryTextColor(getContext(), R.style.AppTheme_Repos));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

		TextView text = (TextView) v.findViewById(android.R.id.text1);

		Notification item = getItem(position);

		text.setText(item.subject.title);

		return v;
	}

	@Override
	public View getHeaderView(int i, View view, ViewGroup viewGroup) {
		View v = mInflater.inflate(R.layout.notification_row_header, viewGroup, false);

		final Notification item = getItem(i);

		TextView tv = (TextView) v.findViewById(R.id.text);
		tv.setText(item.repository.full_name);

		ImageView iv = (ImageView) v.findViewById(R.id.clearNotifications);
		iv.setImageDrawable(iconDrawable);

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bus.post(new ClearNotification(item, true));
			}
		});

		return v;
	}

	@Override
	public long getHeaderId(int i) {
		return getItem(i).adapter_repo_parent_id;
	}
}
