package com.alorma.github.ui.adapter;

import android.content.Context;
import android.support.v7.internal.widget.ViewUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsAdapter extends ArrayAdapter<Notification> implements StickyListHeadersAdapter {

	private final LayoutInflater mInflater;

	public NotificationsAdapter(Context context, List<Notification> notifications) {
		super(context, 0, notifications);
		mInflater = LayoutInflater.from(context);
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
		TextView tv = (TextView) v.findViewById(android.R.id.text1);
		ImageView iv = (ImageView) v.findViewById(R.id.clearNotifications);

		Notification item = getItem(i);

		GithubIconDrawable iconDrawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_check);
		iconDrawable.sizeRes(R.dimen.header_height);
		iconDrawable.color(AttributesUtils.getIconsColor(getContext(), R.style.AppTheme_Repos));
		iv.setImageDrawable(iconDrawable);

		tv.setText(item.repository.full_name);

		return tv;
	}

	@Override
	public long getHeaderId(int i) {
		return getItem(i).adapter_repo_parent_id;
	}
}
