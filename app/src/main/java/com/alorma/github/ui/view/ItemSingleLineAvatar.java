package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;

public class ItemSingleLineAvatar extends RelativeLayout {
  @BindView(R.id.image) ImageView imageView;
  @BindView(R.id.text) TextView textView;

  public ItemSingleLineAvatar(Context context) {
    super(context);
    init();
  }

  public ItemSingleLineAvatar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ItemSingleLineAvatar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ItemSingleLineAvatar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.draw_item_single_line_avatar_with_text, this);
    ButterKnife.bind(this, this);
  }

  public ImageView getImageView() {
    return imageView;
  }

  public TextView getTextView() {
    return textView;
  }
}
