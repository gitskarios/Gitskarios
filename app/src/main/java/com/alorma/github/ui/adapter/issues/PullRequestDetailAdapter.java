package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.Story;
import com.alorma.github.ui.adapter.issues.holders.Holder;
import com.alorma.github.ui.adapter.issues.holders.PullRequestHolder;
import com.alorma.github.ui.listeners.IssueCommentRequestListener;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;
import core.repositories.Permissions;

public class PullRequestDetailAdapter extends StoryDetailAdapter<PullRequest> {

  public PullRequestDetailAdapter(Context context, LayoutInflater inflater, Story<PullRequest> story, RepoInfo repoInfo,
      IssueDetailRequestListener issueDetailRequestListener, IssueCommentRequestListener issueCommentRequestListener) {
    super(context, inflater, story, repoInfo, issueDetailRequestListener, issueCommentRequestListener);
  }

  @Override
  protected Holder<PullRequest> createItemHolder(Context context, ViewGroup.LayoutParams params, Story<PullRequest> story,
      Permissions permissions, IssueDetailRequestListener issueDetailRequestListener) {

    PullRequestDetailView detailView = new PullRequestDetailView(context);
    detailView.setPullRequestActionsListener((PullRequestDetailView.PullRequestActionsListener) issueDetailRequestListener);
    detailView.setLayoutParams(params);

    return new PullRequestHolder(detailView, story, permissions,
        (PullRequestDetailView.PullRequestActionsListener) issueDetailRequestListener);
  }
}
