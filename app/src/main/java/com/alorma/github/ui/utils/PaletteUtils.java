package com.alorma.github.ui.utils;

import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;

/**
 * Created by Bernat on 15/07/2014.
 */
public class PaletteUtils {

    public static PaletteItem getProfilePaletteItem(Palette palette) {
        if (palette != null) {
            PaletteItem vibrantColor = palette.getVibrantColor();
            PaletteItem lightVibrantColor = palette.getLightVibrantColor();
            PaletteItem darkVibrantColor = palette.getDarkVibrantColor();
            PaletteItem mutedColor = palette.getMutedColor();
            PaletteItem lightMutedColor = palette.getLightMutedColor();
            PaletteItem darkMutedColor = palette.getDarkMutedColor();
            PaletteItem item = null;
            if (vibrantColor != null) {
                item = vibrantColor;
            } else if (darkVibrantColor != null) {
                item = darkVibrantColor;
            } else if (lightMutedColor != null) {
                item = lightMutedColor;
            } else if (lightVibrantColor != null) {
                item = lightVibrantColor;
            } else if (darkMutedColor != null) {
                item = darkMutedColor;
            } else if (mutedColor != null) {
                item = mutedColor;
            }
            return item;
        }

        return null;
    }

    public static PaletteItem getDarkPaletteItem(Palette palette) {
        PaletteItem darkVibrantColor = palette.getDarkVibrantColor();
        PaletteItem darkMutedColor = palette.getDarkMutedColor();
        PaletteItem vibrantColor = palette.getVibrantColor();

        if (darkVibrantColor != null) {
            return darkVibrantColor;
        } else if (darkMutedColor != null) {
            return darkMutedColor;
        } else {
            return null;
        }

    }
}
