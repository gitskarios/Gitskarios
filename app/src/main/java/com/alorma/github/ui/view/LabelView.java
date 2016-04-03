package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.ui.utils.PaletteUtils;

/**
 * Created by Bernat on 09/04/2015.
 */
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
    setPadding(padding, padding, padding, padding);

    ViewCompat.setElevation(this, padding);
  }

  public void setLabel(Label label) {
    if (label != null) {
      int color = Color.parseColor("#" + label.color);
      setBackgroundColor(color);
      setTextColor(PaletteUtils.colorTextFromBackgroundColor(color));
      setText(label.name);
    }
  }
}
