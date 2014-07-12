package com.alorma.github.ui.activity;

import android.app.ListFragment;

import com.alorma.github.ui.activity.base.NavigationActivity;
import com.alorma.github.ui.fragment.navigation.MainNavigationFragment;
import com.alorma.github.ui.fragment.navigation.NavigationDrawerFragment;

public class MainActivity extends NavigationActivity {
    @Override
    protected NavigationDrawerFragment getNavigationFragment() {
        return MainNavigationFragment.newInstance();
    }

    @Override
    protected CharSequence getActivityTitle() {
        return "Main";
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                setContainerFragment(GistsFragment.newInstance());
                break;
            default:
                setContainerFragment(new ListFragment());
                break;
        }
    }
}
