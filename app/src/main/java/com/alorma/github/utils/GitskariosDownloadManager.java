package com.alorma.github.utils;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;
import com.alorma.github.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class GitskariosDownloadManager {

  public void download(final Context context, final String path, final String name,
      @Nullable final View view) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED) {
      downloadFromAndroid(context, path, name);
    } else {
      PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
          downloadFromAndroid(context, path, name);
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
          if (response.isPermanentlyDenied() && view != null) {
            Snackbar snackbar =
                Snackbar.make(view, context.getString(R.string.external_storage_permission_request),
                    Snackbar.LENGTH_LONG);

            snackbar.setAction(
                context.getString(R.string.external_storage_permission_request_action),
                new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    openSettings(context);
                  }
                });

            snackbar.show();
          }
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
            PermissionToken token) {
          token.continuePermissionRequest();
        }
      };
      if (!Dexter.isRequestOngoing()) {
        Dexter.checkPermission(permissionListener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
      }
    }
  }

  private void downloadFromAndroid(Context context, String path, String name) {
    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));
    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

    request.setTitle(name);
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
        "gitskarios/" + name);
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    request.allowScanningByMediaScanner();
    dm.enqueue(request);

    Toast.makeText(context, name + " queued to download at gitskarios/", Toast.LENGTH_SHORT).show();
  }

  private void openSettings(Context context) {
    Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:" + context.getPackageName()));
    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
    myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(myAppSettings);
  }
}
