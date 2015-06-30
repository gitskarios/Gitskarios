package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.CreateMilestoneClient;
import com.alorma.github.sdk.services.issues.GetMilestonesClient;
import com.alorma.github.sdk.services.issues.GithubIssueLabelsClient;
import com.alorma.github.sdk.services.issues.PostNewIssueClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.users.UsersAdapterSpinner;
import com.alorma.github.basesdk.client.BaseClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewIssueActivity extends BackActivity implements BaseClient.OnResultCallback<Issue> {

    public static final String REPO_INFO = "REPO_INFO";

    private boolean creatingIssue = false;
    private RepoInfo repoInfo;
    private EditText editTitle;
    private EditText editBody;
    private User issueAssignee;
    private TextView userTextView;
    private TextView milestoneTextView;
    private TextView labelsTextView;
    private Milestone issueMilestone;

    private Integer[] positionsSelectedLabels;
    private CharSequence[] selectedLabels;

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
            milestoneTextView.setCompoundDrawables(pushAccessInfoIcon(Octicons.Icon.oct_milestone), null, null, null);
            labelsTextView.setCompoundDrawables(pushAccessInfoIcon(Octicons.Icon.oct_tag), null, null, null);

            View.OnClickListener pushAccessListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.assignee:
                            openAssignee();
                            break;
                        case R.id.milestone:
                            openMilestone();
                            break;
                        case R.id.labels:
                            openLabels();
                            break;
                    }
                }
            };

            userTextView.setOnClickListener(pushAccessListener);
            milestoneTextView.setOnClickListener(pushAccessListener);
            labelsTextView.setOnClickListener(pushAccessListener);

            if (getToolbar() != null) {
                ViewCompat.setElevation(getToolbar(), 8);
            }
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

    private void checkDataAndCreateIssue() {
        if (editTitle.length() <= 0) {
            editTitle.setError(getString(R.string.issue_title_mandatory));
            return;
        }

        IssueRequest issue = new IssueRequest();
        issue.title = editTitle.getText().toString();
        issue.body = editBody.getText().toString();

        if (repoInfo != null && repoInfo.permissions != null && repoInfo.permissions.push) {
            if (issueAssignee != null) {
                issue.assignee = issueAssignee.login;
            }

            if (selectedLabels != null) {
                issue.labels = selectedLabels;
            }

            if (issueMilestone != null) {
                issue.milestone = issueMilestone.number;
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
            issueInfo.repoInfo = repoInfo;
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
        ErrorHandler.onError(this, "Creating issue", error);
        invalidateOptionsMenu();
        Toast.makeText(this, R.string.create_issue_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Assignee
     */

    private void openAssignee() {
        GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(getApplicationContext(), repoInfo);
        contributorsClient.setOnResultCallback(new ContributorsCallback());
        contributorsClient.execute();
    }

    private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
        @Override
        public void onResponseOk(ListContributors contributors, Response r) {
            final List<User> users = new ArrayList<>();
            String owner = repoInfo.owner;
            boolean exist = false;
            if (contributors != null) {
                for (Contributor contributor : contributors) {
                    exist = contributor.author.login.equals(owner);
                    users.add(contributor.author);
                }
            }

            if (!exist) {
                User user = new User();
                user.login = owner;
                users.add(user);
            }

            Collections.reverse(users);
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
    }

    private class MilestonesCallback implements BaseClient.OnResultCallback<List<Milestone>> {
        @Override
        public void onResponseOk(final List<Milestone> milestones, Response r) {
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
        this.issueMilestone = milestone;
        milestoneTextView.setText(milestone.title);
    }

    private void clearMilestone() {
        milestoneTextView.setText(null);
    }

    /**
     * Labels
     */

    private void openLabels() {
        GithubIssueLabelsClient labelsClient = new GithubIssueLabelsClient(this, repoInfo);
        labelsClient.setOnResultCallback(new LabelsCallback());
        labelsClient.execute();
    }

    private class LabelsCallback implements BaseClient.OnResultCallback<List<Label>> {

        @Override
        public void onResponseOk(List<Label> labels, Response r) {
            if (labels != null) {
                List<String> items = new ArrayList<>();
                for (Label label : labels) {
                    items.add(label.name);
                }

                MaterialDialog.Builder builder = new MaterialDialog.Builder(NewIssueActivity.this);
                builder.items(items.toArray(new String[items.size()]));
                builder.alwaysCallMultiChoiceCallback();
                builder.itemsCallbackMultiChoice(positionsSelectedLabels, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        selectedLabels = charSequences;
                        positionsSelectedLabels = integers;
                        return true;
                    }
                });
                builder.forceStacking(true);
                builder.positiveText(R.string.ok);
//                builder.neutralText(R.string.add_new_label);
                builder.negativeText(R.string.clear_labels);
                builder.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        setLabels(selectedLabels);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        selectedLabels = null;
                        positionsSelectedLabels = null;
                        setLabels(null);
                    }

//                    @Override
//                    public void onNeutral(MaterialDialog dialog) {
//                        super.onNeutral(dialog);
//                    }
                });
                builder.show();
            }
        }

        @Override
        public void onFail(RetrofitError error) {

        }
    }

    private void setLabels(CharSequence[] selectedLabels) {
        if (selectedLabels != null) {
            StringBuilder builder = new StringBuilder();
            for (CharSequence selectedLabel : selectedLabels) {
                builder.append(selectedLabel);
                builder.append(", ");
            }

            if (selectedLabels.length > 0) {
                String labels = builder.toString();
                int lastIndexOf = labels.lastIndexOf(", ");
                labels = labels.substring(0, lastIndexOf);
                labelsTextView.setText(labels);
            }

        } else {
            labelsTextView.setText(null);
        }
    }
}