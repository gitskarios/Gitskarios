package com.alorma.github.ui.utils.uris;

import android.content.Context;
import android.net.Uri;

public class FileUriType extends UriType {
  @Override
  public String getPath(Context context, Uri uri) {
    return uri.getPath();
  }
}
