package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.account.GithubLoginFragment;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.gitskarios.core.client.BaseClient;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WelcomeActivity extends AccountAuthenticatorActivity
    implements BaseClient.OnResultCallback<User>, GithubLoginFragment.LoginCallback {

  @Bind(R.id.imageView) ImageView imageView;

  @Bind(R.id.imageUser) ImageView imageUser;

  @Bind(R.id.progressBar) ProgressBar progressBar;

  @Bind(R.id.appName) TextView appNameTextView;

  @Bind(R.id.buttonGithub) Button buttonGithub;

  @Bind(R.id.buttonOpen) Button buttonOpen;

  private GithubLoginFragment loginFragment;
  private String accessToken;

  private Long startTime;
  private int countClick = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    ButterKnife.bind(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    List<Account> accounts = getAccounts(getString(R.string.account_type));

    String action = getIntent().getAction();

    if (action != null && action.equals(Intent.ACTION_MAIN) && accounts.size() > 0) {
      openMain();
    } else {
      showInitialButtons();
    }

    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (startTime == null) {
          startTime = System.currentTimeMillis();
        }
        countClick++;

        if (countClick >= 20
            && startTime + TimeUnit.SECONDS.toMillis(5) <= System.currentTimeMillis()) {

        }
      }
    });

    loginFragment = new GithubLoginFragment();
    loginFragment.setLoginCallback(this);
    getFragmentManager().beginTransaction().add(loginFragment, "login").commit();
  }

  @NonNull
  protected List<Account> getAccounts(String... accountTypes) {

    AccountManager accountManager = AccountManager.get(this);

    List<Account> accountList = new ArrayList<>();

    if (accountTypes != null) {
      for (String accountType : accountTypes) {
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        accountList.addAll(Arrays.asList(accounts));
      }
    }
    return accountList;
  }

  private void showInitialButtons() {
    imageView.setVisibility(View.VISIBLE);
    imageUser.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
    buttonOpen.setVisibility(View.INVISIBLE);
    buttonGithub.animate().alpha(1f).setDuration(TimeUnit.SECONDS.toMillis(1)).start();
    buttonGithub.setVisibility(View.VISIBLE);
    buttonGithub.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openCreate();
      }
    });
  }

  private void openMain() {
    MainActivity.startActivity(this);
    finish();
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
    progressBar.animate()
        .alpha(1f)
        .setStartDelay(300)
        .setDuration(TimeUnit.SECONDS.toMillis(1))
        .start();

    progressBar.setVisibility(View.VISIBLE);

    boolean login = loginFragment.login();
    if (!login) {
      showInitialButtons();
    }
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
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (loginFragment != null) {
      loginFragment.finishPurchase(requestCode, resultCode, data);
    }
  }

  @Override
  public void endAccess(String accessToken) {
    this.accessToken = accessToken;
    GetAuthUserClient authUserClient = new GetAuthUserClient(this, accessToken);
    authUserClient.setOnResultCallback(this);
    authUserClient.execute();
  }

  @Override
  public void onResponseOk(final User user, Response r) {
    appNameTextView.setText(user.login);

    imageUser.setVisibility(View.VISIBLE);

    buttonOpen.animate().alpha(1f).setDuration(600).start();

    buttonOpen.setVisibility(View.VISIBLE);
    buttonOpen.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addAccount(user);
        openMain();
      }
    });

    ImageLoader.getInstance().loadImage(user.avatar_url, new ImageLoadingListener() {
      @Override
      public void onLoadingStarted(String imageUri, View view) {

      }

      @Override
      public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

      }

      @Override
      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        imageUser.setImageBitmap(loadedImage);
        progressBar.setVisibility(View.INVISIBLE);
      }

      @Override
      public void onLoadingCancelled(String imageUri, View view) {

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

    if (!BuildConfig.DEBUG && Fabric.isInitialized()) {
      Answers.getInstance().logSignUp(new SignUpEvent().putMethod(account.type).putSuccess(true));
    }

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
      ContentResolver.addPeriodicSync(account, getString(R.string.account_type), Bundle.EMPTY,
          1800);
      ContentResolver.setSyncAutomatically(account, getString(R.string.account_type), true);
    }
  }

  @Override
  public void onFail(RetrofitError error) {

  }

  @Override
  public void onError(RetrofitError error) {

  }

  @Override
  public void loginNotAvailable() {
    showInitialButtons();
  }
}
