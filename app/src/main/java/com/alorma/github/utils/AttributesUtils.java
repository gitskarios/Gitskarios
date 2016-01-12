package com.alorma.github.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;

import com.alorma.github.R;

/**
 * Created by Bernat on 06/01/2015.
 */
public class AttributesUtils {

    public static int getSecondaryTextColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.gitskarios_secondary_text));
    }

    public static int getIconsColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.gitskarios_icons));
    }

    public static int getTitleColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.gitskarios_title));
    }

    public static int getWebviewColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.gitskarios_webview));
    }

    public static int getPrimaryLightColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.gitskarios_primary_light));
    }

    public static int getPrimaryDarkColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.colorPrimaryDark));
    }

    public static int getPrimaryColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.colorPrimary));
    }

    public static int getAccentColor(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.colorAccent));
    }

    public static int getControlNormal(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.colorControlNormal));
    }

    public static int getControlHighlight(Context context) {
        return ContextCompat.getColor(context, getAttributeId(context, R.attr.colorControlHighlight));
    }

    public static int getAttributeId(Context context, int attr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(R.style.AppTheme, new int[]{attr});
        return a.getResourceId(0, 0);
    }
}
