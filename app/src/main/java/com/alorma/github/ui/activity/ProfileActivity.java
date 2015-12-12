package com.alorma.github.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.login.AccountsHelper;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.sdk.services.user.follow.CheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.FollowUserClient;
import com.alorma.github.sdk.services.user.follow.UnfollowUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity {

    public static final String EXTRA_COLOR = "EXTRA_COLOR";
    public static final String URL_PROFILE = "URL_PROFILE";
    private static final String USER = "USER";
    private static final String ACCOUNT = "ACCOUNT";
    private static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";

    private User user;

    private boolean followingUser = false;
    private boolean updateProfile = false;
    private Account selectedAccount;
    private boolean colorApplied;
    private int avatarColor;

    @Bind(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.appbarLayout)
    AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.ctlLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.userAvatar)
    ImageView userAvatar;

    @Bind(R.id.userLogin)
    TextView userLogin;

    @Bind(R.id.userName)
    TextView userName;

    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

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
            extras.putBoolean(AUTHENTICATED_USER, user.login.equalsIgnoreCase(settings.getUserName()));
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

        avatarColor = ContextCompat.getColor(this, R.color.primary);

        TabLayout.Tab info = tabLayout.newTab().setText("INFO");
        tabLayout.addTab(info);
        tabLayout.addTab(tabLayout.newTab().setText("REPOSITORIES"));
        tabLayout.addTab(tabLayout.newTab().setText("EVENTS"));
        info.select();


        if (getIntent().getExtras().containsKey(EXTRA_COLOR)) {
            avatarColor = getIntent().getIntExtra(EXTRA_COLOR, -1);
        }

    }

    @Override
    protected void getContent() {
        GithubClient<User> requestClient;
        user = null;
        String avatar = null;
        String login = null;
        String name = null;
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(ACCOUNT)) {
                selectedAccount = getIntent().getParcelableExtra(ACCOUNT);
                avatar = AccountsHelper.getUserAvatar(this, selectedAccount);
                login = selectedAccount.name;
                name = AccountsHelper.getUserName(this, selectedAccount);
            }
            if (getIntent().getExtras().containsKey(USER)) {
                user = getIntent().getParcelableExtra(USER);
                avatar = user.avatar_url;
                login = user.login;
                name = user.name;
            }
        }

        if (login != null) {
            userLogin.setText(login);
        }

        if (name != null) {
            userName.setText(name);
        }

        if (avatar != null) {

            Glide.with(this)
                    .load(avatar)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(userAvatar);

            Glide.with(this)
                    .load(avatar)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch profileSwatch = PaletteUtils.getProfileLightSwatch(palette);
                                    if (profileSwatch == null) {
                                        profileSwatch = PaletteUtils.getProfileLightSwatch(palette);
                                    }
                                    if (profileSwatch == null) {
                                        profileSwatch = PaletteUtils.getProfileSwatch(palette);
                                    }

                                    if (profileSwatch != null) {
                                        applySwatchBackground(profileSwatch);
                                        applySwatchTexts(profileSwatch);
                                    }
                                }
                            });
                        }
                    });
        }

        StoreCredentials settings = new StoreCredentials(this);

        if (user != null) {
            if (user.login.equalsIgnoreCase(settings.getUserName())) {
                requestClient = new GetAuthUserClient(this);
                updateProfile = true;
                collapsingToolbarLayout.setTitle(settings.getUserName());
            } else {
                requestClient = new RequestUserClient(this, user.login);
                collapsingToolbarLayout.setTitle(user.login);
            }
        } else {
            requestClient = new GetAuthUserClient(this);
            updateProfile = true;
        }

        invalidateOptionsMenu();

        requestClient.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
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

    private void applySwatchTexts(Palette.Swatch swatch) {

        int rgb = swatch.getTitleTextColor();
        generateAvatarBackground(rgb);
        userLogin.setTextColor(rgb);
        userName.setTextColor(rgb);

        tabLayout.setTabTextColors(swatch.getBodyTextColor(), rgb);
        tabLayout.setSelectedTabIndicatorColor(rgb);
    }

    private void applySwatchBackground(Palette.Swatch swatch) {

        int bkg = swatch.getRgb();

        coordinatorLayout.setStatusBarBackgroundColor(bkg);
        toolbar.setBackgroundColor(bkg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(bkg);
            getWindow().setNavigationBarColor(bkg);
        }
    }

    private void generateAvatarBackground(int color) {
        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.getPaint().setColor(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            userAvatar.setBackground(circle);
        } else {
            userAvatar.setBackgroundDrawable(circle);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu != null) {

            menu.clear();

            StoreCredentials settings = new StoreCredentials(this);

            if (user != null && !settings.getUserName().equals(user.login)) {
                if (followingUser) {
                    menu.add(0, R.id.action_menu_unfollow_user, 0, R.string.action_menu_unfollow_user);
                } else {
                    menu.add(0, R.id.action_menu_follow_user, 0, R.string.action_menu_follow_user);
                }

                menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_menu_follow_user) {
            followUserAction(new FollowUserClient(this, user.login));
        } else if (item.getItemId() == R.id.action_menu_unfollow_user) {
            followUserAction(new UnfollowUserClient(this, user.login));
        }

        item.setEnabled(false);

        return true;
    }

    private void followUserAction(GithubClient<Boolean> githubClient) {
        githubClient.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
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

        userName.setText(user.name);

        collapsingToolbarLayout.setTitle(user.login);

        StoreCredentials settings = new StoreCredentials(this);

        invalidateOptionsMenu();

        if (updateProfile && selectedAccount != null) {
            AccountManager accountManager = AccountManager.get(this);
            accountManager.setUserData(selectedAccount, AccountsHelper.USER_PIC, user.avatar_url);
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().clearDiskCache();
        }

        if (!user.login.equalsIgnoreCase(settings.getUserName())) {
            followUserAction(new CheckFollowingUser(this, user.login));
        }
    }
/*
  private void loadImageAvatar(final User user) {

      TextDrawable drawable = TextDrawable.builder()
              .beginConfig()
              .endConfig()
              .buildRect(user.login.substring(0, 1), ColorGenerator.MATERIAL.getColor(user.login.substring(0, 1)));
      image.setImageDrawable(drawable);

    ImageLoader.getInstance().displayImage(user.avatar_url, image, new ImageLoadingListener() {
      @Override
      public void onLoadingStarted(String imageUri, View view) {

      }

      @Override
      public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

      }

      @Override
      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (!colorApplied) {
          Atelier.with(ProfileActivity.this, user.avatar_url)
              .load(loadedImage)
              .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
              .listener(new Atelier.OnPaletteRenderedListener() {
                @Override
                public void onRendered(Palette palette) {
                  applyColors(palette.getVibrantColor(avatarColor));
                }
              })
              .into(image);
        } else {
          profileItemsAdapter.setAvatarColor(avatarColor);
        }
      }

      @Override
      public void onLoadingCancelled(String imageUri, View view) {

      }
    });
  }

  private void applyColors(int rgb) {
    try {
      colorApplied = true;
      if (collapsingToolbarLayout != null) {
        collapsingToolbarLayout.setContentScrimColor(rgb);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setStatusBarScrimColor(rgb);
      }
      if (profileItemsAdapter != null) {
        profileItemsAdapter.setAvatarColor(rgb);
      }
      if (rgb != 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          getWindow().setStatusBarColor(rgb);
          getWindow().setNavigationBarColor(rgb);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void fillCardBio(User user) {
    if (!TextUtils.isEmpty(user.company)) {
      Intent intent = new Intent(Intent.ACTION_SEARCH);
      intent.putExtra(SearchManager.QUERY, user.company);
      ProfileItem profileUserOrganization =
          new ProfileItem(Octicons.Icon.oct_organization, user.company, intent);
      profileItemsAdapter.add(profileUserOrganization);
    }
    if (!TextUtils.isEmpty(user.location)) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      Uri geo = Uri.parse("geo:0,0?q=" + user.location);
      intent.setData(geo);
      ProfileItem profileUserLocation =
          new ProfileItem(Octicons.Icon.oct_location, user.location, intent);
      profileItemsAdapter.add(profileUserLocation);
    }
    if (!TextUtils.isEmpty(user.email)) {
      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setData(Uri.parse("mailto:"));
      intent.putExtra(Intent.EXTRA_EMAIL, new String[] { user.email });
      ProfileItem profileUserEmail = new ProfileItem(Octicons.Icon.oct_mail, user.email, intent);
      profileItemsAdapter.add(profileUserEmail);
    }
    if (user.created_at != null) {
      ProfileItem profileUserCreated = new ProfileItem(Octicons.Icon.oct_clock,
          TimeUtils.getDateToText(this, user.created_at, R.string.joined_at), null);
      profileItemsAdapter.add(profileUserCreated);
    }
  }

  private void fillCardGithubData(User user) {
    if (user.public_repos > 0) {
      String text = getString(R.string.repos_num, user.public_repos);
      Intent intent = ReposActivity.launchIntent(this, user.login, user.type);
      ProfileItem profileItemRepos = new ProfileItem(Octicons.Icon.oct_repo, text, intent);
      profileItemsAdapter.add(profileItemRepos);
    }
    if (user.public_gists > 0) {
      String text = getString(R.string.gists_num, user.public_gists);
      Intent intent = GistsMainActivity.createLauncherIntent(this, user.login);
      ProfileItem profileItemGists = new ProfileItem(Octicons.Icon.oct_gist, text, intent);
      profileItemsAdapter.add(profileItemGists);
    }

    Intent intent = OrganizationsActivity.launchIntent(this, user.login);
    final ProfileItem profileItemOrgs =
        new ProfileItem(Octicons.Icon.oct_organization, getString(R.string.orgs_num_empty), intent);
    profileItemsAdapter.add(profileItemOrgs);

    GetOrgsClient orgsClient = new GetOrgsClient(this, user.login);
    orgsClient.observable()
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Pair<List<Organization>, Integer>, List<Organization>>() {
          @Override
          public List<Organization> call(Pair<List<Organization>, Integer> listIntegerPair) {
            return listIntegerPair.first;
          }
        })
        .subscribe(new Subscriber<List<Organization>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(List<Organization> organizations) {
            if (organizations != null && organizations.size() > 0) {
              profileItemOrgs.value = getString(R.string.orgs_num, organizations.size());
              profileItemsAdapter.notifyDataSetChanged();
            } else {
              profileItemsAdapter.remove(profileItemOrgs);
            }
          }
        });

    if (user.type == UserType.User) {
      Intent intentStarred = StarredReposActivity.launchIntent(this, user.login);
      ProfileItem profileItemStar =
          new ProfileItem(Octicons.Icon.oct_star, getString(R.string.profile_starreds),
              intentStarred);
      profileItemsAdapter.add(profileItemStar);

      Intent intentWatched = WatchedReposActivity.launchIntent(this, user.login);
      ProfileItem profileItemWatched =
          new ProfileItem(Octicons.Icon.oct_eye, getString(R.string.profile_watched),
              intentWatched);
      profileItemsAdapter.add(profileItemWatched);
    }
  }
  */

    @Override
    protected void close(boolean navigateUp) {
        if (user != null && updateProfile) {
            Intent intent = new Intent();
            Bundle extras = new Bundle();
            extras.putString(URL_PROFILE, user.avatar_url);
            intent.putExtras(extras);
            setResult(selectedAccount != null ? RESULT_FIRST_USER : RESULT_OK, intent);
        }
        super.close(navigateUp);
    }
}
