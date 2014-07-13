package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.NavigationActivity;
import com.alorma.github.ui.fragment.GistsFragment;
import com.alorma.github.ui.fragment.ProfileFragment;
import com.alorma.github.ui.fragment.navigation.MainNavigationFragment;
import com.alorma.github.ui.fragment.navigation.NavigationDrawerFragment;

public class MainActivity extends NavigationActivity {

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

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
                int rgb = getResources().getColor(R.color.accent);
                ColorDrawable cd = new ColorDrawable(rgb);
                if (getActionBar() != null) {
                    getActionBar().setBackgroundDrawable(cd);
                }
                break;
            case 1:
                setContainerFragment(ProfileFragment.newInstance());
                break;
            case 2:
                setContainerFragment(ProfileFragment.newInstance("kix2902"));
                break;
            case 3:
                setContainerFragment(ProfileFragment.newInstance("octocats"));
                break;
            default:
                setContainerFragment(new ListFragment());
                break;
        }
    }
}
