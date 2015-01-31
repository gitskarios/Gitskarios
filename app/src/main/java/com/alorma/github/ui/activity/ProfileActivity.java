package com.alorma.github.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.BaseUsersClient;
import com.alorma.github.sdk.services.user.GetAuthUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.sdk.services.user.follow.CheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.FollowUserClient;
import com.alorma.github.sdk.services.user.follow.OnCheckFollowingUser;
import com.alorma.github.sdk.services.user.follow.UnfollowUserClient;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.cards.profile.BioCard;
import com.alorma.github.ui.cards.profile.GithubDataCard;
import com.alorma.github.ui.cards.profile.GithubPlanCard;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

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
	private static final String FROM_INTENT_FILTER = "FROM_INTENT_FILTER";

	private ImageView image;
	private int avatarColor;
	private User user;
	private boolean followingUser = false;
	private boolean isAuthUser;

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

	public static Intent createIntentFilterLauncherActivity(Context context, User user) {
		Intent intent = new Intent(context, ProfileActivity.class);
		if (user != null) {
			intent.putExtra(USER, user);
			intent.putExtra(FROM_INTENT_FILTER, true);
		}
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);

		avatarColor = AttributesUtils.getAccentColor(this, R.style.AppTheme_Repos);
		image = (ImageView) findViewById(R.id.image);

		BaseUsersClient<User> requestClient;
		User user = null;
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey(USER)) {
				user = getIntent().getParcelableExtra(USER);
			}
		}

		if (user != null) {
			requestClient = new RequestUserClient(this, user.login);
			getToolbar().setTitle(user.login);
		} else {
			requestClient = new GetAuthUserClient(this);
		}

		requestClient.setOnResultCallback(this);
		requestClient.execute();
	}

	@Override
	public void onResponseOk(User user, Response r) {
		this.user = user;
		getToolbar().setTitle(user.login);

		GitskariosSettings gitskariosSettings = new GitskariosSettings(this);
		String authUser = gitskariosSettings.getAuthUser(null);
		isAuthUser = !TextUtils.isEmpty(authUser) && authUser.equals(user.login);

		if (!isAuthUser) {
			CheckFollowingUser checkFollowingUser = new CheckFollowingUser(this, user.login);
			checkFollowingUser.setOnCheckFollowingUser(this);
			checkFollowingUser.execute();
		}

		if (getSupportActionBar() != null) {
			new PaletteUtils().loadImageAndPalette(user.avatar_url, this);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (!isAuthUser) {
			menu.clear();

			int title = R.string.follow;
			if (followingUser) {
				title = R.string.unfollow;
			}
			menu.add(0, R.id.action_user_star, 0, title);

			MenuItem item = menu.findItem(R.id.action_user_star);

			GithubIconDrawable drawable = new GithubIconDrawable(this, GithubIconify.IconValue.octicon_heart);
			drawable.setStyle(Paint.Style.FILL);
			if (followingUser) {
				drawable.color(avatarColor);
			} else {
				drawable.color(AttributesUtils.getIconsColor(this, R.style.AppTheme_Repos));
			}
			drawable.actionBarSize();

			item.setIcon(drawable);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
			case R.id.action_user_star:
				if (followingUser) {
					UnfollowUserClient unfollowUserClient = new UnfollowUserClient(this, user.login);
					unfollowUserClient.setOnCheckFollowingUser(this);
					unfollowUserClient.execute();
				} else {
					FollowUserClient followUserClient = new FollowUserClient(this, user.login);
					followUserClient.setOnCheckFollowingUser(this);
					followUserClient.execute();
				}
				break;
		}

		return true;
	}

	private void fillCardBio(User user) {
		CardView view = (CardView) findViewById(R.id.bioCardLayout);
		view.setCardElevation(4);
		BioCard card = new BioCard(user, view, avatarColor);
		card.setBioCardListener(this);
	}

	private void fillCardRepos(User user) {
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

	}

	@Override
	public void onImageLoaded(Bitmap loadedImage, Palette palette) {

		Palette.Swatch profileSwatchDark = PaletteUtils.getProfileSwatchDark(palette);
		Palette.Swatch profileSwatch = PaletteUtils.getProfileSwatch(palette);

		Drawable drawable = new BitmapDrawable(getResources(), loadedImage);

		image.setImageDrawable(drawable);
		if (profileSwatchDark != null && profileSwatch != null) {
			avatarColor = profileSwatchDark.getRgb();

			if (avatarColor == 0 || avatarColor == -10790053 || avatarColor == -14343336 || avatarColor == -12566464 || avatarColor == -9826790) {
				avatarColor = Color.GRAY;
				getToolbar().setTitleTextColor(avatarColor);
			}

			if (profileSwatch.getRgb() != 0) {
				if (!isAuthUser) {
					invalidateOptionsMenu();
				}
			}

			if (avatarColor != 0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					getWindow().setStatusBarColor(avatarColor);
					getWindow().setNavigationBarColor(avatarColor);
				}
			}
		}

		fillCardBio(user);

		fillCardRepos(user);

		fillCardPlan(user);
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
		Intent intent = ReposActivity.launchIntent(this, username);
		startActivity(intent);
	}

	@Override
	public void onCheckFollowUser(String username, boolean following) {
		followingUser = following;

		invalidateOptionsMenu();
	}
}
