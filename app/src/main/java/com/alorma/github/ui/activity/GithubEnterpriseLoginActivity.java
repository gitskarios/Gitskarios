package com.alorma.github.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.gitskarios.core.client.BaseClient;
import com.alorma.gitskarios.core.client.StoreCredentials;

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
    @Bind(R.id.enterpriseLogin)
    Button enterpriseLogin;
    private StoreCredentials credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_enterprise_login);
        ButterKnife.bind(this);


        enterpriseToken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enterpriseLogin.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.enterpriseGenerateToken)
    public void generateToken() {
        if (enterpriseUrl.length() > 0) {
            String url = enterpriseUrl.getText().toString() + "/settings/tokens";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
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
