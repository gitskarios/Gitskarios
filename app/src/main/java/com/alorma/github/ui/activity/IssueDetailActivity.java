package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.services.issues.GetIssueClient;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.CloseIssueClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.dialog.NewCommentDialog;
import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class IssueDetailActivity extends BackActivity implements RefreshListener
		, IssueDiscussionFragment.IssueDiscussionListener {

	public static final String ISSUE_INFO = "ISSUE_INFO";
	public static final String PERMISSIONS = "PERMISSIONS";
	private static final int NEW_COMMENT_REQUEST = 1243;

	private SmoothProgressBar smoothBar;
	private IssueDiscussionFragment issueDiscussionFragment;
	private IssueState issueState;
	private Permissions permissions;
	private boolean shouldRefreshOnBack;
	private Issue issue;
	private IssueResponse issueResponse;
	private IssueInfo issueInfo;
	private View issueDetailInfo;

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
		smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);

		issueDetailInfo = findViewById(R.id.issueState);
	}

	protected void checkForState() {

		int color = R.color.issue_state_open;
		if (IssueState.closed == issueState) {
			color = R.color.issue_state_close;
		}

		if (issueState == IssueState.open) {
			if (permissions != null && permissions.push) {

			} else {

			}
		} else {

		}

		invalidateOptionsMenu();

		setColor(color);
	}

	private void setColor(int colorRes) {
		if (getActionBar() != null) {
			int color = getResources().getColor(colorRes);
			ColorDrawable colorDrawable = new ColorDrawable(color);
			getActionBar().setBackgroundDrawable(colorDrawable);
			if (issueDetailInfo != null) {
				issueDetailInfo.setBackgroundColor(color);
			}
		}
	}

	private void setData() {
		if (getActionBar() != null) {
			getActionBar().setTitle(issue.title);
			getActionBar().setSubtitle(Html.fromHtml(getString(R.string.issue_detail_title, issue.number)));
		}

		if (issueDiscussionFragment == null) {
			issueDiscussionFragment = IssueDiscussionFragment.newInstance(issueInfo);
			issueDiscussionFragment.setIssueDiscussionListener(this);
			issueDiscussionFragment.setRefreshListener(this);
		}

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

		if (permissions != null && permissions.pull && issueState == IssueState.open) {
			menu.add(0, R.id.action_add_comment, 0, getString(R.string.addComment));
			menu.findItem(R.id.action_add_comment).setIcon(new IconDrawable(this, Iconify.IconValue.fa_plus).actionBarSize().colorRes(R.color.white));
			menu.findItem(R.id.action_add_comment).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case android.R.id.home:
				setResult(shouldRefreshOnBack ? RESULT_FIRST_USER : RESULT_OK);
				finish();
				break;
			case R.id.action_add_comment:
				// TODO Listener on close issue
				break;
		}

		return true;
	}

	@Override
	public void showRefresh() {
		if (smoothBar != null) {
			smoothBar.progressiveStart();
		}
	}

	@Override
	public void cancelRefresh() {
		if (smoothBar != null) {
			smoothBar.progressiveStop();
		}
	}

	@Override
	public IssueComment requestIssue() {
		return issue;
	}

	// TODO change to use comment dialog and close
	private class FabCloseIssueClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			String title = getString(R.string.closeIssue);
			String accept = getString(R.string.accept);
			String cancel = getString(R.string.cancel);
			CustomDialog.Builder builder = new CustomDialog.Builder(v.getContext(), title, accept);
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
					// Do nothing
				}
			});
			customDialog.show();
		}
	}

	private void closeIssue() {
		CloseIssueClient closeIssueClient = new CloseIssueClient(this, issueInfo.owner, issueInfo.repo, issueInfo.num);
		closeIssueClient.setOnResultCallback(issueResponse);
		closeIssueClient.execute();

		if (smoothBar != null && !smoothBar.isActivated()) {
			smoothBar.progressiveStart();
		}
	}

	private class IssueResponse implements BaseClient.OnResultCallback<Issue> {

		@Override
		public void onResponseOk(Issue issue, Response r) {
			if (issue != null) {
				if (smoothBar != null && smoothBar.isActivated()) {
					smoothBar.progressiveStop();
				}
				IssueDetailActivity.this.issue = issue;
				IssueDetailActivity.this.issueState = issue.state;
				checkForState();
				setData();
				shouldRefreshOnBack = true;
			}
		}

		@Override
		public void onFail(RetrofitError error) {
			ErrorHandler.onRetrofitError(IssueDetailActivity.this, "Closing issue: ", error);
		}
	}

	private class FabAddCommentIssueClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			smoothBar.progressiveStart();
			Intent intent = NewCommentDialog.launchIntent(IssueDetailActivity.this, issueInfo);
			startActivityForResult(intent, NEW_COMMENT_REQUEST);
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
				smoothBar.progressiveStop();
				issueDiscussionFragment = IssueDiscussionFragment.newInstance(issueInfo);
				issueDiscussionFragment.setRefreshListener(IssueDetailActivity.this);

				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.discussionFeed, issueDiscussionFragment);
				ft.commit();
			}
		}
	}
}
