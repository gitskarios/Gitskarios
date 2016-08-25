package com.alorma.github.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.alorma.github.R;
import com.alorma.github.StoreCredentials;
import com.alorma.github.utils.AttributesUtils;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class DrawerImage implements DrawerImageLoader.IDrawerImageLoader {

  @Override
  public void set(final ImageView imageView, Uri uri, Drawable placeholder) {
    ImageLoader.getInstance().loadImage(uri.toString(), new ImageLoadingListener() {
      @Override
      public void onLoadingStarted(String imageUri, View view) {
        if (imageView != null) {
          imageView.setImageDrawable(generatePlaceHolder(imageView.getContext(), getUsername(imageUri)));
        }
      }

      private String getUsername(String imageUri) {
        return Uri.parse(imageUri).getQueryParameter("username");
      }

      @Override
      public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        if (imageView != null) {
          imageView.setImageDrawable(generatePlaceHolder(imageView.getContext(), getUsername(imageUri)));
        }
      }

      @Override
      public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (imageView != null) {
          imageView.setImageBitmap(loadedImage);
        }
      }

      @Override
      public void onLoadingCancelled(String imageUri, View view) {
        if (imageView != null) {
          imageView.setImageDrawable(generatePlaceHolder(imageView.getContext(), getUsername(imageUri)));
        }
      }
    });
  }

  @Override
  public void cancel(ImageView imageView) {
    String userName = new StoreCredentials(imageView.getContext()).getUserName();
    imageView.setImageDrawable(generatePlaceHolder(imageView.getContext(), userName));
  }

  @Override
  public Drawable placeholder(Context context) {
    String userName = new StoreCredentials(context).getUserName();
    return generatePlaceHolder(context, userName);
  }

  @Override
  public Drawable placeholder(Context context, String tag) {
    String userName = new StoreCredentials(context).getUserName();
    return generatePlaceHolder(context, userName);
  }

  private Drawable generatePlaceHolder(Context context, String userName) {
    if (!TextUtils.isEmpty(userName)) {
      return TextDrawable.builder()
          .beginConfig()
          .width(context.getResources().getDimensionPixelOffset(R.dimen.avatar_size))
          .height(context.getResources().getDimensionPixelOffset(R.dimen.avatar_size))
          .endConfig()
          .buildRound(userName.substring(0, 1), ColorGenerator.MATERIAL.getColor(userName.substring(0, 1)));
    } else {
      IconicsDrawable drawable = new IconicsDrawable(context, Octicons.Icon.oct_octoface);
      drawable.color(AttributesUtils.getSecondaryTextColor(context));
      drawable.sizeDp(24);
      drawable.backgroundColor(ColorGenerator.MATERIAL.getRandomColor());
      return drawable;
    }
  }
}
