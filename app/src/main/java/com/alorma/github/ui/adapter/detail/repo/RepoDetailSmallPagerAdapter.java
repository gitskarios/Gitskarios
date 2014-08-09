package com.alorma.github.ui.adapter.detail.repo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.ui.fragment.detail.repo.TextDescriptionFragment;

/**
 * Created by Bernat on 09/08/2014.
 */
public class RepoDetailSmallPagerAdapter extends FragmentPagerAdapter {

    private ListBranches branches;
    private String textDescription;
    private TextDescriptionFragment fragmentSpinner;
    private TextDescriptionFragment fragmentText;

    public RepoDetailSmallPagerAdapter(FragmentManager fm) {
        super(fm);
        this.branches = new ListBranches();
        Branch master = new Branch();
        master.name = "master";
        branches.add(master);
    }

    public RepoDetailSmallPagerAdapter(FragmentManager fm, String textDescription) {
        this(fm);
        this.textDescription = textDescription;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment f = null;

        switch (i) {
            case 0:
                if (TextUtils.isEmpty(textDescription)) {
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
        return TextUtils.isEmpty(textDescription) ? 1 : 2;
    }

    public void setTextDescription(String description) {
        this.textDescription = description;
        getFragmentText().setText(textDescription);
        notifyDataSetChanged();
    }

    public void setBranches(ListBranches branches) {
        this.branches = branches;
        getFragmentSpinner().setText("Branches: " + branches.size());
        notifyDataSetChanged();
    }

    public TextDescriptionFragment getFragmentSpinner() {
        if (fragmentSpinner == null) {
            fragmentSpinner = TextDescriptionFragment.newInstance("Branches: " + branches.size());
        }

        return fragmentSpinner;
    }

    public TextDescriptionFragment getFragmentText() {
        if (fragmentText == null) {
            fragmentText = TextDescriptionFragment.newInstance(textDescription);
        }

        fragmentText.setText(textDescription);
        return fragmentText;
    }
}
