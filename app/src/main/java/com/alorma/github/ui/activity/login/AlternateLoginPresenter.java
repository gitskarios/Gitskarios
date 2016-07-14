package com.alorma.github.ui.activity.login;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import com.alorma.github.AccountsHelper;
import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.CreateAuthorization;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.login.CreateAuthorizationClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.utils.AccountUtils;
import com.alorma.gitskarios.core.Pair;
import java.lang.ref.WeakReference;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AlternateLoginPresenter {

  private AlternateLoginPresenterViewInterface alternateLoginPresenterViewInterfaceNull =
      new AlternateLoginPresenterViewInterface.NullView();
  private AlternateLoginPresenterViewInterface alternateLoginPresenterViewInterface = alternateLoginPresenterViewInterfaceNull;

  private String username;
  private String token;
  private WeakReference<AccountAuthenticatorActivity> accountAuthenticatorActivity;

  public AlternateLoginPresenter(AccountAuthenticatorActivity accountAuthenticatorActivity) {
    this.accountAuthenticatorActivity = new WeakReference<>(accountAuthenticatorActivity);
  }

  public void login(String username, String token) {
    this.username = username;
    this.token = token;
    createAuthorization(null);
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

    CreateAuthorizationClient createAuthorizationClient = new CreateAuthorizationClient(username, token, createRequest);

    if (otpCode != null) {
      createAuthorizationClient.setOtpCode(otpCode);
    }

    Observable<Pair<User, String>> observable = new GetAuthUserClient(token).observable();
    observable.subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(() -> alternateLoginPresenterViewInterface.willLogin())
        .doOnError(throwable -> alternateLoginPresenterViewInterface.didLogin())
        .doOnCompleted(() -> alternateLoginPresenterViewInterface.didLogin())
        .subscribe(new UserSubscription());
  }

  private void onGenericError() {
    alternateLoginPresenterViewInterface.onGenericError();
  }

  public void start(AlternateLoginPresenterViewInterface welcomePresenterViewInterface) {
    this.alternateLoginPresenterViewInterface = welcomePresenterViewInterface;
  }

  public void stop() {
    this.alternateLoginPresenterViewInterface = alternateLoginPresenterViewInterfaceNull;
  }

  private void addAccount(User user, String accessToken) {
    if (accessToken != null && accountAuthenticatorActivity != null && accountAuthenticatorActivity.get() != null) {
      Account account = new Account(new AccountUtils().getNameForAccount(user.login),
          accountAuthenticatorActivity.get().getString(R.string.account_type));
      Bundle userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url);
      userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

      AccountManager accountManager = AccountManager.get(accountAuthenticatorActivity.get());
      accountManager.addAccountExplicitly(account, null, userData);
      accountManager.setAuthToken(account, accountAuthenticatorActivity.get().getString(R.string.account_type), accessToken);

      Bundle result = new Bundle();
      result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
      result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
      result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
      accountAuthenticatorActivity.get().setAccountAuthenticatorResult(result);
      alternateLoginPresenterViewInterface.finishAccess(user);
    }
  }

  private class UserSubscription extends rx.Subscriber<Pair<User, String>> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
      onGenericError();
    }

    @Override
    public void onNext(Pair<User, String> userStringPair) {
      addAccount(userStringPair.first, userStringPair.second);
    }
  }
}
