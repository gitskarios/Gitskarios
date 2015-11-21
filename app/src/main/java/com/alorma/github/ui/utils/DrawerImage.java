package com.alorma.github.ui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by bernat.borras on 24/10/15.
 */
public class DrawerImage implements DrawerImageLoader.IDrawerImageLoader {

  @Override
  public void set(ImageView imageView, Uri uri, Drawable placeholder) {
    ImageLoader.getInstance().displayImage(uri.toString(), imageView);
  }

  @Override
  public void cancel(ImageView imageView) {

  }

  @Override
  public Drawable placeholder(Context context) {
    return new IconicsDrawable(context, Octicons.Icon.oct_octoface);
  }

  @Override
  public Drawable placeholder(Context ctx, String tag) {
    IconicsDrawable drawable = new IconicsDrawable(ctx, Octicons.Icon.oct_octoface);
    drawable.color(AttributesUtils.getSecondaryTextColor(ctx));
    drawable.sizeDp(24);
    return drawable;
  }
}
