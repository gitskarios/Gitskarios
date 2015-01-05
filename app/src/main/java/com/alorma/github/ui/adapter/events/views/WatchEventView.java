package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.WatchedEventPayload;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 04/10/2014.
 */
public class WatchEventView extends GithubEventView<WatchedEventPayload> {
	public WatchEventView(Context context) {
		super(context);
	}

	public WatchEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WatchEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void inflate() {
		inflate(getContext(), R.layout.payload_watch, this);
	}

	@Override
	protected void populateView(GithubEvent event) {
		TextView actionType = (TextView) findViewById(R.id.actionType);
		actionType.setText(R.string.watched);

		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

		ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);

		TextView authorName = (TextView) findViewById(R.id.authorName);
		authorName.setText(event.actor.login);

		ImageView actionImage = (ImageView) findViewById(R.id.actionImage);
		Drawable drawable = null;
		if (eventPayload.action.equals("started")) {
			drawable = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_eye).color(AttributesUtils.getIconsColor(getContext(), R.style.AppTheme_Repos));
		}

		if (drawable != null) {
			actionImage.setImageDrawable(drawable);
			TextView action = (TextView) findViewById(R.id.action);
			action.setText(event.repo.name);
		}
	}

	@Override
	protected WatchedEventPayload convert(Gson gson, String s) {
		return gson.fromJson(s, WatchedEventPayload.class);
	}
}
