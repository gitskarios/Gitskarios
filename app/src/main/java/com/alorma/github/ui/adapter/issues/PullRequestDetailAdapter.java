package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryCommit;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComment;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueCommentView;
import com.alorma.github.ui.view.issue.IssueTimelineView;
import com.alorma.github.ui.view.pullrequest.PullRequestDetailView;

/**
 * Created by Bernat on 08/04/2015.
 */
public class PullRequestDetailAdapter extends RecyclerView.Adapter<PullRequestDetailAdapter.Holder> {

    private static final int VIEW_DEFAULT = -1;
    private static final int VIEW_PULLREQUEST = 0;
    private static final int VIEW_EVENT = 1;
    private static final int VIEW_COMMENT = 2;
    private static final int VIEW_COMMIT = 3;
    private static final int VIEW_REVIEW_COMMENT = 4;

    private Context context;
    private LayoutInflater inflater;
    private PullRequestStory pullRequestStory;
    private RepoInfo repoInfo;
    private Permissions permissions;
    private PullRequestDetailView.PullRequestActionsListener listener;
    private IssueDetailRequestListener issueDetailRequestListener;

    public PullRequestDetailAdapter(Context context, LayoutInflater inflater, PullRequestStory pullRequestStory, RepoInfo repoInfo, Permissions permissions, PullRequestDetailView.PullRequestActionsListener listener) {
        this.context = context;
        this.inflater = inflater;
        this.pullRequestStory = pullRequestStory;
        this.repoInfo = repoInfo;
        this.permissions = permissions;
        this.listener = listener;
    }

    public void setIssueDetailRequestListener(IssueDetailRequestListener issueDetailRequestListener) {
        this.issueDetailRequestListener = issueDetailRequestListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_PULLREQUEST:
                PullRequestDetailView view = new PullRequestDetailView(context);
                view.setIssueDetailRequestListener(issueDetailRequestListener);
                return new PullRequestHolder(view);
            case VIEW_COMMENT:
                return new CommentHolder(new IssueCommentView(context));
            case VIEW_EVENT:
                return new TimelineHolder(new IssueTimelineView(context));
            case VIEW_COMMIT:
                return new TimelineHolder(new IssueTimelineView(context));
            case VIEW_REVIEW_COMMENT:
                return new ReviewCommentHolder(new TextView(context));
            default:
                return new Holder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (position == 0) {
            ((PullRequestHolder) holder).pullRequestDetailView.setPullRequest(repoInfo, pullRequestStory.pullRequest, permissions);
            ((PullRequestHolder) holder).pullRequestDetailView.setPullRequestActionsListener(listener);
        } else {
            IssueStoryDetail event = pullRequestStory.details.get(position - 1).second;
            if (holder instanceof CommentHolder) {
                IssueStoryComment issueStoryDetail = (IssueStoryComment) event;
                ((CommentHolder) holder).issueCommentView.setComment(repoInfo, issueStoryDetail);
            } else if (holder instanceof TimelineHolder) {
                if (event instanceof IssueStoryEvent) {
                    IssueStoryEvent issueStoryDetail = (IssueStoryEvent) event;
                    ((TimelineHolder) holder).issueTimelineView.setLastItem((position + 1) == getItemCount());
                    ((TimelineHolder) holder).issueTimelineView.setIssueEvent(issueStoryDetail);
                } else if (event instanceof IssueStoryCommit) {
                    ((TimelineHolder) holder).issueTimelineView.setLastItem((position + 1) == getItemCount());
                    ((TimelineHolder) holder).issueTimelineView.setIssueStoryCommit((IssueStoryCommit) event);
                }
            } else if (holder instanceof ReviewCommentHolder) {
                if (event instanceof IssueStoryReviewComment) {
                    ReviewComment reviewComment = ((IssueStoryReviewComment) event).event;
                    ((ReviewCommentHolder) holder).textView.setText(reviewComment.body);
                }
            } else {
                if (event instanceof IssueStoryEvent) {
                    holder.text.setText(((IssueStoryEvent) event).event.event);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (pullRequestStory != null && pullRequestStory.details != null) ? pullRequestStory.details.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_PULLREQUEST;
        } else {
            IssueStoryDetail issueStoryDetail = pullRequestStory.details.get(position - 1).second;
            if (issueStoryDetail instanceof IssueStoryComment) {
                return VIEW_COMMENT;
            } else if (issueStoryDetail instanceof IssueStoryEvent) {
                return VIEW_EVENT;
            }  else if (issueStoryDetail instanceof IssueStoryCommit) {
                return VIEW_COMMIT;
            }  else if (issueStoryDetail instanceof IssueStoryReviewComment) {
                return VIEW_REVIEW_COMMENT;
            } else {
                return VIEW_DEFAULT;
            }
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

        public TimelineHolder(IssueTimelineView itemView) {
            super(itemView);
            issueTimelineView = itemView;
        }
    }

    private class ReviewCommentHolder extends Holder {
        private final TextView textView;

        public ReviewCommentHolder(TextView itemView) {
            super(itemView);
            textView = itemView;
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView text;

        public Holder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);
        }

    }
}
