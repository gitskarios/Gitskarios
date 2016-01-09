package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.os.Bundle;

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
