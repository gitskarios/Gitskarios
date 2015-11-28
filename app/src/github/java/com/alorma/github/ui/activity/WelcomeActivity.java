package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.alorma.github.R;
import com.alorma.github.account.GithubLoginFragment;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WelcomeActivity extends AccountAuthenticatorActivity implements GithubLoginFragment.LoginCallback {

  @Bind(R.id.progressBar) ProgressBar progressBar;

  private GithubLoginFragment loginFragment;
  private String accessToken;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    ButterKnife.bind(this);

    Toolbar secondToolbar = (Toolbar) findViewById(R.id.second_toolbar);
    ViewCompat.setElevation(secondToolbar, R.dimen.gapMedium);

    secondToolbar.setTitle(R.string.app_name);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    loginFragment = new GithubLoginFragment();
    loginFragment.setLoginCallback(this);
    getFragmentManager().beginTransaction().add(loginFragment, "login").commit();
  }

  @OnClick(R.id.openGithub)
  public void openExternal() {
    loginFragment.login(this);
    //TODO Animate
    ButterKnife.findById(this, R.id.loginLayout).setVisibility(View.VISIBLE);
    ButterKnife.findById(this, R.id.enterLayout).setVisibility(View.GONE);
  }

  @OnClick(R.id.loginGithub)
  public void login() {
    EditText accessToken = ButterKnife.findById(this, R.id.accessToken);
    endAccess(accessToken.getText().toString());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (loginFragment != null) {
      loginFragment.finishPurchase(requestCode, resultCode, data);
    }
  }

  public void endAccess(String accessToken) {
    this.accessToken = accessToken;
    progressBar.setVisibility(View.VISIBLE);
    GetAuthUserClient authUserClient = new GetAuthUserClient(this, accessToken);
    authUserClient.observable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
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

    checkAndEnableSyncAdapter(account);

    setResult(RESULT_OK);
  }

  private void checkAndEnableSyncAdapter(Account account) {
    ContentResolver.setIsSyncable(account, getString(R.string.account_type),
        ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE);
    if (ContentResolver.getSyncAutomatically(account, getString(R.string.account_type))) {
      ContentResolver.addPeriodicSync(account, getString(R.string.account_type), Bundle.EMPTY, 1800);
      ContentResolver.setSyncAutomatically(account, getString(R.string.account_type), true);
    }
  }

  @Override
  public void onError(Throwable error) {

  }

  @Override
  public void loginNotAvailable() {

  }
}
