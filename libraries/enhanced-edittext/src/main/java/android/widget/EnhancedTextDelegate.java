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
package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import fr.dvilleneuve.android.DrawablePosition;
import fr.dvilleneuve.android.EnhancedText;
import fr.dvilleneuve.android.OnClickDrawableListener;
import fr.dvilleneuve.android.R;
import fr.dvilleneuve.android.TextDrawable;

public class EnhancedTextDelegate implements View.OnTouchListener, EnhancedText {

	public static final int DRAWABLE_CLICK_PADDING = 10;

	private final TextView textView;

	private String prefixIcon;
	private String prefixText;
	private ColorStateList prefixColors;
	private String suffixIcon;
	private String suffixText;
	private ColorStateList suffixColors;
	private TextDrawable prefixTextDrawable;
	private IconDrawable prefixIconDrawable;
	private TextDrawable suffixTextDrawable;
	private IconDrawable suffixIconDrawable;
	private OnClickDrawableListener onClickDrawableListener;

	protected EnhancedTextDelegate(TextView textView) {
		this.textView = textView;
	}

	protected void initAttrs(Context context, AttributeSet attrs, int defStyle) {
		TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.EnhancedEditText, defStyle, 0);
		prefixIcon = attr.getString(R.styleable.EnhancedEditText_prefixIcon);
		prefixText = attr.getString(R.styleable.EnhancedEditText_prefixText);
		if (attr.hasValue(R.styleable.EnhancedEditText_prefixColor)) {
			prefixColors = attr.getColorStateList(R.styleable.EnhancedEditText_prefixColor);
		} else {
			prefixColors = textView.getTextColors();
		}

		suffixIcon = attr.getString(R.styleable.EnhancedEditText_suffixIcon);
		suffixText = attr.getString(R.styleable.EnhancedEditText_suffixText);
		if (attr.hasValue(R.styleable.EnhancedEditText_suffixColor)) {
			suffixColors = attr.getColorStateList(R.styleable.EnhancedEditText_suffixColor);
		} else {
			suffixColors = textView.getTextColors();
		}
		attr.recycle();
	}

	protected void init() {
		textView.setCompoundDrawablePadding(16);

		if (!textView.isInEditMode()) {
			if (prefixIcon != null) {
				setPrefixIcon(Iconify.IconValue.valueOf(prefixIcon));
			}
			if (suffixIcon != null) {
				setSuffixIcon(Iconify.IconValue.valueOf(suffixIcon));
			}
		}
		setPrefixText(prefixText);
		setSuffixText(suffixText);

		updateTextColor();
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
			if (onClickDrawableListener != null) {
				Drawable prefixDrawable = getPrefixDrawable();
				if (touchesDrawable(motionEvent, prefixDrawable, DrawablePosition.PREFIX)) {
					onClickDrawableListener.onClickDrawable(prefixDrawable, DrawablePosition.PREFIX);
					return true;
				}
				Drawable suffixDrawable = getSuffixDrawable();
				if (touchesDrawable(motionEvent, suffixDrawable, DrawablePosition.SUFFIX)) {
					onClickDrawableListener.onClickDrawable(suffixDrawable, DrawablePosition.SUFFIX);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Drawable getPrefixDrawable() {
		return prefixTextDrawable != null ? prefixTextDrawable : prefixIconDrawable;
	}

	@Override
	public void setPrefixIcon(Iconify.IconValue prefixIcon) {
		prefixIconDrawable = getIconDrawable(prefixIcon, prefixColors);
		updateCompoundDrawables();
	}

	@Override
	public void setPrefixText(String prefix) {
		prefixTextDrawable = getTextDrawable(prefix, prefixColors);
		updateCompoundDrawables();
	}

	@Override
	public void setPrefixTextRes(int prefix) {
		setPrefixText(textView.getContext().getString(prefix));
	}

	@Override
	public void setPrefixColors(ColorStateList prefixColors) {
		this.prefixColors = prefixColors;
		updateTextColor();
	}

	@Override
	public void setPrefixColors(int prefixColorsRes) {
		setPrefixColors(textView.getContext().getResources().getColorStateList(prefixColorsRes));
	}

	@Override
	public void setPrefixColor(int prefixColor) {
		setPrefixColors(ColorStateList.valueOf(prefixColor));
	}

	@Override
	public void setPrefixColorRes(int prefixColorRes) {
		setPrefixColor(textView.getContext().getResources().getColor(prefixColorRes));
	}

	@Override
	public Drawable getSuffixDrawable() {
		return suffixTextDrawable != null ? suffixTextDrawable : suffixIconDrawable;
	}

	@Override
	public void setSuffixIcon(Iconify.IconValue suffixIcon) {
		suffixIconDrawable = getIconDrawable(suffixIcon, suffixColors);
		updateCompoundDrawables();
	}

	@Override
	public void setSuffixText(String suffix) {
		suffixTextDrawable = getTextDrawable(suffix, suffixColors);
		updateCompoundDrawables();
	}

	@Override
	public void setSuffixTextRes(int suffixRes) {
		setSuffixText(textView.getContext().getString(suffixRes));
	}

	@Override
	public void setSuffixColors(ColorStateList suffixColors) {
		this.suffixColors = suffixColors;
		updateTextColor();
	}

	@Override
	public void setSuffixColors(int suffixColorsRes) {
		setSuffixColors(textView.getContext().getResources().getColorStateList(suffixColorsRes));
	}

	@Override
	public void setSuffixColor(int suffixColor) {
		setSuffixColors(ColorStateList.valueOf(suffixColor));
	}

	@Override
	public void setSuffixColorRes(int suffixColorRes) {
		setSuffixColor(textView.getContext().getResources().getColor(suffixColorRes));
	}

	@Override
	public void setOnClickDrawableListener(OnClickDrawableListener onClickDrawableListener) {
		this.onClickDrawableListener = onClickDrawableListener;

		if (onClickDrawableListener != null) {
			textView.setOnTouchListener(this);
		} else {
			textView.setOnTouchListener(null);
		}
	}

	protected IconDrawable getIconDrawable(Iconify.IconValue iconValue, ColorStateList colors) {
		if (textView.isInEditMode()) return null;
		if (iconValue == null) return null;

		return new IconDrawable(textView.getContext(), iconValue) //
				.sizePx((int) textView.getTextSize()) //
				.color(getCurrentDrawablColor(colors));
	}

	protected TextDrawable getTextDrawable(String value, ColorStateList colors) {
		if (value == null || value.isEmpty()) return null;

		return new TextDrawable(textView.getContext(), value) //
				.sizePx(textView.getTextSize()).typeface(textView.getTypeface()) //
				.color(getCurrentDrawablColor(colors));
	}

	protected int getCurrentDrawablColor(ColorStateList colors) {
		return colors.getColorForState(textView.getDrawableState(), textView.getCurrentTextColor());
	}

	protected void updateCompoundDrawables() {
		Drawable leftDrawable = prefixIconDrawable != null ? prefixIconDrawable : prefixTextDrawable;
		Drawable rightDrawable = suffixIconDrawable != null ? suffixIconDrawable : suffixTextDrawable;
		textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null);
	}

	protected void updateTextSize() {
		float textSize = textView.getTextSize();
		if (prefixIconDrawable != null) prefixIconDrawable.sizePx((int) textSize);
		if (prefixTextDrawable != null) prefixTextDrawable.sizePx((int) textSize);
		if (suffixIconDrawable != null) suffixIconDrawable.sizePx((int) textSize);
		if (suffixTextDrawable != null) suffixTextDrawable.sizePx((int) textSize);
	}

	protected void updateTextColor() {
		int prefixColor = getCurrentDrawablColor(prefixColors);
		if (prefixIconDrawable != null) prefixIconDrawable.color(prefixColor);
		if (prefixTextDrawable != null) prefixTextDrawable.color(prefixColor);

		int suffixColor = getCurrentDrawablColor(suffixColors);
		if (suffixIconDrawable != null) suffixIconDrawable.color(suffixColor);
		if (suffixTextDrawable != null) suffixTextDrawable.color(suffixColor);
	}

	protected boolean touchesDrawable(MotionEvent motionEvent, Drawable drawable, DrawablePosition drawablePosition) {
		if (drawable != null) {
			final int x = (int) motionEvent.getX();
			final int y = (int) motionEvent.getY();
			final Rect bounds = drawable.getBounds();

			if (y >= (textView.getHeight() - bounds.height()) / 2 - DRAWABLE_CLICK_PADDING && y <= (textView.getHeight() + bounds.height()) / 2 + DRAWABLE_CLICK_PADDING) {
				if (drawablePosition == DrawablePosition.PREFIX && x <= bounds.width() + DRAWABLE_CLICK_PADDING) {
					return true;
				} else if (drawablePosition == DrawablePosition.SUFFIX && x >= textView.getWidth() - bounds.width() - DRAWABLE_CLICK_PADDING) {
					return true;
				}
			}
		}
		return false;
	}
}
