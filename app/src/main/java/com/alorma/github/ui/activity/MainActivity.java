package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
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
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements MenuFragment.OnMenuItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, NotificationsActionProvider.OnNotificationListener, BaseClient.OnResultCallback<User> {

    private ReposFragment reposFragment;
    private StarredReposFragment starredFragment;
    private WatchedReposFragment watchedFragment;
    private EventsListFragment eventsFragment;
    private SearchReposFragment searchReposFragment;
    private SearchView searchView;
    private NotificationsActionProvider notificationProvider;

    @Inject
    Bus bus;

    private GetAuthUserClient client;
    private User user;
    private AccountHeader.Result headerResult;

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GitskariosApplication.get(this).inject(this);

        setContentView(R.layout.activity_main);

        client = new GetAuthUserClient(this);
        client.setOnResultCallback(this);
        client.execute();

        createDrawer();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("TITLE")) {
                setTitle(savedInstanceState.getString("TITLE"));
            }
        } else {
            setTitle(R.string.navigation_repos);
            onReposSelected();
        }
    }

    @Override
    public void onResponseOk(User user, Response r) {
        this.user = user;
        if (headerResult != null) {
            ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                    .withName(user.name)
                    .withEmail(user.email);
            headerResult.addProfile(profileDrawerItem, 0);
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    private void createDrawer() {
        // Create the AccountHeader
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public void onProfileChanged(View view, IProfile profile) {
                    }
                })
                .build();

        //Now create your drawer and pass the AccountHeader.Result
        Drawer.Result result = new Drawer()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_events).withIcon(getGithubDawable(GithubIconify.IconValue.octicon_calendar)).withIdentifier(0),
                        new PrimaryDrawerItem().withName(R.string.navigation_repos).withIcon(getGithubDawable(GithubIconify.IconValue.octicon_repo)).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.navigation_starred_repos).withIcon(getGithubDawable(GithubIconify.IconValue.octicon_star)).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.navigation_watched_repos).withIcon(getGithubDawable(GithubIconify.IconValue.octicon_eye)).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.navigation_people).withIcon(getGithubDawable(GithubIconify.IconValue.octicon_person)).withIdentifier(4),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.navigation_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(10)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                setTitle(R.string.menu_events);
                                onUserEventsSelected();
                                break;
                            case 2:
                                setTitle(R.string.navigation_repos);
                                onReposSelected();
                                break;
                            case 3:
                                setTitle(R.string.navigation_starred_repos);
                                onStarredSelected();
                                break;
                            case 4:
                                setTitle(R.string.navigation_watched_repos);
                                onWatchedSelected();
                                break;
                            case 5:
                                onPeopleSelected();
                                break;
                            case 7:
                                onSettingsSelected();
                                break;
                        }
                    }
                })
                .withSelectedItem(1)
                .build();
    }

    private Drawable getGithubDawable(GithubIconify.IconValue icon) {
        return new GithubIconDrawable(this, icon);
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
        if (reposFragment == null) {
            reposFragment = ReposFragment.newInstance();
        }

        setFragment(reposFragment);
        return true;
    }

    @Override
    public boolean onStarredSelected() {
        if (starredFragment == null) {
            starredFragment = StarredReposFragment.newInstance();
        }

        setFragment(starredFragment);
        return true;
    }

    @Override
    public boolean onWatchedSelected() {
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
        GitskariosSettings settings = new GitskariosSettings(this);
        String user = settings.getAuthUser(null);
        if (user != null) {
            if (eventsFragment == null) {
                eventsFragment = EventsListFragment.newInstance(user);
            }
            setFragment(eventsFragment);
        }
        return true;
    }

    @Override
    public boolean onSettingsSelected() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return false;
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
