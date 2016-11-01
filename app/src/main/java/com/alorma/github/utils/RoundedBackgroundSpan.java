package com.alorma.github.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.text.style.ReplacementSpan;

public class RoundedBackgroundSpan extends ReplacementSpan {


  private int backgroundColor = 0;
  private int textColor = 0;
  private float radius = 0f;

  public RoundedBackgroundSpan(@ColorInt int backgroundColor, @ColorInt int textColor, @Dimension float radius) {
    super();
    this.backgroundColor = backgroundColor;
    this.textColor = textColor;
    this.radius = radius;
  }

  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
    RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
    paint.setColor(backgroundColor);
    canvas.drawRoundRect(rect, radius, radius, paint);
    paint.setColor(textColor);
    canvas.drawText(text, start, end, x, y, paint);
  }

  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
    return Math.round(paint.measureText(text, start, end));
  }

  private float measureText(Paint paint, CharSequence text, int start, int end) {
    return paint.measureText(text, start, end);
  }
}
