package com.alorma.github.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.user.RequestUserClient;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 31/07/2014.
 */
public class InterceptorActivity extends Activity implements BaseClient.OnResultCallback<User> {

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            uri = getIntent().getData();

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
                        executeUserSearch(user);
                        break;
                }
            }
        }
    }

    private void executeUserSearch(String user) {
        RequestUserClient requestUserClient = new RequestUserClient(this, user);
        requestUserClient.setOnResultCallback(this);
        requestUserClient.execute();
    }

    @Override
    public void onResponseOk(User user, Response r) {
        Intent profile = ProfileActivity.createIntentFilterLauncherActivity(this, user);
        startActivity(profile);
    }

    @Override
    public void onFail(RetrofitError error) {
        onFail();
    }

    private void onFail() {

    }
}
