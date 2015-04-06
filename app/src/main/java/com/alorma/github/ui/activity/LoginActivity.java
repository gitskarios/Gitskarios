package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.BuildConfig;
import com.alorma.github.Interceptor;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.gists.CreateGistActivity;
import com.alorma.github.ui.adapter.AccountsAdapter;
import com.crashlytics.android.Crashlytics;

import dmax.dialog.SpotsDialog;
import io.fabric.sdk.android.Fabric;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AccountAuthenticatorActivity implements BaseClient.OnResultCallback<User> {

    public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "ARG_AUTH_TYPE";
    public static final String ADDING_FROM_ACCOUNTS = "ADDING_FROM_ACCOUNTS";
    public static final String ADDING_FROM_APP = "ADDING_FROM_APP";

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private SpotsDialog progressDialog;
    private String accessToken;
    private String scope;
    private RequestTokenClient requestTokenClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enableCreateGist(false);

        AccountManager accountManager = AccountManager.get(this);

        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));

        boolean fromLogin = getIntent().getData() != null && getIntent().getData().getScheme().equals("gitskarios");
        boolean fromAccounts = getIntent().getBooleanExtra(ADDING_FROM_ACCOUNTS, false);
        boolean fromApp = getIntent().getBooleanExtra(ADDING_FROM_APP, false);

        if (fromApp) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMain();
                }
            });
        }

        AccountsAdapter adapter = new AccountsAdapter(this, accounts);
        recyclerView.setAdapter(adapter);

        if (fromLogin) {
            Uri uri = getIntent().getData();
            String code = uri.getQueryParameter("code");
            if (requestTokenClient == null) {
                requestTokenClient = new RequestTokenClient(LoginActivity.this, code);
                requestTokenClient.setOnResultCallback(new BaseClient.OnResultCallback<Token>() {
                    @Override
                    public void onResponseOk(Token token, Response r) {
                        if (token.access_token != null) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            endAccess(token.access_token, token.scope);
                        } else if (token.error != null) {
                            Toast.makeText(LoginActivity.this, token.error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFail(RetrofitError error) {
                        ErrorHandler.onRetrofitError(LoginActivity.this, "WebViewCustomClient", error);
                    }
                });
                requestTokenClient.execute();
            }
        } else if (fromAccounts) {
            login();
        } else if (!fromApp && accounts != null && accounts.length > 0) {
            openMain();
        }
    }

    private void login() {
        String url = OAUTH_URL + "?client_id=" + ApiConstants.CLIENT_ID;

        url = url + "&scope=gist,user,notifications,repo";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }

    private void enableCreateGist(boolean b) {
        int flag = b ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        ComponentName componentName = new ComponentName(this, Interceptor.class);
        getPackageManager().setComponentEnabledSetting(componentName, flag, PackageManager.DONT_KILL_APP);
    }

    private void openMain() {
        enableCreateGist(true);
        MainActivity.startActivity(LoginActivity.this);
        finish();
    }

    private void endAccess(String accessToken, String scope) {
        this.accessToken = accessToken;
        this.scope = scope;

        GetAuthUserClient userClient = new GetAuthUserClient(this, accessToken);
        userClient.setOnResultCallback(this);
        userClient.execute();
    }

    @Override
    public void onResponseOk(User user, Response r) {
        Account account = new Account(user.login, getString(R.string.account_type));
        Bundle userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url, scope);
        userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        AccountManager accountManager = AccountManager.get(this);

        accountManager.addAccountExplicitly(account, null, userData);
        accountManager.setAuthToken(account, getString(R.string.account_type), accessToken);

        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        setAccountAuthenticatorResult(result);

        if (getIntent().getBooleanExtra(ADDING_FROM_ACCOUNTS, false)) {
            Intent intent = new Intent();
            intent.putExtras(result);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            openMain();
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void showDialog() {
        try {
            progressDialog = new SpotsDialog(this, R.style.SpotDialog_Login);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
