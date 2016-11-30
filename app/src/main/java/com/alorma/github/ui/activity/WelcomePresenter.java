package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import com.alorma.github.AccountsHelper;
import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.CreateAuthorization;
import com.alorma.github.sdk.bean.dto.response.GithubAuthorization;
import com.alorma.github.sdk.services.login.CreateAuthorizationClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.TwoFactorAppException;
import com.alorma.github.sdk.services.user.TwoFactorAuthException;
import com.alorma.github.sdk.services.user.UnauthorizedException;
import com.alorma.github.utils.AccountUtils;
import com.alorma.gitskarios.core.Pair;
import core.User;
import java.lang.ref.WeakReference;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WelcomePresenter {

  private WelcomePresenterViewInterface welcomePresenterViewInterfaceNull = new WelcomePresenterViewInterface.NullView();
  private WelcomePresenterViewInterface welcomePresenterViewInterface = welcomePresenterViewInterfaceNull;

  private String username;
  private String password;
  private WeakReference<AccountAuthenticatorActivity> accountAuthenticatorActivity;

  public WelcomePresenter(AccountAuthenticatorActivity accountAuthenticatorActivity) {
    this.accountAuthenticatorActivity = new WeakReference<>(accountAuthenticatorActivity);
  }

  public void login(String username, String password) {
    this.username = username;
    this.password = password;
    createAuthorization(null);
  }

  public void setOtpCode(String otpCode) {
    createAuthorization(otpCode);
  }

  private void createAuthorization(String otpCode) {
    CreateAuthorization createRequest = new CreateAuthorization();
    createRequest.note = "gitskarios";
    createRequest.scopes = new String[] {
        "gist", "user", "notifications", "repo", "delete_repo"
    };
    createRequest.client_id = BuildConfig.CLIENT_ID;
    createRequest.client_secret = BuildConfig.CLIENT_SECRET;
    createRequest.note_url = "http://gitskarios.github.io";

    CreateAuthorizationClient createAuthorizationClient = new CreateAuthorizationClient(username, password, createRequest);

    if (otpCode != null) {
      createAuthorizationClient.setOtpCode(otpCode);
    }

    createAuthorizationClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(() -> welcomePresenterViewInterface.willLogin())
        .doOnError(this::checkErrorFromAuthorization)
        .flatMap(githubAuthorization -> new GetAuthUserClient(githubAuthorization.token).observable())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> welcomePresenterViewInterface.didLogin())
        .doOnCompleted(() -> welcomePresenterViewInterface.didLogin())
        .subscribe(userStringPair -> addAccount(userStringPair.first, userStringPair.second), this::checkError);
  }

  private void checkError(Throwable e) {
    if (e instanceof UnauthorizedException) {
      onErrorUnauthorized();
    } else if (!(e instanceof TwoFactorAppException || e instanceof TwoFactorAuthException)) {
      onGenericError();
    }
  }

  private void checkErrorFromAuthorization(Throwable e) {
    if (e instanceof TwoFactorAppException) {
      welcomePresenterViewInterface.onErrorTwoFactorAppException();
    } else if (e instanceof TwoFactorAuthException) {
      welcomePresenterViewInterface.onErrorTwoFactorException();
    }
  }

  private void onErrorUnauthorized() {
    welcomePresenterViewInterface.onErrorUnauthorized();
  }

  private void onGenericError() {
    welcomePresenterViewInterface.onGenericError();
  }

  public void start(WelcomePresenterViewInterface welcomePresenterViewInterface) {
    this.welcomePresenterViewInterface = welcomePresenterViewInterface;
  }

  public void stop() {
    this.welcomePresenterViewInterface = welcomePresenterViewInterfaceNull;
  }

  private void addAccount(User user, String accessToken) {
    if (accessToken != null && accountAuthenticatorActivity != null && accountAuthenticatorActivity.get() != null) {
      Account account = new Account(new AccountUtils().getNameForAccount(user.getLogin()),
          accountAuthenticatorActivity.get().getString(R.string.account_type));
      Bundle userData = AccountsHelper.buildBundle(user.getName(), user.getEmail(), user.getAvatar());
      userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

      AccountManager accountManager = AccountManager.get(accountAuthenticatorActivity.get());
      accountManager.addAccountExplicitly(account, null, userData);
      accountManager.setAuthToken(account, accountAuthenticatorActivity.get().getString(R.string.account_type), accessToken);

      Bundle result = new Bundle();
      result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
      result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
      result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
      accountAuthenticatorActivity.get().setAccountAuthenticatorResult(result);
      welcomePresenterViewInterface.finishAccess(user);
    }
  }
}
