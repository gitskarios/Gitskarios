package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueCommentEventPayload;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 05/10/2014.
 */
public class IssueCommentEventView extends GithubEventView<IssueCommentEventPayload> {

	public IssueCommentEventView(Context context) {
		super(context);
	}

	public IssueCommentEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IssueCommentEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void inflate() {
		inflate(getContext(), R.layout.payload_issue_comment, this);
	}

	@Override
	protected void populateView(GithubEvent event) {
		TextView actionType = (TextView) findViewById(R.id.actionType);
		actionType.setText(R.string.issue_commented);

		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

		ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);

		TextView authorName = (TextView) findViewById(R.id.authorName);
		authorName.setText(event.actor.login);

		TextView comment = (TextView) findViewById(R.id.comment);

		comment.setText(Html.fromHtml(eventPayload.comment.body));

		GithubIconDrawable left = new GithubIconDrawable(getContext(), GithubIconify.IconValue.octicon_comment_discussion).color(AttributesUtils.getAccentColor(getContext(), R.style.AppTheme_Repos));

		TextView action = (TextView) findViewById(R.id.action);
		action.setCompoundDrawables(left, null, null, null);

		action.setText(eventPayload.issue.title);
	}

	@Override
	protected IssueCommentEventPayload convert(Gson gson, String s) {
		return gson.fromJson(s, IssueCommentEventPayload.class);
	}
}
