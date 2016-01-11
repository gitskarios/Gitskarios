package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.emoji.EmojisActivity;
import com.alorma.github.sdk.bean.dto.request.CreateMilestoneRequestDTO;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
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
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewIssueActivity extends BackActivity {

    public static final String REPO_INFO = "REPO_INFO";
    private static final int EMOJI_CODE = 1554;
    private static final int NEW_ISSUE_REQUEST = 575;

    private boolean creatingIssue = false;
    private RepoInfo repoInfo;

    private EditText editTitle;
    private TextView editBody;
    private TextView userTextView;
    private TextView milestoneTextView;
    private TextView labelsTextView;

    private Integer[] positionsSelectedLabels;

    private boolean editBodyHasFocus;
    private boolean issuePublished = false;

    private IssueRequest issueRequest;

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
            repoInfo = (RepoInfo) getIntent().getExtras().getParcelable(REPO_INFO);
            findViews();

            issueRequest = CacheWrapper.getIssueRequest(repoInfo.owner + "/" + repoInfo.name);
            if (issueRequest != null) {
                setupFromCache(issueRequest);
            } else {
                issueRequest = new IssueRequest();
            }
        } else {
            finish();
        }
    }

    private void setupFromCache(IssueRequest issueRequest) {
        if (issueRequest != null) {
            if (issueRequest.title != null) {
                editTitle.setText(issueRequest.title);
            }
            if (issueRequest.body != null) {
                editBody.setText(issueRequest.body);
            }
            if (issueRequest.labels != null) {
                StringBuilder builder = new StringBuilder();
                for (CharSequence label : issueRequest.labels) {
                    builder.append(label);
                    builder.append(",");
                }
                String s = builder.toString();
                if (s.endsWith(",")) {
                    s = s.substring(0, s.length() - 1);
                }
                labelsTextView.setText(s);
            }
            if (issueRequest.assignee != null) {
                userTextView.setText(issueRequest.assignee);
            }
            if (issueRequest.milestone != null && issueRequest.milestoneName != null) {
                milestoneTextView.setText(issueRequest.milestoneName);
            }
        }
    }

    private void findViews() {
        editTitle = (EditText) findViewById(R.id.editTitle);
        editBody = (TextView) findViewById(R.id.editBody);

        editBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint = getString(R.string.add_issue_body);
                Intent intent = ContentEditorActivity.createLauncherIntent(v.getContext(), repoInfo, 0
                        , hint, editBody.getText().toString(), false, false);

                startActivityForResult(intent, NEW_ISSUE_REQUEST);
            }
        });

        if (repoInfo.permissions != null && !repoInfo.permissions.push) {
            findViewById(R.id.pushAccessLayout).setVisibility(View.GONE);
        } else {
            userTextView = (TextView) findViewById(R.id.assignee);
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

        editBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editBodyHasFocus = hasFocus;
                invalidateOptionsMenu();
            }
        });
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

    private IssueRequest checkDataAndCreateIssue() {
        if (editTitle.length() <= 0) {
            editTitle.setError(getString(R.string.issue_title_mandatory));
            return null;
        }

        if (issueRequest == null) {
            issueRequest = new IssueRequest();
        }

        issueRequest.title = editTitle.getText().toString();
        issueRequest.body = editBody.getText().toString();

        creatingIssue = true;

        return issueRequest;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_send);
        if (item != null) {
            item.setEnabled(!creatingIssue);
        }

        if (editBodyHasFocus) {
            menu.add(0, R.id.action_add_emoji, 100, R.string.add_emoji);

            MenuItem emojiMenu = menu.findItem(R.id.action_add_emoji);
            emojiMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            Drawable emojiIcon = new IconicsDrawable(this, Octicons.Icon.oct_octoface).actionBar().color(Color.WHITE);
            emojiMenu.setIcon(emojiIcon);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_send:
                issueRequest = checkDataAndCreateIssue();
                invalidateOptionsMenu();
                createIssue(issueRequest);
                showProgressDialog(R.string.creating_issue);
                break;
            case R.id.action_add_emoji:
                Intent intent = new Intent(this, EmojisActivity.class);
                startActivityForResult(intent, EMOJI_CODE);
                break;
        }
        return true;
    }

    @Override
    protected void close(boolean navigateUp) {
        super.close(navigateUp);
        if (!issuePublished && issueRequest != null) {
            issueRequest.title = editTitle.getText().toString();
            CacheWrapper.setNewIssueRequest(repoInfo.owner + "/" + repoInfo.name, issueRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String content = data.getStringExtra(ContentEditorActivity.CONTENT);
            issueRequest.body = content;
            editBody.setText(content);
        }
    }

    private void createIssue(IssueRequest issue) {
        issuePublished = true;
        issueRequest.milestoneName = null;
        PostNewIssueClient postNewIssueClient = new PostNewIssueClient(repoInfo, issue);
        postNewIssueClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Issue>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
                creatingIssue = false;
                ErrorHandler.onError(NewIssueActivity.this, "Creating issue", e);
                invalidateOptionsMenu();
                Toast.makeText(NewIssueActivity.this, R.string.create_issue_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Issue issue) {
                hideProgressDialog();
                if (issue != null) {
                    IssueInfo issueInfo = new IssueInfo();
                    issueInfo.repoInfo = repoInfo;
                    issueInfo.num = issue.number;
                    Intent launcherIntent = IssueDetailActivity.createLauncherIntent(NewIssueActivity.this, issueInfo);
                    startActivity(launcherIntent);
                    setResult(RESULT_OK);
                    CacheWrapper.clearIssueRequest(repoInfo.owner + "/" + repoInfo.name);
                    finish();
                }
            }
        });
    }

    /**
     * Assignee
     */

    private void openAssignee() {
        GetRepoContributorsClient contributorsClient = new GetRepoContributorsClient(repoInfo);
        contributorsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Contributor>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Contributor> contributors) {
                onContributorsLoaded(contributors);
            }
        });
    }

    public void onContributorsLoaded(List<Contributor> contributors) {
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

    private void setAssigneeUser(User user) {
        if (userTextView != null) {
            if (user != null) {
                issueRequest.assignee = user.login;
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
        GetMilestonesClient milestonesClient = new GetMilestonesClient(repoInfo, MilestoneState.open, true);
        milestonesClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Milestone>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Milestone> milestones) {
                onMilestonesLoaded(milestones);
            }
        });
    }

    public void onMilestonesLoaded(final List<Milestone> milestones) {
        if (milestones.size() == 0) {
            showCreateMilestone();
        } else {
            String[] itemsMilestones = new String[milestones.size()];

            for (int i = 0; i < milestones.size(); i++) {
                itemsMilestones[i] = milestones.get(i).title;
            }

            MaterialDialog.Builder builder = new MaterialDialog.Builder(NewIssueActivity.this).title(R.string.select_milestone)
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

    private void showCreateMilestone() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.add_milestone);
        builder.content(R.string.add_milestone_description);
        builder.input(R.string.add_milestone_hint, 0, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog materialDialog, CharSequence milestoneName) {
                createMilestone(milestoneName.toString());
            }
        }).negativeText(R.string.cancel);

        builder.show();
    }

    private void createMilestone(String milestoneName) {
        CreateMilestoneRequestDTO createMilestoneRequestDTO = new CreateMilestoneRequestDTO(milestoneName);

        CreateMilestoneClient createMilestoneClient = new CreateMilestoneClient(repoInfo, createMilestoneRequestDTO);
        createMilestoneClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Milestone>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog();
            }

            @Override
            public void onNext(Milestone milestone) {
                addMilestone(milestone);
            }
        });
    }

    private void addMilestone(Milestone milestone) {
        issueRequest.milestone = milestone.number;
        issueRequest.milestoneName = milestone.title;
        milestoneTextView.setText(milestone.title);
    }

    private void clearMilestone() {
        milestoneTextView.setText(null);
    }

    /**
     * Labels
     */

    private void openLabels() {
        GithubIssueLabelsClient labelsClient = new GithubIssueLabelsClient(repoInfo, true);
        labelsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Label>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Label> labels) {
                onLabelsLoaded(labels);
            }
        });
    }

    public void onLabelsLoaded(List<Label> labels) {
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
                    List<String> labels = new ArrayList<>();
                    for (CharSequence charSequence : charSequences) {
                        labels.add(charSequence.toString());
                    }
                    issueRequest.labels = labels.toArray(new String[labels.size()]);
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
                    setLabels(issueRequest.labels);
                }

                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);
                    issueRequest.labels = null;
                    positionsSelectedLabels = null;
                    setLabels(null);
                }
            });
            builder.show();
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