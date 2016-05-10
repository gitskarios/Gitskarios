package com.alorma.github.ui.activity.issue;

import android.widget.Checkable;
import com.alorma.github.sdk.bean.dto.response.Label;

public class LabelUiModel implements Checkable {
  public Label label;
  private boolean selected;

  public LabelUiModel(Label label) {
    this.label = label;
  }

  @Override
  public boolean isChecked() {
    return selected;
  }

  @Override
  public void setChecked(boolean checked) {
    selected = checked;
  }

  @Override
  public void toggle() {
    selected = !selected;
  }
}
