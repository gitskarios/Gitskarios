package com.alorma.github.ui.adapter.users;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersHolder  {
	public final ImageView authorAvatar;
	public final TextView authorLogin;
	public final TextView authorDate;

	public UsersHolder(View v) {
		this.authorAvatar = (ImageView) v.findViewById(R.id.authorAvatar);
		this.authorLogin = (TextView) v.findViewById(R.id.authorLogin);
		this.authorDate = (TextView) v.findViewById(R.id.authorDate);
	}
}
