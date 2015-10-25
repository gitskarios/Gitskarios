package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.alorma.github.R;
import com.alorma.github.account.GithubLoginFragment;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.gitskarios.core.client.BaseClient;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WelcomeActivity extends AccountAuthenticatorActivity
    implements BaseClient.OnResultCallback<User>, GithubLoginFragment.LoginCallback {

    @Bind(R.id.imageView)
    ImageView imageView;

    @Bind(R.id.imageUser)
    ImageView imageUser;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.appName)
    TextView appNameTextView;

    @Bind(R.id.buttonGithub)
    Button buttonGithub;

    @Bind(R.id.buttonEnterprise)
    Button buttonEnterprise;

    @Bind(R.id.buttonOpen)
    Button buttonOpen;

    private GithubLoginFragment loginFragment;
    private String accessToken;

    private Long startTime;
    private int countClick = 0;
    private String accountType;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        StoreCredentials credentials = new StoreCredentials(this);
        credentials.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        List<Account> accounts = getAccounts(getString(R.string.account_type), getString(R.string.enterprise_account_type));

        String action = getIntent().getAction();

        if (action != null && action.equals(Intent.ACTION_MAIN) && accounts.size() > 0) {
            openMain();
        } else {
            showInitialButtons();
        }

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
                Account[] accounts = accountManager.getAccountsByType(accountType);
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

        buttonEnterprise.animate().alpha(1f).setDuration(TimeUnit.SECONDS.toMillis(1)).start();
        buttonEnterprise.setVisibility(View.VISIBLE);
        buttonEnterprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateEnterprise();
            }
        });
    }

    private void openMain() {
        MainActivity.startActivity(this);
        finish();
    }

    private void openCreate() {

        accountType = getString(R.string.account_type);
        buttonEnterprise.setVisibility(View.INVISIBLE);

        buttonGithub.animate().alpha(0f).setDuration(TimeUnit.SECONDS.toMillis(1)).setListener(new Animator.AnimatorListener() {
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
        }).start();
        progressBar.animate().alpha(1f).setStartDelay(300).setDuration(TimeUnit.SECONDS.toMillis(1)).start();

        progressBar.setVisibility(View.VISIBLE);

        boolean login = loginFragment.login();
        if (!login) {
            showInitialButtons();
        }
    }

    private void openCreateEnterprise() {

        accountType = getString(R.string.enterprise_account_type);
        buttonGithub.setVisibility(View.INVISIBLE);
        buttonEnterprise.animate()
            .alpha(0f)
            .setDuration(TimeUnit.SECONDS.toMillis(1))
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    buttonEnterprise.setVisibility(View.INVISIBLE);
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

        Intent intent = new Intent(this, GithubEnterpriseLoginActivity.class);
        startActivityForResult(intent, 2112);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        StoreCredentials credentials = new StoreCredentials(this);
        credentials.clear();

        if (loginFragment != null) {
            loginFragment.onNewIntent(intent);
            loginFragment.setLoginCallback(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2112) {
            if (data != null && data.getExtras() != null) {
                if (data.getExtras().containsKey(GithubEnterpriseLoginActivity.EXTRA_ENTERPRISE_URL) && data.getExtras()
                    .containsKey(GithubEnterpriseLoginActivity.EXTRA_ENTERPRISE_TOKEN)) {
                    url = data.getStringExtra(GithubEnterpriseLoginActivity.EXTRA_ENTERPRISE_URL);
                    String token = data.getStringExtra(GithubEnterpriseLoginActivity.EXTRA_ENTERPRISE_TOKEN);

                    StoreCredentials credentials = new StoreCredentials(this);
                    credentials.storeUrl(url);

                    endAccess(token);
                }
            }
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
        progressBar.setVisibility(View.INVISIBLE);

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
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void addAccount(User user) {
        if (checkSelfPermission("android.permission.AUTHENTICATE_ACCOUNTS") == PackageManager.PERMISSION_GRANTED) {
            Account account = new Account(user.login, accountType);
            Bundle userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url, url);

            userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

            AccountManager accountManager = AccountManager.get(this);
            accountManager.addAccountExplicitly(account, null, userData);
            accountManager.setAuthToken(account, accountType, accessToken);

            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
            setAccountAuthenticatorResult(result);

            checkAndEnableSyncAdapter(account);

            setResult(RESULT_OK);
        }
    }

    private void checkAndEnableSyncAdapter(Account account) {
        ContentResolver.setIsSyncable(account, account.type, ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE);
        if (ContentResolver.getSyncAutomatically(account, account.type)) {
            ContentResolver.addPeriodicSync(account, account.type, Bundle.EMPTY, 1800);
            ContentResolver.setSyncAutomatically(account, account.type, true);
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
