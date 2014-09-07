package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alorma.github.ui.view.FABCenterLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.nostra13.universalimageloader.core.ImageLoader;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.dvilleneuve.android.TextDrawable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class IssueDetailActivity extends BackActivity implements RefreshListener, BaseClient.OnResultCallback<Issue> {

	public static final String OWNER = "OWNER";
	public static final String REPO = "REPO";
	public static final String NUMBER = "NUMBER";
	public static final String STATE = "STATE";
	public static final String PERMISSIONS = "PERMS";
	public static final String CREATOR = "CREATOR";
	private static final String DESCRIPTION = "DESCRIPTION";
	private static final int NEW_COMMENT_REQUEST = 1243;

	private String owner;
	private String repo;
	private int number;
	private SmoothProgressBar smoothBar;
	protected FABCenterLayout fabLayout;
	private IssueDiscussionFragment issueDiscussionFragment;
	private IssueState issueState;
	private Permissions permissions;
	private User creator;
	private String desctiption;
	private ImageView avatarAuthor;
	private TextView issueTitle;
	private Issue issue;
	private boolean shouldRefreshOnBack;

	public static Intent createLauncherIntent(Context context, Issue issue) {
		Bundle bundle = new Bundle();

		bundle.putInt(NUMBER, issue.number);
		bundle.putString(OWNER, issue.repository.owner.login);
		bundle.putParcelable(CREATOR, issue.user);
		bundle.putString(REPO, issue.repository.name);
		bundle.putString(STATE, issue.state.toString());
		bundle.putString(DESCRIPTION, issue.title);
		bundle.putParcelable(PERMISSIONS, issue.repository.permissions);

		Intent intent = new Intent(context, IssueDetailActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);

		if (getIntent().getExtras() != null) {
			number = getIntent().getExtras().getInt(NUMBER);
			owner = getIntent().getExtras().getString(OWNER);
			repo = getIntent().getExtras().getString(REPO);
			desctiption = getIntent().getExtras().getString(DESCRIPTION);
			permissions = getIntent().getExtras().getParcelable(PERMISSIONS);
			creator = getIntent().getExtras().getParcelable(CREATOR);

			String state = getIntent().getExtras().getString(STATE);

			this.issueState = IssueState.open;
			if (IssueState.closed.toString().equals(state)) {
				issueState = IssueState.closed;
			}

			findViews();
			setPreviewData();
			checkForState();
		}
	}

	protected void checkForState() {

		int color = R.color.issue_state_open;
		if (IssueState.closed == issueState) {
			color = R.color.issue_state_close;
		}

		if (issueState == IssueState.open) {
			if (permissions != null && permissions.push) {
				fabLayout.setFABDrawable(new TextDrawable(this, "x").color(Color.WHITE));
				fabLayout.setFabClickListener(new FabCloseIssueClickListener(), getString(R.string.closeIssue));
				fabLayout.setFabVisible(permissions.push || permissions.pull);
			} else {
				fabLayout.setFabVisible(false);
			}
		} else {
			fabLayout.setFabVisible(false);
		}
		invalidateOptionsMenu();

		setColor(color);
	}

	private void setColor(int colorRes) {
		if (getActionBar() != null) {
			int color = getResources().getColor(colorRes);
			getActionBar().setBackgroundDrawable(new ColorDrawable(color));
			View issueDetailInfo = findViewById(R.id.issueDetailInfo);
			if (issueDetailInfo != null) {
				issueDetailInfo.setBackgroundColor(color);
			}
		}
	}

	private void findViews() {
		smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);
		fabLayout = (FABCenterLayout) findViewById(R.id.fabLayout);

		avatarAuthor = (ImageView) findViewById(R.id.avatarAuthor);
		issueTitle = (TextView) findViewById(R.id.issueTitle);
	}

	private void setPreviewData() {
		if (getActionBar() != null) {
			getActionBar().setTitle(repo);
			getActionBar().setSubtitle(Html.fromHtml(getString(R.string.issue_detail_title, number)));
		}

		if (issueDiscussionFragment == null) {
			issueDiscussionFragment = IssueDiscussionFragment.newInstance(owner, repo, number);
			issueDiscussionFragment.setRefreshListener(this);
		}

		setTopData();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.discussionFeed, issueDiscussionFragment);
		ft.commit();
	}

	private void setTopData() {
		if (creator != null) {
			ImageLoader.getInstance().displayImage(creator.avatar_url, avatarAuthor);
			issueTitle.setText(desctiption);
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
				new FabAddCommentIssueClickListener().onClick(fabLayout);
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
		CloseIssueClient closeIssueClient = new CloseIssueClient(this, owner, repo, number);
		closeIssueClient.setOnResultCallback(this);
		closeIssueClient.execute();

		if (smoothBar != null && !smoothBar.isActivated()) {
			smoothBar.progressiveStart();
		}
	}

	@Override
	public void onResponseOk(Issue issue, Response r) {
		if (issue != null) {
			if (smoothBar != null && smoothBar.isActivated()) {
				smoothBar.progressiveStop();
			}
			this.issue = issue;
			this.issueState = issue.state;
			checkForState();
			shouldRefreshOnBack = true;
		}
	}

	@Override
	public void onFail(RetrofitError error) {
		ErrorHandler.onRetrofitError(this, "Closing issue: ", error);
	}

	private class FabAddCommentIssueClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			IssueInfo issueInfo = new IssueInfo();
			issueInfo.owner = owner;
			issueInfo.repo = repo;
			issueInfo.num = number;
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
				issueDiscussionFragment = IssueDiscussionFragment.newInstance(owner, repo, number);
				issueDiscussionFragment.setRefreshListener(IssueDetailActivity.this);

				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.discussionFeed, issueDiscussionFragment);
				ft.commit();
			}
		}
	}
}
