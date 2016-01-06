package com.alorma.github.ui.adapter.users;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersHolder {
  public final ImageView authorAvatar;
  public final TextView authorLogin;
  public final TextView authorEmail;

  public UsersHolder(View v) {
    this.authorAvatar = (ImageView) v.findViewById(R.id.profileIcon);
    this.authorLogin = (TextView) v.findViewById(R.id.name);
    this.authorEmail = (TextView) v.findViewById(R.id.email);
  }

  public void fill(User user) {
    if (authorAvatar != null) {
      this.authorAvatar.setVisibility(View.VISIBLE);

      UniversalImageLoaderUtils.loadUserAvatar(authorAvatar, user);
    }

    if (authorLogin != null) {
      this.authorLogin.setVisibility(View.VISIBLE);

      if (!TextUtils.isEmpty(user.login)) {
        authorLogin.setText(user.login);
      }
    }

    if (authorEmail != null) {
      this.authorEmail.setVisibility(View.VISIBLE);
      if (!TextUtils.isEmpty(user.login)) {
        authorEmail.setText(user.email);
      }
    }
  }

  public void clear() {
    if (authorLogin != null) {
      authorLogin.setText("");
      this.authorLogin.setVisibility(View.INVISIBLE);
    }
    if (authorEmail != null) {
      authorEmail.setText("");
      this.authorEmail.setVisibility(View.INVISIBLE);
    }
    if (authorAvatar != null) {
      authorAvatar.setImageDrawable(null);
      this.authorAvatar.setVisibility(View.INVISIBLE);
    }
  }
}
