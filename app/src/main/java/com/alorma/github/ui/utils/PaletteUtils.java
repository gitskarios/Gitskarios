package com.alorma.github.ui.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
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

    public static Palette.Swatch getProfileDarkSwatch(Palette palette) {
        if (palette != null) {
            Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
            Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
            Palette.Swatch item = null;
            if (darkVibrantSwatch != null) {
                item = darkVibrantSwatch;
            }
            if (darkMutedSwatch != null) {
                item = darkMutedSwatch;
            }
            return item;
        }

        return null;
    }

    public static Palette.Swatch getProfileLightSwatch(Palette palette) {
        if (palette != null) {
            Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
            Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
            Palette.Swatch item = null;
            if (lightMutedSwatch != null) {
                item = lightMutedSwatch;
            } else if (lightVibrantSwatch != null) {
                item = lightVibrantSwatch;
            }
            return item;
        }

        return null;
    }

    public static Palette.Swatch getProfileMutedSwatch(Palette palette) {
        if (palette != null) {
            Palette.Swatch mutedSwatch = palette.getMutedSwatch();
            Palette.Swatch lightMutedSwatch = palette.getLightMutedSwatch();
            Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
            Palette.Swatch item = null;
            if (lightMutedSwatch != null) {
                item = lightMutedSwatch;
            } else if (darkMutedSwatch != null) {
                item = darkMutedSwatch;
            } else if (mutedSwatch != null) {
                item = mutedSwatch;
            }
            return item;
        }

        return null;
    }

    public static Palette.Swatch getProfileVibrantSwatch(Palette palette) {
        if (palette != null) {
            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
            Palette.Swatch lightVibrantSwatch = palette.getLightVibrantSwatch();
            Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
            Palette.Swatch item = null;
            if (vibrantSwatch != null) {
                item = vibrantSwatch;
            } else if (darkVibrantSwatch != null) {
                item = darkVibrantSwatch;
            } else if (lightVibrantSwatch != null) {
                item = lightVibrantSwatch;
            }
            return item;
        }

        return null;
    }

    public static int colorTextFromBackgroundColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;

        //@see http://stackoverflow.com/questions/12043187/how-to-check-if-hex-color-is-too-black
        double luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

        if (luma < 128) {
            return Color.rgb(255, 255, 255);
        } else {
            return Color.rgb(0, 0, 0);
        }
    }

    public interface PaletteUtilsListener {
        void onImageLoaded(Bitmap loadedImage, Palette palette);
    }
}
