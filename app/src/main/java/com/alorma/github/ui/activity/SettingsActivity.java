package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.preference.GitskariosPreferenceFragment;

public class SettingsActivity extends BackActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, new GitskariosPreferenceFragment());
		ft.commit();

	}



}
