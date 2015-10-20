package com.alorma.github.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.security.GithubDeveloperCredentials;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.gitskarios.core.client.BaseClient;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.alorma.gitskarios.core.client.credentials.SimpleDeveloperCredentialsProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/10/2015.
 */
public class GithubEnterpriseLoginActivity extends BackActivity implements BaseClient.OnResultCallback<User> {

    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_ENTERPRISE_TOKEN = "EXTRA_ENTERPRISE_TOKEN";
    public static final String EXTRA_ENTERPRISE_URL = "EXTRA_ENTERPRISE_URL";

    @Bind(R.id.enterpriseUrl)
    EditText enterpriseUrl;
    @Bind(R.id.enterpriseToken)
    EditText enterpriseToken;
    private StoreCredentials credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_enterprise_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.enterpriseLogin)
    public void onLogin() {
        if (enterpriseUrl.length() > 0 && enterpriseToken.length() > 0) {
            credentials = new StoreCredentials(this);
            credentials.storeToken(enterpriseToken.getText().toString());
            credentials.storeUrl(enterpriseUrl.getText().toString());

            GetAuthUserClient requestClient = new GetAuthUserClient(this, null);
            requestClient.setOnResultCallback(this);
            requestClient.execute();
        }
    }

    @Override
    public void onResponseOk(User user, Response r) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_USER, user);
        bundle.putString(EXTRA_ENTERPRISE_URL, credentials.getUrl());
        bundle.putString(EXTRA_ENTERPRISE_TOKEN, credentials.token());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFail(RetrofitError error) {
        Toast.makeText(GithubEnterpriseLoginActivity.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
    }
}
