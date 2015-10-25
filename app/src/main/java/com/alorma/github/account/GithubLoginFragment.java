package com.alorma.github.account;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.security.GithubDeveloperCredentials;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.ui.activity.AccountsFragmentManager;
import com.alorma.github.ui.activity.PurchasesFragment;
import com.alorma.gitskarios.core.client.BaseClient;
import java.util.ArrayList;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GithubLoginFragment extends Fragment {

    public static final String SCOPES = "gist,user,notifications,repo,delete_repo";

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private RequestTokenClient requestTokenClient;

    private LoginCallback loginCallback;
    private AccountsFragmentManager accountsFragment;
    private PurchasesFragment purchasesFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountsFragment = new AccountsFragmentManager();
        getFragmentManager().beginTransaction().add(accountsFragment, "accountsFragment").commit();
        purchasesFragment = new PurchasesFragment();
        getFragmentManager().beginTransaction().add(purchasesFragment, "purchasesFragment").commit();
    }

    public boolean login() {
        if (accountsFragment.getAccounts().isEmpty()) {
            openExternalLogin();
            return true;
        } else if (accountsFragment.multipleAccountsAllowed()){
            openExternalLogin();
            return true;
        } else {
            purchasesFragment.checkSku(new PurchasesFragment.PurchasesCallback() {
                @Override
                public void onMultiAccountPurchaseResult(boolean multiAccountPurchased) {
                    if (multiAccountPurchased) {
                        openExternalLogin();
                    } else {
                        purchasesFragment.showDialogBuyMultiAccount();
                    }
                }
            });
            return false;
        }
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

    public void finishPurchase(int requestCode, int resultCode, Intent data) {
        purchasesFragment.finishPurchase(requestCode, resultCode, data, new PurchasesFragment.PurchasesCallback() {
            @Override
            public void onMultiAccountPurchaseResult(boolean multiAccountPurchased) {
                if (multiAccountPurchased) {
                    openExternalLogin();
                } else {
                    if (loginCallback != null){
                        loginCallback.loginNotAvailable();
                    }
                }
            }
        });
    }

    public interface LoginCallback {
        void endAccess(String accessToken);

        void onError(RetrofitError error);

        void loginNotAvailable();
    }
}
