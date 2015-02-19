package com.alorma.github.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.Notification;

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
		View v = mInflater.inflate(R.layout.commit_row_header, viewGroup, false);
		TextView tv = (TextView) v.findViewById(android.R.id.text1);

		Notification item = getItem(i);

		tv.setText(item.repository.full_name);

		return tv;
	}

	@Override
	public long getHeaderId(int i) {
		return getItem(i).adapter_repo_parent_id;
	}
}
