package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.Interceptor;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.adapter.AccountsAdapter;
import com.android.vending.billing.IInAppBillingService;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AccountAuthenticatorActivity implements BaseClient.OnResultCallback<User> {

    public static final String ARG_ACCOUNT_TYPE = "ARG_ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "ARG_AUTH_TYPE";
    public static final String ADDING_FROM_ACCOUNTS = "ADDING_FROM_ACCOUNTS";
    public static final String ADDING_FROM_APP = "ADDING_FROM_APP";
    private static final String SKU_MULTI_ACCOUNT = "com.alorma.github.multiaccount";

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private SpotsDialog progressDialog;
    private String accessToken;
    private String scope;
    private RequestTokenClient requestTokenClient;
    private boolean fromAccounts;

    private IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    private Account[] accounts;
    private String purchaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enableCreateGist(false);

        AccountManager accountManager = AccountManager.get(this);

        accounts = accountManager.getAccountsByType(getString(R.string.account_type));

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        boolean fromLogin = getIntent().getData() != null && getIntent().getData().getScheme().equals("gitskarios");
        fromAccounts = getIntent().getBooleanExtra(ADDING_FROM_ACCOUNTS, false);
        final boolean fromApp = getIntent().getBooleanExtra(ADDING_FROM_APP, false);

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromApp) {
                    openMain();
                } else {
                    finish();
                }
            }
        });

        AccountsAdapter adapter = new AccountsAdapter(this, accounts);
        recyclerView.setAdapter(adapter);

        if (fromLogin) {
            findViewById(R.id.login).setEnabled(false);
            showDialog();
            Uri uri = getIntent().getData();
            String code = uri.getQueryParameter("code");
            if (requestTokenClient == null) {
                requestTokenClient = new RequestTokenClient(LoginActivity.this, code);
                requestTokenClient.setOnResultCallback(new BaseClient.OnResultCallback<Token>() {
                    @Override
                    public void onResponseOk(Token token, Response r) {
                        if (token.access_token != null) {
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

        if (accounts != null && accounts.length > 0) {
            SKUTask task = new SKUTask();
            task.execute(SKU_MULTI_ACCOUNT);
        } else {
            openExternalLogin();
        }
    }

    private void openExternalLogin() {
        String url = OAUTH_URL + "?client_id=" + ApiConstants.CLIENT_ID;

        url = url + "&scope=gist,user,notifications,repo";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        finish();
    }

    private class SKUTask extends AsyncTask<String, Void, Bundle> {

        @Override
        protected Bundle doInBackground(String... strings) {
            ArrayList<String> skuList = new ArrayList<>();
            Collections.addAll(skuList, strings);
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

            try {
                return mService.getPurchases(3, getPackageName(), "inapp", null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle ownedItems) {
            super.onPostExecute(ownedItems);
            if (ownedItems != null) {
                int response = ownedItems.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                    if (ownedSkus.size() == 0) {
                        showDialogBuyMultiAccount();
                    } else {
                        openExternalLogin();
                    }
                }
            }
        }
    }

    private void showDialogBuyMultiAccount() {
        try {
            purchaseId = UUID.randomUUID().toString();
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    SKU_MULTI_ACCOUNT, "inapp", purchaseId);
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            startIntentSenderForResult(pendingIntent.getIntentSender(),
                    1001, new Intent(), 0, 0, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String developerPayload = jo.getString("developerPayload");
                    if (developerPayload.equals(purchaseId) && SKU_MULTI_ACCOUNT.equals(sku)) {
                        openExternalLogin();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }
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

        progressDialog.setMessage(getString(R.string.loading_user));

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

        openMain();
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}
