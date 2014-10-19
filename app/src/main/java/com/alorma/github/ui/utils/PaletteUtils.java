package com.alorma.github.ui.utils;

import android.support.v7.graphics.Palette;

/**
 * Created by Bernat on 15/07/2014.
 */
public class PaletteUtils {

	public static Palette.Swatch getProfilePaletteItem(Palette palette) {
		if (palette != null) {
			Palette.Swatch vibrantColor = palette.getVibrantSwatch();
			Palette.Swatch lightVibrantColor = palette.getLightVibrantSwatch();
			Palette.Swatch darkVibrantColor = palette.getDarkVibrantSwatch();
			Palette.Swatch mutedColor = palette.getMutedSwatch();
			Palette.Swatch lightMutedColor = palette.getLightMutedSwatch();
			Palette.Swatch darkMutedColor = palette.getDarkMutedSwatch();
			Palette.Swatch item = null;
			if (vibrantColor != null) {
				item = vibrantColor;
			} else if (darkVibrantColor != null) {
				item = darkVibrantColor;
			} else if (lightMutedColor != null) {
				item = lightMutedColor;
			} else if (darkMutedColor != null) {
				item = darkMutedColor;
			} else if (mutedColor != null) {
				item = mutedColor;
			} else if (lightVibrantColor != null) {
				item = lightVibrantColor;
			}
			return item;
		}

		return null;
	}

	public static Palette.Swatch getDarkPaletteItem(Palette palette) {
		Palette.Swatch darkVibrantColor = palette.getDarkVibrantSwatch();
		Palette.Swatch darkMutedColor = palette.getDarkMutedSwatch();
		Palette.Swatch vibrantColor = palette.getVibrantSwatch();

		if (darkVibrantColor != null) {
			return darkVibrantColor;
		} else if (darkMutedColor != null) {
			return darkMutedColor;
		} else {
			return null;
		}

	}
}
