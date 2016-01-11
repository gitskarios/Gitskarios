package com.alorma.github.account;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.services.login.RequestTokenClient;
import com.alorma.github.ui.activity.AccountsManager;
import com.alorma.github.ui.activity.PurchasesFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GithubLoginFragment extends Fragment {

    public static final String SCOPES = "gist,user,notifications,repo,delete_repo";
    public static String ADVANCED_URL = "https://github.com/settings/tokens/new";
    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";

    private LoginCallback loginCallback;
    private AccountsManager accountsFragment;
    private PurchasesFragment purchasesFragment;
    private RequestTokenClient requestTokenClient;
    private boolean advanced;

    public GithubLoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountsFragment = new AccountsManager();
        purchasesFragment = new PurchasesFragment();
        getFragmentManager().beginTransaction().add(purchasesFragment, "purchasesFragment").commit();
    }

    public boolean login(Context context) {
        if (accountsFragment.getAccounts(context).isEmpty()) {
            openExternalLogin();
            return true;
        } else if (accountsFragment.multipleAccountsAllowed()) {
            openExternalLogin();
            return true;
        } else {
            purchasesFragment.checkSku(new PurchasesFragment.PurchasesCallback() {
                @Override
                public void onMultiAccountPurchaseResult(boolean multiAccountPurchased) {
                    if (multiAccountPurchased) {
                        openExternalLogin();
                    } else {
                        advanced = false;
                        purchasesFragment.showDialogBuyMultiAccount();
                    }
                }
            });
            return false;
        }
    }

    public boolean loginAdvanced(Context context) {
        if (accountsFragment.getAccounts(context).isEmpty()) {
            openAdvancedLogin(ADVANCED_URL);
            return true;
        } else if (accountsFragment.multipleAccountsAllowed()) {
            openAdvancedLogin(ADVANCED_URL);
            return true;
        } else {
            purchasesFragment.checkSku(new PurchasesFragment.PurchasesCallback() {
                @Override
                public void onMultiAccountPurchaseResult(boolean multiAccountPurchased) {
                    if (multiAccountPurchased) {
                        openAdvancedLogin(ADVANCED_URL);
                    } else {
                        advanced = true;
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

        new StoreCredentials(getActivity()).clear();
        if (requestTokenClient == null) {
            requestTokenClient = new RequestTokenClient(code,
                    BuildConfig.CLIENT_ID,
                    BuildConfig.CLIENT_SECRET,
                    BuildConfig.CLIENT_CALLBACK);

            requestTokenClient.observable().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Token>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (loginCallback != null) {
                                loginCallback.onError(e);
                            }
                        }

                        @Override
                        public void onNext(Token token) {
                            if (loginCallback != null && token.access_token != null) {
                                loginCallback.endAccess(token.access_token);
                            }
                        }
                    });
        }
    }

    private void openExternalLogin() {
        Uri callbackUri =
                Uri.EMPTY.buildUpon().scheme(getString(R.string.oauth_scheme)).authority("oauth").build();

        Uri uri = Uri.parse(OAUTH_URL)
                .buildUpon()
                .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                .appendQueryParameter("scope", SCOPES)
                .appendQueryParameter("redirect_uri", callbackUri.toString())
                .build();

        final List<ResolveInfo> browserList = getBrowserList();

        if (browserList != null && !browserList.isEmpty()) {
            final List<LabeledIntent> intentList = new ArrayList<>();

            for (final ResolveInfo resolveInfo : browserList) {
                final Intent newIntent = new Intent(Intent.ACTION_VIEW, uri);
                newIntent.setComponent(
                        new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));

                intentList.add(
                        new LabeledIntent(newIntent, resolveInfo.resolvePackageName, resolveInfo.labelRes,
                                resolveInfo.icon));
            }

            final Intent chooser =
                    Intent.createChooser(intentList.remove(0), "Choose your favorite browser");
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

            startActivity(chooser);
        } else {
            final Intent chooser = Intent.createChooser(new Intent(Intent.ACTION_VIEW, uri),
                    "Choose your favorite browser");

            startActivity(chooser);
        }
    }

    private void openAdvancedLogin(String url) {
        final List<ResolveInfo> browserList = getBrowserList();

        if (browserList != null && !browserList.isEmpty()) {
            final List<LabeledIntent> intentList = new ArrayList<>();

            for (final ResolveInfo resolveInfo : browserList) {
                final Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                newIntent.setComponent(
                        new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));

                intentList.add(
                        new LabeledIntent(newIntent, resolveInfo.resolvePackageName, resolveInfo.labelRes,
                                resolveInfo.icon));
            }

            final Intent chooser =
                    Intent.createChooser(intentList.remove(0), "Choose your favorite browser");
            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

            startActivity(chooser);
        } else {
            final Intent chooser = Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    ADVANCED_URL)),
                    "Choose your favorite browser");

            startActivity(chooser);
        }
    }

    private List<ResolveInfo> getBrowserList() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sometesturl.com"));

        return getActivity().getPackageManager().queryIntentActivities(intent, 0);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public void finishPurchase(int requestCode, int resultCode, Intent data) {
        purchasesFragment.finishPurchase(requestCode, resultCode, data,
                new PurchasesFragment.PurchasesCallback() {
                    @Override
                    public void onMultiAccountPurchaseResult(boolean multiAccountPurchased) {
                        if (multiAccountPurchased) {
                            if (advanced) {
                                openAdvancedLogin(ADVANCED_URL);
                            } else {
                                openExternalLogin();
                            }
                        } else {
                            if (loginCallback != null) {
                                loginCallback.loginNotAvailable();
                            }
                        }
                    }
                });
    }

    public interface LoginCallback {
        void onError(Throwable error);

        void loginNotAvailable();

        void endAccess(String access_token);
    }
}
