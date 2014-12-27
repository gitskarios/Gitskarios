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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.BaseUsersClient;
import com.alorma.github.sdk.services.user.RequestAutenticatedUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.cards.profile.BioCard;
import com.alorma.github.ui.cards.profile.GithubDataCard;
import com.alorma.github.ui.fragment.profile.ProfileFragment;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.github.ui.view.FABCenterLayout;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import it.gmariotti.cardslib.library.view.CardViewNative;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity implements BaseClient.OnResultCallback<User>,
		PaletteUtils.PaletteUtilsListener, BioCard.BioCardListener, GithubDataCard.GithubDataCardListener, View.OnClickListener, FABCenterLayout.FABScrollContentListener {

	private CardViewNative cardBio;
	private CardViewNative cardRepos;
	private ViewGroup cardsContainer;
	private FABCenterLayout fabLayout;
	private ImageView image;
	private GithubIconDrawable fabDrawable;
	private int avatarColor;
	private User user;
	private int avatarSecondaryColor;

	public static Intent createLauncherIntent(Context context) {
		Intent intent = new Intent(context, ProfileActivity.class);
		return intent;
	}

	public static Intent createLauncherIntent(Context context, User user) {
		Intent intent = new Intent(context, ProfileActivity.class);
		if (user != null) {
			intent.putExtra(ProfileFragment.USER, user);
		}
		return intent;
	}

	public static Intent createIntentFilterLauncherActivity(Context context, User user) {
		Intent intent = new Intent(context, ProfileActivity.class);
		if (user != null) {
			intent.putExtra(ProfileFragment.USER, user);
			intent.putExtra(ProfileFragment.FROM_INTENT_FILTER, true);
		}
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);

		fabLayout = (FABCenterLayout) findViewById(R.id.fabLayout);

		fabLayout.setFabScrollContentListener(this);

		image = (ImageView) findViewById(R.id.image);

		cardsContainer = (ViewGroup) findViewById(R.id.cardsContainer);

		cardBio = (CardViewNative) findViewById(R.id.cardBio);
		cardRepos = (CardViewNative) findViewById(R.id.cardRepos);

		BaseUsersClient<User> requestClient;
		User user = null;
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey(ProfileFragment.USER)) {
				user = getIntent().getParcelableExtra(ProfileFragment.USER);
			}
		}

		if (user != null) {
			requestClient = new RequestUserClient(this, user.login);
			getToolbar().setTitle(user.login);
		} else {
			requestClient = new RequestAutenticatedUserClient(this);
		}

		requestClient.setOnResultCallback(this);
		requestClient.execute();
	}

	@Override
	public void onResponseOk(User user, Response r) {
		this.user = user;
		getToolbar().setTitle(user.login);

		fabDrawable = new GithubIconDrawable(this, GithubIconify.IconValue.octicon_heart);
		fabDrawable.setStyle(Paint.Style.FILL);
		fabDrawable.colorRes(R.color.icons);
		fabDrawable.actionBarSize();
		fabLayout.setFabIcon(fabDrawable);
		fabLayout.setFabClickListener(this, "Follow");

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
				fabDrawable.color(avatarColor);
				fabLayout.setFabIcon(fabDrawable);
			}

			if (avatarColor != 0) {
				fabLayout.setFabColorPressed(avatarColor);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					getWindow().setStatusBarColor(avatarColor);
				}
			}
		}
		
		fillCardBio(user);

		if (user.public_repos > 0 && user.public_gists > 0) {
			fillCardRepos(user);
		} else {
			cardsContainer.removeView(cardRepos);
		}
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
}
