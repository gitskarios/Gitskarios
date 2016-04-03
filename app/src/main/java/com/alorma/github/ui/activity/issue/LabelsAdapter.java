package com.alorma.github.ui.activity.issue;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LabelsAdapter extends RecyclerArrayAdapter<LabelUiModel, LabelsAdapter.ViewHolder> {

  private final Set<String> selectedLabels;

  public LabelsAdapter(LayoutInflater inflater, List<String> selectedLabels) {
    super(inflater);
    this.selectedLabels = new HashSet<>(selectedLabels);
  }

  @Override
  protected void onBindViewHolder(ViewHolder holder, LabelUiModel labelUiModel) {
    int color = Color.parseColor("#" + labelUiModel.label.color);
    GradientDrawable gd = new GradientDrawable();
    gd.setColor(color);
    gd.setCornerRadius(8f);
    holder.labelColor.setImageDrawable(gd);
    holder.labelTitle.setText(labelUiModel.label.name);

    holder.labelCheck.setChecked(selectedLabels.contains(labelUiModel.label.name));
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(getInflater().inflate(R.layout.row_label, parent, false));
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.labelColor) ImageView labelColor;
    @Bind(R.id.labelTitle) TextView labelTitle;
    @Bind(R.id.labelCheck) CheckBox labelCheck;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(v -> {
        changeState();
      });

      labelTitle.setOnClickListener(v -> {
        changeState();
      });
    }

    private void changeState() {
      LabelUiModel item = getItem(getAdapterPosition());
      item.toggle();

      if (item.isChecked()) {
        selectedLabels.add(item.label.name);
      } else {
        selectedLabels.remove(item.label.name);
      }

      notifyDataSetChanged();
    }
  }

  public Set<String> getSelectedLabels() {
    return selectedLabels;
  }
}
