package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.alorma.github.sdk.bean.dto.response.Repo;

/**
 * Created by Bernat on 28/09/2014.
 */
public class RepoSettingsFragment extends Fragment {

	public static RepoSettingsFragment newInstance(Repo repo) {
		Bundle args = new Bundle();
		args.putParcelable("REPO", repo);

		RepoSettingsFragment f = new RepoSettingsFragment();

		f.setArguments(args);

		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}
}
