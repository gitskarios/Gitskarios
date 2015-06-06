package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.UnhandledPayload;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 04/10/2014.
 */
public class UnhandledEventView extends GithubEventView<UnhandledPayload> {
	public UnhandledEventView(Context context) {
		super(context);
	}

	public UnhandledEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UnhandledEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void inflate() {
		inflate(getContext(), R.layout.payload_unhandled, this);
	}

	@Override
	protected void populateView(GithubEvent event) {
		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

		ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);
	}

	@Override
	protected UnhandledPayload convert(Gson gson, String s) {
		return gson.fromJson(s, UnhandledPayload.class);
	}

}
