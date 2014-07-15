package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.NavigationActivity;
import com.alorma.github.ui.fragment.GistsFragment;
import com.alorma.github.ui.fragment.ProfileFragment;
import com.alorma.github.ui.fragment.navigation.MainNavigationFragment;
import com.alorma.github.ui.fragment.navigation.NavigationDrawerFragment;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));
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
                setContainerFragment(GistsFragment.newInstance());
                restoreActionBar();
                if (getActionBar() != null) {
                    getActionBar().setTitle(R.string.title_gists);
                }
                break;
            case 1:
                setContainerFragment(ProfileFragment.newInstance("JakeWharton"));
                break;
        }
    }

    private void restoreActionBar() {
        int rgb = getResources().getColor(R.color.accent);
        ColorDrawable cd = new ColorDrawable(rgb);
        if (getActionBar() != null) {
            getActionBar().setBackgroundDrawable(cd);
            getActionBar().setSubtitle("");
        }
    }
}
