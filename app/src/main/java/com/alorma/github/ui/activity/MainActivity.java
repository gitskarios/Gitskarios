package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.alorma.github.AccountsHelper;
import com.alorma.github.BuildConfig;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.fragment.GeneralPeopleFragment;
import com.alorma.github.ui.fragment.donate.DonateFragment;
import com.alorma.github.ui.fragment.events.EventsListFragment;
import com.alorma.github.ui.fragment.gists.AuthUserGistsFragment;
import com.alorma.github.ui.fragment.gists.AuthUserStarredGistsFragment;
import com.alorma.github.ui.fragment.issues.GeneralIssuesListFragment;
import com.alorma.github.ui.fragment.repos.GeneralReposFragment;
import com.alorma.github.ui.utils.DrawerImage;
import com.alorma.github.utils.AccountUtils;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
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
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements AccountHeader.OnAccountHeaderListener {

  private static final int PROFILE_REQUEST_CODE = 555;
  private static final int REQUEST_INVITE = 121;

  private Account selectedAccount;
  private Fragment lastUsedFragment;
  private Drawer resultDrawer;
  private int notificationsSizeCount = 0;
  private DonateFragment donateFragment;

  public static void startActivity(Activity context) {
    Intent intent = new Intent(context, MainActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!FirebaseApp.getApps(this).isEmpty()) {
      FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    checkInvites();

    if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("rebirth", false)) {
      onSettingsSelected();
    }

    AccountsManager accountsFragment = new AccountsManager();
    List<Account> accounts = accountsFragment.getAccounts(this);

    if (accounts.isEmpty()) {
      Intent intent = new Intent(this, WelcomeActivity.class);
      startActivity(intent);
      finish();
    }

    donateFragment = new DonateFragment();

    if (getSupportFragmentManager() != null) {
      getSupportFragmentManager().beginTransaction().add(donateFragment, "donate").commit();
    }

    setContentView(R.layout.generic_toolbar_responsive);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    boolean changeLog = checkChangeLog();
    if (changeLog) {
      View view = findViewById(R.id.content);
      Snackbar.make(view, R.string.app_updated, Snackbar.LENGTH_LONG).setAction("Changelog", v -> {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://gitskarios.github.io")));
      }).show();
    }
  }

  private void checkInvites() {
    try {
      GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppInvite.API).enableAutoManage(this, null).build();
      // Check for App Invite invitations and launch deep-link activity if possible.
      // Requires that an Activity is registered in AndroidManifest.xml to handle
      // deep-link URLs.
      AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, true).setResultCallback(result -> {
        if (result.getStatus().isSuccess()) {
          // Extract information from the intent
          Intent intent = result.getInvitationIntent();
          String deepLink = AppInviteReferral.getDeepLink(intent);
          String invitationId = AppInviteReferral.getInvitationId(intent);

          // Because autoLaunchDeepLink = true we don't have to do anything
          // here, but we could set that to false and manually choose
          // an Activity to launch to handle the deep link here.
          // ...
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean checkChangeLog() {
    if (getSupportFragmentManager() != null) {
      int currentVersion = BuildConfig.VERSION_CODE;
      GitskariosSettings settings = new GitskariosSettings(this);
      int version = settings.getVersion(0);

      if (currentVersion > version) {
        settings.saveVersion(currentVersion);
        return true;
      }
    }

    return false;
  }

  @Override
  public void onStart() {
    super.onStart();

    if (resultDrawer == null) {
      List<Account> accounts = getAccounts();
      if (!accounts.isEmpty()) {
        selectedAccount = accounts.get(0);
        createDrawer();
        selectAccount(selectedAccount);
        onUserEventsSelected();
      }
    }
  }

  private String getUserExtraName(Account account) {
    String accountName = getNameFromAccount(account);
    String userMail = AccountsHelper.getUserMail(this, account);
    String userName = AccountsHelper.getUserName(this, account);
    if (!TextUtils.isEmpty(userMail)) {
      return userMail;
    } else if (!TextUtils.isEmpty(userName)) {
      return userName;
    }
    return accountName;
  }

  private String getNameFromAccount(Account account) {
    return new AccountUtils().getNameFromAccount(account.name);
  }

  private void createDrawer() {
    AccountHeader accountHeader = buildHeader();
    DrawerBuilder drawer = new DrawerBuilder();
    drawer.withActivity(this);
    drawer.withToolbar(getToolbar());
    drawer.withAccountHeader(accountHeader);

    List<IDrawerItem> userItems = getUserDrawerItems();

    for (IDrawerItem userItem : userItems) {
      drawer.addDrawerItems(userItem);
    }

    List<IDrawerItem> allProfilesItems = getAllProfilesItems();
    for (IDrawerItem allProfilesItem : allProfilesItems) {
      drawer.addStickyDrawerItems(allProfilesItem);
    }

    resultDrawer = drawer.build();
    resultDrawer.setSelection(R.id.nav_drawer_events);

    addDummyOrgs(accountHeader);
  }

  private void addDummyOrgs(AccountHeader accountHeader) {
    List<ProfileDrawerItem> organizationsProfiles = new ArrayList<>();
    organizationsProfiles.add(
        getOrganizationProfileDrawerItem("Gitskarios", "https://avatars1.githubusercontent.com/u/11989662?v=3&s=200"));
    organizationsProfiles.add(getOrganizationProfileDrawerItem("Catmobil", "https://avatars1.githubusercontent.com/u/6428632?v=3&s=200"));
    organizationsProfiles.add(getOrganizationProfileDrawerItem("scm-spain", "https://avatars0.githubusercontent.com/u/6713142?v=3&s=200"));
    organizationsProfiles.add(
        getOrganizationProfileDrawerItem("FineCinnamon", "https://avatars2.githubusercontent.com/u/11597937?v=3&s=200"));
    organizationsProfiles.add(
        getOrganizationProfileDrawerItem("SchibstedSpain", "https://avatars1.githubusercontent.com/u/18301133?v=3&s=200"));

    for (ProfileDrawerItem organizationsProfile : organizationsProfiles) {
      accountHeader.addProfiles(organizationsProfile);
    }
  }

  private List<IDrawerItem> getUserDrawerItems() {
    int iconColor = ContextCompat.getColor(this, R.color.icons);

    List<IDrawerItem> items = new ArrayList<>();

    items.add(new PrimaryDrawerItem().withName(R.string.menu_events)
        .withIcon(Octicons.Icon.oct_calendar)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_events)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onUserEventsSelected();
          return false;
        }));
    items.add(new PrimaryDrawerItem().withName(R.string.navigation_general_repositories)
        .withIcon(Octicons.Icon.oct_repo)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_repositories)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onReposSelected();
          return false;
        }));
    items.add(new PrimaryDrawerItem().withName(R.string.navigation_people)
        .withIcon(Octicons.Icon.oct_organization)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_people)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onPeopleSelected();
          return false;
        }));
    items.add(new PrimaryDrawerItem().withName(R.string.navigation_issues)
        .withIcon(Octicons.Icon.oct_issue_opened)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_issues)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onIssuesSelected();
          return false;
        }));
    items.add(new DividerDrawerItem());
    items.add(new PrimaryDrawerItem().withName(R.string.navigation_gists)
        .withIcon(Octicons.Icon.oct_gist)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_gists)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onGistsSelected();
          return false;
        }));
    items.add(new PrimaryDrawerItem().withName(R.string.navigation_gists_starred)
        .withIcon(Octicons.Icon.oct_star)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_gists_starred)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onStarredGistsSelected();
          return false;
        }));

    return items;
  }

  private List<IDrawerItem> getAllProfilesItems() {
    int iconColor = ContextCompat.getColor(this, R.color.icons);

    List<IDrawerItem> items = new ArrayList<>();
    items.add(new SecondaryDrawerItem().withName(R.string.menu_enable_notifications)
        .withIdentifier(R.id.nav_drawer_notifications)
        .withSelectable(false)
        .withIcon(Octicons.Icon.oct_bell)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          openNotifications();
          return false;
        })
        .withIconColor(iconColor));

    items.add(new SecondaryDrawerItem().withName(R.string.navigation_settings)
        .withIcon(Octicons.Icon.oct_gear)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_settings)
        .withIsExpanded(false)
        .withSelectable(false)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onSettingsSelected();
          return false;
        }));
    items.add(new SecondaryDrawerItem().withName(R.string.open_gitskarios_issue)
        .withIconColor(iconColor)
        .withIdentifier(R.id.open_gitskarios_issue)
        .withIcon(Octicons.Icon.oct_issue_opened)
        .withSelectable(false)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onGitskariosIssueSelected();
          return false;
        }));

    items.add(new DividerDrawerItem());

    if (donateFragment.enabled()) {
      PrimaryDrawerItem donateItem = new SecondaryDrawerItem().withName(R.string.support_development)
          .withIcon(Octicons.Icon.oct_heart)
          .withIconColor(iconColor)
          .withIdentifier(R.id.nav_drawer_support_development)
          .withSelectable(false)
          .withOnDrawerItemClickListener((view, position, drawerItem) -> {
            if (donateFragment != null && donateFragment.enabled()) {
              donateFragment.launchDonate();
            }
            return false;
          });

      items.add(donateItem);
    }

    items.add(new SecondaryDrawerItem().withName(R.string.navigation_invite)
        .withIcon(Octicons.Icon.oct_organization)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_invite)
        .withSelectable(false)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onInviteClicked();
          return false;
        }));

    items.add(new SecondaryDrawerItem().withName(R.string.navigation_about)
        .withIcon(Octicons.Icon.oct_octoface)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_about)
        .withSelectable(false)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onAboutSelected();
          return false;
        }));
    items.add(new SecondaryDrawerItem().withName(R.string.navigation_sign_out)
        .withIcon(Octicons.Icon.oct_sign_out)
        .withIconColor(iconColor)
        .withIdentifier(R.id.nav_drawer_sign_out)
        .withSelectable(false)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          signOut();
          return false;
        }));

    return items;
  }

  private void openFavorites() {
    Intent intent = new Intent(this, SyncFavoritesActivity.class);
    startActivity(intent);
  }

  private void onInviteClicked() {
    Intent intent =
        new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title)).setMessage(getString(R.string.invitation_message))
            .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
            .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
            .setCallToActionText(getString(R.string.invitation_cta))
            .build();
    startActivityForResult(intent, REQUEST_INVITE);
  }

  private AccountHeader buildHeader() {
    DrawerImageLoader.init(new DrawerImage());

    AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder().withActivity(this).withHeaderBackground(R.color.md_grey_600);

    headerBuilder.withOnAccountHeaderListener(this);

    ProfileDrawerItem userDrawerItem = getUserDrawerItem();

    headerBuilder.addProfiles(userDrawerItem);

    return headerBuilder.build();
  }

  @NonNull
  private ProfileDrawerItem getOrganizationProfileDrawerItem(String name, String avatar) {
    int iconColor = ContextCompat.getColor(this, R.color.icons);

    ProfileDrawerItem orgProfile = new ProfileDrawerItem().withName(name).withIcon(avatar);

    orgProfile.withSubItems(new PrimaryDrawerItem().withName("Events")
        .withIcon(Octicons.Icon.oct_calendar)
        .withIconColor(iconColor)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onOrgEventsSelected(name);
          return false;
        }));
    orgProfile.withSubItems(new PrimaryDrawerItem().withName("Repositories")
        .withIcon(Octicons.Icon.oct_repo)
        .withIconColor(iconColor)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onOrgReposSelected(name);
          return false;
        }));
    orgProfile.withSubItems(new PrimaryDrawerItem().withName("People")
        .withIcon(Octicons.Icon.oct_organization)
        .withIconColor(iconColor)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onOrgPeopleSelected(name);
          return false;
        }));
    orgProfile.withSubItems(new PrimaryDrawerItem().withName("Teams")
        .withIcon(Octicons.Icon.oct_jersey)
        .withIconColor(iconColor)
        .withOnDrawerItemClickListener((view, position, drawerItem) -> {
          onOrgTeamsSelected(name);
          return false;
        }));
    return orgProfile;
  }

  private ProfileDrawerItem getUserDrawerItem() {
    String userAvatar = AccountsHelper.getUserAvatar(this, selectedAccount);
    ProfileDrawerItem userDrawerItem = new ProfileDrawerItem().withName(getUserExtraName(selectedAccount))
        .withEmail(getNameFromAccount(selectedAccount))
        .withNameShown(false)
        .withSubItems(getUserDrawerItems())
        .withIdentifier(selectedAccount.hashCode());
    if (!TextUtils.isEmpty(userAvatar)) {
      userDrawerItem.withIcon(userAvatar);
    }
    return userDrawerItem;
  }

  @Override
  public boolean onProfileChanged(View view, IProfile iProfile, boolean current) {
    if (iProfile.getIdentifier() != 1101) {
      if (current) {
        User user = new User();
        user.login = iProfile.getName().getText();
        Intent launcherIntent = ProfileActivity.createLauncherIntent(MainActivity.this, selectedAccount);
        startActivityForResult(launcherIntent, PROFILE_REQUEST_CODE);
        return false;
      } else {
        String accountName = iProfile.getName().getText();
        // TODO On organization selected

        if (iProfile instanceof ProfileDrawerItem) {
          List<IDrawerItem> subItems = ((ProfileDrawerItem) iProfile).getSubItems();
          if (subItems != null) {
            resultDrawer.removeAllItems();
            for (IDrawerItem subItem : subItems) {
              resultDrawer.addItems(subItem);
            }
          }
        }
        return true;
      }
    } else {
      Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
      return true;
    }
  }

  private void selectAccount(final Account account) {
    boolean changingUser = selectedAccount != null && !getNameFromAccount(selectedAccount).equals(getNameFromAccount(account));
    this.selectedAccount = account;

    accountNameProvider.setName(getNameFromAccount(account));

    StoreCredentials credentials = new StoreCredentials(MainActivity.this);
    credentials.clear();
    String authToken = AccountsHelper.getUserToken(this, account);

    credentials.storeToken(authToken);
    credentials.storeUsername(getNameFromAccount(account));

    credentials.storeUrl(AccountsHelper.getUrl(this, account));

    String url = AccountsHelper.getUrl(this, account);

    credentials.storeUrl(url);

    if (changingUser) {
      lastUsedFragment = null;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    getMenuInflater().inflate(R.menu.main_menu, menu);
    menu.findItem(R.id.action_search)
        .setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_search).color(Color.WHITE).sizeDp(24).respectFontBounds(true));

    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (notificationsSizeCount > 0) {
      BadgeStyle badgeStyle =
          new BadgeStyle(BadgeStyle.Style.DEFAULT, R.layout.menu_action_item_badge, getResources().getColor(R.color.accent),
              getResources().getColor(R.color.accent_dark), Color.WHITE, getResources().getDimensionPixelOffset(R.dimen.gapMicro));
      ActionItemBadge.update(this, menu.findItem(R.id.action_notifications), Octicons.Icon.oct_bell, badgeStyle, notificationsSizeCount);
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
    GetNotificationsClient client = new GetNotificationsClient();
    client.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<Notification>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(List<Notification> notifications) {
            notificationsSizeCount = notifications.size();
            invalidateOptionsMenu();
          }
        });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == R.id.action_search) {
      Intent intent = SearchActivity.launchIntent(this);
      startActivity(intent);
    } else if (item.getItemId() == R.id.action_notifications) {
      openNotifications();
    }

    return false;
  }

  private void openNotifications() {
    Intent intent = NotificationsActivity.launchIntent(this);
    startActivity(intent);
  }

  private void setFragment(Fragment fragment) {
    setFragment(fragment, true);
  }

  private void setFragment(Fragment fragment, boolean addToBackStack) {
    try {
      if (fragment != null && getSupportFragmentManager() != null) {
        this.lastUsedFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (ft != null) {
          ft.replace(R.id.content, fragment);
          if (addToBackStack) {
            ft.addToBackStack(null);
          }
          ft.commit();
        }
      }
    } catch (Exception e) {
      ErrorHandler.onError(this, "MainActivity.setFragment()", e);
    }
  }

  public boolean onReposSelected() {
    setFragment(GeneralReposFragment.newInstance(), false);
    return true;
  }

  public boolean onPeopleSelected() {
    setFragment(GeneralPeopleFragment.newInstance(), false);
    return false;
  }

  public boolean onIssuesSelected() {
    setFragment(GeneralIssuesListFragment.newInstance(), false);
    return false;
  }

  public boolean onGistsSelected() {
    AuthUserGistsFragment gistsFragment = AuthUserGistsFragment.newInstance();
    setFragment(gistsFragment);
    return false;
  }

  public boolean onStarredGistsSelected() {
    AuthUserStarredGistsFragment gistsFragment = AuthUserStarredGistsFragment.newInstance();
    setFragment(gistsFragment);
    return false;
  }

  public boolean onUserEventsSelected() {
    String user = new StoreCredentials(this).getUserName();
    if (user != null) {
      setFragment(EventsListFragment.newInstance(user), false);
    }
    return true;
  }

  public void onOrgEventsSelected(String orgName) {

  }

  public void onOrgReposSelected(String orgName) {

  }

  public void onOrgPeopleSelected(String orgName) {

  }

  public void onOrgTeamsSelected(String orgName) {

  }

  public boolean onSettingsSelected() {
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
    return false;
  }

  public boolean onGitskariosIssueSelected() {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = "gitskarios";
    repoInfo.name = "gitskarios";
    Intent intent = NewIssueActivity.createLauncherIntent(this, repoInfo);
    startActivity(intent);
    return false;
  }

  public void signOut() {
    if (selectedAccount != null) {
      removeAccount(selectedAccount, () -> {
        StoreCredentials storeCredentials = new StoreCredentials(MainActivity.this);
        storeCredentials.clear();

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
      });
    }
  }

  public boolean onAboutSelected() {
    Libs.ActivityStyle activityStyle = Libs.ActivityStyle.LIGHT_DARK_TOOLBAR;
    int theme = R.style.AppTheme;
    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
    if ("theme_dark".equalsIgnoreCase(pref_theme)) {
      activityStyle = Libs.ActivityStyle.DARK;
      theme = R.style.AppTheme_Dark;
    }
    new LibsBuilder()
        //Pass the fields of your application to the lib so it can find all external lib information
        .withFields(R.string.class.getFields())
        .withActivityTitle(getString(R.string.app_name))
        .withActivityStyle(activityStyle)
        .withActivityTheme(theme)
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
      } else if (resultDrawer != null) {
        resultDrawer.setSelection(R.id.nav_drawer_events);
      }
    }
  }
}
