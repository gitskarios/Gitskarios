package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapter extends ArrayAdapter<User> {

	private final LayoutInflater mInflater;

	public UsersAdapter(Context context, List<User> users) {
		super(context, 0, users);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = mInflater.inflate(R.layout.row_user, viewGroup, false);
		User user = getItem(position);

		CardViewNative cardViewNative = (CardViewNative) v.findViewById(R.id.carddemo_largeimage);

		MaterialLargeImageCard card =
				MaterialLargeImageCard.with(getContext())
						.setTitle(user.login)
						.useDrawableExternal(new UiDrawableExternal(user.avatar_url))
		.build();
		cardViewNative.setCard(card);

		return v;
	}

	public void addAll(Collection<? extends User> collection, boolean paging) {
		if (!paging) {
			clear();
		}
		super.addAll(collection);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}
