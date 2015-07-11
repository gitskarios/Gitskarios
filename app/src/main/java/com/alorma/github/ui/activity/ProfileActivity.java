package com.alorma.github.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.basesdk.client.StoreCredentials;
import com.alorma.github.bean.ProfileItem;
import com.alorma.github.sdk.bean.dto.response.ListOrganizations;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.sdk.services.orgs.GetOrgsClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.GithubUsersClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.sdk.services.user.follow.CheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.FollowUserClient;
import com.alorma.github.sdk.services.user.follow.OnCheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.UnfollowUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.activity.gists.GistsMainActivity;
import com.alorma.github.ui.adapter.ProfileItemsAdapter;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.musenkishi.atelier.Atelier;
import com.musenkishi.atelier.ColorType;
import com.musenkishi.atelier.swatch.DarkVibrantSwatch;
import com.musenkishi.atelier.swatch.VibrantSwatch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity implements BaseClient.OnResultCallback<User>,
//        PaletteUtils.PaletteUtilsListener,
        OnCheckFollowingUser {

    private static final String USER = "USER";
    private static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";

    private ImageView image;

    private User user;
    private boolean followingUser = false;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProfileItemsAdapter profileItemsAdapter;

    public static Intent createLauncherIntent(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putBoolean(AUTHENTICATED_USER, true);
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

        image = (ImageView) findViewById(R.id.imgToolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctlLayout);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setItemAnimator(new DefaultItemAnimator());
        profileItemsAdapter = new ProfileItemsAdapter(this);
        recycler.setAdapter(profileItemsAdapter);
    }

    @Override
    protected void getContent() {
        if (profileItemsAdapter == null || profileItemsAdapter.getItemCount() == 0) {
            GithubUsersClient<User> requestClient;
            user = null;
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey(USER)) {
                    user = getIntent().getParcelableExtra(USER);
                }
            }

            StoreCredentials settings = new StoreCredentials(this);

            if (user != null) {
                if (user.login.equalsIgnoreCase(settings.getUserName())) {
                    requestClient = new GetAuthUserClient(this);
                    collapsingToolbarLayout.setTitle(settings.getUserName());
                } else {
                    requestClient = new RequestUserClient(this, user.login);
                    collapsingToolbarLayout.setTitle(user.login);
                }
            } else {
                requestClient = new GetAuthUserClient(this);
            }

            invalidateOptionsMenu();

            showProgressDialog(R.style.SpotDialog_LoadingUser);

            requestClient.setOnResultCallback(this);
            requestClient.execute();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_menu_follow_user) {
            showProgressDialog(R.style.SpotDialog_LoadingUser);

            FollowUserClient followUserClient = new FollowUserClient(this, user.login);
            followUserClient.setOnCheckFollowingUser(this);
            followUserClient.execute();
        } else if (item.getItemId() == R.id.action_menu_unfollow_user) {
            showProgressDialog(R.style.SpotDialog_LoadingUser);

            UnfollowUserClient unfollowUserClient = new UnfollowUserClient(this, user.login);
            unfollowUserClient.setOnCheckFollowingUser(this);
            unfollowUserClient.execute();
        }

        item.setEnabled(false);

        return true;
    }

    @Override
    public void onResponseOk(final User user, Response r) {
        this.user = user;
        collapsingToolbarLayout.setTitle(user.login);

        hideProgressDialog();

        StoreCredentials settings = new StoreCredentials(this);

        invalidateOptionsMenu();

        if (!user.login.equalsIgnoreCase(settings.getUserName())) {
            CheckFollowingUser checkFollowingUser = new CheckFollowingUser(this, user.login);
            checkFollowingUser.setOnCheckFollowingUser(this);
            checkFollowingUser.execute();
        }

        fillCardBio(user);

        fillCardGithubData(user);

        fillCardPlan(user);

        if (getSupportActionBar() != null) {

            ImageLoader.getInstance().displayImage(user.avatar_url, image, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Atelier.with(ProfileActivity.this, user.avatar_url)
                            .load(loadedImage)
                            .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
                            .listener(new Atelier.OnPaletteRenderedListener() {
                                @Override
                                public void onRendered(Palette palette, int generatedColor) {
                                    applyColors(generatedColor);
                                    profileItemsAdapter.setAvatarColor(generatedColor);
                                }
                            })
                            .into(image);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
//            new PaletteUtils().loadImageAndPalette(user.avatar_url, this);
        }
    }

//    @Override
//    public void onImageLoaded(Bitmap loadedImage, Palette palette) {
//
//        Drawable drawable = new BitmapDrawable(getResources(), loadedImage);
//
//        image.setImageDrawable(drawable);
//
//        if (palette.getSwatches().size() > 0) {
//            Palette.Swatch swatch = palette.getSwatches().get(0);
//            applyColors(swatch.getRgb(), swatch.getBodyTextColor());
//        } else {
//            applyColors(getResources().getColor(R.color.primary), Color.WHITE);
//        }
//
//        fillCardBio(user);
//
//        fillCardGithubData(user);
//
//        fillCardPlan(user);
//
//    }

    private void applyColors(int rgb) {
        collapsingToolbarLayout.setContentScrimColor(rgb);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setStatusBarScrimColor(rgb);
        profileItemsAdapter.setAvatarColor(rgb);

        try {
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
            ProfileItem profileUserOrganization = new ProfileItem(Octicons.Icon.oct_organization, user.company, intent);
            profileItemsAdapter.add(profileUserOrganization);
        }
        if (!TextUtils.isEmpty(user.location)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geo = Uri.parse("geo:0,0?q=" + user.location);
            intent.setData(geo);
            ProfileItem profileUserLocation = new ProfileItem(Octicons.Icon.oct_location, user.location, intent);
            profileItemsAdapter.add(profileUserLocation);
        }
        if (!TextUtils.isEmpty(user.email)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.email});
            ProfileItem profileUserEmail = new ProfileItem(Octicons.Icon.oct_mail, user.email, intent);
            profileItemsAdapter.add(profileUserEmail);
        }
        if (user.created_at != null) {
            ProfileItem profileUserCreated = new ProfileItem(Octicons.Icon.oct_clock, TimeUtils.getDateToText(this, user.created_at), null);
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
        final ProfileItem profileItemOrgs = new ProfileItem(Octicons.Icon.oct_organization, getString(R.string.orgs_num_empty), intent);
        profileItemsAdapter.add(profileItemOrgs);

        GetOrgsClient orgsClient = new GetOrgsClient(this, user.login);
        orgsClient.setOnResultCallback(new BaseClient.OnResultCallback<ListOrganizations>() {
            @Override
            public void onResponseOk(ListOrganizations organizations, Response r) {
                if (organizations != null && organizations.size() > 0) {
                    profileItemOrgs.value = getString(R.string.orgs_num, organizations.size());
                    profileItemsAdapter.notifyDataSetChanged();
                } else {
                    profileItemsAdapter.remove(profileItemOrgs);
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        orgsClient.execute();

        if (user.type == UserType.User) {
            Intent intentStarred = StarredReposActivity.launchIntent(this, user.login);
            ProfileItem profileItemStar = new ProfileItem(Octicons.Icon.oct_star, getString(R.string.profile_starreds), intentStarred);
            profileItemsAdapter.add(profileItemStar);

            Intent intentWatched = WatchedReposActivity.launchIntent(this, user.login);
            ProfileItem profileItemWatched = new ProfileItem(Octicons.Icon.oct_eye, getString(R.string.profile_watched), intentWatched);
            profileItemsAdapter.add(profileItemWatched);
        }
    }

    private void fillCardPlan(User user) {
        if (user.plan != null) {

        }
    }

    @Override
    public void onFail(RetrofitError error) {
        hideProgressDialog();
    }

    @Override
    public void onCheckFollowUser(String username, boolean following) {
        followingUser = following;
        hideProgressDialog();
        invalidateOptionsMenu();
    }
}
