package com.alorma.github.ui.adapter.viewpager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alorma.github.ui.listeners.TitleProvider;

import java.util.List;

public class NavigationPagerAdapter extends FragmentPagerAdapter {

    private Resources resources;
    private List<Fragment> listFragments;

    public NavigationPagerAdapter(FragmentManager fm, Resources resources, List<Fragment> listFragments) {
        super(fm);
        this.resources = resources;
        this.listFragments = listFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (listFragments.get(position) instanceof TitleProvider) {
            int title = ((TitleProvider) listFragments.get(position)).getTitle();
            return resources.getString(title);
        }
        return "";
    }
}