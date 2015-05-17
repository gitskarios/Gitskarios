package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.view.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collection;
import java.util.List;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapter extends LazyAdapter<User> {


	private String owner;

	public UsersAdapter(Context context, List<User> users) {
		super(context, 0, users);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = inflate(R.layout.row_user, viewGroup, false);
		User user = getItem(position);

		CircularImageView imageView = (CircularImageView) v.findViewById(R.id.avatarAuthorImage);

		ImageLoader.getInstance().displayImage(user.avatar_url, imageView);

		TextView textView = (TextView) v.findViewById(R.id.textAuthorLogin);

		textView.setText(user.login);

		View divider = v.findViewById(R.id.divider);

		if (position == getCount()) {
			divider.setVisibility(View.GONE);
		}

		TextView userRepoOwner = (TextView) v.findViewById(R.id.userRepoOwner);
		if (owner != null && owner.equalsIgnoreCase(user.login)) {
			userRepoOwner.setVisibility(View.VISIBLE);
		} else {
			userRepoOwner.setVisibility(View.INVISIBLE);
		}

		return v;
	}

	public void setRepoOwner(String owner) {
		this.owner = owner;
	}
}
