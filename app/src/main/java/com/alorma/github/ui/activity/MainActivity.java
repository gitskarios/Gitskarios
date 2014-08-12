package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends BaseActivity {

    private TextView currentState;
    private ImageView chevron;

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

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, ReposFragment.newInstance());
        ft.commit();
    }
}
