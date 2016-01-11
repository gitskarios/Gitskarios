package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.alorma.github.AccountsHelper;
import com.alorma.github.R;
import com.alorma.github.account.GithubLoginFragment;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WelcomeActivity extends AccountAuthenticatorActivity implements GithubLoginFragment.LoginCallback {

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private GithubLoginFragment loginFragment;
    private String accessToken;
    private Toolbar secondToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        secondToolbar = (Toolbar) findViewById(R.id.second_toolbar);
        ViewCompat.setElevation(secondToolbar, R.dimen.gapMedium);

        secondToolbar.setTitle(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        loginFragment = new GithubLoginFragment();
        loginFragment.setLoginCallback(this);
        getFragmentManager().beginTransaction().add(loginFragment, "login").commit();
    }

    private void changeLayouts() {
        ButterKnife.findById(this, R.id.enterLayout).setVisibility(View.GONE);
        ButterKnife.findById(this, R.id.loginLayout).setVisibility(View.VISIBLE);

        IconicsDrawable back = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_arrow_back)
                .actionBar()
                .paddingDp(4)
                .color(Color.BLACK);

        secondToolbar.setNavigationIcon(back);

        secondToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreView();
            }
        });
    }

    private void restoreView() {
        ButterKnife.findById(this, R.id.enterLayout).setVisibility(View.VISIBLE);
        ButterKnife.findById(this, R.id.loginLayout).setVisibility(View.GONE);
        secondToolbar.setNavigationIcon(null);
        secondToolbar.setNavigationOnClickListener(null);
    }

    @OnClick(R.id.openLogin)
    public void openLogin() {
        progressBar.setVisibility(View.VISIBLE);
        ButterKnife.findById(this, R.id.accessToken).setVisibility(View.GONE);
        ButterKnife.findById(this, R.id.generateToken).setVisibility(View.GONE);
        ButterKnife.findById(this, R.id.loginGithub).setVisibility(View.GONE);
        loginFragment.login(this);
        changeLayouts();
    }

    @OnClick(R.id.openLoginAdvanced)
    public void openLoginAdvanced() {
        changeLayouts();
        ButterKnife.findById(this, R.id.accessToken).setVisibility(View.VISIBLE);
        ButterKnife.findById(this, R.id.generateToken).setVisibility(View.VISIBLE);
        ButterKnife.findById(this, R.id.loginGithub).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.generateToken)
    public void generateTokenGithub() {
        loginFragment.loginAdvanced(this);
    }

    @OnClick(R.id.loginGithub)
    public void loginGithub() {
        EditText tokenText = ButterKnife.findById(this, R.id.accessToken);
        endAccess(tokenText.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (loginFragment != null) {
            loginFragment.finishPurchase(requestCode, resultCode, data);
        }
    }

    @Override
    public void endAccess(String accessToken) {
        this.accessToken = accessToken;
        progressBar.setVisibility(View.VISIBLE);

        if (Fabric.isInitialized()) {
            EditText tokenText = ButterKnife.findById(this, R.id.accessToken);
            Answers.getInstance().logLogin(new LoginEvent()
                    .putMethod(TextUtils.isEmpty(tokenText.getText()) ? "oauth" : "advanced")
                    .putSuccess(true));
        }

        GetAuthUserClient authUserClient = new GetAuthUserClient(accessToken);
        authUserClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                WelcomeActivity.this.onError(e);
            }

            @Override
            public void onNext(User user) {
                addAccount(user);
                MainActivity.startActivity(WelcomeActivity.this);
                finish();
            }
        });
    }

    private void addAccount(User user) {
        Account account = new Account(user.login, getString(R.string.account_type));
        Bundle userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url);
        userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccountExplicitly(account, null, userData);
        accountManager.setAuthToken(account, getString(R.string.account_type), accessToken);

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (loginFragment != null) {
            loginFragment.onNewIntent(intent);
            loginFragment.setLoginCallback(this);
        }
    }


    @Override
    public void onError(Throwable error) {

    }

    @Override
    public void loginNotAvailable() {

    }
}
