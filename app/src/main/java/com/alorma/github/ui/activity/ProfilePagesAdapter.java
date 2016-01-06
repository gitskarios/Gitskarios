package com.alorma.github.ui.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alorma.github.ui.listeners.TitleProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bernat.borras on 13/12/15.
 */
public class ProfilePagesAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments;
    private final Context context;

    public ProfilePagesAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.context = context;
        if (fragments == null) {
            this.fragments = new ArrayList<>();
        } else {
            this.fragments = fragments;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (fragments.get(position) instanceof TitleProvider) {
            return context.getString(((TitleProvider) fragments.get(position)).getTitle());
        } else {
            return "";
        }
    }
}
