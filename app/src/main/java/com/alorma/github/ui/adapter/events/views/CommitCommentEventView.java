package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.ShaUrl;
import com.alorma.github.sdk.bean.dto.response.events.payload.CommitCommentEventPayload;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.utils.TimeUtils;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 05/10/2014.
 */
public class CommitCommentEventView extends GithubEventView<CommitCommentEventPayload> {

	public CommitCommentEventView(Context context) {
		super(context);
	}

	public CommitCommentEventView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommitCommentEventView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void inflate() {
		inflate(getContext(), R.layout.payload_commit_comment, this);
	}

	@Override
	protected void populateView(GithubEvent event) {
		ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

		ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);

		TextView authorName = (TextView) findViewById(R.id.authorName);
		authorName.setText(event.actor.login);

		int textRes = R.string.event_commit_comment_by;

		String text = getContext().getString(textRes,
				event.actor.login, event.repo.name, ShaUrl.shortShaStatic(eventPayload.comment.commit_id));

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
	protected CommitCommentEventPayload convert(Gson gson, String s) {
		return gson.fromJson(s, CommitCommentEventPayload.class);
	}
}
