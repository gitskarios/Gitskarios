package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.ui.adapter.issues.holders.CommentHolder;
import com.alorma.github.ui.adapter.issues.holders.Holder;
import com.alorma.github.ui.adapter.issues.holders.IssueHolder;
import com.alorma.github.ui.adapter.issues.holders.LabelsHolder;
import com.alorma.github.ui.adapter.issues.holders.TimelineHolder;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueCommentView;
import com.alorma.github.ui.view.issue.IssueDetailView;
import com.alorma.github.ui.view.issue.IssueStoryLabelDetailView;
import com.alorma.github.ui.view.issue.IssueTimelineView;

public class IssueDetailAdapter extends RecyclerView.Adapter<Holder> {

  private static final int VIEW_ISSUE = 0;
  private static final int VIEW_COMMENT = 1;
  private static final int VIEW_EVENT = 2;
  private static final int VIEW_LABELED_LIST = 3;

  private Context context;
  private LayoutInflater inflater;
  private IssueStory issueStory;
  private RepoInfo repoInfo;
  private IssueDetailRequestListener issueDetailRequestListener;

  public IssueDetailAdapter(Context context, LayoutInflater inflater, IssueStory issueStory,
      RepoInfo repoInfo) {
    this.context = context;
    this.inflater = inflater;
    this.issueStory = issueStory;
    this.repoInfo = repoInfo;
  }

  public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
    this.issueDetailRequestListener = issueDetailRequestListener;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_ISSUE:
        return new IssueHolder(new IssueDetailView(context), issueDetailRequestListener);
      case VIEW_COMMENT:
        return new CommentHolder(new IssueCommentView(context));
      case VIEW_EVENT:
        return new TimelineHolder(new IssueTimelineView(context));
      case VIEW_LABELED_LIST:
        return new LabelsHolder(new IssueStoryLabelDetailView(context));
      default:
        return new Holder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false)) {

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
    holder.setIssue(repoInfo, issueStory.issue);
    if (position > 0) {
      IssueStoryDetail issueStoryDetail = issueStory.details.get(position - 1);
      holder.setDetail(issueStoryDetail);
    }
  }

  @Override
  public int getItemCount() {
    int detailsSize = issueStory.details != null ? issueStory.details.size() : 0;
    return issueStory != null ? detailsSize + 1 : 0;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_ISSUE;
    } else {
      IssueStoryDetail issueStoryDetail = issueStory.details.get(position - 1);

      if (issueStoryDetail.getType().equals("commented")) {
        return VIEW_COMMENT;
      } else if (issueStoryDetail.isList() && (issueStoryDetail.getType().equals("labeled")
          || issueStoryDetail.getType().equals("unlabeled"))) {
        return VIEW_LABELED_LIST;
      }
      return VIEW_EVENT;
    }
  }
}
