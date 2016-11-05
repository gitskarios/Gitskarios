package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.alorma.github.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

public class ShareAction extends Action<Void> {

  private final Context context;
  private final String title;
  private final String url;
  private String contentType;

  public ShareAction(Context context, String title, String url) {
    this.context = context;
    this.title = title;
    this.url = url;
  }

  public ShareAction setType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  @Override
  public Action<Void> execute() {

    if (Fabric.isInitialized()) {
      ShareEvent shareEvent = new ShareEvent();
      shareEvent.putMethod("default");
      if (contentType != null) {
        shareEvent.putContentType(contentType);
      }
      Answers.getInstance().logShare(shareEvent);
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
  private Intent getIntent() {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Intent.EXTRA_SUBJECT, title);
    intent.putExtra(Intent.EXTRA_TEXT, url);
    return intent;
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
}
