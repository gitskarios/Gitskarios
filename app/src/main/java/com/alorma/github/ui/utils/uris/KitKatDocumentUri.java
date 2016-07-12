package com.alorma.github.ui.utils.uris;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class KitKatDocumentUri extends UriType {

  public static final String DOCUMENT_TYPE_PRIMARY = "primary";
  public static final String DOCUMENT_TYPE_IMAGE = "image";
  public static final String DOCUMENT_TYPE_VIDEO = "video";
  public static final String DOCUMENT_TYPE_AUDIO = "audio";

  @Override
  public String getPath(Context context, Uri uri) {
    if (isExternalStorageDocument(uri)) {
      final String image = getFromExternalStorage(uri);
      if (image != null) return image;
    } else if (isDownloadsDocument(uri)) {
      return getFromDownloads(context, uri);
    } else if (isMediaDocument(uri)) {
      return getFromMediaDocument(context, uri);
    }
    return String.valueOf(uri);
  }

  private String getFromMediaDocument(Context context, Uri uri) {
    final String docId = DocumentsContract.getDocumentId(uri);
    final String[] split = docId.split(":");
    final String type = split[0];

    Uri contentUri = null;
    if (DOCUMENT_TYPE_IMAGE.equals(type)) {
      contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    } else if (DOCUMENT_TYPE_VIDEO.equals(type)) {
      contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    } else if (DOCUMENT_TYPE_AUDIO.equals(type)) {
      contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    final String selection = "_id=?";
    final String[] selectionArgs = new String[] {
        split[1]
    };

    return getDataColumn(context, contentUri, selection, selectionArgs);
  }

  private String getFromDownloads(Context context, Uri uri) {
    final String id = DocumentsContract.getDocumentId(uri);
    final Uri contentUri =
        ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
            Long.valueOf(id));

    return getDataColumn(context, contentUri, null, null);
  }

  @Nullable
  private String getFromExternalStorage(Uri uri) {
    final String docId = DocumentsContract.getDocumentId(uri);
    final String[] split = docId.split(":");
    final String type = split[0];

    if (DOCUMENT_TYPE_PRIMARY.equalsIgnoreCase(type)) {
      return Environment.getExternalStorageDirectory() + "/" + split[1];
    }
    return null;
  }

  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }
}
