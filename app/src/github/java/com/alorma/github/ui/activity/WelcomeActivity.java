package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.gitskarios.core.client.BaseClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WelcomeActivity extends BaseActivity implements BaseClient.OnResultCallback<User>,GithubLoginActivity.LoginCallback {

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.buttonGithub)
    Button buttonGithub;
    private GithubLoginActivity loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        List<Account> accounts = getAccounts(getString(R.string.account_type));

        if (accounts.size() > 0) {
            openMain();
        } else {
            buttonGithub.animate().alpha(1f).setDuration(TimeUnit.SECONDS.toMillis(1)).start();
            buttonGithub.setVisibility(View.VISIBLE);
            buttonGithub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCreate();
                }
            });
        }
    }

    private void openMain() {
        MainActivity.startActivity(this);
    }

    private void openCreate() {

        buttonGithub.animate()
            .alpha(0f)
            .setDuration(TimeUnit.SECONDS.toMillis(1))
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    buttonGithub.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            })
            .start();
        progressBar.animate().alpha(1f).setStartDelay(300).setDuration(TimeUnit.SECONDS.toMillis(1)).start();

        progressBar.setVisibility(View.VISIBLE);

        loginFragment = new GithubLoginActivity();
        loginFragment.setLoginCallback(this);
        getSupportFragmentManager().beginTransaction().add(loginFragment, "login").commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (loginFragment != null) {
            loginFragment.onNewIntent(intent);
        }
    }

    @Override
    public void onResponseOk(User user, Response r) {
        progressBar.setVisibility(View.INVISIBLE);
        ImageLoader.getInstance().displayImage(user.avatar_url, imageView);
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    @Override
    public void endAccess(String accessToken) {
        GetAuthUserClient authUserClient = new GetAuthUserClient(this, accessToken);
        authUserClient.setOnResultCallback(this);
        authUserClient.execute();
    }

    @Override
    public void onError(RetrofitError error) {

    }
}
