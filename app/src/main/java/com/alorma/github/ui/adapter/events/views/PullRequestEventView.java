package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.PullRequestEventPayload;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;

/**
 * Created by Bernat on 05/10/2014.
 */
public class PullRequestEventView extends GithubEventView<PullRequestEventPayload> {

    public PullRequestEventView(Context context) {
        super(context);
    }

    public PullRequestEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullRequestEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.payload_pull_request_event, this);
    }

    @Override
    protected void populateView(GithubEvent event) {
        ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        //load the profile image from url with optimal settings
        handleImage(authorAvatar, event);

        TextView authorName = (TextView) findViewById(R.id.authorName);
        authorName.setText(event.actor.login);

        TextView textCommits = (TextView) findViewById(R.id.textCommits);

        int textRes = R.string.event_pull_request_by;
        if (eventPayload.action.equalsIgnoreCase("closed")) {
            String action = "closed";
            if (eventPayload.pull_request.merged) {
                action = "merged";
            }

            String text = getContext().getString(textRes,
                    event.actor.login, action, event.repo.name, eventPayload.pull_request.number);

            authorName.setText(Html.fromHtml(text));

        } else {
            String text = getContext().getString(textRes,
                    event.actor.login, eventPayload.action, event.repo.name, eventPayload.pull_request.number);

            authorName.setText(Html.fromHtml(text));

            textCommits.setVisibility(View.GONE);
        }

        TextView textTitle = (TextView) findViewById(R.id.textTitle);

        RepoInfo repoInfo = new RepoInfo();
        repoInfo.owner = event.repo.name.split("/")[0];
        repoInfo.name = event.repo.name.split("/")[1];

        textTitle.setText(eventPayload.pull_request.title);

        int commits = eventPayload.pull_request.commits;
        int additions = eventPayload.pull_request.additions;
        int deletions = eventPayload.pull_request.deletions;

        String textCommitsStr;
        if (additions > 0 && deletions > 0) {
            textCommitsStr = getContext().getString(R.string.event_pull_request_commits_add_del, commits, additions, deletions);
        } else if (additions > 0) {
            textCommitsStr = getContext().getString(R.string.event_pull_request_commits_add, commits, additions);
        } else if (deletions > 0) {
            textCommitsStr = getContext().getString(R.string.event_pull_request_commits_del, commits, deletions);
        } else {
            textCommitsStr = getContext().getString(R.string.event_pull_request_commits, commits);
        }

        textCommits.setText(Html.fromHtml(textCommitsStr));
        textCommits.setVisibility(View.VISIBLE);

        TextView textDate = (TextView) findViewById(R.id.textDate);

        String timeString = TimeUtils.getTimeString(textDate.getContext(), event.created_at);

        textDate.setText(timeString);
    }

    @Override
    protected PullRequestEventPayload convert(Gson gson, String s) {
        return gson.fromJson(s, PullRequestEventPayload.class);
    }
}
