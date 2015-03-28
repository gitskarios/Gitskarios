package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import com.alorma.github.ui.fragment.events.EventsListFragment;
import com.alorma.github.ui.fragment.menu.MenuFragment;
import com.alorma.github.ui.fragment.menu.MenuItem;
import com.alorma.github.ui.fragment.repos.ReposFragment;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;
import com.alorma.github.ui.fragment.search.SearchReposFragment;
import com.alorma.github.ui.view.NotificationsActionProvider;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.bumptech.glide.Glide;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements MenuFragment.OnMenuItemSelectedListener,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        NotificationsActionProvider.OnNotificationListener {

    private ReposFragment reposFragment;
    private StarredReposFragment starredFragment;
    private WatchedReposFragment watchedFragment;
    private EventsListFragment eventsFragment;
    private SearchReposFragment searchReposFragment;
    private SearchView searchView;
    private NotificationsActionProvider notificationProvider;
    private Handler handler = new Handler();

    @Inject
    Bus bus;

    private AccountHeader.Result headerResult;
    private StoreCredentials credentials;

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GitskariosApplication.get(this).inject(this);

        setContentView(R.layout.activity_main);

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));

        if (accounts != null && accounts.length > 0) {

            createDrawer(accounts);

            selectAccount(accounts[0]);

            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey("TITLE")) {
                    setTitle(savedInstanceState.getString("TITLE"));
                }
            } else {
                setTitle(R.string.navigation_repos);
            }
        } else {
            signOut();
        }
    }

    private void createDrawer(Account[] accounts) {
        final Map<String, Account> accountMap = new HashMap<>();
        // Create the AccountHeader

        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(MainActivity.this).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {

            }
        });

        AccountHeader headerBuilder = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header);

        AccountManager accountManager = AccountManager.get(this);
        for (Account account : accounts) {
            accountMap.put(account.name, account);
            String userAvatar = accountManager.getUserData(account, LoginActivity.USER_PIC);
            String userMail = accountManager.getUserData(account, LoginActivity.USER_MAIL);
            ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(account.name).withIcon(userAvatar).withEmail(userMail);
            headerBuilder.addProfiles(profileDrawerItem);
        }

        ProfileSettingDrawerItem itemAdd = new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBarSize().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(15);

        headerBuilder.addProfiles(itemAdd);

        headerBuilder.withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
            @Override
            public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                Account account = accountMap.get(iProfile.getName());
                selectAccount(account);
                return false;
            }
        });

        headerResult = headerBuilder.build();

        //Now create your drawer and pass the AccountHeader.Result
        new Drawer()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_events).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_calendar)).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.navigation_repos).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_repo)).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.navigation_starred_repos).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_star)).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.navigation_watched_repos).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_eye)).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.navigation_people).withIcon(getGithubDrawable(GithubIconify.IconValue.octicon_person)).withIdentifier(4),
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
                            case 6:
                                onSettingsSelected();
                                break;
                            case 7:
                                signOut();
                                break;
                        }
                    }
                })
                .withSelectedItem(1)
                .build();
    }

    private void selectAccount(final Account account) {
        clearFragments();
        credentials = new StoreCredentials(MainActivity.this);
        credentials.clear();

        AccountManager manager = AccountManager.get(MainActivity.this);
        String authToken = manager.getUserData(account, AccountManager.KEY_AUTHTOKEN);

        credentials.storeToken(authToken);
        credentials.storeUsername(account.name);

        onReposSelected();
    }

    private void clearFragments() {
        reposFragment = null;
        starredFragment = null;
        watchedFragment = null;
        eventsFragment = null;
        searchReposFragment = null;

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

            android.view.MenuItem searchItem = menu.findItem(R.id.action_search);

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(android.view.MenuItem item) {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(android.view.MenuItem item) {
                    clearSearch();
                    return false;
                }
            });

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);

            android.view.MenuItem notificationsItem = menu.findItem(R.id.action_notifications);

            notificationProvider = (NotificationsActionProvider) MenuItemCompat.getActionProvider(notificationsItem);

            notificationProvider.setOnNotificationListener(this);

            bus.register(notificationProvider);

        }

        return true;
    }

    private void clearSearch() {
        if (searchReposFragment != null) {
            getFragmentManager().popBackStack();
            searchReposFragment = null;
        }

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        search(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public boolean onClose() {
        clearSearch();
        return false;
    }

    private void search(String query) {
        if (searchReposFragment != null) {
            searchReposFragment.setQuery(query);
        } else {
            searchReposFragment = SearchReposFragment.newInstance(query);
            setFragment(searchReposFragment, true);
        }
    }

    private void setFragment(Fragment fragment) {
        if (searchView != null) {
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
            }
        }
        setFragment(fragment, false);
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
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

    @Override
    public void onMenuItemSelected(@NonNull MenuItem item, boolean changeTitle) {
        if (changeTitle) {
            setTitle(item.text);
        }
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
        StoreCredentials credentials = new StoreCredentials(this);
        credentials.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_CLEAR, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onAboutSelected() {
        Intent intent = AboutActivity.launchIntent(this);
        startActivity(intent);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNotificationRequested() {
        setTitle(R.string.notifications);
        setFragment(NotificationsFragment.newInstance());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("TITLE", getToolbar().getTitle().toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        try {
            bus.unregister(notificationProvider);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        bus.unregister(this);
        super.onPause();
    }
}
