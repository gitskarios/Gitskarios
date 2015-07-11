package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.ui.listeners.IssueDetailRequestListener;
import com.alorma.github.ui.view.issue.IssueCommentView;
import com.alorma.github.ui.view.issue.IssueDetailView;
import com.alorma.github.ui.view.issue.IssueTimelineView;

/**
 * Created by Bernat on 08/04/2015.
 */
public class IssueDetailAdapter extends RecyclerView.Adapter<IssueDetailAdapter.Holder> {

    private static final int VIEW_DEFAULT = -1;
    private static final int VIEW_ISSUE = 0;
    private static final int VIEW_EVENT = 1;
    private static final int VIEW_COMMENT = 2;

    private Context context;
    private LayoutInflater inflater;
    private IssueStory issueStory;
    private RepoInfo repoInfo;
    private IssueDetailRequestListener issueDetailRequestListener;

    public IssueDetailAdapter(Context context, LayoutInflater inflater, IssueStory issueStory, RepoInfo repoInfo) {
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
                IssueDetailView issueDetailView = new IssueDetailView(context);
                issueDetailView.setIssueDetailRequestListener(issueDetailRequestListener);
                return new IssueHolder(issueDetailView);
            case VIEW_COMMENT:
                return new CommentHolder(new IssueCommentView(context));
            case VIEW_EVENT:
                return new TimelineHolder(new IssueTimelineView(context));
            default:
                return new Holder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (position == 0) {
            ((IssueHolder) holder).issueDetailView.setIssue(repoInfo, issueStory.issue);
            ((IssueHolder) holder).issueDetailView.setIssueDetailRequestListener(issueDetailRequestListener);
        } else if (holder instanceof CommentHolder) {
            IssueStoryComment issueStoryDetail = (IssueStoryComment) issueStory.details.get(position - 1).second;
            ((CommentHolder) holder).issueCommentView.setComment(repoInfo, issueStoryDetail);
        } else if (holder instanceof TimelineHolder) {
            if (issueStory.details.get(position - 1).second instanceof IssueStoryEvent) {
                IssueStoryEvent issueStoryDetail = (IssueStoryEvent) issueStory.details.get(position - 1).second;
                ((TimelineHolder) holder).issueTimelineView.setLastItem((position + 1) == getItemCount());
                ((TimelineHolder) holder).issueTimelineView.setIssueEvent(issueStoryDetail);
            }
        } else {
            IssueStoryDetail issueStoryDetail = issueStory.details.get(position - 1).second;
            if (issueStoryDetail instanceof IssueStoryEvent) {
                holder.text.setText(((IssueStoryEvent) issueStoryDetail).event.event);
            }
        }
    }

    @Override
    public int getItemCount() {
        return issueStory != null ? issueStory.details.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_ISSUE;
        } else {
            IssueStoryDetail issueStoryDetail = issueStory.details.get(position - 1).second;
            if (issueStoryDetail instanceof IssueStoryComment) {
                return VIEW_COMMENT;
            } else if (issueStoryDetail instanceof IssueStoryEvent) {
                return VIEW_EVENT;
            } else {
                return VIEW_DEFAULT;
            }
        }
    }

    private class IssueHolder extends Holder {
        private final IssueDetailView issueDetailView;

        public IssueHolder(IssueDetailView issueDetailView) {
            super(issueDetailView);
            this.issueDetailView = issueDetailView;
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

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView text;

        public Holder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);
        }

    }
}
