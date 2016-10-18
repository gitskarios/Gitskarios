package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.AccountsHelper;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.orgs.GetOrgsClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.sdk.services.user.follow.CheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.FollowUserClient;
import com.alorma.github.sdk.services.user.follow.UnfollowUserClient;
import com.alorma.github.ui.activity.base.PeopleThemeActivity;
import com.alorma.github.ui.fragment.events.CreatedEventsListFragment;
import com.alorma.github.ui.fragment.repos.UsernameReposFragment;
import com.alorma.github.ui.fragment.users.UserResumeFragment;
import com.alorma.github.utils.AccountUtils;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import core.User;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileActivity extends PeopleThemeActivity implements UserResumeFragment.UserResumeCallback {

  public static final String EXTRA_COLOR = "EXTRA_COLOR";
  public static final String URL_PROFILE = "URL_PROFILE";
  private static final String USER = "USER";
  private static final String ACCOUNT = "ACCOUNT";
  private static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";
  @BindView(R.id.coordinator) CoordinatorLayout coordinatorLayout;
  @BindView(R.id.appbarLayout) AppBarLayout appBarLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.ctlLayout) CollapsingToolbarLayout collapsingToolbarLayout;
  @BindView(R.id.userAvatar) ImageView userAvatar;
  @BindView(R.id.userName) TextView userName;
  @BindView(R.id.tabLayout) TabLayout tabLayout;
  @BindView(R.id.viewpager) ViewPager viewPager;

  private User user;
  private boolean followingUser = false;
  private boolean updateProfile = false;
  private Account selectedAccount;

  private UserResumeFragment userResumeFragment;

  public static Intent createLauncherIntent(Context context, Account selectedAccount) {
    Intent intent = new Intent(context, ProfileActivity.class);
    Bundle extras = new Bundle();
    extras.putBoolean(AUTHENTICATED_USER, true);
    extras.putParcelable(ACCOUNT, selectedAccount);
    intent.putExtras(extras);
    return intent;
  }

  public static Intent createLauncherIntent(Context context, User user) {
    Bundle extras = new Bundle();
    if (user != null) {
      extras.putParcelable(USER, user);

      StoreCredentials settings = new StoreCredentials(context);
      if (user.getLogin() != null) {
        extras.putBoolean(AUTHENTICATED_USER, user.getLogin().equalsIgnoreCase(settings.getUserName()));
      }
    }
    Intent intent = new Intent(context, ProfileActivity.class);
    intent.putExtras(extras);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.profile_activity);

    ButterKnife.bind(this);

    if (getSupportFragmentManager() != null && getSupportFragmentManager().getFragments() != null) {
      for (Fragment fragment : getSupportFragmentManager().getFragments()) {
        if (fragment instanceof UserResumeFragment) {
          userResumeFragment = (UserResumeFragment) fragment;
        }
      }
    }

    if (userResumeFragment == null) {
      userResumeFragment = new UserResumeFragment();
    }

    getContent();
  }

  @Override
  public void onStart() {
    super.onStart();

    if (getSupportActionBar() != null) {
      getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    if (userResumeFragment != null) {
      userResumeFragment.setUserResumeCallback(this);
    }
  }

  @Override
  protected void onStop() {
    if (userResumeFragment != null) {
      userResumeFragment.setUserResumeCallback(null);
    }
    super.onStop();
  }

  @Override
  protected void getContent() {
    if (user == null) {
      Observable<User> requestClient;
      String avatar = null;
      String login = null;
      String name = null;
      if (getIntent().getExtras() != null) {
        if (getIntent().getExtras().containsKey(ACCOUNT)) {
          selectedAccount = getIntent().getParcelableExtra(ACCOUNT);
          avatar = AccountsHelper.getUserAvatar(this, selectedAccount);
          login = new AccountUtils().getNameFromAccount(selectedAccount.name);
          name = AccountsHelper.getUserName(this, selectedAccount);
        }
        if (getIntent().getExtras().containsKey(USER)) {
          user = getIntent().getParcelableExtra(USER);
          avatar = user.getAvatar();
          login = user.getLogin();
          name = user.getName();
        }
      }

      if (login != null) {
        collapsingToolbarLayout.setTitle("@" + login);

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(userResumeFragment);
        fragments.add(UsernameReposFragment.newInstance(login));
        fragments.add(CreatedEventsListFragment.newInstance(login));

        PagerAdapter adapter = new ProfilePagesAdapter(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        tabLayout.setupWithViewPager(viewPager);
      }

      if (name != null) {
        userName.setText(name);
      }

      if (avatar != null) {
        loadAvatar(login, avatar);
      }

      StoreCredentials settings = new StoreCredentials(this);

      if (user != null) {
        if (user.getLogin() != null && settings.getUserName() != null &&
            user.getLogin().equalsIgnoreCase(settings.getUserName())) {
          requestClient = getGetAuthUserClient();
          updateProfile = true;
        } else {
          requestClient = new RequestUserClient(user.getLogin()).observable();
        }
      } else {
        requestClient = getGetAuthUserClient();
        updateProfile = true;
      }

      invalidateOptionsMenu();

      Observable<Integer> organizations =
          new GetOrgsClient(login).observable().subscribeOn(Schedulers.io()).map(listIntegerPair -> listIntegerPair.first.size());

      Observable.combineLatest(requestClient.subscribeOn(Schedulers.io()), organizations, (user1, organizations1) -> {
        user1.setOrganizationsNum(organizations1);
        return user1;
      }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<User>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(User user) {
          onUserLoaded(user);
        }
      });
    }
  }

  @NonNull
  private Observable<User> getGetAuthUserClient() {
    return new GetAuthUserClient().observable().map(userStringPair -> userStringPair.first);
  }

  private void loadAvatar(String login, String avatar) {

    int avatarSize = getResources().getDimensionPixelOffset(R.dimen.avatar_size);

    TextDrawable fallback = TextDrawable.builder()
        .beginConfig()
        .width(avatarSize)
        .height(avatarSize)
        .endConfig()
        .buildRound(login.substring(0, 1), ColorGenerator.MATERIAL.getColor(login.substring(0, 1)));

    Glide.with(this)
        .load(avatar)
        .bitmapTransform(new CropCircleTransformation(this))
        .placeholder(fallback)
        .error(fallback)
        .into(userAvatar);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    if (menu != null) {
      menu.clear();

      StoreCredentials settings = new StoreCredentials(this);

      if (user != null && !settings.getUserName().equals(user.getLogin())
          && UserType.User.name().equals(user.getType())) {
        MenuItem item;
        if (followingUser) {
          item = menu.add(0, R.id.action_menu_unfollow_user, 0, R.string.action_menu_unfollow_user);
        } else {
          item = menu.add(0, R.id.action_menu_follow_user, 0, R.string.action_menu_follow_user);
        }

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
      }
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    if (item.getItemId() == R.id.action_menu_follow_user) {
      followUserAction(new FollowUserClient(user.getLogin()));
    } else if (item.getItemId() == R.id.action_menu_unfollow_user) {
      followUserAction(new UnfollowUserClient(user.getLogin()));
    }

    item.setEnabled(false);

    return true;
  }

  private void followUserAction(GithubClient<Boolean> githubClient) {
    githubClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
      @Override
      public void onCompleted() {

      }

      @Override
      public void onError(Throwable e) {

      }

      @Override
      public void onNext(Boolean aBoolean) {
        followingUser = aBoolean;
        invalidateOptionsMenu();
      }
    });
  }

  public void onUserLoaded(final User user) {
    this.user = user;
    invalidateOptionsMenu();

    loadAvatar(user.getLogin(), user.getAvatar());

    userName.setText(user.getName());

    StoreCredentials settings = new StoreCredentials(this);

    invalidateOptionsMenu();

    if (updateProfile && selectedAccount != null) {
      AccountManager accountManager = AccountManager.get(this);
      accountManager.setUserData(selectedAccount, AccountsHelper.USER_PIC, user.getAvatar());
      ImageLoader.getInstance().clearMemoryCache();
      ImageLoader.getInstance().clearDiskCache();
    }

    if (!user.getLogin().equalsIgnoreCase(settings.getUserName())) {
      followUserAction(new CheckFollowingUser(user.getLogin()));
    }

    userResumeFragment.fill(user);
  }

  @Override
  protected void close(boolean navigateUp) {
    if (user != null && updateProfile) {
      Intent intent = new Intent();
      Bundle extras = new Bundle();
      extras.putString(URL_PROFILE, user.getAvatar());
      intent.putExtras(extras);
      setResult(selectedAccount != null ? RESULT_FIRST_USER : RESULT_OK, intent);
    }
    super.close(navigateUp);
  }

  @Override
  public void openRepos(String login) {
    if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() >= 1) {
      viewPager.setCurrentItem(1);
    }
  }
}
