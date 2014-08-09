package com.alorma.github.ui.adapter.detail.repo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.text.TextUtils;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.ui.fragment.detail.repo.BranchesSpinnerFragment;
import com.alorma.github.ui.fragment.detail.repo.TextDescriptionFragment;

/**
 * Created by Bernat on 09/08/2014.
 */
public class RepoDetailSmallPagerAdapter extends FragmentPagerAdapter {

    private BranchesSpinnerFragment fragmentSpinner;
    private TextDescriptionFragment fragmentText;
    private Repo repo;
    private BranchesSpinnerFragment.OnBranchesListener onBranchesListener;

    public RepoDetailSmallPagerAdapter(FragmentManager fm, Repo repo, BranchesSpinnerFragment.OnBranchesListener onBranchesListener) {
        super(fm);
        this.repo = repo;
        this.onBranchesListener = onBranchesListener;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment f = null;

        switch (i) {
            case 0:
                if (TextUtils.isEmpty(repo.description)) {
                    f = getFragmentSpinner();
                } else {
                    f = getFragmentText();
                }
                break;
            case 1:
                f = getFragmentSpinner();
                break;
        }
        return f;
    }

    @Override
    public int getCount() {
        return TextUtils.isEmpty(repo.description) ? 1 : 2;
    }

    public BranchesSpinnerFragment getFragmentSpinner() {
        if (fragmentSpinner == null) {
            String owner = this.repo.owner.login;
            String repo = this.repo.name;
            fragmentSpinner = BranchesSpinnerFragment.newInstance(owner, repo, this.repo.default_branch);
        }

        fragmentSpinner.setUseDivider(!TextUtils.isEmpty(repo.description));

        fragmentSpinner.setOnBranchesListener(onBranchesListener);

        return fragmentSpinner;
    }

    public TextDescriptionFragment getFragmentText() {
        if (fragmentText == null) {
            fragmentText = TextDescriptionFragment.newInstance(repo.description);
        }

        fragmentText.setText(repo.description);
        return fragmentText;
    }

    public void setRepo(Repo repo) {
        this.repo = repo;
        notifyDataSetChanged();
    }

}
