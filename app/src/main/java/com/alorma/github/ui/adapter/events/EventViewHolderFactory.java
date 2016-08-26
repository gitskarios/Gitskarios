package com.alorma.github.ui.adapter.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.ui.adapter.events.holders.CommitCommentEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.CreatedBranchEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.CreatedRepositoryEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.DeletedEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.EmptyEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.EventViewHolder;
import com.alorma.github.ui.adapter.events.holders.ForkEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.IssueCommentEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.IssueEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.MemberEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.PublicRepoEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.PullRequestEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.PullRequestReviewCommentEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.PushEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.ReleaseEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.StarredEventViewHolder;
import com.alorma.github.ui.adapter.events.holders.WatchedEventViewHolder;

public class EventViewHolderFactory {

  private static final int NO_VIEW = 0;
  private static final int EVENT_STARRED = 1;
  private static final int EVENT_WATCHED = 2;
  private static final int EVENT_CREATED_BRANCH = 3;
  private static final int EVENT_CREATED_REPOSITORY = 4;
  private static final int EVENT_ISSUE = 5;
  private static final int EVENT_ISSUE_COMMENT = 6;
  private static final int EVENT_FORK = 7;
  private static final int EVENT_MEMBER = 8;
  private static final int EVENT_PUBLIC_REPO = 9;
  private static final int EVENT_PULL_REQUEST = 10;
  private static final int EVENT_PULL_REQUEST_REVIEW_COMMENT = 11;
  private static final int EVENT_PUSH = 12;
  private static final int EVENT_COMMIT_COMMENT = 13;
  private static final int EVENT_DELETED = 14;
  private static final int EVENT_RELEASE = 15;

  EventViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.row_event, parent, false);
    if (viewType == EVENT_STARRED) {
      return new StarredEventViewHolder(view);
    } else if (viewType == EVENT_WATCHED) {
      return new WatchedEventViewHolder(view);
    } else if (viewType == EVENT_CREATED_BRANCH) {
      return new CreatedBranchEventViewHolder(view);
    } else if (viewType == EVENT_CREATED_REPOSITORY) {
      return new CreatedRepositoryEventViewHolder(view);
    } else if (viewType == EVENT_ISSUE) {
      return new IssueEventViewHolder(view);
    } else if (viewType == EVENT_ISSUE_COMMENT) {
      return new IssueCommentEventViewHolder(view);
    } else if (viewType == EVENT_FORK) {
      return new ForkEventViewHolder(view);
    } else if (viewType == EVENT_MEMBER) {
      return new MemberEventViewHolder(view);
    } else if (viewType == EVENT_PUBLIC_REPO) {
      return new PublicRepoEventViewHolder(view);
    } else if (viewType == EVENT_PULL_REQUEST) {
      return new PullRequestEventViewHolder(view);
    } else if (viewType == EVENT_PULL_REQUEST_REVIEW_COMMENT) {
      return new PullRequestReviewCommentEventViewHolder(view);
    } else if (viewType == EVENT_PUSH) {
      return new PushEventViewHolder(view);
    } else if (viewType == EVENT_COMMIT_COMMENT) {
      return new CommitCommentEventViewHolder(view);
    } else if (viewType == EVENT_DELETED) {
      return new DeletedEventViewHolder(view);
    } else if (viewType == EVENT_RELEASE) {
      return new ReleaseEventViewHolder(view);
    }
    return new EmptyEventViewHolder(view, null);
  }

  public int viewType(GithubEvent event) {
    if (event.type == EventType.WatchEvent) {
      if (event.payload.action.equalsIgnoreCase("started")) {
        return EVENT_STARRED;
      } else {
        return EVENT_WATCHED;
      }
    } else if (event.type == EventType.CreateEvent) {
      if (event.payload.ref != null) {
        return EVENT_CREATED_BRANCH;
      } else {
        return EVENT_CREATED_REPOSITORY;
      }
    } else if (event.type == EventType.IssuesEvent) {
      return EVENT_ISSUE;
    } else if (event.type == EventType.IssueCommentEvent) {
      return EVENT_ISSUE_COMMENT;
    } else if (event.type == EventType.ForkEvent) {
      return EVENT_FORK;
    } else if (event.type == EventType.MemberEvent) {
      return EVENT_MEMBER;
    } else if (event.type == EventType.PullRequestEvent) {
      return EVENT_PULL_REQUEST;
    } else if (event.type == EventType.PullRequestReviewCommentEvent) {
      return EVENT_PULL_REQUEST_REVIEW_COMMENT;
    } else if (event.type == EventType.PushEvent) {
      return EVENT_PUSH;
    } else if (event.type == EventType.CommitCommentEvent) {
      return EVENT_COMMIT_COMMENT;
    } else if (event.type == EventType.DeleteEvent) {
      return EVENT_DELETED;
    } else if (event.type == EventType.ReleaseEvent) {
      return EVENT_RELEASE;
    } else if (event.type == EventType.PublicEvent) {
      if (event.repo != null) {
        return EVENT_PUBLIC_REPO;
      }
    }
    return NO_VIEW;
  }
}
