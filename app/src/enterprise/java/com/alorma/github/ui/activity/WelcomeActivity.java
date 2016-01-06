package com.alorma.github.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alorma.github.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by bernat.borras on 10/12/15.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        ButterKnife.bind(this);
    }

    @OnClick(R.id.openLoginGithub)
    public void openGithub() {
        Intent intent = new Intent(this, GithubLoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.openLoginGithubEnterprise)
    public void openGithubEnterprise() {
        Intent intent = new Intent(this, GithubEnterpriseLoginActivity.class);
        startActivity(intent);
    }
}
