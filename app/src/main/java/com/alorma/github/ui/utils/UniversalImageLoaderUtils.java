package com.alorma.github.ui.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.alorma.github.R;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;
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
		GithubIconDrawable drawable = new GithubIconDrawable(context, GithubIconify.IconValue.octicon_octoface);
		drawable.color(AttributesUtils.getSecondaryTextColor(context, R.style.AppTheme_Repos));
		drawable.sizeDp(24);
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();

	}
}
