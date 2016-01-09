package com.alorma.github.ui.adapter.issues;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.LabelView;
import com.alorma.github.utils.TextUtils;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.wefika.flowlayout.FlowLayout;

/**
 * Created by Bernat on 22/08/2014.
 */
public class IssuesAdapter extends RecyclerArrayAdapter<Issue, IssuesAdapter.ViewHolder> {

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

        String timeAgo = TimeUtils.getTimeAgoString(issue.created_at);
        String info = holder.info.getContext().getResources().getString(R.string.issue_info, issue.number, timeAgo, issue.user.login);

        holder.info.setText(info);

        int colorState = getColorState(issue);

        IconicsDrawable iconDrawable = new IconicsDrawable(holder.itemView.getContext(), getIconStateDrawable(issue));
        iconDrawable.colorRes(colorState);
        holder.imageState.setImageDrawable(iconDrawable);

        if (issue.labels != null) {
            holder.labelsLayout.removeAllViews();
            if (issue.labels.size() > 0) {
                holder.labelsLayout.setVisibility(View.VISIBLE);
                int margin = holder.labelsLayout.getContext().getResources().getDimensionPixelOffset(R.dimen.gapSmall);
                for (Label label : issue.labels) {
                    LabelView labelView = new LabelView(holder.labelsLayout.getContext());
                    labelView.setLabel(label);
                    holder.labelsLayout.addView(labelView);

                    if (labelView.getLayoutParams() != null && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
                        FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) labelView.getLayoutParams();
                        layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.setMargins(margin, margin, margin, margin);
                        labelView.setLayoutParams(layoutParams);
                    }
                }
            } else {
                holder.labelsLayout.setVisibility(View.GONE);
            }
        } else {
            holder.labelsLayout.setVisibility(View.GONE);
        }

        TextUtils.applyNumToTextView(holder.numComments, Octicons.Icon.oct_comment_discussion, issue.comments);
    }

    @NonNull
    protected IIcon getIconStateDrawable(Issue issue) {
        IIcon iconDrawable;
        if (issue.state == IssueState.closed) {
            iconDrawable = Octicons.Icon.oct_issue_closed;
        } else {
            iconDrawable = Octicons.Icon.oct_issue_opened;
        }
        return iconDrawable;
    }

    protected int getColorState(Issue issue) {
        int colorState = R.color.issue_state_close;
        if (IssueState.open == issue.state) {
            colorState = R.color.issue_state_open;
        }
        return colorState;
    }

    public void setIssuesAdapterListener(IssuesAdapterListener issuesAdapterListener) {
        this.issuesAdapterListener = issuesAdapterListener;
    }

    public interface IssuesAdapterListener {
        void onIssueOpenRequest(Issue issue);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView info;
        private final ImageView imageState;
        private final TextView numComments;
        private FlowLayout labelsLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textTitle);
            info = (TextView) itemView.findViewById(R.id.textInfo);
            numComments = (TextView) itemView.findViewById(R.id.numComments);
            imageState = (ImageView) itemView.findViewById(R.id.imageState);
            labelsLayout = (FlowLayout) itemView.findViewById(R.id.labelsLayout);

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
}
