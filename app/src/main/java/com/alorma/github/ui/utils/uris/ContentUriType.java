package com.alorma.github.ui.utils.uris;

import android.content.Context;
import android.net.Uri;

public class ContentUriType extends UriType {
  @Override
  public String getPath(Context context, Uri uri) {
    if (isGooglePhotosUri(uri)) {
      return uri.getLastPathSegment();
    }
    return getDataColumn(context, uri, null, null);
  }

  private boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }
}
