package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueCommentEventPayload;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.utils.TimeUtils;
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
		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        //load the profile image from url with optimal settings
        handleImage(authorAvatar, event);

		TextView authorName = (TextView) findViewById(R.id.authorName);
		authorName.setText(event.actor.login);

		int textRes = eventPayload.issue.pullRequest == null ? R.string.event_issue_comment_by : R.string.event_pr_comment_by;

		String text = getContext().getString(textRes,
				event.actor.login, event.repo.name, eventPayload.issue.number);

		authorName.setText(Html.fromHtml(text));

		TextView textTitle = (TextView) findViewById(R.id.textTitle);

		RepoInfo repoInfo = new RepoInfo();
		repoInfo.owner = event.repo.name.split("/")[0];
		repoInfo.name = event.repo.name.split("/")[1];


		textTitle.setText(eventPayload.comment.shortMessage());

		TextView textDate = (TextView) findViewById(R.id.textDate);

		String timeString = TimeUtils.getTimeString(textDate.getContext(), event.created_at);

		textDate.setText(timeString);
	}

	@Override
	protected IssueCommentEventPayload convert(Gson gson, String s) {
		return gson.fromJson(s, IssueCommentEventPayload.class);
	}
}
