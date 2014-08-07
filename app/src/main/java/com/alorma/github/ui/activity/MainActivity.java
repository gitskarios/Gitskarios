package com.alorma.github.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.activity.base.NavigationActivity;
import com.alorma.github.ui.events.ColorEvent;
import com.alorma.github.ui.fragment.FollowersFragment;
import com.alorma.github.ui.fragment.FollowingFragment;
import com.alorma.github.ui.fragment.GistsFragment;
import com.alorma.github.ui.fragment.ProfileFragment;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.navigation.MainNavigationFragment;
import com.alorma.github.ui.fragment.navigation.NavigationDrawerFragment;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MainActivity extends NavigationActivity {

    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = new Bus();
        ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        GistsApplication.AB_COLOR = getResources().getColor(R.color.accent);
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void colorReceived(ColorEvent event) {
        setUpActionBarColor(event.getRgb());
    }

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected NavigationDrawerFragment getNavigationFragment() {
        return MainNavigationFragment.newInstance();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                setContainerFragment(ReposManagerFragment.newInstance());
                if (getActionBar() != null) {
                    getActionBar().setTitle(R.string.title_repos);
                }
                break;
            /*case 1:
                setContainerFragment(GistsFragment.newInstance());
                restoreActionBar();
                if (getActionBar() != null) {
                    getActionBar().setTitle(R.string.title_gists);
                }
                break;*/
            case 1:
                setContainerFragment(FollowingFragment.newInstance());
                if (getActionBar() != null) {
                    getActionBar().setTitle(R.string.title_following);
                }
                break;
            case 2:
                setContainerFragment(FollowersFragment.newInstance());
                if (getActionBar() != null) {
                    getActionBar().setTitle(R.string.title_followers);
                }
                break;
        }

        restoreActionBar();
    }

    @Override
    public void profileSelected(User user) {
        closeDrawer();
        Intent launcherIntent = ProfileActivity.createLauncherIntent(this, user);
        startActivity(launcherIntent);
    }

    private void restoreActionBar() {
        GistsApplication.AB_COLOR = getResources().getColor(R.color.accent);
        if (getActionBar() != null) {
            getActionBar().setSubtitle(null);
        }

        bus.post(new ColorEvent(GistsApplication.AB_COLOR));
    }
}
