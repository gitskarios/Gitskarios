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
import android.widget.ImageView;

import com.alorma.github.R;
import com.alorma.github.bean.ProfileItem;
import com.alorma.github.sdk.bean.dto.response.User;
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
import com.alorma.github.ui.cards.profile.BioCard;
import com.alorma.github.ui.cards.profile.GithubDataCard;
import com.alorma.github.ui.cards.profile.GithubPlanCard;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.gitskarios.basesdk.client.StoreCredentials;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity implements BaseClient.OnResultCallback<User>,
        PaletteUtils.PaletteUtilsListener, BioCard.BioCardListener,
        GithubDataCard.GithubDataCardListener,
        OnCheckFollowingUser, GithubPlanCard.GithubDataCardListener {

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
    public void onResponseOk(User user, Response r) {
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

        if (getSupportActionBar() != null) {
            new PaletteUtils().loadImageAndPalette(user.avatar_url, this);
        }
    }

    @Override
    public void onImageLoaded(Bitmap loadedImage, Palette palette) {

        Drawable drawable = new BitmapDrawable(getResources(), loadedImage);

        image.setImageDrawable(drawable);

        if (palette.getSwatches().size() > 0) {
            Palette.Swatch swatch = palette.getSwatches().get(0);
            applyColors(swatch.getRgb(), swatch.getBodyTextColor());
        } else {
            applyColors(getResources().getColor(R.color.primary), Color.WHITE);
        }
        fillCardBio(user);

        fillCardGithubData(user);

        fillCardPlan(user);

    }

    private void applyColors(int rgb, int textColor) {
        collapsingToolbarLayout.setContentScrimColor(rgb);
        collapsingToolbarLayout.setExpandedTitleColor(textColor);
        collapsingToolbarLayout.setCollapsedTitleTextColor(textColor);
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
        List<ProfileItem> profileItems = new ArrayList<>();
        if (!TextUtils.isEmpty(user.company)) {
            ProfileItem profileUserOrganization = new ProfileItem(Octicons.Icon.oct_organization, user.company, null);
            profileItems.add(profileUserOrganization);
        }
        if (!TextUtils.isEmpty(user.location)) {
            ProfileItem profileUserLocation = new ProfileItem(Octicons.Icon.oct_location, user.location, null);
            profileItems.add(profileUserLocation);
        }
        if (!TextUtils.isEmpty(user.email)) {
            ProfileItem profileUserEmail = new ProfileItem(Octicons.Icon.oct_mail, user.email, null);
            profileItems.add(profileUserEmail);
        }
        if (user.created_at != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.joined_at_date_format), Locale.getDefault());

            String joined = getString(R.string.joined_at, sdf.format(user.created_at));

            ProfileItem profileUserCreated = new ProfileItem(Octicons.Icon.oct_clock, joined, null);
            profileItems.add(profileUserCreated);
        }

        List<ProfileItem> profileItems2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int i1 = 0; i1 < profileItems.size(); i1++) {
                profileItems2.add(profileItems.get(i1));
            }
        }

        profileItemsAdapter.addAll(profileItems2);


        /*CardView view = (CardView) findViewById(R.id.bioCardLayout);
        view.setCardElevation(4);
        BioCard card = new BioCard(user, view, avatarColor);
        card.setBioCardListener(this);*/
    }

    private void fillCardGithubData(User user) {
        /*CardView view = (CardView) findViewById(R.id.dataCardLayout);
        view.setCardElevation(4);
        GithubDataCard dataCard = new GithubDataCard(user, view, avatarColor);
        dataCard.setGithubDataCardListener(this);*/
    }

    private void fillCardPlan(User user) {
        /*CardView view = (CardView) findViewById(R.id.planCardLayout);
        view.setCardElevation(4);
        if (user.plan != null) {
            GithubPlanCard dataCard = new GithubPlanCard(user, view, avatarColor);
            dataCard.setGithubDataCardListener(this);
        } else {
            view.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onFail(RetrofitError error) {
        hideProgressDialog();
    }

    @Override
    public void onCompanyRequest(String company) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, company);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onLocationRequest(String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri geo = Uri.parse("geo:0,0?q=" + location);
        intent.setData(geo);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onMailRequest(String mail) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRepositoriesRequest(String username) {
        Intent intent = ReposActivity.launchIntent(this, username, user.type);
        startActivity(intent);
    }

    @Override
    public void onOrganizationsRequest(String username) {
        Intent intent = OrganizationsActivity.launchIntent(this, username);
        startActivity(intent);
    }

    @Override
    public void onGistsRequest(String username) {
        Intent intent = GistsMainActivity.createLauncherIntent(this, username);
        startActivity(intent);
    }

    @Override
    public void onStarredRequest(String username) {
        Intent intent = StarredReposActivity.launchIntent(this, username);
        startActivity(intent);
    }

    @Override
    public void onWatchedRequest(String username) {
        Intent intent = WatchedReposActivity.launchIntent(this, username);
        startActivity(intent);
    }

    @Override
    public void onCheckFollowUser(String username, boolean following) {
        followingUser = following;
        hideProgressDialog();
        invalidateOptionsMenu();
    }
}
