package com.alorma.github.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.alorma.github.ui.view.FABCenterLayout;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import it.gmariotti.cardslib.library.view.CardViewNative;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity implements BaseClient.OnResultCallback<User>,
		PaletteUtils.PaletteUtilsListener, BioCard.BioCardListener,
		GithubDataCard.GithubDataCardListener,
		View.OnClickListener,
		FABCenterLayout.FABScrollContentListener,
		OnCheckFollowingUser {

	private static final String USER = "USER";
	private static final String FROM_INTENT_FILTER = "FROM_INTENT_FILTER";


	private CardViewNative cardBio;
	private CardViewNative cardRepos;
	private ViewGroup cardsContainer;
	private FABCenterLayout fabLayout;
	private ImageView image;
	private GithubIconDrawable fabDrawable;
	private int avatarColor;
	private User user;
	private int avatarSecondaryColor;
	private boolean followingUser = false;
	private CardViewNative cardPlan;

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
		avatarSecondaryColor = AttributesUtils.getPrimaryColor(this, R.style.AppTheme_Repos);

		fabLayout = (FABCenterLayout) findViewById(R.id.fabLayout);
		fabLayout.setFabViewVisibility(View.INVISIBLE, false);

		fabLayout.setFabScrollContentListener(this);

		image = (ImageView) findViewById(R.id.image);

		cardsContainer = (ViewGroup) findViewById(R.id.cardsContainer);

		cardsContainer.setVisibility(View.INVISIBLE);

		cardBio = (CardViewNative) findViewById(R.id.cardBio);
		cardRepos = (CardViewNative) findViewById(R.id.cardRepos);
		cardPlan = (CardViewNative) findViewById(R.id.cardPlan);

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

		if (!TextUtils.isEmpty(authUser) && authUser.equals(user.login)) {
			fabLayout.removeFab();
		} else {

			CheckFollowingUser checkFollowingUser = new CheckFollowingUser(this, user.login);
			checkFollowingUser.setOnCheckFollowingUser(this);
			checkFollowingUser.execute();
		}

		if (getSupportActionBar() != null) {
			new PaletteUtils().loadImageAndPalette(user.avatar_url, this);
		}
	}

	private void fillCardBio(User user) {
		BioCard card = new BioCard(this, user, avatarSecondaryColor);

		card.setBioCardListener(this);

		cardBio.setCard(card);
		cardBio.setVisibility(View.VISIBLE);
	}

	private void fillCardRepos(User user) {
		GithubDataCard card = new GithubDataCard(this, user, avatarSecondaryColor);

		card.setGithubDataCardListener(this);

		cardRepos.setCard(card);
		cardRepos.setVisibility(View.VISIBLE);
	}

	private void fillCardPlan(User user) {
		if (user.plan != null) {
			GithubPlanCard card = new GithubPlanCard(this, user, avatarSecondaryColor);

			cardPlan.setCard(card);
			cardPlan.setVisibility(View.VISIBLE);
		} else {
			cardPlan.setVisibility(View.INVISIBLE);
			cardsContainer.removeView(cardPlan);
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
			avatarSecondaryColor = profileSwatch.getRgb();
			if (profileSwatch.getRgb() != 0) {
				fabLayout.setFabColor(avatarSecondaryColor);
				if (fabDrawable != null) {
					fabDrawable.color(avatarColor);
					fabLayout.setFabIcon(fabDrawable);
				}
			}

			if (avatarColor != 0) {
				fabLayout.setFabColorPressed(avatarColor);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					getWindow().setStatusBarColor(avatarColor);
				}
			}
		}

		fillCardBio(user);

		fillCardRepos(user);

		fillCardPlan(user);

		cardsContainer.setVisibility(View.VISIBLE);
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
	public void onClick(View v) {
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


		fabDrawable = new GithubIconDrawable(this, GithubIconify.IconValue.octicon_heart);
		fabDrawable.setStyle(Paint.Style.FILL);
		if (following) {
			fabDrawable.color(avatarColor);
		} else {
			fabDrawable.color(AttributesUtils.getIconsColor(this, R.style.AppTheme_Repos));
		}
		fabDrawable.actionBarSize();
		fabLayout.setFabIcon(fabDrawable);
		fabLayout.setFabClickListener(this, getTagFab());
		fabLayout.setFabViewVisibility(View.VISIBLE, false);

		fabLayout.setFabClickListener(this, null);
	}

	public String getTagFab() {
		int string = followingUser ? R.string.unfollow : R.string.follow;
		return getString(string);
	}
}
