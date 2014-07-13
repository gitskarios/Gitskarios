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

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import com.joanzapata.android.iconify.Iconify;

public interface EnhancedText {

	Drawable getPrefixDrawable();

	void setPrefixIcon(Iconify.IconValue prefixIcon);

	void setPrefixText(String prefix);

	void setPrefixTextRes(int prefix);

	void setPrefixColors(ColorStateList prefixColors);

	void setPrefixColors(int prefixColorsRes);

	void setPrefixColor(int prefixColor);

	void setPrefixColorRes(int prefixColorRes);

	Drawable getSuffixDrawable();

	void setSuffixIcon(Iconify.IconValue suffixIcon);

	void setSuffixText(String suffix);

	void setSuffixTextRes(int suffixRes);

	void setSuffixColors(ColorStateList suffixColors);

	void setSuffixColors(int suffixColorsRes);

	void setSuffixColor(int suffixColor);

	void setSuffixColorRes(int suffixColorRes);

	void setOnClickDrawableListener(OnClickDrawableListener onClickDrawableListener);

}
