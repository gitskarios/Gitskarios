package com.alorma.github.ui.adapter.issues;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.emoji.EmojiBitmapLoader;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesAdapter extends RecyclerArrayAdapter<Issue , IssuesAdapter.ViewHolder> {

    private IssuesAdapterListener issuesAdapterListener;

    public IssuesAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_issue, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Issue issue) {
        holder.title.setText(issue.title);

        holder.num.setText("#" + issue.number);

        if (issue.user != null) {
            holder.autor.setText(Html.fromHtml(holder.itemView.getResources().getString(R.string.issue_created_by, issue.user.login)));
            ImageLoader instance = ImageLoader.getInstance();
            instance.displayImage(issue.user.avatar_url, holder.avatar);
        }

        int colorState = holder.itemView.getResources().getColor(R.color.issue_state_close);
        if (IssueState.open == issue.state) {
            colorState = holder.itemView.getResources().getColor(R.color.issue_state_open);
        }

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
            num = (TextView) itemView.findViewById(R.id.textIssueNumber);
            autor = (TextView) itemView.findViewById(R.id.textAuthor);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthor);
            pullRequest = (ImageView) itemView.findViewById(R.id.pullRequest);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Issue item = getItem(getAdapterPosition());

                    if (issuesAdapterListener != null) {
                        issuesAdapterListener.onIssueOpenRequest(item);
                    }
                }
            });
        }
    }

    public interface IssuesAdapterListener {
        void onIssueOpenRequest(Issue issue);
    }
}
