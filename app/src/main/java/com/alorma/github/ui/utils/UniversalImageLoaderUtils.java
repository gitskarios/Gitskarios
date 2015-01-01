package com.alorma.github.ui.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.alorma.github.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Bernat on 15/07/2014.
 */
public class UniversalImageLoaderUtils {

	public static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.defaultDisplayImageOptions(getDisplayImageOptions(context))
				.build();
		return config;
	}

	public static DisplayImageOptions getDisplayImageOptions(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(new ColorDrawable(context.getResources().getColor(R.color.accent)))
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		return options;

	}
}
