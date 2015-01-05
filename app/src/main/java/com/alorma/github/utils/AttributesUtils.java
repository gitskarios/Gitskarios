package com.alorma.github.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StyleRes;

import com.alorma.github.R;

/**
 * Created by Bernat on 06/01/2015.
 */
public class AttributesUtils {

	public static int getSecondaryTextColor(Context context, @StyleRes int style) {
		return context.getResources().getColor(getAttributeId(context, style, R.attr.gitskarios_secondary_text));
	}

	public static int getIconsColor(Context context, @StyleRes int style) {
		return context.getResources().getColor(getAttributeId(context, style, R.attr.gitskarios_icons));
	}

	public static int getPrimaryLightColor(Context context, @StyleRes int style) {
		return context.getResources().getColor(getAttributeId(context, style, R.attr.gitskarios_primary_light));
	}

	public static int getPrimaryDarkColor(Context context, @StyleRes int style) {
		return context.getResources().getColor(getAttributeId(context, style, R.attr.colorPrimaryDark));
	}

	public static int getPrimaryColor(Context context, @StyleRes int style) {
		return context.getResources().getColor(getAttributeId(context, style, R.attr.colorPrimary));
	}

	public static int getAccentColor(Context context, @StyleRes int style) {
		return context.getResources().getColor(getAttributeId(context, style, R.attr.colorAccent));
	}

	public static int getAttributeId(Context context, int style, int attr) {
		TypedArray a = context.getTheme().obtainStyledAttributes(style, new int[]{attr});
		return a.getResourceId(0, 0);
	}

}
