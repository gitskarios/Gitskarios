package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.ForkEventPayload;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 04/10/2014.
 */
public class ForkEventView extends GithubEventView<ForkEventPayload> {
	public ForkEventView(Context context) {
		super(context);
	}

	public ForkEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ForkEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void inflate() {
		inflate(getContext(), R.layout.payload_forked, this);
	}

	@Override
	protected void populateView(GithubEvent event) {
		int textRes = R.string.event_forked_by;

		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        //load the profile image from url with optimal settings
        handleImage(authorAvatar, event);

		TextView authorName = (TextView) findViewById(R.id.authorName);
		String original = event.repo.name;
		String destination = eventPayload.forkee != null ? eventPayload.forkee.full_name : "";
		authorName.setText(Html.fromHtml(getContext().getResources().getString(textRes, event.actor.login, original, destination)));

		TextView textDate = (TextView) findViewById(R.id.textDate);

		String timeString = TimeUtils.getTimeString(textDate.getContext(), event.created_at);

		textDate.setText(timeString);
	}

	@Override
	protected ForkEventPayload convert(Gson gson, String s) {
		return gson.fromJson(s, ForkEventPayload.class);
	}
}
