package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.bean.issue.IssueStoryLabelList;
import com.alorma.github.sdk.bean.issue.IssueStoryUnlabelList;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueCommentView;
import com.alorma.github.ui.view.issue.IssueStoryLabelDetailView;
import com.alorma.github.ui.view.issue.IssueTimelineView;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;

/**
 * Created by Bernat on 08/04/2015.
 */
public class PullRequestDetailAdapter extends RecyclerView.Adapter<PullRequestDetailAdapter.Holder> {

    private static final int VIEW_INVALID = -1;
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

    public PullRequestDetailAdapter(Context context, LayoutInflater inflater, PullRequestStory pullRequestStory, RepoInfo repoInfo,
                                    Permissions permissions, PullRequestDetailView.PullRequestActionsListener pullRequestActionsListener) {
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
                return new PullRequestHolder(new PullRequestDetailView(context));
            case VIEW_COMMENT:
                IssueCommentView itemViewComment = new IssueCommentView(context);
                return new CommentHolder(itemViewComment);
            case VIEW_EVENT:
                return new TimelineHolder(mInflater.inflate(R.layout.timeline_simple_view, parent, false));
            case VIEW_LABELED_LIST:
                return new LabelsHolder(new IssueStoryLabelDetailView(context));
            default:
                return new Holder(mInflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        int viewType = getItemViewType(position);

        if (position == 0) {
            ((PullRequestHolder) holder).pullRequestDetailView.setPullRequest(repoInfo, pullRequestStory.pullRequest,
                    pullRequestStory.statusResponse, permissions);
            ((PullRequestHolder) holder).pullRequestDetailView.setIssueDetailRequestListener(issueDetailRequestListener);
            ((PullRequestHolder) holder).pullRequestDetailView.setPullRequestActionsListener(pullRequestActionsListener);
        } else {
            IssueStoryDetail issueStoryDetail = pullRequestStory.details.get(position - 1);
            if (viewType == VIEW_LABELED_LIST) {
                if (issueStoryDetail instanceof IssueStoryLabelList) {
                    ((LabelsHolder) holder).itemView.setLabelsEvent((IssueStoryLabelList) issueStoryDetail);
                } else if (issueStoryDetail instanceof IssueStoryUnlabelList) {
                    ((LabelsHolder) holder).itemView.setLabelsEvent((IssueStoryUnlabelList) issueStoryDetail);
                }
            } else if (viewType == VIEW_COMMENT) {
                ((CommentHolder) holder).issueCommentView.setComment(repoInfo, (IssueStoryComment) issueStoryDetail);
            } else if (viewType == VIEW_EVENT) {
                ((TimelineHolder) holder).issueTimelineView.setIssueEvent(((IssueStoryEvent) issueStoryDetail));
            }
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
                if (issueStoryDetail.getType().equals("labeled") || issueStoryDetail.getType().equals("unlabeled")) {
                    return VIEW_LABELED_LIST;
                }
            }

            return VIEW_EVENT;
        }
    }

    private class PullRequestHolder extends Holder {
        private final PullRequestDetailView pullRequestDetailView;

        public PullRequestHolder(PullRequestDetailView pullRequestDetailView) {
            super(pullRequestDetailView);
            this.pullRequestDetailView = pullRequestDetailView;
        }
    }

    private class CommentHolder extends Holder {
        private final IssueCommentView issueCommentView;

        public CommentHolder(IssueCommentView itemView) {
            super(itemView);
            issueCommentView = itemView;
        }
    }

    private class TimelineHolder extends Holder {
        private final IssueTimelineView issueTimelineView;

        public TimelineHolder(View itemView) {
            super(itemView);
            issueTimelineView = (IssueTimelineView) itemView.findViewById(R.id.timeline);
        }
    }

    private class LabelsHolder extends Holder {
        private final IssueStoryLabelDetailView itemView;

        public LabelsHolder(IssueStoryLabelDetailView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
