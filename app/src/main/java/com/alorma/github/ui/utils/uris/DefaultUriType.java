package com.alorma.github.ui.utils.uris;

import android.content.Context;
import android.net.Uri;

public class DefaultUriType extends UriType {
  @Override
  public String getPath(Context context, Uri uri) {
    return String.valueOf(uri);
  }
}
