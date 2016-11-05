package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class ViewInAction extends Action<Void> {

  private final Context context;
  private final String url;
  private String contentType;

  public ViewInAction(Context context, String url) {
    this.context = context;
    this.url = url;
  }

  public ViewInAction setType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  @Override
  public Action<Void> execute() {
    if (Fabric.isInitialized()) {
      ContentViewEvent contentViewEvent = new ContentViewEvent();
      if (contentType != null) {
        contentViewEvent.putContentType(contentType);
      }
      Answers.getInstance().logContentView(contentViewEvent);
    }

    Intent intent = getIntent();

    List<Intent> targetIntents = getIntentsExcludeGitskarios(intent);
    if (targetIntents.size() == 1) {
      context.startActivity(targetIntents.remove(0));
    } else if (targetIntents.size() > 1) {
      Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "");
      chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[] {}));
      context.startActivity(chooserIntent);
    } else {
      Toast.makeText(context, "No app found", Toast.LENGTH_SHORT).show();
    }
    return this;
  }

  @NonNull
  private List<Intent> getIntentsExcludeGitskarios(Intent intent) {
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
    List<Intent> targetIntents = new ArrayList<>();
    for (ResolveInfo currentInfo : activities) {
      String packageName = currentInfo.activityInfo.packageName;
      if (!packageName.contains("com.alorma.github")) {
        Intent targetIntent = getIntent();
        targetIntent.setPackage(packageName);
        targetIntents.add(targetIntent);
      }
    }
    return targetIntents;
  }

  @NonNull
  private Intent getIntent() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.setData(Uri.parse(url));
    return intent;
  }
}
