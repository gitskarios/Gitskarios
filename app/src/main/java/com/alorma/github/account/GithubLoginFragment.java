package com.alorma.github.account;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import com.alorma.github.ui.activity.AccountsManager;
import com.alorma.github.ui.activity.PurchasesFragment;
import java.util.ArrayList;
import java.util.List;

public class GithubLoginFragment extends Fragment {

  public static String URL = "https://github.com/settings/tokens/new";

  private LoginCallback loginCallback;
  private AccountsManager accountsFragment;
  private PurchasesFragment purchasesFragment;

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
            purchasesFragment.showDialogBuyMultiAccount();
          }
        }
      });
      return false;
    }
  }

  private void openExternalLogin() {
    final List<ResolveInfo> browserList = getBrowserList();

    if (browserList != null && !browserList.isEmpty()) {
      final List<LabeledIntent> intentList = new ArrayList<>();

      for (final ResolveInfo resolveInfo : browserList) {
        final Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
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
      final Intent chooser = Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)),
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
              openExternalLogin();
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
  }
}
