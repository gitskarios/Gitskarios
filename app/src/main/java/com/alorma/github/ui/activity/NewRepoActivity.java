package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.alorma.github.ui.activity.base.BackActivity;

/**
 * Created by Bernat on 07/10/2014.
 */
public class NewRepoActivity extends BackActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, Repo)
	}
}
