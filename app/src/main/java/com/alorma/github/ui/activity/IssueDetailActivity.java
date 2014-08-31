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
import android.widget.FABCenterLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.issue.IssueDetailInfoFragment;
import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.dvilleneuve.android.TextDrawable;

public class IssueDetailActivity extends BackActivity implements RefreshListener {

	public static final String OWNER = "OWNER";
	public static final String REPO = "REPO";
	public static final String NUMBER = "NUMBER";
	public static final String STATE = "STATE";
	public static final String PERMISSIONS = "PERMS";
	public static final String CREATOR = "CREATOR";
	private static final String DESCRIPTION = "DESCRIPTION";

	private String owner;
	private String repo;
	private int number;
	private SmoothProgressBar smoothBar;
	protected FABCenterLayout fabLayout;
	private IssueDiscussionFragment issueDiscussionFragment;
	private IssueDetailInfoFragment issueInfoFragment;
	private IssueState issueState;
	private Permissions permissions;
	private User creator;
	private String desctiption;
	private ImageView avatarAuthor;
	private TextView issueTitle;

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

			findViews();
			setPreviewData();
			checkForState();
		}
	}

	protected void checkForState() {
		String state = getIntent().getExtras().getString(STATE);

		int color = R.color.issue_state_open;
		issueState = IssueState.open;
		if (IssueState.closed.toString().equals(state)) {
			issueState = IssueState.closed;
			color = R.color.issue_state_close;
		}

		if (issueState == IssueState.open) {
			if (permissions.push) {
				fabLayout.setFABDrawable(new TextDrawable(this, "x").color(Color.WHITE));
				fabLayout.setFabClickListener(new FabCloseIssueClickListener(), getString(R.string.closeIssue));
			} else {
				fabLayout.setFABDrawable(new TextDrawable(this, "+").color(Color.WHITE));
				fabLayout.setFabClickListener(new FabAddCommentIssueClickListener(), getString(R.string.addComment));
			}
			fabLayout.setFabVisible(permissions.push || permissions.pull);
			invalidateOptionsMenu();
		}

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

		if (issueInfoFragment == null) {
			issueInfoFragment = new IssueDetailInfoFragment();
		}

		if (issueDiscussionFragment == null) {
			issueDiscussionFragment = IssueDiscussionFragment.newInstance(owner, repo, number);
			issueDiscussionFragment.setRefreshListener(this);
		}

		setTopData();

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.issueDetailInfo, issueInfoFragment);
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

		if (permissions.push) {
			menu.add(0, R.id.action_add_comment, 0, getString(R.string.addComment));
			menu.findItem(R.id.action_add_comment).setIcon(new IconDrawable(this, Iconify.IconValue.fa_plus).actionBarSize().colorRes(R.color.white));
			menu.findItem(R.id.action_add_comment).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
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
			//Toast.makeText(v.getContext(), "Close issue", Toast.LENGTH_SHORT).show();
		}
	}

	private class FabAddCommentIssueClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			//Toast.makeText(v.getContext(), "Add comment", Toast.LENGTH_SHORT).show();
		}
	}
}
