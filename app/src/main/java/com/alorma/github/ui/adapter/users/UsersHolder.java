package com.alorma.github.ui.adapter.users;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
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

    public UsersHolder(Activity act) {
        this.authorAvatar = (ImageView) act.findViewById(R.id.profileIcon);
        this.authorLogin = (TextView) act.findViewById(R.id.name);
        this.authorEmail = (TextView) act.findViewById(R.id.email);
    }

    public void fill(User user) {
        this.authorAvatar.setVisibility(View.VISIBLE);
        this.authorLogin.setVisibility(View.VISIBLE);
        this.authorEmail.setVisibility(View.VISIBLE);

        ImageLoader.getInstance().displayImage(user.avatar_url, authorAvatar);
        if (!TextUtils.isEmpty(user.login)) {
            authorLogin.setText(user.login);
        }
        if (!TextUtils.isEmpty(user.login)) {
            authorEmail.setText(user.email);
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
