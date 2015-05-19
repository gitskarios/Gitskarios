package com.alorma.github;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.alorma.github.ui.view.UrlsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 31/07/2014.
 */
public class Interceptor extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();

            if (uri != null) {
                Intent intent = new UrlsManager(this).checkUri(uri);
                if (intent != null) {
                    startActivity(intent);
                    finish();
                } else {
                    onFail();
                }
            } else {
                onFail();
            }
        }
    }

    private void onFail() {
        Intent intent = new Intent(getIntent().getAction());
        intent.setData(getIntent().getData());
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (!resolveInfos.isEmpty()) {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();

            for (ResolveInfo resolveInfo : resolveInfos) {
                String packageName = resolveInfo.activityInfo.packageName;
                if (!packageName.equals(getPackageName())) {
                    Intent targetedShareIntent = new Intent(getIntent().getAction());
                    targetedShareIntent.setData(getIntent().getData());
                    targetedShareIntent.setPackage(packageName);
                    targetedShareIntents.add(targetedShareIntent);
                }
            }

            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Open with...");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));

            startActivity(chooserIntent);
        }
    }
}
