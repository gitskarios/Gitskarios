package com.alorma.github.ui.adapter.issues;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 22/08/2014.
 */
public class PullRequestsAdapter extends IssuesAdapter {
    public PullRequestsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    protected int getColorState(Issue issue) {
        int colorState = R.color.pullrequest_state_close;
        if (IssueState.open == issue.state) {
            colorState = R.color.pullrequest_state_open;
        } else if ((issue instanceof PullRequest) && ((PullRequest) issue).merged) {
            colorState = R.color.pullrequest_state_merged;
        }
        return colorState;
    }

    @NonNull
    @Override
    protected IIcon getIconStateDrawable(Issue issue) {
        IIcon iconDrawable;
        if (issue.state == IssueState.closed) {
            iconDrawable = Octicons.Icon.oct_issue_closed;
        } else if ((issue instanceof PullRequest) && ((PullRequest) issue).merged) {
            iconDrawable = Octicons.Icon.oct_git_merge;
        } else {
            iconDrawable = Octicons.Icon.oct_issue_opened;
        }

        return iconDrawable;
    }

/*
    private IssuesAdapterListener issuesAdapterListener;

    public PullRequestsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_issue, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, PullRequest issue) {
        holder.title.setText(issue.title);

        holder.num.setText(String.format("#%d", issue.number));

        if (issue.user != null) {
            holder.autor.setText(Html.fromHtml(holder.itemView.getResources().getString(R.string.issue_created_by, issue.user.login)));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(issue.user.avatar_url, holder.avatar);
        }

        int colorState = getColorState(holder, issue);

        holder.num.setTextColor(colorState);
        IconicsDrawable iconDrawable;
        if (issue.state == IssueState.closed) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_issue_closed);
        } else {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_issue_opened);
        }
        iconDrawable.colorRes(R.color.gray_github_medium);
        holder.pullRequest.setImageDrawable(iconDrawable);
    }

    private int getColorState(ViewHolder holder, PullRequest issue) {
        int colorState = holder.itemView.getResources().getColor(R.color.pullrequest_state_close);
        if (IssueState.open == issue.state) {
            colorState = holder.itemView.getResources().getColor(R.color.pullrequest_state_open);
        } else if (issue.merged) {
            colorState = holder.itemView.getResources().getColor(R.color.pullrequest_state_merged);
        }
        return colorState;
    }

    public void setIssuesAdapterListener(IssuesAdapterListener issuesAdapterListener) {
        this.issuesAdapterListener = issuesAdapterListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView num;
        private final TextView autor;
        private final ImageView avatar;
        private final ImageView pullRequest;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textTitle);
            num = (TextView) itemView.findViewById(R.id.textInfo);
            autor = (TextView) itemView.findViewById(R.id.textAuthor);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthor);
            pullRequest = (ImageView) itemView.findViewById(R.id.pullRequest);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PullRequest item = getItem(getAdapterPosition());

                    if (issuesAdapterListener != null) {
                        issuesAdapterListener.onPullRequestOpenRequest(item);
                    }
                }
            });
        }
    }

    public interface IssuesAdapterListener {
        void onPullRequestOpenRequest(PullRequest issue);
    }*/
}
