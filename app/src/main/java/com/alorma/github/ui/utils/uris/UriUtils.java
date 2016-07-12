package com.alorma.github.ui.utils.uris;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

public class UriUtils {

  /**
   * Get a file path from a Uri.
   *
   * @see <a href="http://stackoverflow.com/questions/19834842/android-gallery-on-kitkat-returns-different-uri-for-intent-action-get-content">stackoverflow.com</a>
   */
  @TargetApi(Build.VERSION_CODES.KITKAT)
  public static String getPath(Context context, Uri uri) throws NullPointerException{
    UriType uriType = new DefaultUriType();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
        context, uri)) {
      uriType = new KitKatDocumentUri();
    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
      uriType = new ContentUriType();
    } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
      uriType = new FileUriType();
    }

    return uriType.getPath(context, uri);
  }
}
