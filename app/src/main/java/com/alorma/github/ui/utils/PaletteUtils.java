package com.alorma.github.ui.utils;

import android.support.v7.graphics.Palette;

/**
 * Created by Bernat on 15/07/2014.
 */
public class PaletteUtils {

	public static Palette.Swatch getProfileSwatch(Palette palette) {
		if (palette != null) {
			Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
			Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
			Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
			Palette.Swatch mutedSwatch = palette.getMutedSwatch();
			Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
			Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
			Palette.Swatch item = null;
			if (vibrantSwatch != null) {
				item = vibrantSwatch;
			} else if (darkVibrantSwatch != null) {
				item = darkVibrantSwatch;
			} else if (lightMutedSwatch != null) {
				item = lightMutedSwatch;
			} else if (darkMutedSwatch != null) {
				item = darkMutedSwatch;
			} else if (mutedSwatch != null) {
				item = mutedSwatch;
			} else if (lightVibrantSwatch != null) {
				item = lightVibrantSwatch;
			}
			return item;
		}

		return null;
	}
}
