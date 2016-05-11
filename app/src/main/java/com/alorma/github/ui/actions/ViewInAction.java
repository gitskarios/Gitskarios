package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import io.fabric.sdk.android.Fabric;

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


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(Uri.parse(url));

        context.startActivity(Intent.createChooser(intent, ""));
        return this;
    }
}
