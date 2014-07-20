/**
 * Copyright 2013 Damien Villeneuve
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * It uses Iconify by Joan Zapata, licensed under Apache License version 2, which is compatible
 * with this library's license.
 */
package fr.dvilleneuve.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class TextDrawable extends Drawable {

	private final Paint paint = new Paint();
	private final Rect bounds = new Rect();
	private final Context context;
	private final String text;
	private Paint.FontMetricsInt fontMetrics;

	public TextDrawable(Context context, String text) {
		this.context = context;
		this.text = text;

		paint.setColor(Color.BLACK);
		paint.setTextSize(convertSpToPx(15));
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.RIGHT);
	}

	/**
	 * Set the size of the drawable.
	 *
	 * @param dimenRes The dimension resource.
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable sizeRes(int dimenRes) {
		return sizePx(context.getResources().getDimensionPixelSize(dimenRes));
	}

	/**
	 * Set the size of the drawable.
	 *
	 * @param size The size in density-independent pixels (dp).
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable sizeDp(float size) {
		return sizePx(convertDpToPx(size));
	}

	/**
	 * Set the size of the drawable.
	 *
	 * @param size The size in pixels (px).
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable sizePx(float size) {
		paint.setTextSize(size);
		invalidateSelf();
		return this;
	}

	/**
	 * Set the typeface of the drawable.
	 *
	 * @param typeface The typeface
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable typeface(Typeface typeface) {
		paint.setTypeface(typeface);
		invalidateSelf();
		return this;
	}

	/**
	 * Set the alignment of the drawable.
	 *
	 * @param align The align
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable align(Paint.Align align) {
		paint.setTextAlign(align);
		invalidateSelf();
		return this;
	}

	/**
	 * Set the size of the drawable.
	 *
	 * @param isBold true if the text should be bold
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable bold(boolean isBold) {
		paint.setFakeBoldText(isBold);
		invalidateSelf();
		return this;
	}

	/**
	 * Set the color of the drawable.
	 *
	 * @param color The color, usually from android.graphics.Color or 0xFF012345.
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable color(int color) {
		paint.setColor(color);
		invalidateSelf();
		return this;
	}

	/**
	 * Set the color of the drawable.
	 *
	 * @param colorRes The color resource, from your R file.
	 * @return The current TextDrawable for chaining.
	 */
	public TextDrawable colorRes(int colorRes) {
		return color(context.getResources().getColor(colorRes));
	}

	/**
	 * Set the alpha of this drawable.
	 *
	 * @param alpha The alpha, between 0 (transparent) and 255 (opaque).
	 * @return The current IconDrawable for chaining.
	 */
	public TextDrawable alpha(int alpha) {
		setAlpha(alpha);
		invalidateSelf();
		return this;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawText(text, getIntrinsicWidth(), getIntrinsicHeight() - getFontMetrics().descent, paint);
	}

	@Override
	public int getIntrinsicWidth() {
		paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.width();
	}

	@Override
	public int getIntrinsicHeight() {
		return getFontMetrics().bottom - getFontMetrics().top;
	}

	@Override
	public void setAlpha(int alpha) {
		paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	private Paint.FontMetricsInt getFontMetrics() {
		if (fontMetrics == null) {
			fontMetrics = paint.getFontMetricsInt();
		}
		return fontMetrics;
	}

	private float convertSpToPx(float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, context.getResources().getDisplayMetrics());
	}

	private float convertDpToPx(float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
}