package com.alorma.github.ui.fragment.detail.repo;

import android.support.v4.app.Fragment;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.listeners.TitleProvider;

/**
 * Created by Bernat on 01/01/2015.
 */
public class RepoAboutFragment extends Fragment implements TitleProvider,BranchManager {
	
	public static RepoAboutFragment newInstance(RepoInfo repoInfo) {
		return new RepoAboutFragment();
	}

	@Override
	public int getTitle() {
		return R.string.overview_fragment_title;
	}

	@Override
	public void setCurrentBranch(String branch) {

	}
}
