package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.ui.utils.PaletteUtils;

public class LabelView extends TextView {
  public LabelView(Context context) {
    super(context);
    init();
  }

  public LabelView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LabelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    setMinHeight(getResources().getDimensionPixelOffset(R.dimen.label_size_height));
    setGravity(Gravity.CENTER);
    int padding = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
    setPadding(padding * 2, padding, padding * 2, padding);
  }

  public void setLabel(Label label) {
    if (label != null) {
      int color = Color.parseColor("#" + label.color);
      setTextColor(PaletteUtils.foregroundColorFromBackgroundColor(color));
      setText(label.name);

      GradientDrawable bg = new GradientDrawable();
      bg.setColor(color);
      bg.setCornerRadius(getResources().getDimensionPixelOffset(R.dimen.gapLarge));

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        setBackground(bg);
      } else {
        setBackgroundDrawable(bg);
      }
    }
  }
}
