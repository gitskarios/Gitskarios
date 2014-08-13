package com.alorma.github.ui.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.animations.HeightEvaluator;
import com.alorma.github.ui.animations.WidthEvaluator;
import com.alorma.github.ui.fragment.FollowersFragment;
import com.alorma.github.ui.fragment.FollowingFragment;
import com.alorma.github.ui.fragment.menu.MenuFragment;
import com.alorma.github.ui.fragment.menu.MenuItem;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class MainActivity extends BaseActivity implements View.OnClickListener, MenuFragment.OnMenuItemSelectedListener {

    private static final long MENU_ANIMATION_TIME = 200;
    private TextView currentState;
    private ImageView chevron;
    private View chevronLy;
    private boolean isMenuOpen = false;
    private MenuFragment menuFragment;
    private View menuFragmentLy;
    private View searchIcon;
    private int menuHeight = -1;
    private ReposFragment reposFragment;
    private StarredReposFragment starredFragment;
    private WatchedReposFragment watchedFragment;
    private FollowersFragment followersFragment;
    private FollowingFragment followingFragment;

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentState = (TextView) findViewById(R.id.currentState);

        IconDrawable chevronDown = new IconDrawable(this, Iconify.IconValue.fa_chevron_down);
        chevronDown.colorRes(R.color.gray_github_dark);

        chevron = (ImageView) findViewById(R.id.chevron);
        chevron.setImageDrawable(chevronDown);

        chevronLy = findViewById(R.id.chevronLy);
        chevronLy.setOnClickListener(this);

        menuFragmentLy = findViewById(R.id.menuContent);

        searchIcon = findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, ReposFragment.newInstance());
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chevronLy:
                if (isMenuOpen) {
                    hideMenu();
                } else {
                    showMenu();
                }
                break;
            case R.id.searchIcon:
                Intent search = SearchReposActivity.createLauncherIntent(this);
                startActivity(search);
        }
    }

    private void showMenu() {

        if (menuFragment == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            menuFragment = MenuFragment.newInstance();
            menuFragment.setOnMenuItemSelectedListener(this);
            ft.replace(R.id.menuContent, menuFragment);
            ft.commit();
        }

        IconDrawable chevronUp = new IconDrawable(this, Iconify.IconValue.fa_chevron_up);
        chevronUp.colorRes(R.color.gray_github_dark);
        chevron.setImageDrawable(chevronUp);

        isMenuOpen = true;

        Float dimension = getResources().getDimension(R.dimen.icon56dp);
        ValueAnimator searchIconAnimator = ValueAnimator.ofObject(new WidthEvaluator(searchIcon), dimension.intValue(), 0);
        searchIconAnimator.setDuration(MENU_ANIMATION_TIME);
        searchIconAnimator.start();

        if (menuHeight == -1) {
            menuHeight = menuFragmentLy.getHeight();
        }

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new HeightEvaluator(menuFragmentLy, true), 0, menuHeight);
        valueAnimator.setDuration(MENU_ANIMATION_TIME);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    private void hideMenu() {
        if (menuFragment != null) {
            IconDrawable chevronDown = new IconDrawable(this, Iconify.IconValue.fa_chevron_down);
            chevronDown.colorRes(R.color.gray_github_dark);
            chevron.setImageDrawable(chevronDown);
            
            isMenuOpen = false;

            Float dimension = getResources().getDimension(R.dimen.icon56dp);
            ValueAnimator searchIconAnimator = ValueAnimator.ofObject(new WidthEvaluator(searchIcon), 0, dimension.intValue());
            searchIconAnimator.setDuration(MENU_ANIMATION_TIME);
            searchIconAnimator.start();

            ValueAnimator valueAnimator = ValueAnimator.ofObject(new HeightEvaluator(menuFragmentLy, false), menuHeight, 0);
            valueAnimator.setDuration(MENU_ANIMATION_TIME);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();

        hideMenu();
    }

    @Override
    public void onReposSelected() {
        if (reposFragment == null) {
            reposFragment = ReposFragment.newInstance();
        }

        setFragment(reposFragment);
    }

    @Override
    public void onStarredSelected() {
        if (starredFragment == null) {
            starredFragment = StarredReposFragment.newInstance();
        }

        setFragment(starredFragment);
    }

    @Override
    public void onWatchedSelected() {
        if (watchedFragment == null) {
            watchedFragment = WatchedReposFragment.newInstance();
        }

        setFragment(watchedFragment);
    }

    @Override
    public void onFollowersSelected() {
        if (followersFragment == null) {
            followersFragment = FollowersFragment.newInstance();
        }

        setFragment(followersFragment);
    }

    @Override
    public void onFollowingSelected() {
        if (followingFragment == null) {
            followingFragment = FollowingFragment.newInstance();
        }

        setFragment(followingFragment);
    }

    @Override
    public void onMenuItemSelected(@NonNull MenuItem item) {
        currentState.setText(item.text);
    }

    @Override
    public void closeMenu() {
        hideMenu();
    }
}
