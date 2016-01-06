package com.alorma.github;

import android.net.Uri;

/**
 * Created by Bernat on 22/05/2015.
 */
public class UriNotHandledException extends Exception {
  public UriNotHandledException(Uri uri) {
    super(uri.toString());
  }
}
