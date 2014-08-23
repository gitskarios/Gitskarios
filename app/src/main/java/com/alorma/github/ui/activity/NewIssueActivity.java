package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    public static final String PUSH = "PUSH";
    private String owner;
    private String repo;
    private boolean pushAcces;
    private View pushAccesLayout;

    public static Intent createLauncherIntent(Context context, String owner, String repo, boolean pushAcces) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);
        bundle.putBoolean(PUSH, pushAcces);
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
            pushAcces = getIntent().getExtras().getBoolean(PUSH, false);

            findViews();

            if (!pushAcces) {
                pushAccesLayout.setVisibility(View.GONE);
            }
        } else {
            finish();
        }
    }

    private void findViews() {
        pushAccesLayout = findViewById(R.id.pushAccessLayout);

    }

    private void checkDataAndCreateIssue() {
        IssueRequest issue = new IssueRequest();
        issue.title = "Test issue from android Gitsarios app";
        issue.body = "This is the body of issue";
        issue.assignee = "alorma";
        //issue.milestone = 1;
        issue.labels = new String[]{"TEST", "ANDROID", "CHECK"};

        createIssue(issue);
    }

    private void createIssue(IssueRequest issue) {
        PostNewIssueClient postNewIssueClient = new PostNewIssueClient(this, owner, repo, issue);
        postNewIssueClient.setOnResultCallback(this);
        postNewIssueClient.execute();
    }

    @Override
    public void onResponseOk(Issue issue, Response r) {
        if (issue != null) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onRetrofitError(this, "Creating issue", error);
    }
}
