package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;

/**
 * Created by Bernat on 20/10/2015.
 */
public class GithubEnterpriseLoginActivity extends AccountAuthenticatorActivity {

    @Bind(R.id.enterpriseUrl)
    EditText enterpriseUrl;
    @Bind(R.id.accessToken)
    EditText enterpriseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_github_enterprise_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        ButterKnife.bind(this);
    }

    @OnClick(R.id.generateToken)
    public void generateToken() {
        if (enterpriseUrl.length() > 0) {
            String url = enterpriseUrl.getText().toString() + "/settings/tokens";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    @OnClick(R.id.loginGithub)
    public void onLogin() {
        if (enterpriseUrl.length() > 0 && enterpriseToken.length() > 0) {
            final String url = enterpriseUrl.getText().toString();
            final String token = enterpriseToken.getText().toString();

            if (Fabric.isInitialized()) {
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("enterprise")
                        .putSuccess(true));
            }
            StoreCredentials storeCredentials = new StoreCredentials(this);
            storeCredentials.storeUrl(url);

            GetAuthUserClient authUserClient = new GetAuthUserClient(this, token);
            authUserClient.observable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(User user) {
                    addAccount(user, url, token);
                    MainActivity.startActivity(GithubEnterpriseLoginActivity.this);
                    finish();
                }
            });
        }
    }

    private void addAccount(User user, String url, String accessToken) {
        Account account = new Account(user.login, getString(R.string.enterprise_account_type));
        Bundle userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url, url);
        userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        AccountManager accountManager = AccountManager.get(this);
        accountManager.addAccountExplicitly(account, null, userData);
        accountManager.setAuthToken(account, getString(R.string.enterprise_account_type), accessToken);

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
    }
}
