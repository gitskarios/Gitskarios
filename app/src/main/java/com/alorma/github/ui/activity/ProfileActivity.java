package com.alorma.github.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.user.GithubUsersClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.user.BaseUsersClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.sdk.services.user.follow.CheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.FollowUserClient;
import com.alorma.github.sdk.services.user.follow.OnCheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.UnfollowUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.activity.gists.GistsMainActivity;
import com.alorma.github.ui.cards.profile.BioCard;
import com.alorma.github.ui.cards.profile.GithubDataCard;
import com.alorma.github.ui.cards.profile.GithubPlanCard;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.github.ui.view.FABCenterLayout;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.gitskarios.basesdk.client.StoreCredentials;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity implements BaseClient.OnResultCallback<User>,
		PaletteUtils.PaletteUtilsListener, BioCard.BioCardListener,
		GithubDataCard.GithubDataCardListener,
		FABCenterLayout.FABScrollContentListener,
		OnCheckFollowingUser, GithubPlanCard.GithubDataCardListener, View.OnClickListener {

	private static final String USER = "USER";
	private static final String FROM_INTENT_FILTER = "FROM_INTENT_FILTER";

	private ImageView image;
	private FABCenterLayout fabLayout;

	private int avatarColor;
	private int avatarSecondaryColor;

	private User user;
	private boolean followingUser = false;
	private boolean isAuthUser;
	private IconicsDrawable fabDrawable;

	public static Intent createLauncherIntent(Context context) {
		return new Intent(context, ProfileActivity.class);
	}

	public static Intent createLauncherIntent(Context context, User user) {
		Intent intent = new Intent(context, ProfileActivity.class);
		if (user != null) {
			intent.putExtra(USER, user);
		}
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);

		avatarColor = AttributesUtils.getAccentColor(this);
		avatarSecondaryColor = AttributesUtils.getPrimaryColor(this);

		fabLayout = (FABCenterLayout) findViewById(R.id.fabLayout);
		fabLayout.setFabViewVisibility(View.INVISIBLE, false);
		fabLayout.setVisibility(View.INVISIBLE);

		fabLayout.setFabScrollContentListener(this);
		image = (ImageView) findViewById(R.id.image);
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
                isAuthUser = true;
				requestClient = new GetAuthUserClient(this);
			} else {
				requestClient = new RequestUserClient(this, user.login);
				getToolbar().setTitle(user.login);
			}
		} else {
            isAuthUser = true;
			requestClient = new GetAuthUserClient(this);
		}

		showProgressDialog(R.style.SpotDialog_LoadingUser);

		requestClient.setOnResultCallback(this);
		requestClient.execute();
	}

	@Override
	public void onResponseOk(User user, Response r) {
		this.user = user;
		getToolbar().setTitle(user.login);

		hideProgressDialog();

		if (isAuthUser) {
			fabLayout.removeFab();

		} else {
			CheckFollowingUser checkFollowingUser = new CheckFollowingUser(this, user.login);
			checkFollowingUser.setOnCheckFollowingUser(this);
			checkFollowingUser.execute();
		}

		if (getSupportActionBar() != null) {
			new PaletteUtils().loadImageAndPalette(user.avatar_url, this);
		}

		fabLayout.setVisibility(View.VISIBLE);
	}

    @Override
    public void onImageLoaded(Bitmap loadedImage, Palette palette) {

        Palette.Swatch profileSwatchDark = PaletteUtils.getProfileSwatchDark(palette);
        Palette.Swatch profileSwatch = PaletteUtils.getProfileSwatch(palette);

        Drawable drawable = new BitmapDrawable(getResources(), loadedImage);

        image.setImageDrawable(drawable);
        if (profileSwatchDark != null && profileSwatch != null) {
            avatarColor = profileSwatchDark.getRgb();
            avatarSecondaryColor = profileSwatch.getRgb();

            try {
                if (avatarColor != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(avatarColor);
                        getWindow().setNavigationBarColor(avatarColor);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fillCardBio(user);

        fillCardGithubData(user);

        fillCardPlan(user);
    }

	private void fillCardBio(User user) {
		CardView view = (CardView) findViewById(R.id.bioCardLayout);
		view.setCardElevation(4);
		BioCard card = new BioCard(user, view, avatarColor);
		card.setBioCardListener(this);
	}

	private void fillCardGithubData(User user) {
		CardView view = (CardView) findViewById(R.id.dataCardLayout);
		view.setCardElevation(4);
		GithubDataCard dataCard = new GithubDataCard(user, view, avatarColor);
		dataCard.setGithubDataCardListener(this);
	}

	private void fillCardPlan(User user) {
		CardView view = (CardView) findViewById(R.id.planCardLayout);
		view.setCardElevation(4);
		if (user.plan != null) {
			GithubPlanCard dataCard = new GithubPlanCard(user, view, avatarColor);
			dataCard.setGithubDataCardListener(this);
		} else {
			view.setVisibility(View.GONE);
		}
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
	public void onClick(View v) {
		showProgressDialog(R.style.SpotDialog_LoadingUser);

		fabLayout.setFabClickListener(null, getTagFab());

		if (followingUser) {
			UnfollowUserClient unfollowUserClient = new UnfollowUserClient(this, user.login);
			unfollowUserClient.setOnCheckFollowingUser(this);
			unfollowUserClient.execute();
		} else {
			FollowUserClient followUserClient = new FollowUserClient(this, user.login);
			followUserClient.setOnCheckFollowingUser(this);
			followUserClient.execute();
		}
		fabLayout.setFabClickListener(null, null);
	}

	@Override
	public void onScrollFactor(int alpha, float factor) {
		if (getSupportActionBar() != null) {
			ColorDrawable cd = new ColorDrawable(avatarColor);

			if (alpha < 0) {
				alpha = -alpha;
			}

			if (alpha > 255) {
				alpha = 255;
			}

			cd.setAlpha(alpha);

			getSupportActionBar().setBackgroundDrawable(cd);
		}
	}

	@Override
	public void onCheckFollowUser(String username, boolean following) {
		followingUser = following;

		hideProgressDialog();

		fabDrawable = new IconicsDrawable(this, Octicons.Icon.oct_heart);
		if (following) {
			fabDrawable.color(avatarColor);
		} else {
			fabDrawable.color(AttributesUtils.getIconsColor(this));
		}
		fabDrawable.actionBarSize();
		fabLayout.setFabIcon(fabDrawable);

		fabLayout.setFabColor(avatarSecondaryColor);

		fabLayout.setFabColorPressed(avatarColor);

		fabLayout.setFabClickListener(this, getTagFab());
		fabLayout.setFabViewVisibility(View.VISIBLE, false);
	}

	public String getTagFab() {
		int string = followingUser ? R.string.unfollow : R.string.follow;
		return getString(string);
	}
}
