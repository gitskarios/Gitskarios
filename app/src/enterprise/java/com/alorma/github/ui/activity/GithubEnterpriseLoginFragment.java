package com.alorma.github.ui.activity;

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
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.services.login.RequestTokenClient;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class GithubEnterpriseLoginFragment extends Fragment {

  private LoginCallback loginCallback;
  private AccountsManager accountsFragment;
  private boolean advanced;

  public GithubEnterpriseLoginFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    accountsFragment = new AccountsManager();
  }

  public interface LoginCallback {
    void onError(Throwable error);

    void loginNotAvailable();

    void endAccess(String access_token);
  }
}
