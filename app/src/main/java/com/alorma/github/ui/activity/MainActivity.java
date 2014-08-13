package com.alorma.github.ui.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.animations.HeightEvaluator;
import com.alorma.github.ui.animations.WidthEvaluator;
import com.alorma.github.ui.fragment.menu.MenuFragment;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final long MENU_ANIMATION_TIME = 300;
    private TextView currentState;
    private ImageView chevron;
    private View chevronLy;
    private boolean isMenuOpen = false;
    private MenuFragment menuFragment;
    private View menuFragmentLy;
    private View searchIcon;
    private int menuHeight = -1;

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
        IconDrawable chevronUp = new IconDrawable(this, Iconify.IconValue.fa_chevron_up);
        chevronUp.colorRes(R.color.gray_github_dark);
        chevron.setImageDrawable(chevronUp);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        menuFragment = MenuFragment.newInstance();
        ft.replace(R.id.menuContent, menuFragment);
        ft.commit();

        isMenuOpen = true;

        expandMenu();
    }

    private void hideMenu() {
        if (menuFragment != null) {
            IconDrawable chevronDown = new IconDrawable(this, Iconify.IconValue.fa_chevron_down);
            chevronDown.colorRes(R.color.gray_github_dark);
            chevron.setImageDrawable(chevronDown);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(menuFragment);
            ft.commit();

            isMenuOpen = false;

            collapseMenu();
        }
    }

    private void expandMenu() {
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

    public void collapseMenu() {
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
