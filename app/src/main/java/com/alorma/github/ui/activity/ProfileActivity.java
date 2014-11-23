package com.alorma.github.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.BaseUsersClient;
import com.alorma.github.sdk.services.user.RequestAutenticatedUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.cards.profile.BioCard;
import com.alorma.github.ui.cards.profile.ReposCard;
import com.alorma.github.ui.fragment.profile.ProfileFragment;
import com.alorma.github.ui.utils.PaletteUtils;
import com.alorma.github.ui.view.MyScroll;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2014.
 */
public class ProfileActivity extends BackActivity implements BaseClient.OnResultCallback<User>,
		PaletteUtils.PaletteUtilsListener, BioCard.BioCardListener {

	private CardViewNative cardBio;
	private CardViewNative cardRepos;
	private ViewGroup cardsContainer;

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
		getToolbar().setTitle(user.login);

		fillCardBio(user);

		if (user.public_repos > 0 && user.public_gists > 0){
			fillCardRepos(user);
		} else {
			cardsContainer.removeView(cardRepos);
		}

		if (getSupportActionBar() != null) {
			new PaletteUtils().loadImageAndPalette(user.avatar_url, this);
		}
	}

	private void fillCardBio(User user) {
		BioCard card = new BioCard(this, user);

		card.setBioCardListener(this);

		cardBio.setCard(card);
		cardBio.setVisibility(View.VISIBLE);
	}

	private void fillCardRepos(User user) {
		ReposCard card = new ReposCard(this, user);

		cardRepos.setCard(card);
		cardRepos.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFail(RetrofitError error) {

	}

	@Override
	public void onImageLoaded(Bitmap loadedImage, Palette palette) {

		Palette.Swatch profileSwatchDark = PaletteUtils.getProfileSwatchDark(palette);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setBackgroundDrawable(new BitmapDrawable(getResources(), loadedImage));

			if (profileSwatchDark != null && profileSwatchDark.getRgb() != 0) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					getWindow().setStatusBarColor(profileSwatchDark.getRgb());
				}
			}
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
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {mail});
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}
}
