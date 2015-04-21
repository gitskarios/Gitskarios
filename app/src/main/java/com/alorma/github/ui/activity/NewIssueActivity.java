package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.issues.PostNewIssueClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.users.UsersAdapterSpinner;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewIssueActivity extends BackActivity implements BaseClient.OnResultCallback<Issue> {

    public static final String REPO_INFO = "REPO_INFO";


    private View pushAccesLayout;
    private Spinner spinnerAssignee;
    private UsersAdapterSpinner assigneesAdapter;
    private MaterialEditText editLabels;
    private MaterialEditText editTitle;
    private MaterialEditText editBody;
    private boolean creatingIssue = false;
    private RepoInfo repoInfo;

    public static Intent createLauncherIntent(Context context, RepoInfo info) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, info);
        Intent intent = new Intent(context, NewIssueActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public int getToolbarId() {
        return super.getToolbarId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);

        if (getIntent().getExtras() != null) {
            repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);

            findViews();

            if (repoInfo.permissions != null && !repoInfo.permissions.push) {
                pushAccesLayout.setVisibility(View.INVISIBLE);
            } else {
                findViewsAcces();
                GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getApplicationContext(), repoInfo);
                contributorsClient.setOnResultCallback(new ContributorsCallback());
                contributorsClient.execute();
            }
        } else {
            finish();
        }
    }

    private void findViews() {
        pushAccesLayout = findViewById(R.id.pushAccessLayout);
        editTitle = (MaterialEditText) findViewById(R.id.editTitle);
        editBody = (MaterialEditText) findViewById(R.id.editBody);
    }

    private void findViewsAcces() {
        spinnerAssignee = (Spinner) findViewById(R.id.spinnerAssignee);

        List<User> users = new ArrayList<User>(1);
        User cleanUser = new User();
        cleanUser.login = "No assignee";
        users.add(cleanUser);
        assigneesAdapter = new UsersAdapterSpinner(NewIssueActivity.this, users);
        spinnerAssignee.setAdapter(assigneesAdapter);

        editLabels = (MaterialEditText) findViewById(R.id.editLabels);
    }

    private void checkDataAndCreateIssue() {
        if (editTitle.length() <= 0) {
            editTitle.setError(getString(R.string.issue_title_mandatory));
            return;
        }

        IssueRequest issue = new IssueRequest();
        issue.title = editTitle.getText().toString();
        issue.body = editBody.getText().toString();

        if (repoInfo.permissions.push) {
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
        }

        creatingIssue = true;

        invalidateOptionsMenu();

        createIssue(issue);

        showProgressDialog(R.style.SpotDialog_CreatingIssue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_issue, menu);

        MenuItem item = menu.findItem(R.id.action_send);
        if (item != null) {
            IconicsDrawable githubIconDrawable = new IconicsDrawable(this, Octicons.Icon.oct_plus);
            githubIconDrawable.actionBarSize();
            githubIconDrawable.colorRes(R.color.white);
            item.setIcon(githubIconDrawable);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_send);
        if (item != null) {
            item.setEnabled(!creatingIssue);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_send:
                checkDataAndCreateIssue();
                break;
        }
        return true;
    }

    private void createIssue(IssueRequest issue) {
        PostNewIssueClient postNewIssueClient = new PostNewIssueClient(this, repoInfo, issue);
        postNewIssueClient.setOnResultCallback(this);
        postNewIssueClient.execute();
    }

    @Override
    public void onResponseOk(Issue issue, Response r) {
        hideProgressDialog();
        if (issue != null) {
            IssueInfo issueInfo = new IssueInfo();
            issueInfo.repo = repoInfo;
            Intent launcherIntent = IssueDetailActivity.createLauncherIntent(this, issueInfo);
            startActivity(launcherIntent);
            finish();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        hideProgressDialog();
        creatingIssue = false;
        ErrorHandler.onRetrofitError(this, "Creating issue", error);
        invalidateOptionsMenu();
        Toast.makeText(this, R.string.create_issue_error, Toast.LENGTH_SHORT).show();
    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {
            if (contributors != null) {
                List<User> users = new ArrayList<User>(contributors.size());
                User cleanUser = new User();
                cleanUser.login = "No assignee";
                users.add(cleanUser);
                for (Contributor contributor : contributors) {
                    users.add(contributor.author);
                }
                assigneesAdapter = new UsersAdapterSpinner(NewIssueActivity.this, users);
                spinnerAssignee.setAdapter(assigneesAdapter);
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            List<User> users = new ArrayList<User>(1);
            User cleanUser = new User();
            cleanUser.login = "No assignee";
            users.add(cleanUser);
            assigneesAdapter = new UsersAdapterSpinner(NewIssueActivity.this, users);
            spinnerAssignee.setAdapter(assigneesAdapter);
        }
    }
}