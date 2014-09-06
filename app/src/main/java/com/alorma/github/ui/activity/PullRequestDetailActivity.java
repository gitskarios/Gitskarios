package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class PullRequestDetailActivity extends IssueDetailActivity {

	public static Intent createLauncherIntent(Context context, Issue issue) {
		Bundle bundle = new Bundle();

		bundle.putInt(NUMBER, issue.number);
		bundle.putString(OWNER, issue.repository.owner.login);
		bundle.putString(REPO, issue.repository.name);
		bundle.putString(STATE, issue.state.toString());

		Intent intent = new Intent(context, IssueDetailActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Toast.makeText(this, "Pull request", Toast.LENGTH_SHORT).show();

		fabLayout.setFABDrawable(new IconDrawable(this, Iconify.IconValue.fa_code_fork).colorRes(R.color.white));
	}


}
