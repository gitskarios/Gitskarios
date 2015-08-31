package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.alorma.github.BuildConfig;
import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.basesdk.client.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.activity.gists.GistsMainActivity;
import com.alorma.github.ui.fragment.ChangelogDialogSupport;
import com.alorma.github.ui.fragment.donate.DonateFragment;
import com.alorma.github.ui.fragment.events.EventsListFragment;
import com.alorma.github.ui.fragment.menu.OnMenuItemSelectedListener;
import com.alorma.github.ui.fragment.repos.GeneralReposFragment;
import com.alorma.github.ui.view.GitskariosProfileDrawerItem;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends BaseActivity implements OnMenuItemSelectedListener {

    private GeneralReposFragment reposFragment;
    private EventsListFragment eventsFragment;

    private AccountHeader headerResult;
    private HashMap<String, Account> accountMap;
    private Account selectedAccount;
    private Fragment lastUsedFragment;
    private Drawer resultDrawer;
    private ChangelogDialogSupport dialog;
    private int notificationsSizeCount = 0;
    private DonateFragment donateFragment;


    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donateFragment = new DonateFragment();

        getSupportFragmentManager().beginTransaction().add(donateFragment, "donate").commit();

        setContentView(R.layout.generic_toolbar);

        createDrawer();

        checkChangeLog();
    }

    private boolean checkChangeLog() {
        int currentVersion = BuildConfig.VERSION_CODE;
        GitskariosSettings settings = new GitskariosSettings(this);
        int version = settings.getVersion(0);

        if (currentVersion > version) {
            settings.saveVersion(currentVersion);
            dialog = ChangelogDialogSupport.create();
            dialog.show(getSupportFragmentManager(), "changelog");
        }

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

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
                            .actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text))
                    .withIdentifier(-1);

            headerResult.addProfiles(itemAdd);

            selectAccount(accounts[0]);
        }
    }

    private void addAccountToHeader(Account account, int i) {
        accountMap.put(account.name, account);
        String userAvatar = AccountsHelper.getUserAvatar(this, account);
        ProfileDrawerItem profileDrawerItem = new GitskariosProfileDrawerItem()
                .withName(account.name)
                .withIcon(userAvatar)
                .withEmail(getUserExtraName(account))
                .withIdentifier(i);
        headerResult.addProfiles(profileDrawerItem);
    }

    private String getUserExtraName(Account account) {
        String accountName = account.name;
        String userMail = AccountsHelper.getUserMail(this, account);
        String userName = AccountsHelper.getUserName(this, account);
        if (!TextUtils.isEmpty(userMail)) {
            return userMail;
        } else if (!TextUtils.isEmpty(userName)) {
            return userName;
        }
        return accountName;
    }

    private void createDrawer() {

        int iconColor = getResources().getColor(R.color.icons);

        buildHeader();
        //Now create your drawer and pass the AccountHeader.Result
        DrawerBuilder drawer = new DrawerBuilder();
        drawer.withActivity(this);
        drawer.withToolbar(getToolbar());
        drawer.withAccountHeader(headerResult);
        drawer.addDrawerItems(
                new PrimaryDrawerItem().withName(R.string.menu_events).withIcon(Octicons.Icon.oct_calendar).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_events),
                new PrimaryDrawerItem().withName(R.string.navigation_general_repositories).withIcon(Octicons.Icon.oct_repo).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_repositories),
                new PrimaryDrawerItem().withName(R.string.navigation_people).withIcon(Octicons.Icon.oct_person).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_people).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.navigation_gists).withIcon(Octicons.Icon.oct_gist).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_gists).withSelectable(false),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(R.string.navigation_settings).withIcon(Octicons.Icon.oct_gear).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_settings).withSelectable(false),
                new SecondaryDrawerItem().withName(R.string.support_development).withIcon(Octicons.Icon.oct_heart).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_support_development).withSelectable(false),
                new SecondaryDrawerItem().withName(R.string.navigation_about).withIcon(Octicons.Icon.oct_octoface).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_about).withSelectable(false),
                new SecondaryDrawerItem().withName(R.string.navigation_sign_out).withIcon(Octicons.Icon.oct_sign_out).withIconColor(iconColor).withIdentifier(R.id.nav_drawer_sign_out).withSelectable(false)
        );
        drawer.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                int identifier = drawerItem.getIdentifier();
                switch (identifier) {
                    case R.id.nav_drawer_events:
                        onUserEventsSelected();
                        break;
                    case R.id.nav_drawer_repositories:
                        onReposSelected();
                        break;
                    case R.id.nav_drawer_people:
                        onPeopleSelected();
                        break;
                    case R.id.nav_drawer_gists:
                        onGistsSelected();
                        break;
                    case R.id.nav_drawer_settings:
                        onSettingsSelected();
                        break;
                    case R.id.nav_drawer_about:
                        onAboutSelected();
                        break;
                    case R.id.nav_drawer_sign_out:
                        signOut();
                        break;
                    case R.id.nav_drawer_support_development:
                        if (donateFragment != null) {
                            donateFragment.launchDonate();
                        }
                        break;
                }

                return false;
            }
        });
        drawer.withSelectedItemByPosition(0);
        resultDrawer = drawer.build();
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

            @Override
            public Drawable placeholder(Context context) {
                return new IconicsDrawable(context, Octicons.Icon.oct_octoface);
            }
        });

        AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary_dark);

        headerBuilder.withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
            @Override
            public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                if (iProfile.getIdentifier() != -1) {
                    Account account = accountMap.get(iProfile.getName().getText());
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
        StoreCredentials credentials = new StoreCredentials(MainActivity.this);
        credentials.clear();
        String authToken = AccountsHelper.getUserToken(this, account);

        credentials.storeToken(authToken);
        credentials.storeUsername(account.name);

        if (changingUser) {
            lastUsedFragment = null;
            clearFragments();
        }

        if (lastUsedFragment != null) {
            setFragment(lastUsedFragment);
        } else {
            onUserEventsSelected();
        }

    }

    private void clearFragments() {
        reposFragment = null;
        eventsFragment = null;


        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private boolean hasInflated = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (notificationsSizeCount > 0) {
            BadgeStyle badgeStyle = new BadgeStyle(BadgeStyle.Style.DEFAULT,
                    R.layout.menu_badge,
                    getResources().getColor(R.color.accent),
                    R.color.accent_dark,
                    Color.WHITE, getResources().getDimensionPixelOffset(R.dimen.gapMicro));

            ActionItemBadge.update(this, menu.findItem(R.id.action_notifications), Octicons.Icon.oct_inbox, badgeStyle, notificationsSizeCount);
        } else {
            ActionItemBadge.hide(menu.findItem(R.id.action_notifications));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotifications();
    }

    private void checkNotifications() {
        GetNotificationsClient client = new GetNotificationsClient(this);
        client.setOnResultCallback(new BaseClient.OnResultCallback<List<Notification>>() {
            @Override
            public void onResponseOk(List<Notification> notifications, Response r) {
                if (notifications != null) {
                    notificationsSizeCount = notifications.size();
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        client.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Intent intent = SearchActivity.launchIntent(this);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_notifications) {
            Intent intent = NotificationsActivity.launchIntent(this);
            startActivity(intent);
        }

        return false;
    }

    private void setFragment(Fragment fragment) {
        setFragment(fragment, true);
    }

    private void setFragment(Fragment fragment, boolean addToBackStack) {
        this.lastUsedFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        clearFragments();

        if (reposFragment == null) {
            reposFragment = GeneralReposFragment.newInstance();
        }

        setFragment(reposFragment, false);
        return true;
    }

    @Override
    public boolean onPeopleSelected() {
        setFragment(GeneralPeopleFragment.newInstance(), false);
        return false;
    }

    public boolean onGistsSelected() {
        Intent intent = GistsMainActivity.createLauncherIntent(this);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onUserEventsSelected() {
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
            GitskariosSettings settings = new GitskariosSettings(this);
            settings.saveVersion(0);
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
        new LibsBuilder()
                //Pass the fields of your application to the lib so it can find all external lib information
                .withFields(R.string.class.getFields())
                .withActivityTitle(getString(R.string.app_name))
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTheme(R.style.AppTheme)
                .start(this);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (resultDrawer != null && resultDrawer.isDrawerOpen()) {
            resultDrawer.closeDrawer();
        } else {
            if (lastUsedFragment instanceof EventsListFragment) {
                finish();
            } else if (lastUsedFragment instanceof GeneralReposFragment && resultDrawer != null) {
                resultDrawer.setSelection(0);
            } else {
                clearFragments();
                onUserEventsSelected();
            }
        }
    }

    @Override
    public void onStop() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

}
