package com.alorma.gistsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.gists.UserGistsClient;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements BaseClient.OnResultCallback<ListGists> {

    private Toolbar toolbar;
    private AccountHeader.Result header;
    private Account selectedAccount;
    private HashMap<String, Account> accountMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createDrawer();
    }

    private void createDrawer() {

        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(MainActivity.this).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {

            }
        });

        AccountHeader accountHeaderBuilder = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        if (iProfile.getIdentifier() != -1) {
                            Account account = accountMap.get(iProfile.getName());
                            selectAccount(account);
                            return false;
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra(LoginActivity.ADDING_FROM_APP, true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            return true;
                        }
                    }
                });

        header = accountHeaderBuilder.build();

        new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Account[] accounts = AccountManager.get(this).getAccountsByType(getString(R.string.account_type));
        if (accounts != null && accounts.length > 0) {
            if (header != null) {
                if (header.getProfiles() != null) {
                    ArrayList<IProfile> iProfiles = new ArrayList<>(header.getProfiles());
                    for (IProfile iProfile : iProfiles) {
                        header.removeProfile(iProfile);
                    }
                }

                for (int i = 0; i < accounts.length; i++) {
                    addAccountToHeader(accounts[i], i);
                }

                ProfileSettingDrawerItem itemAdd = new ProfileSettingDrawerItem().withName(getString(R.string.add_acount))
                        .withDescription(getString(R.string.add_account_description))
                        .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add)
                                .actionBarSize()
                                .paddingDp(5)
                                .colorRes(R.color.material_drawer_primary_text))
                        .withIdentifier(-1);

                header.addProfiles(itemAdd);
            }
            selectAccount(accounts[0]);
        }
    }

    private void addAccountToHeader(Account account, int i) {
        accountMap.put(account.name, account);
        String userAvatar = AccountsHelper.getUserAvatar(this, account);
        String userMail = AccountsHelper.getUserMail(this, account);
        String userName = AccountsHelper.getUserName(this, account);
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(account.name).withIcon(userAvatar)
                .withEmail(userMail != null ? userMail : userName).withIdentifier(i);
        header.addProfiles(profileDrawerItem);
    }

    private void selectAccount(Account account) {
        this.selectedAccount = account;
        StoreCredentials credentials = new StoreCredentials(MainActivity.this);
        credentials.clear();
        String authToken = AccountsHelper.getUserToken(this, account);

        credentials.storeToken(authToken);
        credentials.storeUsername(account.name);

        loadGists();
    }

    private void loadGists() {
        UserGistsClient gistsClient = new UserGistsClient(this);
        gistsClient.setOnResultCallback(this);
        gistsClient.execute();
    }

    @Override
    public void onResponseOk(ListGists gists, Response r) {
        Toast.makeText(this, "Gists: " + gists.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(RetrofitError error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}
