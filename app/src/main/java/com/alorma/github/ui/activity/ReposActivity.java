package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.repos.ReposFragment;

public class ReposActivity extends BackActivity {

	private static final String USER = "USER";

	public static Intent launchIntent(Context context, String user) {
		Intent intent = new Intent(context, ReposActivity.class);
		intent.putExtra(USER, user);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generic_toolbar);

		String user = getIntent().getStringExtra(USER);

		setTitle(getString(R.string.title_activity_repos, user));

		ReposFragment reposFragment = ReposFragment.newInstance(user);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, reposFragment);
		ft.commit();
	}
}
