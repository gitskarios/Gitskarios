package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.services.issues.GetIssueClient;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.CloseIssueClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.dialog.NewIssueCommentDialog;
import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class IssueDetailActivity extends BackActivity implements RefreshListener
		, IssueDiscussionFragment.IssueDiscussionListener {

	public static final String ISSUE_INFO = "ISSUE_INFO";
	public static final String PERMISSIONS = "PERMISSIONS";
	private static final int NEW_COMMENT_REQUEST = 1243;

	private IssueDiscussionFragment issueDiscussionFragment;
	private IssueState issueState;
	private Permissions permissions;
	private boolean shouldRefreshOnBack;
	private Issue issue;
	private IssueResponse issueResponse;
	private IssueInfo issueInfo;

	public static Intent createLauncherIntent(Context context, IssueInfo issueInfo, Permissions permissions) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(ISSUE_INFO, issueInfo);
		bundle.putParcelable(PERMISSIONS, permissions);

		Intent intent = new Intent(context, IssueDetailActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);

		if (getIntent().getExtras() != null) {
			issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);
			permissions = getIntent().getExtras().getParcelable(PERMISSIONS);

			issueResponse = new IssueResponse();

			GetIssueClient issuesClient = new GetIssueClient(this, issueInfo);
			issuesClient.setOnResultCallback(issueResponse);
			issuesClient.execute();

			findViews();
		}
	}

	private void findViews() {

	}

	// TODO Set something todispay that is closed or not

	private void setData() {
		if (getActionBar() != null) {
			getActionBar().setTitle(issue.title);
			getActionBar().setSubtitle(Html.fromHtml(getString(R.string.issue_detail_title, issue.number)));
		}

		addDiscussionFragment();
	}

	private void addDiscussionFragment() {
		issueDiscussionFragment = IssueDiscussionFragment.newInstance(issueInfo);
		issueDiscussionFragment.setIssueDiscussionListener(this);
		issueDiscussionFragment.setRefreshListener(this);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.discussionFeed, issueDiscussionFragment);
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.issue_detail, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();

		if (permissions != null && permissions.push && issueState == IssueState.open) {
			menu.add(0, R.id.action_close_issue, 0, getString(R.string.closeIssue));
			menu.findItem(R.id.action_close_issue).setIcon(new IconDrawable(this, Iconify.IconValue.fa_times).actionBarSize().colorRes(R.color.white));
			menu.findItem(R.id.action_close_issue).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(shouldRefreshOnBack ? RESULT_FIRST_USER : RESULT_OK);
				finish();
				break;
			case R.id.action_close_issue:
				closeIssueDialog();
				break;
		}

		return true;
	}

	@Override
	public void showRefresh() {

	}

	@Override
	public void cancelRefresh() {

	}

	@Override
	public IssueComment requestIssue() {
		return issue;
	}

	@Override
	public void onAddComment() {
		Intent intent = NewIssueCommentDialog.launchIntent(IssueDetailActivity.this, issueInfo);
		startActivityForResult(intent, NEW_COMMENT_REQUEST);
	}

	private void closeIssueDialog() {
		String title = getString(R.string.closeIssue);
		String accept = getString(R.string.accept);
		String cancel = getString(R.string.cancel);
		CustomDialog.Builder builder = new CustomDialog.Builder(this, title, accept);
		builder.darkTheme(false);
		builder.positiveColor(getString(R.string.lDialogPositve));
		builder.darkTheme(false);
		builder.negativeText(cancel);
		builder.negativeColor(getString(R.string.lDialogNegative));
		builder.darkTheme(true);
		CustomDialog customDialog = builder.build();
		customDialog.setClickListener(new CustomDialog.ClickListener() {
			@Override
			public void onConfirmClick() {
				closeIssue();
			}

			@Override
			public void onCancelClick() {

			}
		});
		customDialog.show();
	}

	private void closeIssue() {
		CloseIssueClient closeIssueClient = new CloseIssueClient(this, issueInfo.owner, issueInfo.repo, issueInfo.num);
		closeIssueClient.setOnResultCallback(issueResponse);
		closeIssueClient.execute();

	}

	private class IssueResponse implements BaseClient.OnResultCallback<Issue> {

		@Override
		public void onResponseOk(Issue issue, Response r) {
			if (issue != null) {
				IssueDetailActivity.this.issue = issue;
				IssueDetailActivity.this.issueState = issue.state;
				invalidateOptionsMenu();
				setData();
				shouldRefreshOnBack = true;
			}
		}

		@Override
		public void onFail(RetrofitError error) {
			ErrorHandler.onRetrofitError(IssueDetailActivity.this, "Closing issue: ", error);
		}
	}

	@Override
	public void onBackPressed() {
		setResult(shouldRefreshOnBack ? RESULT_FIRST_USER : RESULT_OK);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == NEW_COMMENT_REQUEST) {
				addDiscussionFragment();
			}
		}
	}
}
