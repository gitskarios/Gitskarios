package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import com.alorma.github.R;

public class DiffTextView extends TextView {

  private int additionColor;
  private int deletionColor;
  private int infoColor;
  private int additionTextColor;
  private int deletionTextColor;
  private int infoTextColor;
  private boolean showInfo;
  private int maxLines = -1;

  public DiffTextView(Context context) {
    this(context, null);
  }

  public DiffTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, R.attr.diff_theme);
  }

  public DiffTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DiffTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    isInEditMode();

    final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DiffTextViewStyle, defStyleAttr, 0);
    this.additionColor = array.getColor(R.styleable.DiffTextViewStyle_diff_addition_color, Color.parseColor("#CCFFCC"));
    this.deletionColor = array.getColor(R.styleable.DiffTextViewStyle_diff_deletion_color, Color.parseColor("#FFDDDD"));
    this.infoColor = array.getColor(R.styleable.DiffTextViewStyle_diff_info_color, Color.parseColor("#EEEEEE"));
    this.additionTextColor = array.getColor(R.styleable.DiffTextViewStyle_diff_addition_text_color, Color.TRANSPARENT);
    this.deletionTextColor = array.getColor(R.styleable.DiffTextViewStyle_diff_deletion_text_color, Color.TRANSPARENT);
    this.infoTextColor = array.getColor(R.styleable.DiffTextViewStyle_diff_info_text_color, Color.TRANSPARENT);
    this.showInfo = array.getBoolean(R.styleable.DiffTextViewStyle_diff_show_diff_info, false);
    array.recycle();
  }

  @Override
  public void setText(CharSequence text, BufferType type) {
    if (!TextUtils.isEmpty(text)) {
      String diff = text.toString();
      String[] split = diff.split("\\r?\\n|\\r");

      if (split.length > 0) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        int lines;
        if (maxLines > 0) {
          lines = Math.min(maxLines, split.length);
        } else {
          lines = split.length;
        }

        for (int i = 0; i < lines; i++) {
          String token = split[i];
          if (!token.startsWith("@@") || showInfo) {
            if (i < (lines - 1)) {
              token = token.concat("\n");
            }

            char firstChar = token.charAt(0);

            int color = Color.TRANSPARENT;
            int textColor = Color.TRANSPARENT;
            if (firstChar == '+') {
              color = additionColor;
              textColor = additionTextColor;
            } else if (firstChar == '-') {
              color = deletionColor;
              textColor = deletionTextColor;
            } else if (token.startsWith("@@")) {
              color = infoColor;
              textColor = infoTextColor;
            }

            SpannableString spannableDiff = new SpannableString(token);
            // Span for line color (where transparent is considered as default)
            if (color != Color.TRANSPARENT) {
              DiffLineSpan span = new DiffLineSpan(color, getPaddingLeft());
              spannableDiff.setSpan(span, 0, token.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // Span for text color (where transparent is considered as default)
            if (textColor != Color.TRANSPARENT) {
              ForegroundColorSpan span = new ForegroundColorSpan(textColor);
              spannableDiff.setSpan(span, 0, token.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            builder.append(spannableDiff);
          }
        }

        super.setText(builder, type);
      }
    } else {
      super.setText(text, type);
    }
  }

  @Override
  public int getMaxLines() {
    return maxLines;
  }

  @Override
  public void setMaxLines(int maxlines) {
    this.maxLines = maxlines;
  }
}
