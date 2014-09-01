package com.alorma.github.ui.adapter.detail.issue;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.alorma.github.ui.fragment.detail.issue.IssueDiscussionFragment;
import com.alorma.github.ui.listeners.RefreshListener;

/**
 * Created by Bernat on 23/08/2014.
 */
public class IssueDetailPagerAdapter extends FragmentStatePagerAdapter {

	private String owner;
	private String repo;
	private int num;
	private RefreshListener refreshListener;
	private IssueDiscussionFragment issueDiscussionFragment;

	public IssueDetailPagerAdapter(FragmentManager fm, String owner, String repo, int num) {
		super(fm);
		this.owner = owner;
		this.repo = repo;
		this.num = num;
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
			case 0:
				return new ListFragment();
			case 1:
				if (issueDiscussionFragment == null) {
					issueDiscussionFragment = IssueDiscussionFragment.newInstance(owner, repo, num);
					issueDiscussionFragment.setRefreshListener(refreshListener);
				}
				return issueDiscussionFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

	public void setRefreshListener(RefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}
}
