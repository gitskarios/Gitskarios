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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.joanzapata.android.iconify.Iconify;
import fr.dvilleneuve.android.EnhancedText;
import fr.dvilleneuve.android.OnClickDrawableListener;
import fr.dvilleneuve.android.R;

public class EnhancedTextView extends TextView implements EnhancedText {

	private EnhancedTextDelegate enhancedTextDelegate;

	public EnhancedTextView(Context context) {
		super(context);
        isInEditMode();
		enhancedTextDelegate = new EnhancedTextDelegate(this);
		enhancedTextDelegate.init();
	}

	public EnhancedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
        isInEditMode();
		enhancedTextDelegate = new EnhancedTextDelegate(this);
		enhancedTextDelegate.initAttrs(context, attrs, R.style.enhancedEditText);
		enhancedTextDelegate.init();
	}

	public EnhancedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        isInEditMode();
		enhancedTextDelegate = new EnhancedTextDelegate(this);
		enhancedTextDelegate.initAttrs(context, attrs, defStyle);
		enhancedTextDelegate.init();
	}

	@Override
	public void setTextSize(int unit, float size) {
		super.setTextSize(unit, size);
		enhancedTextDelegate.updateTextSize();
	}

	@Override
	public void drawableStateChanged() {
		super.drawableStateChanged();
		enhancedTextDelegate.updateTextColor();
	}

	@Override
	public Drawable getPrefixDrawable() {
		return enhancedTextDelegate.getPrefixDrawable();
	}

	@Override
	public void setPrefixIcon(Iconify.IconValue prefixIcon) {
		enhancedTextDelegate.setPrefixIcon(prefixIcon);
	}

	@Override
	public void setPrefixText(String prefix) {
		enhancedTextDelegate.setPrefixText(prefix);
	}

	@Override
	public void setPrefixTextRes(int prefix) {
		enhancedTextDelegate.setPrefixTextRes(prefix);
	}

	@Override
	public void setPrefixColors(ColorStateList prefixColors) {
		enhancedTextDelegate.setPrefixColors(prefixColors);
	}

	@Override
	public void setPrefixColors(int prefixColorsRes) {
		enhancedTextDelegate.setPrefixColors(prefixColorsRes);
	}

	@Override
	public void setPrefixColor(int prefixColor) {
		enhancedTextDelegate.setPrefixColor(prefixColor);
	}

	@Override
	public void setPrefixColorRes(int prefixColorRes) {
		enhancedTextDelegate.setPrefixColorRes(prefixColorRes);
	}

	@Override
	public Drawable getSuffixDrawable() {
		return enhancedTextDelegate.getSuffixDrawable();
	}

	@Override
	public void setSuffixIcon(Iconify.IconValue suffixIcon) {
		enhancedTextDelegate.setSuffixIcon(suffixIcon);
	}

	@Override
	public void setSuffixText(String suffix) {
		enhancedTextDelegate.setSuffixText(suffix);
	}

	@Override
	public void setSuffixTextRes(int suffixRes) {
		enhancedTextDelegate.setSuffixTextRes(suffixRes);
	}

	@Override
	public void setSuffixColors(ColorStateList suffixColors) {
		enhancedTextDelegate.setSuffixColors(suffixColors);
	}

	@Override
	public void setSuffixColors(int suffixColorsRes) {
		enhancedTextDelegate.setSuffixColors(suffixColorsRes);
	}

	@Override
	public void setSuffixColor(int suffixColor) {
		enhancedTextDelegate.setSuffixColor(suffixColor);
	}

	@Override
	public void setSuffixColorRes(int suffixColorRes) {
		enhancedTextDelegate.setSuffixColorRes(suffixColorRes);
	}

	@Override
	public void setOnClickDrawableListener(OnClickDrawableListener onClickDrawableListener) {
		enhancedTextDelegate.setOnClickDrawableListener(onClickDrawableListener);
	}

}
