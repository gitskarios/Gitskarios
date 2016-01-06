package com.alorma.github.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.alorma.github.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;


/**
 * Created by bernat.borras on 4/1/16.
 */
public class EnterpriseAdActivity extends AppCompatActivity implements View.OnClickListener {

    View textTitle;
    View button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterprise_ad_activity);

        if (Fabric.isInitialized()) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Enterpise visited")
                    .putContentId("enterprise_visited"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        setTitle("");
        ViewCompat.setElevation(toolbar, 0);

        textTitle = findViewById(R.id.view_new_app_layout);
        button = findViewById(R.id.button);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        animateLayout();
    }

    private void animateLayout() {
        ViewCompat.setAlpha(textTitle, 0);
        ViewCompat.setAlpha(button, 0);

        textTitle.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);

        ViewPropertyAnimatorCompat alphaText = ViewCompat.animate(textTitle).alpha(1f);
        ViewPropertyAnimatorCompat alphaButton = ViewCompat.animate(button).alpha(1f);

        alphaButton.setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                view.setOnClickListener(null);
            }

            @Override
            public void onAnimationEnd(View view) {
                view.setOnClickListener(EnterpriseAdActivity.this);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        });

        configAnim(alphaText).start();
        configAnim(alphaButton).start();
    }

    private ViewPropertyAnimatorCompat configAnim(ViewPropertyAnimatorCompat animatorCompat) {
        animatorCompat.setDuration(TimeUnit.SECONDS.toMillis(2));
        animatorCompat.setInterpolator(new DecelerateInterpolator());
        return animatorCompat;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (Fabric.isInitialized()) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Enterpise click")
                    .putContentId("enterprise_click"));
        }

        final String appPackageName = "com.alorma.github_enterprise";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
