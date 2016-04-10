package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.ui.adapter.issues.holders.CommentHolder;
import com.alorma.github.ui.adapter.issues.holders.Holder;
import com.alorma.github.ui.adapter.issues.holders.LabelsHolder;
import com.alorma.github.ui.adapter.issues.holders.PullRequestHolder;
import com.alorma.github.ui.adapter.issues.holders.TimelineHolder;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueCommentView;
import com.alorma.github.ui.view.issue.IssueStoryLabelDetailView;
import com.alorma.github.ui.view.issue.IssueTimelineView;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;

public class PullRequestDetailAdapter extends RecyclerView.Adapter<Holder> {

  private static final int VIEW_ISSUE = 0;
  private static final int VIEW_COMMENT = 1;
  private static final int VIEW_EVENT = 2;
  private static final int VIEW_LABELED_LIST = 3;

  private final LayoutInflater mInflater;

  private Context context;
  private PullRequestStory pullRequestStory;
  private RepoInfo repoInfo;
  private Permissions permissions;
  private PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener;
  private IssueDetailRequestListener issueDetailRequestListener;

  public PullRequestDetailAdapter(Context context, LayoutInflater inflater,
      PullRequestStory pullRequestStory, RepoInfo repoInfo, Permissions permissions,
      PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener) {
    this.context = context;
    this.pullRequestStory = pullRequestStory;
    this.repoInfo = repoInfo;
    this.permissions = permissions;
    this.pullRequestActionsListener = pullRequestActionsListener;
    this.mInflater = inflater;
  }

  public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
    this.issueDetailRequestListener = issueDetailRequestListener;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_ISSUE:
        PullRequestDetailView detailView = new PullRequestDetailView(context);
        return new PullRequestHolder(detailView, pullRequestStory, permissions,
            issueDetailRequestListener, pullRequestActionsListener);
      case VIEW_COMMENT:
        return new CommentHolder(new IssueCommentView(context));
      case VIEW_EVENT:
        return new TimelineHolder(new IssueTimelineView(context));
      case VIEW_LABELED_LIST:
        return new LabelsHolder(new IssueStoryLabelDetailView(context));
      default:
        return new Holder(mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)) {
          @Override
          public void setIssue(RepoInfo repoInfo, Issue issue) {

          }

          @Override
          public void setDetail(IssueStoryDetail detail) {

          }
        };
    }
  }

  @Override
  public void onBindViewHolder(Holder holder, int position) {
    holder.setIssue(repoInfo, pullRequestStory.pullRequest);
    if (position > 0) {
      IssueStoryDetail issueStoryDetail = pullRequestStory.details.get(position - 1);
      holder.setDetail(issueStoryDetail);
    }
  }

  @Override
  public int getItemCount() {
    return pullRequestStory != null ? pullRequestStory.details.size() + 1 : 0;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_ISSUE;
    } else {
      IssueStoryDetail issueStoryDetail = pullRequestStory.details.get(position - 1);

      if (issueStoryDetail.getType().equals("commented")) {
        return VIEW_COMMENT;
      } else if (issueStoryDetail.isList()) {
        if (issueStoryDetail.getType().equals("labeled") || issueStoryDetail.getType()
            .equals("unlabeled")) {
          return VIEW_LABELED_LIST;
        }
      }

      return VIEW_EVENT;
    }
  }
}
