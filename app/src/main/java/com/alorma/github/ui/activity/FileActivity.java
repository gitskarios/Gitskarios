package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.fragment.FileFragment;

/**
 * Created by Bernat on 20/07/2014.
 */
public class FileActivity extends ActionBarActivity implements FileFragment.FileFragmentListener {

	private static final String REPO_INFO = "REPO_INFO";
	private static final String NAME = "NAME";
	private static final String PATH = "PATH";
	private static final String PATCH = "PATCH";

	public static Intent createLauncherIntent(Context context, RepoInfo repoInfo, String name, String path) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(REPO_INFO, repoInfo);
		bundle.putString(NAME, name);
		bundle.putString(PATH, path);

		Intent intent = new Intent(context, FileActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	public static Intent createLauncherIntent(Context context, String patch, String name) {
		Bundle bundle = new Bundle();
		bundle.putString(PATCH, patch);
		bundle.putString(NAME, name);

		Intent intent = new Intent(context, FileActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FileFragment fileFragment = new FileFragment();
		fileFragment.setFileFragmentListener(this);
		fileFragment.setArguments(getIntent().getExtras());
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, fileFragment);
		ft.commit();
	}

	@Override
	public boolean showUpIndicator() {
		return true;
	}
}
