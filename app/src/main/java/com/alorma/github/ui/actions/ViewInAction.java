package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ViewInAction extends Action<Void> {

  private final Context context;
  private final String url;

  public ViewInAction(Context context, String url) {
    this.context = context;
    this.url = url;
  }

  @Override
  public Action<Void> execute() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(url));
    context.startActivity(intent);
    return this;
  }
}
