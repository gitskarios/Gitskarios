package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.PushEventPayload;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 04/10/2014.
 */
public class PushEventView extends GithubEventView<PushEventPayload> {
	public PushEventView(Context context) {
		super(context);
	}

	public PushEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PushEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void inflate() {
		inflate(getContext(), R.layout.payload_push, this);
	}

	@Override
	protected void populateView(GithubEvent event) {

		TextView actionType = (TextView) findViewById(R.id.actionType);
		actionType.setText(R.string.pushed);

		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

		ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);

		TextView authorName = (TextView) findViewById(R.id.authorName);
		authorName.setText(event.actor.login);

		ViewGroup commits = (ViewGroup) findViewById(R.id.commits);

		if (eventPayload != null) {
			if (eventPayload.commits != null) {
				for (Commit commit : eventPayload.commits) {
					TextView textView = new TextView(getContext());
					textView.setText(commit.message);
					commits.addView(textView);
				}
			}
		}

		GithubIconDrawable left = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_repo_push).color(AttributesUtils.getAccentColor(getContext(), R.style.AppTheme_Repos));

		TextView action = (TextView) findViewById(R.id.action);
		action.setCompoundDrawables(left, null, null, null);

		action.setText(event.repo.name);
	}

	@Override
	protected PushEventPayload convert(Gson gson, String s) {
		return gson.fromJson(s, PushEventPayload.class);
	}
}
