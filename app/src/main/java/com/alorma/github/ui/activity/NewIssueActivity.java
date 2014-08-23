package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.PostNewIssueClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
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
    private Spinner spinnerAssignee;
    private UsersAdapter assigneesAdapter;
    private EditText editLabels;
    private EditText editTitle;
    private EditText editBody;
    private boolean sending;
    private SmoothProgressBar smoothBar;

    public static Intent createLauncherIntent(Context context, String owner, String repo, boolean pushAcces) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);
        bundle.putBoolean(PUSH, pushAcces);
        Intent intent = new Intent(context, NewIssueActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);

        if (getIntent().getExtras() != null) {
            owner = getIntent().getExtras().getString(OWNER);
            repo = getIntent().getExtras().getString(REPO);

            if (getActionBar() != null) {
                getActionBar().setSubtitle(owner + "/" + repo);
            }

            pushAcces = getIntent().getExtras().getBoolean(PUSH, false);

            findViews();

            if (!pushAcces) {
                pushAccesLayout.setVisibility(View.GONE);
            } else {
                findViewsAcces();

                smoothBar.progressiveStart();

                GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getApplicationContext(), owner, repo);
                contributorsClient.setOnResultCallback(new ContributorsCallback());
                contributorsClient.execute();
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_issue, menu);
        MenuItem itemSend = menu.findItem(R.id.action_send);

        if (itemSend != null) {
            IconDrawable iconDrawable = new IconDrawable(this, Iconify.IconValue.fa_send);
            iconDrawable.color(Color.WHITE);
            iconDrawable.actionBarSize();
            itemSend.setIcon(iconDrawable);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSend = menu.findItem(R.id.action_send);

        if (itemSend != null) {
            itemSend.setEnabled(!sending);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_send) {
            checkDataAndCreateIssue();
        }
        return true;
    }

    private void findViews() {
        pushAccesLayout = findViewById(R.id.pushAccessLayout);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editBody = (EditText) findViewById(R.id.editBody);
        smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);
    }

    private void findViewsAcces() {
        spinnerAssignee = (Spinner) findViewById(R.id.spinnerAssignee);

        List<User> users = new ArrayList<User>(1);
        User cleanUser = new User();
        cleanUser.login = "No assignee";
        users.add(cleanUser);
        assigneesAdapter = new UsersAdapter(NewIssueActivity.this, users);
        spinnerAssignee.setAdapter(assigneesAdapter);

        editLabels = (EditText) findViewById(R.id.editLabels);
    }

    private void checkDataAndCreateIssue() {
        if (editTitle.length() < 0) {
            editTitle.setError(getString(R.string.issue_title_mandatory));
            return;
        }

        IssueRequest issue = new IssueRequest();
        issue.title = editTitle.getText().toString();
        issue.body = editBody.getText().toString();

        if (spinnerAssignee.getSelectedItemPosition() > 0) {
            issue.assignee = assigneesAdapter.getItem(spinnerAssignee.getSelectedItemPosition()).login;
        }

        if (editLabels.length() > 0) {
            String[] labels = editLabels.getText().toString().split(",");

            String[] clearLabels = new String[labels.length];

            for (int i = 0; i < labels.length; i++) {
                clearLabels[i] = labels[i].trim();
            }

            issue.labels = clearLabels;
        }

        sending = true;
        invalidateOptionsMenu();
        smoothBar.progressiveStart();

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
        smoothBar.progressiveStop();
        sending = false;
        invalidateOptionsMenu();
        Toast.makeText(this, R.string.create_issue_error, Toast.LENGTH_SHORT).show();
    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {
            smoothBar.progressiveStop();
            List<User> users = new ArrayList<User>(contributors.size());
            User cleanUser = new User();
            cleanUser.login = "No assignee";
            users.add(cleanUser);
            for (Contributor contributor : contributors) {
                users.add(contributor.author);
            }
            assigneesAdapter = new UsersAdapter(NewIssueActivity.this, users);
            spinnerAssignee.setAdapter(assigneesAdapter);
        }

        @Override
        public void onFail(RetrofitError error) {
            smoothBar.progressiveStop();
            List<User> users = new ArrayList<User>(1);
            User cleanUser = new User();
            cleanUser.login = "No assignee";
            users.add(cleanUser);
            assigneesAdapter = new UsersAdapter(NewIssueActivity.this, users);
            spinnerAssignee.setAdapter(assigneesAdapter);
        }
    }
}