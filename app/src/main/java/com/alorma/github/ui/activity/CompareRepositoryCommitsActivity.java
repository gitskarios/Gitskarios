package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.CompareCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.CompareCommitsClient;
import com.alorma.github.ui.activity.base.BackActivity;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by a557114 on 31/07/2015.
 */
public class CompareRepositoryCommitsActivity extends BackActivity implements BaseClient.OnResultCallback<CompareCommit> {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String BASE = "BASE";
    private static final String HEAD = "HEAD";

    public static Intent launcherIntent(Context context, RepoInfo repoInfo, String base, String head) {
        Intent intent = new Intent(context, CompareRepositoryCommitsActivity.class);

        intent.putExtra(REPO_INFO, repoInfo);
        intent.putExtra(BASE, base);
        intent.putExtra(HEAD, head);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_commits);

        if (getIntent().getExtras() != null) {
            RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
            String base = getIntent().getExtras().getString(BASE);
            String head = getIntent().getExtras().getString(HEAD);

            CompareCommitsClient compareCommitsClient = new CompareCommitsClient(this, repoInfo, base, head);
            compareCommitsClient.setOnResultCallback(this);
            compareCommitsClient.execute();
        } else {
            finish();
        }
    }

    @Override
    public void onResponseOk(CompareCommit compareCommit, Response r) {

    }

    @Override
    public void onFail(RetrofitError error) {

    }
}
