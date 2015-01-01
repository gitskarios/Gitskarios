package com.alorma.github.ui.adapter.users;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;

/**
 * Created by Bernat on 08/11/2014.
 */
public class UiDrawableExternal implements MaterialLargeImageCard.DrawableExternal {
	private String url;

	public UiDrawableExternal(String url) {
		this.url = url;
	}

	@Override
	public void setupInnerViewElements(ViewGroup viewGroup, View view) {
		if (view instanceof ImageView) {
			ImageLoader.getInstance().displayImage(url, (ImageView) view);
		}
	}
}
