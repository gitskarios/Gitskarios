package com.alorma.github.ui.fragment.base;

import android.app.Fragment;
import android.os.Bundle;

import com.alorma.github.GitskariosApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Bernat on 12/08/2014.
 */
public class BaseFragment extends Fragment {
/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get tracker.
		Tracker t = ((GitskariosApplication) getActivity().getApplication()).getTracker();

		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName(getClass().getSimpleName());

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
	}*/
}
