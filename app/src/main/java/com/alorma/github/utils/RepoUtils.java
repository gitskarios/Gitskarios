package com.alorma.github.utils;

import android.content.Context;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;

public class RepoUtils {

  public static String sortOrder(Context context) {
    GitskariosSettings settings = new GitskariosSettings(context);
    return settings.getRepoSort(context.getString(R.string.sort_pushed_value));
  }
}
