package com.alorma.github.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.security.GithubDeveloperCredentials;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.gitskarios.core.client.BaseClient;
import java.util.ArrayList;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GithubLoginActivity extends Fragment {

    public static final String SCOPES = "gist,user,notifications,repo,delete_repo";
    public static final String EXTRA_ACCESS_TOKEN = "EXTRA_ACCESS_TOKEN";

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private RequestTokenClient requestTokenClient;

    private LoginCallback loginCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openExternalLogin();
    }

    public void onNewIntent(Intent intent) {
        if (intent != null && intent.getData() != null && intent.getData().getScheme() != null) {
            if (intent.getData().getScheme().equals(getString(R.string.oauth_scheme))) {
                finishLogin(intent.getData());
            }
        }
    }

    private void finishLogin(Uri uri) {
        String code = uri.getQueryParameter("code");

        if (requestTokenClient == null) {
            requestTokenClient = new RequestTokenClient(getActivity(), code);
            requestTokenClient.setOnResultCallback(new BaseClient.OnResultCallback<Token>() {
                @Override
                public void onResponseOk(Token token, Response r) {
                    if (loginCallback != null && token.access_token != null) {
                        loginCallback.endAccess(token.access_token);
                    }
                }

                @Override
                public void onFail(RetrofitError error) {
                    if (loginCallback != null) {
                        loginCallback.onError(error);
                    }
                }
            });
            requestTokenClient.execute();
        }
    }

    private void openExternalLogin() {
        String url = String.format("%s?client_id=%s&scope=" + SCOPES, OAUTH_URL,
            GithubDeveloperCredentials.getInstance().getProvider().getApiClient());

        Uri callbackUri = Uri.EMPTY.buildUpon().scheme(getString(R.string.oauth_scheme)).authority("oauth").build();

        url = Uri.parse(url).buildUpon().appendQueryParameter("redirect_uri", callbackUri.toString()).build().toString();

        final List<ResolveInfo> browserList = getBrowserList();

        final List<LabeledIntent> intentList = new ArrayList<>();

        for (final ResolveInfo resolveInfo : browserList) {
            final Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            newIntent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));

            intentList.add(new LabeledIntent(newIntent, resolveInfo.resolvePackageName, resolveInfo.labelRes, resolveInfo.icon));
        }

        final Intent chooser = Intent.createChooser(intentList.remove(0), "Choose your favorite browser");
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

        startActivity(chooser);
    }

    private List<ResolveInfo> getBrowserList() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sometesturl.com"));

        return getActivity().getPackageManager().queryIntentActivities(intent, 0);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public interface LoginCallback {
        void endAccess(String accessToken);

        void onError(RetrofitError error);
    }

    /*
    private void addAccount(User user) {
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

        checkAndEnableSyncAdapter(account);

        setResult(RESULT_OK);
        finish();
    }

    private void checkAndEnableSyncAdapter(Account account) {
        ContentResolver.setIsSyncable(account, getString(R.string.account_type), ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE);
        if (ContentResolver.getSyncAutomatically(account, getString(R.string.account_type))) {
            ContentResolver.addPeriodicSync(account, getString(R.string.account_type), Bundle.EMPTY, 1800);
            ContentResolver.setSyncAutomatically(account, getString(R.string.account_type), true);
        }
    }
    */
}
