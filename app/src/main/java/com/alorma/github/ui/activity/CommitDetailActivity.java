package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.commit.SingleCommitFragment;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitDetailActivity extends BackActivity {

	public static Intent launchIntent(Context context, RepoInfo info, String sha) {
		Bundle b = new Bundle();
		b.putParcelable(SingleCommitFragment.INFO, info);
		b.putString(SingleCommitFragment.SHA, sha);

		Intent intent = new Intent(context, CommitDetailActivity.class);
		intent.putExtras(b);

		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generic_toolbar_no_drawer);

		if (getIntent().getExtras() != null) {
			RepoInfo info = getIntent().getExtras().getParcelable(SingleCommitFragment.INFO);
			String sha = getIntent().getExtras().getString(SingleCommitFragment.SHA);

			setTitle(getString(R.string.title_activity_commits, info, sha));

			SingleCommitFragment reposFragment = SingleCommitFragment.newInstance(info, sha);

			android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content, reposFragment);
			ft.commit();
		}
	}
}
