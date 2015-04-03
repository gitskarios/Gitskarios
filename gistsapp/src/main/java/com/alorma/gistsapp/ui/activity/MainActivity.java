package com.alorma.gistsapp.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.alorma.gistsapp.R;
import com.alorma.gistsapp.ui.fragment.GistDetailFragment;
import com.alorma.gistsapp.ui.fragment.GistsFragment;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.security.StoreCredentials;
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
import java.util.TreeMap;

public class MainActivity extends ActionBarActivity implements GistsFragment.GistsFragmentListener {

    private static final String MOPUB_NATIVE_AD_UNIT_ID = "e4190202617f48f7ade91e512b33d598";

    private Toolbar toolbar;
    private AccountHeader.Result header;
    private Account selectedAccount;
    private HashMap<String, Account> accountMap = new HashMap<>();
    private Toolbar toolbarDetail;
    private GistsFragment gistsFragment;
    private boolean loadUserGists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarDetail = (Toolbar) findViewById(R.id.toolbarDetail);

        String gistId = null;
        String gistUser = null;
        boolean containsUser = false;
        boolean isAuthUser = false;

        Account[] accounts = AccountManager.get(this).getAccountsByType(getString(R.string.account_type));

        if (accounts.length > 0) {
            if ((Intent.ACTION_SEND.equals(getIntent().getAction())) || (Intent.ACTION_VIEW.equals(getIntent().getAction()))) {
                Uri uri = getIntent().getData();
                if (uri == null && getIntent().getStringExtra(Intent.EXTRA_TEXT) != null) {
                    try {
                        uri = Uri.parse(getIntent().getStringExtra(Intent.EXTRA_TEXT));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (uri != null) {
                    gistId = uri.getLastPathSegment();
                    if (uri.getPathSegments().size() > 1) {
                        containsUser = true;
                        gistUser = uri.getPathSegments().get(0);
                    }
                }
            }

            loadUserGists = true;

            if (gistUser != null && gistId != null && toolbarDetail != null) {
                loadUserGists = false;
                setTitle(getString(R.string.user_gists, gistUser));
                showGistsFragment(gistUser);
                showGistDetailFragment(gistId);
            } else if (gistId != null) {
                Intent launcherIntent = GistDetailActivity.createLauncherIntent(this, gistId);
                startActivity(launcherIntent);
                finish();
            }

            for (Account account : accounts) {
                if (account.name.equals(gistUser)) {
                    isAuthUser = true;
                    break;
                }
            }

            if (!containsUser || isAuthUser) {
                createDrawer();
            }
            if (accounts.length > 0) {
                selectAccount(accounts[0]);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Intent.EXTRA_INTENT, getIntent());
            startActivity(intent);
            finish();
        }

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
                })
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile iProfile) {
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

        if (loadUserGists) {
            setTitle(getString(R.string.user_gists, account.name));
            showGistsFragment(null);
        }
    }

    @Override
    public void onGistsRequest(Gist gist) {
        if (toolbarDetail != null) {
            showGistDetailFragment(gist.id);
        } else {
            Intent intent = GistDetailActivity.createLauncherIntent(this, gist.id);
            startActivity(intent);
        }
    }

    private void showGistsFragment(String username) {
        gistsFragment = GistsFragment.newInstance(username);
        gistsFragment.setGistsFragmentListener(this);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, gistsFragment);
        ft.commit();
    }

    private void showGistDetailFragment(String id) {
        final GistDetailFragment gistDetailFragment = GistDetailFragment.newInstance(id);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentDetail, gistDetailFragment);
        fragmentTransaction.commit();

        gistDetailFragment.setGistDetailListener(new GistDetailFragment.GistDetailListener() {
            @Override
            public void onGistLoaded(Gist gist) {
                TreeMap<String, GistFile> filesMap = new TreeMap<>(gist.files);
                GistFile firstFile = filesMap.firstEntry().getValue();
                toolbarDetail.setTitle(firstFile.filename);
                toolbarDetail.setSubtitle(getString(R.string.num_of_files, gist.files.size()));
                toolbarDetail.inflateMenu(gistDetailFragment.getMenuId());
                toolbarDetail.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return gistDetailFragment.onOptionsItemSelected(menuItem);
                    }
                });
            }
        });
    }
}
