package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.Story;
import com.alorma.github.ui.adapter.issues.holders.CommentHolder;
import com.alorma.github.ui.adapter.issues.holders.Holder;
import com.alorma.github.ui.adapter.issues.holders.LabelsHolder;
import com.alorma.github.ui.adapter.issues.holders.ReviewCommentHolder;
import com.alorma.github.ui.adapter.issues.holders.TimelineHolder;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueCommentView;
import com.alorma.github.ui.view.issue.IssueStoryLabelDetailView;
import com.alorma.github.ui.view.issue.IssueTimelineView;
import com.alorma.github.ui.view.issue.ReviewCommentView;

public abstract class StoryDetailAdapter<K extends Issue> extends RecyclerView.Adapter<Holder<K>> {

  private static final int VIEW_ISSUE = 0;
  private static final int VIEW_COMMENT = 1;
  private static final int VIEW_EVENT = 2;
  private static final int VIEW_LABELED_LIST = 3;
  private static final int VIEW_REVIEW_COMMENT = 4;

  private final LayoutInflater mInflater;

  private Context context;
  private Story<K> story;
  private RepoInfo repoInfo;
  private IssueDetailRequestListener issueDetailRequestListener;

  public StoryDetailAdapter(Context context, LayoutInflater inflater, Story<K> story,
      RepoInfo repoInfo, IssueDetailRequestListener issueDetailRequestListener) {
    this.context = context;
    this.story = story;
    this.repoInfo = repoInfo;
    this.issueDetailRequestListener = issueDetailRequestListener;
    this.mInflater = inflater;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case VIEW_ISSUE:
        return createItemHolder(context, story, repoInfo.permissions, issueDetailRequestListener);
      case VIEW_COMMENT:
        return new CommentHolder(new IssueCommentView(context));
      case VIEW_EVENT:
        return new TimelineHolder(new IssueTimelineView(context));
      case VIEW_REVIEW_COMMENT:
        return new ReviewCommentHolder(new ReviewCommentView(context));
      case VIEW_LABELED_LIST:
        return new LabelsHolder(new IssueStoryLabelDetailView(context));
      default:
        return new Holder<K>(
            mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)) {
          @Override
          public void setIssue(RepoInfo repoInfo, Issue issue) {

          }

          @Override
          public void setDetail(IssueStoryDetail detail) {

          }
        };
    }
  }

  protected abstract Holder<K> createItemHolder(Context context, Story<K> story,
      Permissions permissions, IssueDetailRequestListener issueDetailRequestListener);

  @Override
  public void onBindViewHolder(Holder<K> holder, int position) {
    try {
      holder.setIssue(repoInfo, story.item);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (position > 0) {
      IssueStoryDetail issueStoryDetail = story.details.get(position - 1);
      holder.setDetail(issueStoryDetail);
    }
  }

  @Override
  public int getItemCount() {
    return story != null ? story.details.size() + 1 : 0;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_ISSUE;
    } else {
      IssueStoryDetail issueStoryDetail = story.details.get(position - 1);

      if (issueStoryDetail.getType().equals("commented")) {
        return VIEW_COMMENT;
      } else if (issueStoryDetail.getType().equals("review_comment")) {
        return VIEW_REVIEW_COMMENT;
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
