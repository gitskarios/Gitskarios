package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
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
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.PostNewIssueClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.users.UsersAdapterSpinner;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

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
	private UsersAdapterSpinner assigneesAdapter;
	private MaterialEditText editLabels;
	private MaterialEditText editTitle;
	private MaterialEditText editBody;

	public static Intent createLauncherIntent(Context context, RepoInfo info, boolean pushAcces) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, info.owner);
		bundle.putString(REPO, info.name);
		bundle.putBoolean(PUSH, pushAcces);
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
			owner = getIntent().getExtras().getString(OWNER);
			repo = getIntent().getExtras().getString(REPO);

			pushAcces = getIntent().getExtras().getBoolean(PUSH, false);

			findViews();

			if (!pushAcces) {
				pushAccesLayout.setVisibility(View.INVISIBLE);
			} else {
				findViewsAcces();

				RepoInfo repoInfo = new RepoInfo();
				repoInfo.owner = owner;
				repoInfo.name = repo;
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

		if (pushAcces) {
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

		invalidateOptionsMenu();

		createIssue(issue);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.new_issue, menu);

		MenuItem item = menu.findItem(R.id.action_send);
		if (item != null) {
			GithubIconDrawable githubIconDrawable = new GithubIconDrawable(this, GithubIconify.IconValue.octicon_plus);
			githubIconDrawable.actionBarSize();
			githubIconDrawable.colorRes(R.color.white);
			item.setIcon(githubIconDrawable);
		}
		return true;
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
		invalidateOptionsMenu();
		Toast.makeText(this, R.string.create_issue_error, Toast.LENGTH_SHORT).show();
	}

	private class ContributorsCallback implements BaseClient.OnResultCallback<ListContributors> {
		@Override
		public void onResponseOk(ListContributors contributors, Response r) {
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