package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.PostNewIssueClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.repo.RepoDetailFragment;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewIssueActivity extends BackActivity implements BaseClient.OnResultCallback<Issue> {

    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";
    private String owner;
    private String repo;

    public static Intent createLauncherIntent(Context context, String owner, String repo) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);
        Intent intent =  new Intent(context, NewIssueActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);

        if (getIntent().getExtras() != null) {
            owner = getIntent().getExtras().getString(RepoDetailFragment.OWNER);
            repo = getIntent().getExtras().getString(RepoDetailFragment.REPO);

            IssueRequest issue = new IssueRequest();
            issue.title = "Test issue from android Gitsarios app";
            issue.body = "This is the body of issue";
            issue.assignee = "alorma";
            issue.milestone = 1;
            issue.labels = new String[]{"TEST", "ANDROID", "CHECK"};

            PostNewIssueClient postNewIssueClient = new PostNewIssueClient(this, owner, repo, issue);
            postNewIssueClient.setOnResultCallback(this);
            postNewIssueClient.execute();
        } else {
            finish();
        }
    }

    @Override
    public void onResponseOk(Issue issue, Response r) {
        if (issue != null) {
            Log.i("ALORMA", "Issue: " + issue);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onRetrofitError(this, "Creating issue", error);
    }
}
