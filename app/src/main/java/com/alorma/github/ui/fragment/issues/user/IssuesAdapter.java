package com.alorma.github.ui.fragment.issues.user;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.LabelView;
import com.alorma.github.utils.TextUtils;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.wefika.flowlayout.FlowLayout;
import core.issues.Issue;
import core.issues.IssueState;
import core.issues.Label;
import core.issues.Milestone;

public class IssuesAdapter extends RecyclerArrayAdapter<Issue, IssuesAdapter.ViewHolder> {

  public IssuesAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(getInflater().inflate(R.layout.row_issue, parent, false));
  }

  @Override
  protected void onBindViewHolder(ViewHolder holder, Issue issue) {
    holder.title.setText("[" + issue.getRepository().getName() + "] " +  issue.getTitle());

    String timeAgo = TimeUtils.getTimeAgoString(issue.getCreatedAt());
    Resources resources = holder.info.getContext().getResources();
    String info = resources.getString(R.string.issue_info, issue.getNumber(), timeAgo, issue.getUser().getLogin());
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

    Milestone milestone = issue.getMilestone();
    if (milestone != null) {
      String milestoneText = milestone.title + " " + milestone.openIssues + "/" + (milestone.openIssues + milestone.closedIssues);
      holder.textMilestone.setText(milestoneText);
      holder.milestoneLy.setVisibility(View.VISIBLE);
    } else {
      holder.milestoneLy.setVisibility(View.GONE);
    }

    TextUtils.applyNumToTextView(holder.numComments, Octicons.Icon.oct_comment_discussion, issue.getComments());
  }

  @NonNull
  protected IIcon getIconStateDrawable(Issue issue) {
    IIcon iconDrawable;
    if (issue.getPullRequest() != null) {
      iconDrawable = Octicons.Icon.oct_git_pull_request;
    } else if (issue.getState() == IssueState.closed) {
      iconDrawable = Octicons.Icon.oct_issue_closed;
    } else {
      iconDrawable = Octicons.Icon.oct_issue_opened;
    }
    return iconDrawable;
  }

  protected int getColorState(Issue issue) {
    int colorState = R.color.issue_state_close;
    if (IssueState.open == issue.getState()) {
      colorState = R.color.issue_state_open;
    }
    return colorState;
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final TextView info;
    private final ImageView imageState;
    private final TextView numComments;
    private final TextView textMilestone;
    private FlowLayout labelsLayout;
    private ViewGroup milestoneLy;

    public ViewHolder(View itemView) {
      super(itemView);

      title = (TextView) itemView.findViewById(R.id.textTitle);
      info = (TextView) itemView.findViewById(R.id.textInfo);
      numComments = (TextView) itemView.findViewById(R.id.numComments);
      textMilestone = (TextView) itemView.findViewById(R.id.textMilestone);
      imageState = (ImageView) itemView.findViewById(R.id.imageState);
      labelsLayout = (FlowLayout) itemView.findViewById(R.id.labelsLayout);
      milestoneLy = (ViewGroup) itemView.findViewById(R.id.milestoneLy);

      itemView.setOnClickListener(v -> getCallback().onItemSelected(getItem(getAdapterPosition())));
    }
  }
}
