package com.alorma.github.ui.activity.issue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

public class MilestonesAdapter
    extends RecyclerArrayAdapter<Milestone, MilestonesAdapter.ViewHolder> {

  public MilestonesAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  protected void onBindViewHolder(ViewHolder holder, Milestone milestone) {
    holder.milestoneTitle.setText(milestone.title);
    holder.milestoneProgress.setMax(milestone.closedIssues + milestone.openIssues);
    holder.milestoneProgress.setProgress(milestone.closedIssues);
    holder.milestoneUpdated.setText(getTimeFormatter().relative(milestone.updatedAt.getTime()));
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(getInflater().inflate(R.layout.row_milestone, parent, false));
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.milestoneTitle) TextView milestoneTitle;
    @Bind(R.id.milestoneProgress) ProgressBar milestoneProgress;
    @Bind(R.id.milestoneUpdated) TextView milestoneUpdated;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(v -> {
        getCallback().onItemSelected(getItem(getAdapterPosition()));
      });
    }
  }
}
