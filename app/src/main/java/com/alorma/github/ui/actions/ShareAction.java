package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;
import com.alorma.github.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import io.fabric.sdk.android.Fabric;

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
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(Intent.EXTRA_SUBJECT, title);
    intent.putExtra(Intent.EXTRA_TEXT, url);

    if (Fabric.isInitialized()) {
      ShareEvent shareEvent = new ShareEvent();
      shareEvent.putMethod("default");
      if (contentType != null) {
        shareEvent.putContentType(contentType);
      }
      Answers.getInstance().logShare(shareEvent);
    }

    Intent chooser = Intent.createChooser(intent, context.getResources().getString(R.string.share_intent_title));
    context.startActivity(chooser);
    return this;
  }
}
