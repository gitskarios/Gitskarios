package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.CreateMilestoneClient;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.users.UsersAdapterSpinner;
import com.alorma.github.ui.adapter.users.UsersHolder;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewIssueActivity extends BackActivity /*implements BaseClient.OnResultCallback<Issue>*/ {

    public static final String REPO_INFO = "REPO_INFO";

    private boolean creatingIssue = false;
    private RepoInfo repoInfo;
    private EditText editTitle;
    private EditText editBody;
    private User issueAssignee;
    private TextView userTextView;
    private TextView milestoneTextView;
    private TextView labelsTextView;

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
        } else {
            finish();
        }
    }

    public Drawable actionBarIcon(IIcon icon) {
        return new IconicsDrawable(this, icon).actionBar().color(Color.WHITE);
    }

    private void openAssignee() {
        GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getApplicationContext(), repoInfo);
        contributorsClient.setOnResultCallback(new ContributorsCallback());
        contributorsClient.execute();
    }

    private void openLabels() {

    }

    private void findViews() {
        editTitle = (EditText) findViewById(R.id.editTitle);
        editBody = (EditText) findViewById(R.id.editBody);

        if (repoInfo.permissions != null && !repoInfo.permissions.push) {
            findViewById(R.id.pushAccessLayout).setVisibility(View.GONE);
        } else {
            userTextView = (TextView) findViewById(R.id.assignee);
            milestoneTextView = (TextView) findViewById(R.id.milestone);
            labelsTextView = (TextView) findViewById(R.id.labels);

            userTextView.setCompoundDrawables(pushAccessInfoIcon(Octicons.Icon.oct_person), null, null, null);
            milestoneTextView.setCompoundDrawables(pushAccessInfoIcon(Octicons.Icon.oct_tag), null, null, null);
            labelsTextView.setCompoundDrawables(pushAccessInfoIcon(Octicons.Icon.oct_milestone), null, null, null);
        }
    }

    public Drawable pushAccessInfoIcon(IIcon icon) {
        return new IconicsDrawable(this, icon).actionBar().colorRes(R.color.primary);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_issue, menu);

        MenuItem item = menu.findItem(R.id.action_send);
        if (item != null) {
            IconicsDrawable githubIconDrawable = new IconicsDrawable(this, Octicons.Icon.oct_plus);
            githubIconDrawable.actionBar();
            githubIconDrawable.colorRes(R.color.white);
            item.setIcon(githubIconDrawable);
        }
        return true;
    }

/*
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
            issueInfo.num = issue.number;
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

    */

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {
            if (contributors != null) {
                final List<User> users = new ArrayList<>(contributors.size());
                for (Contributor contributor : contributors) {
                    users.add(contributor.author);
                }
                UsersAdapterSpinner assigneesAdapter = new UsersAdapterSpinner(NewIssueActivity.this, users);

                MaterialDialog.Builder builder = new MaterialDialog.Builder(NewIssueActivity.this);
                builder.adapter(assigneesAdapter, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        User user = users.get(i);
                        setAssigneeUser(user);
                        materialDialog.dismiss();
                    }
                });
                builder.negativeText(R.string.no_assignee);
                builder.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        setAssigneeUser(null);
                    }
                });
                builder.show();
            }
        }

        @Override
        public void onFail(RetrofitError error) {

        }
    }

    private void setAssigneeUser(User user) {
        this.issueAssignee = user;
        if (userTextView != null) {
            if (user != null) {
                userTextView.setText(user.login);
            } else {
                userTextView.setText(null);
            }
        }
    }

    /**
     * Milestone
     */

    private void openMilestone() {
        GetMilestonesClient milestonesClient = new GetMilestonesClient(this, repoInfo);
        milestonesClient.setOnResultCallback(new MilestonesCallback());
        milestonesClient.execute();

        showProgressDialog(R.style.SpotDialog_loading_milestones);
    }

    private class MilestonesCallback implements BaseClient.OnResultCallback<List<Milestone>> {
        @Override
        public void onResponseOk(final List<Milestone> milestones, Response r) {
            hideProgressDialog();
            if (milestones.size() == 0) {
                showCreateMilestone();
            } else {
                String[] itemsMilestones = new String[milestones.size()];

                for (int i = 0; i < milestones.size(); i++) {
                    itemsMilestones[i] = milestones.get(i).title;
                }

                MaterialDialog.Builder builder = new MaterialDialog.Builder(NewIssueActivity.this)
                        .title(R.string.select_milestone)
                        .items(itemsMilestones)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence text) {

                                addMilestone(milestones.get(i));

                                return false;
                            }
                        })
                        .forceStacking(true)
                        .widgetColorRes(R.color.primary)
                        .negativeText(R.string.add_milestone);

                builder.callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        showCreateMilestone();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        clearMilestone();
                    }
                });

                builder.show();
            }
        }

        @Override
        public void onFail(RetrofitError error) {

        }
    }

    private void showCreateMilestone() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.add_milestone);
        builder.content(R.string.add_milestone_description);
        builder.input(R.string.add_milestone_hint, 0, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence milestoneName) {
                createMilestone(milestoneName.toString());
            }
        })
                .negativeText(R.string.cancel);

        builder.show();
    }

    private void createMilestone(String milestoneName) {
        CreateMilestoneRequestDTO createMilestoneRequestDTO = new CreateMilestoneRequestDTO(milestoneName);

        CreateMilestoneClient createMilestoneClient = new CreateMilestoneClient(this, repoInfo, createMilestoneRequestDTO);
        createMilestoneClient.setOnResultCallback(new BaseClient.OnResultCallback<Milestone>() {
            @Override
            public void onResponseOk(Milestone milestone, Response r) {
                addMilestone(milestone);
            }

            @Override
            public void onFail(RetrofitError error) {
                hideProgressDialog();
            }
        });
        createMilestoneClient.execute();
    }

    private void addMilestone(Milestone milestone) {
        milestoneTextView.setText(milestone.title);
    }

    private void clearMilestone() {
        milestoneTextView.setText(null);
    }
}