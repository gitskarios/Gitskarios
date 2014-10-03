package com.alorma.github.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.ListEvents;

import java.util.ArrayList;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventAdapter extends LazyAdapter<GithubEvent>{
	public EventAdapter(Context context, ArrayList<GithubEvent> githubEvents) {
		super(context, githubEvents);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflate(android.R.layout.simple_list_item_1, parent, false);

		TextView tv = (TextView) v.findViewById(android.R.id.text1);

		tv.setText(String.valueOf(getItem(position).getType()));

		return v;
	}
}
