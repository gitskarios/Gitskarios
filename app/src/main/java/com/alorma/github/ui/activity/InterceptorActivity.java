package com.alorma.github.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.alorma.github.sdk.services.repo.GetRepoClient;

import java.util.List;

/**
 * Created by Bernat on 31/07/2014.
 */
public class InterceptorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = getIntent().getData();

            if (uri != null) {
                List<String> pathSegments = uri.getPathSegments();
                switch (pathSegments.size()) {
                    case 2:
                        String owner = pathSegments.get(0);
                        String repo = pathSegments.get(1);

                        Intent repoIntent = RepoDetailActivity.createIntentFilterLauncherActivity(this, owner, repo, null);
                        startActivity(repoIntent);
                        finish();
                        break;
                    case 1:
                        String user = pathSegments.get(0);
                        Intent userIntent = ProfileActivity.createIntentFilterLauncherActivity(this, user);
                        startActivity(userIntent);
                        finish();
                        break;
                }
            }
        }
    }
}
