package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.view.FABCenterLayout;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

/**
 * Created by Bernat on 10/01/2015.
 */
public class PullRequestDetailActivity extends BackActivity implements View.OnClickListener {

	public static final String ISSUE_INFO = "ISSUE_INFO";
	
	private FABCenterLayout fabLayout;
	private TextView issueBody;
	
	private IssueInfo issueInfo;

	public static Intent createLauncherIntent(Context context, IssueInfo issueInfo) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(ISSUE_INFO, issueInfo);

		Intent intent = new Intent(context, PullRequestDetailActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);

		if (getIntent().getExtras() != null) {
			issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);

			findViews();
		}
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

	@Override
	public void onClick(View v) {

	}
}
