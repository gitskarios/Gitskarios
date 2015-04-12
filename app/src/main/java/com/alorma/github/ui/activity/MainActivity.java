package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.activity.gists.GistsMainActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import com.alorma.github.ui.fragment.events.EventsListFragment;
import com.alorma.github.ui.fragment.menu.OnMenuItemSelectedListener;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.alorma.github.ui.view.NotificationsActionProvider;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements OnMenuItemSelectedListener,
        NotificationsActionProvider.OnNotificationListener {

    private ReposFragment reposFragment;
    private StarredReposFragment starredFragment;
    private WatchedReposFragment watchedFragment;
    private EventsListFragment eventsFragment;
    private NotificationsActionProvider notificationProvider;

    @Inject
    Bus bus;

    private AccountHeader.Result headerResult;
    private StoreCredentials credentials;
    private Drawer.Result result;
    private HashMap<String, Account> accountMap;
    private Account selectedAccount;
    private Fragment lastUsedFragment;
    private NotificationsFragment notificationsFragment;

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GitskariosApplication.get(this).inject(this);

        setContentView(R.layout.generic_toolbar);

        createDrawer();


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("TITLE")) {
                setTitle(savedInstanceState.getString("TITLE"));
            }
        } else {
            setTitle(R.string.navigation_repos);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));

        if (accounts != null && accounts.length > 0) {
            if (headerResult != null) {
                if (headerResult.getProfiles() != null) {
                    ArrayList<IProfile> iProfiles = new ArrayList<>(headerResult.getProfiles());
                    for (IProfile iProfile : iProfiles) {
                        headerResult.removeProfile(iProfile);
                    }
                }
            }
            accountMap = new HashMap<>();
            int i = 0;
            if (selectedAccount != null) {
                addAccountToHeader(selectedAccount, i++);
            }
            for (Account account : accounts) {
                if (selectedAccount == null || !selectedAccount.name.equals(account.name)) {
                    addAccountToHeader(account, i++);
                }
            }

            ProfileSettingDrawerItem itemAdd = new ProfileSettingDrawerItem()
                    .withName(getString(R.string.add_account))
                    .withDescription(getString(R.string.add_account_description))
                    .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add)
                            .actionBarSize().paddingDp(5).colorRes(R.color.material_drawer_primary_text))
                    .withIdentifier(-1);

            headerResult.addProfiles(itemAdd);

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
        headerResult.addProfiles(profileDrawerItem);
    }

    @Override
    protected void onPause() {
        if (notificationProvider != null) {
            try {
                bus.unregister(notificationProvider);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        bus.unregister(this);
        super.onPause();
    }

    private void createDrawer() {

        buildHeader();
        //Now create your drawer and pass the AccountHeader.Result
        result = new Drawer()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_events).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_calendar)).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.navigation_repos).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_repo)).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.navigation_starred_repos).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_star)).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.navigation_watched_repos).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_eye)).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.navigation_people).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_person)).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.navigation_gists).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_gist)).withIdentifier(5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.navigation_settings).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_settings)).withIdentifier(10),
                        new SecondaryDrawerItem().withName(R.string.navigation_sign_out).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_sign_out)).withIdentifier(11)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:
                                onUserEventsSelected();
                                break;
                            case 1:
                                onReposSelected();
                                break;
                            case 2:
                                onStarredSelected();
                                break;
                            case 3:
                                onWatchedSelected();
                                break;
                            case 4:
                                onPeopleSelected();
                                break;
                            case 5:
                                onGistsSelected();
                                break;
                            case 7:
                                onSettingsSelected();
                                break;
                            case 8:
                                signOut();
                                break;
                        }
                    }
                })
                .withSelectedItem(1)
                .build();
    }

    private void buildHeader() {

        // Create the AccountHeader

        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                ImageLoader.getInstance().displayImage(uri.toString(), imageView);
            }

            @Override
            public void cancel(ImageView imageView) {

            }
        });

        AccountHeader headerBuilder = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.color.repos_primary_dark);

        headerBuilder.withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
            @Override
            public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                if (iProfile.getIdentifier() != -1) {
                    Account account = accountMap.get(iProfile.getName());
                    if (selectedAccount != null) {
                        if (account.name.equals(selectedAccount.name)) {
                            User user = new User();
                            user.login = account.name;
                            Intent launcherIntent = ProfileActivity.createLauncherIntent(MainActivity.this);
                            startActivity(launcherIntent);
                        } else {
                            selectAccount(account);
                        }
                    } else {
                        selectAccount(account);
                    }
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

        headerResult = headerBuilder.build();
    }

    private void selectAccount(final Account account) {
        boolean changingUser = selectedAccount != null && !selectedAccount.name.equals(account.name);
        this.selectedAccount = account;
        credentials = new StoreCredentials(MainActivity.this);
        credentials.clear();
        String authToken = AccountsHelper.getUserToken(this, account);

        credentials.storeToken(authToken);
        credentials.storeUsername(account.name);

        if (lastUsedFragment != null && !changingUser) {
            setFragment(lastUsedFragment);
        } else {
            clearFragments();
            onReposSelected();
        }
    }

    private void clearFragments() {
        reposFragment = null;
        starredFragment = null;
        watchedFragment = null;
        eventsFragment = null;

        getFragmentManager().popBackStack(FragmentManager.POP_BACK_STACK_INCLUSIVE, 0);
        invalidateOptionsMenu();
    }

    private Drawable getGithubDrawable(GithubIconify.IconValue icon) {
        int iconColor = getResources().getColor(R.color.repos_icons);
        return new GithubIconDrawable(this, icon).color(iconColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (getToolbar() != null) {
            getToolbar().inflateMenu(R.menu.main_menu);

            MenuItem notificationsItem = menu.findItem(R.id.action_notifications);

            notificationProvider = (NotificationsActionProvider) MenuItemCompat.getActionProvider(notificationsItem);

            if (notificationProvider != null) {
                notificationProvider.setOnNotificationListener(this);
            }

            bus.register(notificationProvider);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Intent intent = SearchActivity.launchIntent(this);
            startActivity(intent);
        }

        return true;
    }

    private void setFragment(Fragment fragment) {
        setFragment(fragment, false);
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
        this.lastUsedFragment = fragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public boolean onProfileSelected() {
        Intent launcherIntent = ProfileActivity.createLauncherIntent(this);
        startActivity(launcherIntent);
        return false;
    }

    @Override
    public boolean onReposSelected() {
        setTitle(R.string.navigation_repos);
        if (reposFragment == null) {
            reposFragment = ReposFragment.newInstance();
        }

        setFragment(reposFragment);
        return true;
    }

    @Override
    public boolean onStarredSelected() {
        setTitle(R.string.navigation_starred_repos);
        if (starredFragment == null) {
            starredFragment = StarredReposFragment.newInstance();
        }

        setFragment(starredFragment);
        return true;
    }

    @Override
    public boolean onWatchedSelected() {
        setTitle(R.string.navigation_watched_repos);
        if (watchedFragment == null) {
            watchedFragment = WatchedReposFragment.newInstance();
        }

        setFragment(watchedFragment);
        return true;
    }

    @Override
    public boolean onPeopleSelected() {
        Intent intent = PeopleActivity.launchIntent(this);
        startActivity(intent);
        return false;
    }

    public void onGistsSelected() {
        Intent intent = GistsMainActivity.createLauncherIntent(this);
        startActivity(intent);
    }

    @Override
    public boolean onUserEventsSelected() {
        setTitle(R.string.menu_events);
        String user = new StoreCredentials(this).getUserName();
        if (user != null) {
            if (eventsFragment == null) {
                eventsFragment = EventsListFragment.newInstance(user);
            }
        }
        setFragment(eventsFragment);
        return true;
    }

    @Override
    public boolean onSettingsSelected() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return false;
    }

    public void signOut() {
        if (selectedAccount != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                            if (accountManagerFuture.isDone()) {

                                StoreCredentials storeCredentials = new StoreCredentials(MainActivity.this);
                                storeCredentials.clear();

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                    };
                    AccountManager.get(this).removeAccount(selectedAccount, this, callback, new Handler());
                } else {
                    AccountManagerCallback<Boolean> callback = new AccountManagerCallback<Boolean>() {
                        @Override
                        public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                            if (accountManagerFuture.isDone()) {

                                StoreCredentials storeCredentials = new StoreCredentials(MainActivity.this);
                                storeCredentials.clear();

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                    };
                    AccountManager.get(this).removeAccount(selectedAccount, callback, new Handler());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onAboutSelected() {
        Intent intent = AboutActivity.launchIntent(this);
        startActivity(intent);
        return false;
    }

    @Override
    public void onNotificationRequested() {
        setTitle(R.string.notifications);
        if (notificationsFragment == null) {
            notificationsFragment = NotificationsFragment.newInstance();
        }
        setFragment(notificationsFragment, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("TITLE", getToolbar().getTitle().toString());
        }
    }
}
