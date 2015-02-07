package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.CloseIssueClient;
import com.alorma.github.sdk.services.issues.GetIssueClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.dialog.NewIssueCommentActivity;
import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.ui.view.FABCenterLayout;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.crashlytics.android.Crashlytics;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class IssueDetailActivity extends BackActivity implements View.OnClickListener, BaseClient.OnResultCallback<Issue> {

	public static final String ISSUE_INFO = "ISSUE_INFO";
	public static final String PERMISSIONS = "PERMISSIONS";
	private static final int NEW_COMMENT_REQUEST = 1243;

	private IssueDiscussionFragment issueDiscussionFragment;
	private IssueState issueState;
	private Permissions permissions;
	private boolean shouldRefreshOnBack;
	private Issue issue;
	private IssueInfo issueInfo;
	private FABCenterLayout fabLayout;
	private TextView issueBody;

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

			getContent();

			findViews();
		}
	}

	@Override
	protected void getContent() {
		GetIssueClient issuesClient = new GetIssueClient(this, issueInfo);
		issuesClient.setOnResultCallback(this);
		issuesClient.execute();
	}

	private void findViews() {
		fabLayout = (FABCenterLayout) findViewById(R.id.fabLayout);

		int accent = AttributesUtils.getAccentColor(this, R.style.AppTheme_Repos);
		int primaryDark = AttributesUtils.getPrimaryDarkColor(this, R.style.AppTheme_Repos);

		fabLayout.setFabColor(accent);
		fabLayout.setFabColorPressed(primaryDark);

		GithubIconDrawable drawable = new GithubIconDrawable(this, GithubIconify.IconValue.octicon_comment_discussion).color(Color.WHITE).fabSize();
		fabLayout.setFabIcon(drawable);
		fabLayout.setFabClickListener(this, getString(R.string.add_comment));

		issueBody = (TextView) findViewById(R.id.issueBody);
		issueBody.setMaxLines(3);
		issueBody.setEllipsize(TextUtils.TruncateAt.END);
	}

	private void setData() {
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(issue.title);
		}

		addDiscussionFragment();
	}

	private void addDiscussionFragment() {
		try {
			if (!isFinishing()) {
				issueDiscussionFragment = IssueDiscussionFragment.newInstance(issueInfo);

				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.discussionFeed, issueDiscussionFragment);
				ft.commit();
			}
		} catch (IllegalStateException e) {
			Crashlytics.logException(e);
		}
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
			MenuItem menuItem = menu.add(0, R.id.action_close_issue, 1, getString(R.string.closeIssue));
			menuItem.setIcon(new GithubIconDrawable(this, GithubIconify.IconValue.octicon_x).actionBarSize().colorRes(R.color.white));
			menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		MenuItem menuItem;
		GithubIconify.IconValue icon;
		if (fabLayout.isFold()) {
			menuItem = menu.add(0, R.id.action_fold_issue, 0, R.string.unfold_issue);
			icon = GithubIconify.IconValue.octicon_unfold;
		} else {
			menuItem = menu.add(0, R.id.action_fold_issue, 0, R.string.fold_issue);
			icon = GithubIconify.IconValue.octicon_fold;
		}
		menuItem.setIcon(new GithubIconDrawable(this, icon).actionBarSize().colorRes(R.color.white));
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


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
			case R.id.action_fold_issue:
				if (fabLayout.isFold()) {
					issueBody.setMaxLines(Integer.MAX_VALUE);
					issueBody.setEllipsize(null);
				} else {
					issueBody.setMaxLines(3);
					issueBody.setEllipsize(TextUtils.TruncateAt.END);
				}
				fabLayout.setFold(!fabLayout.isFold());
				invalidateOptionsMenu();
				break;
		}

		return true;
	}

	public void onAddComment() {
		Intent intent = NewIssueCommentActivity.launchIntent(IssueDetailActivity.this, issueInfo);
		startActivityForResult(intent, NEW_COMMENT_REQUEST);
	}

	private void closeIssueDialog() {
		String title = getString(R.string.closeIssue);
		String accept = getString(R.string.accept);
		String cancel = getString(R.string.cancel);

		MaterialDialog dialog = new MaterialDialog.Builder(this)
				.title(title)
				.positiveText(accept)
				.negativeText(cancel)
				.callback(new MaterialDialog.SimpleCallback() {
					@Override
					public void onPositive(MaterialDialog materialDialog) {
						closeIssue();
					}
				})
				.build();

		dialog.show();
	}

	private void closeIssue() {
		CloseIssueClient closeIssueClient = new CloseIssueClient(this, issueInfo.repo.owner, issueInfo.repo.name, issueInfo.num);
		closeIssueClient.setOnResultCallback(this);
		closeIssueClient.execute();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == fabLayout.getFabId()) {
			onAddComment();
		}
	}

	@Override
	public void onResponseOk(Issue issue, Response r) {
		if (issue != null) {
			issueBody.setText(issue.body);
			IssueDetailActivity.this.issue = issue;
			IssueDetailActivity.this.issueState = issue.state;
			invalidateOptionsMenu();
			setData();

			//Spanned issueNumber = Html.fromHtml(getString(R.string.issue_detail_title, issue.number));

			shouldRefreshOnBack = true;
		}
	}

	@Override
	public void onFail(RetrofitError error) {
		ErrorHandler.onRetrofitError(IssueDetailActivity.this, "Closing issue: ", error);
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
