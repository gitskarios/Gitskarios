package com.alorma.github.ui.adapter.issues;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.ui.view.issue.IssueDetailView;

/**
 * Created by Bernat on 08/04/2015.
 */
public class IssueDetailAdapter extends RecyclerView.Adapter<IssueDetailAdapter.Holder> {

    private static final int VIEW_ISSUE = 0;
    private static final int VIEW_EVENT = 1;

    private Context context;
    private LayoutInflater inflater;
    private IssueStory issueStory;

    public IssueDetailAdapter(Context context, LayoutInflater inflater, IssueStory issueStory) {
        this.context = context;
        this.inflater = inflater;
        this.issueStory = issueStory;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_ISSUE:
                return new IssueHolder(new IssueDetailView(context));
            case VIEW_EVENT:
                default:
                return new Holder(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (position == 0) {
            ((IssueHolder)holder).issueDetailView.setIssue(issueStory.issue);
        } else {
            IssueStoryDetail issueStoryDetail = issueStory.details.get(position - 1).second.get(0);
            if (issueStoryDetail instanceof IssueStoryComment) {
                holder.text.setText(((IssueStoryComment) issueStoryDetail).comment.body);
            } else if (issueStoryDetail instanceof IssueStoryEvent) {
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
            return VIEW_EVENT;
        }
    }

    private class IssueHolder extends Holder {
        private final IssueDetailView issueDetailView;
        public IssueHolder(IssueDetailView issueDetailView) {
            super(issueDetailView);
            this.issueDetailView = issueDetailView;
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
