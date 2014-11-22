package com.alorma.github.ui.cards.profile;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import java.text.SimpleDateFormat;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Bernat on 22/11/2014.
 */
public class BioCard extends Card{
	private User user;

	public BioCard(Context context, User user) {
		super(context, R.layout.card_bio_layout);
		this.user = user;
		CardHeader header = new CardHeader(context);
		header.setTitle(user.name);
		addCardHeader(header);
		setCardElevation(4f);
	}

	@Override
	public void setupInnerViewElements(ViewGroup parent, View view) {
		super.setupInnerViewElements(parent, view);

		setUpCompany(view);
		setUpLocation(view);
		setUpMail(view);
		setUpDate(view);
	}

	private void setUpCompany(View view) {
		if (!TextUtils.isEmpty(user.company)) {
			ImageView icon = (ImageView) view.findViewById(R.id.iconCompany);

			GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_organization);

			icon.setImageDrawable(githubIconDrawable);

			TextView text = (TextView) view.findViewById(R.id.textCompany);

			text.setText(user.company);
		} else {
			view.findViewById(R.id.company).setVisibility(View.GONE);
			view.findViewById(R.id.dividerCompany).setVisibility(View.GONE);
		}
	}

	private void setUpLocation(View view) {
		if (!TextUtils.isEmpty(user.location)) {
			ImageView icon = (ImageView) view.findViewById(R.id.iconLocation);

			GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_location);

			icon.setImageDrawable(githubIconDrawable);

			TextView text = (TextView) view.findViewById(R.id.textLocation);

			text.setText(user.location);
		} else {
			view.findViewById(R.id.location).setVisibility(View.GONE);
			view.findViewById(R.id.dividerLocation).setVisibility(View.GONE);
		}
	}

	private void setUpMail(View view) {
		if (!TextUtils.isEmpty(user.email)) {
			ImageView icon = (ImageView) view.findViewById(R.id.iconMail);

			GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_mail);

			icon.setImageDrawable(githubIconDrawable);

			TextView text = (TextView) view.findViewById(R.id.textMail);

			text.setText(user.email);
		} else {
			view.findViewById(R.id.mail).setVisibility(View.GONE);
			view.findViewById(R.id.dividerMail).setVisibility(View.GONE);
		}
	}

	private void setUpDate(View view) {
		if (user.created_at != null) {
			ImageView icon = (ImageView) view.findViewById(R.id.iconDate);

			GithubIconDrawable githubIconDrawable = drawable(view.getContext(), GithubIconify.IconValue.octicon_clock);

			icon.setImageDrawable(githubIconDrawable);

			TextView text = (TextView) view.findViewById(R.id.textDate);

			SimpleDateFormat sdf = new SimpleDateFormat(view.getContext().getString(R.string.joined_at_date_format), Locale.getDefault());

			String joined = view.getContext().getString(R.string.joined_at, sdf.format(user.created_at));

			text.setText(joined);
		} else {
			view.findViewById(R.id.date).setVisibility(View.GONE);
		}
	}

	private GithubIconDrawable drawable(Context context, GithubIconify.IconValue icon) {
		GithubIconDrawable githubIconDrawable = new GithubIconDrawable(context, icon);

		githubIconDrawable.colorRes(R.color.accentDark);

		return githubIconDrawable;
	}
}
