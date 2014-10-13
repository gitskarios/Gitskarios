package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.repo.RepoCreateFragment;

/**
 * Created by Bernat on 07/10/2014.
 */
public class NewRepoActivity extends BackActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, RepoCreateFragment.newInstance());
		ft.commit();
	}
}
