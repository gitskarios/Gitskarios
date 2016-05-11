package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alorma.github.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;

import io.fabric.sdk.android.Fabric;

public class ShareRawAction extends Action<Void> {

    private final Context context;
    private final String title;
    private final String url;
    private String contentType;
    private String content;

    public ShareRawAction(Context context, String title, String url, String content) {
        this.context = context;
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public ShareRawAction setType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public Action<Void> execute() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        context.startActivity(Intent.createChooser(intent,
                context.getString(R.string.send_file_to) + title));

        if (Fabric.isInitialized()) {
            ShareEvent shareEvent = new ShareEvent();
            shareEvent.putMethod("raw");
            if (contentType != null) {
                shareEvent.putContentType(contentType);
            }
            Answers.getInstance().logShare(shareEvent);
        }

        Intent chooser =
                Intent.createChooser(intent, context.getResources().getString(R.string.share_intent_title));
        context.startActivity(chooser);
        return this;
    }
}
