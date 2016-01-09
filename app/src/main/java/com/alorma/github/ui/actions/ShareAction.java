package com.alorma.github.ui.actions;

import android.content.Context;
import android.content.Intent;

import com.alorma.github.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 12/10/2015.
 */
public class ShareAction extends Action<Void> {

    private final Context context;
    private final String title;
    private final String url;

    public ShareAction(Context context, String title, String url) {
        this.context = context;
        this.title = title;
        this.url = url;
    }

    @Override
    public Action<Void> execute() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, url);

        if (Fabric.isInitialized()) {
            Answers.getInstance().logShare(new ShareEvent());
        }

        Intent chooser = Intent.createChooser(intent, context.getResources().getString(R.string.share_intent_title));
        context.startActivity(chooser);
        return this;
    }
}
