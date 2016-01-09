package com.alorma.github.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.utils.AttributesUtils;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.musenkishi.atelier.Atelier;
import com.musenkishi.atelier.ColorType;
import com.musenkishi.atelier.swatch.DarkVibrantSwatch;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Bernat on 15/07/2014.
 */
public class UniversalImageLoaderUtils {

    public static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {
        ImageLoaderConfiguration config =
                new ImageLoaderConfiguration.Builder(context)
                        .defaultDisplayImageOptions(getDisplayImageOptions(context))
                        .imageDecoder(new BaseImageDecoder(true))
                        .build();
        return config;
    }

    public static DisplayImageOptions getDisplayImageOptions(Context context) {
        IconicsDrawable drawable = new IconicsDrawable(context, Octicons.Icon.oct_octoface);
        drawable.color(AttributesUtils.getSecondaryTextColor(context));
        drawable.sizeDp(24);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), drawableToBitmap(drawable));
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(bitmapDrawable)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void loadUserAvatar(final ImageView imageView, Organization actor) {
        final int defaultProfileColor = ContextCompat.getColor(imageView.getContext(), R.color.primary);

        int avatarSize = imageView.getResources().getDimensionPixelOffset(R.dimen.avatar_size);

        if (imageView.getWidth() != 0 && imageView.getHeight() != 0) {
            avatarSize = Math.min(imageView.getWidth(), imageView.getHeight());
        }

        if (!TextUtils.isEmpty(actor.login)) {
            ColorGenerator generator = ColorGenerator.MATERIAL;

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(avatarSize)
                    .height(avatarSize)
                    .endConfig()
                    .buildRound(actor.login.substring(0, 1), generator.getColor(actor.login.substring(0, 1)));
            imageView.setImageDrawable(drawable);
        }


        String avatarUrl = actor.avatar_url;

        ImageLoader.getInstance().cancelDisplayTask(imageView);
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .build();
        ImageLoader.getInstance().displayImage(avatarUrl, imageView, displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Context context = imageView.getContext();

                //Set color tag to imageView
                Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
                        .listener(new Atelier.OnPaletteRenderedListener() {
                            @Override
                            public void onRendered(Palette palette) {
                                imageView.setTag(palette.getVibrantColor(defaultProfileColor));
                            }
                        });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static void loadUserAvatarSquare(final ImageView imageView, Organization actor) {
        final int defaultProfileColor = ContextCompat.getColor(imageView.getContext(), R.color.primary);

        int avatarSize = imageView.getResources().getDimensionPixelOffset(R.dimen.avatar_size);

        if (imageView.getWidth() != 0 && imageView.getHeight() != 0) {
            avatarSize = Math.min(imageView.getWidth(), imageView.getHeight());
        }

        if (!TextUtils.isEmpty(actor.login)) {
            ColorGenerator generator = ColorGenerator.MATERIAL;

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(avatarSize)
                    .height(avatarSize)
                    .endConfig()
                    .buildRect(actor.login.substring(0, 1), generator.getColor(actor.login.substring(0, 1)));
            imageView.setImageDrawable(drawable);
        }


        String avatarUrl = actor.avatar_url;

        ImageLoader.getInstance().cancelDisplayTask(imageView);
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .build();
        ImageLoader.getInstance().displayImage(avatarUrl, imageView, displayImageOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Context context = imageView.getContext();


                //Set color tag to imageView
                Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
                        .listener(new Atelier.OnPaletteRenderedListener() {
                            @Override
                            public void onRendered(Palette palette) {
                                imageView.setTag(palette.getVibrantColor(defaultProfileColor));
                            }
                        });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
}
