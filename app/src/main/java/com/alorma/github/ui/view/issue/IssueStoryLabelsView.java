package com.alorma.github.ui.view.issue;

import android.content.Context;
import android.util.AttributeSet;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.ui.view.LabelView;
import com.wefika.flowlayout.FlowLayout;
import java.util.List;

/**
 * Created by Bernat on 18/07/2015.
 */
public class IssueStoryLabelsView extends FlowLayout {
  public IssueStoryLabelsView(Context context) {
    super(context);
  }

  public IssueStoryLabelsView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public IssueStoryLabelsView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setLabels(List<Label> labels) {
    removeAllViews();
    if (labels != null && labels.size() > 0) {
      for (Label label : labels) {
        LabelView labelView = new LabelView(getContext());
        labelView.setLabel(label);
        addView(labelView);

        if (labelView.getLayoutParams() != null
            && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
          int margin = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
          FlowLayout.LayoutParams layoutParams =
              (FlowLayout.LayoutParams) labelView.getLayoutParams();
          layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
          layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
          layoutParams.setMargins(margin, margin, margin, margin);
          labelView.setLayoutParams(layoutParams);
        }
      }
    }
  }
}
