package com.alorma.github.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import com.alorma.github.R;

public class AttributesUtils {

  public static int getSecondaryTextColor(Context context) {
    return getColor(context, R.attr.gitskarios_secondary_text);
  }

  public static int getIconsColor(Context context) {
    return getColor(context, R.attr.gitskarios_icons);
  }

  public static int getTitleColor(Context context) {
    return getColor(context, R.attr.gitskarios_title);
  }

  public static int getWebviewColor(Context context) {
    return getColor(context, R.attr.gitskarios_webview);
  }

  public static int getPrimaryLightColor(Context context) {
    return getColor(context, R.attr.gitskarios_primary_light);
  }

  public static int getPrimaryDarkColor(Context context) {
    return getColor(context, R.attr.colorPrimaryDark);
  }

  public static int getPrimaryColor(Context context) {
    return getColor(context, R.attr.colorPrimary);
  }

  public static int getAccentColor(Context context) {
    return getColor(context, R.attr.colorAccent);
  }

  public static int getControlNormal(Context context) {
    return getColor(context, R.attr.colorControlNormal);
  }

  public static int getControlHighlight(Context context) {
    return getColor(context, R.attr.colorControlHighlight);
  }

  public static int getColor(Context context, int attr) {
    TypedValue typedValue = new TypedValue();
    TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { attr });
    int color = a.getColor(0, 0);
    a.recycle();
    return color;
  }
}
