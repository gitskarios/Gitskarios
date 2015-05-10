package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapterSpinner extends ArrayAdapter<User> {

	private final LayoutInflater mInflater;

	public UsersAdapterSpinner(Context context, List<User> users) {
		super(context, 0, users);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = mInflater.inflate(R.layout.material_drawer_item_profile, viewGroup, false);
		UsersHolder userHolder = new UsersHolder(v);

		User user = getItem(position);

		userHolder.fill(user);

		return v;
	}

	private String getDate(Date created_at) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return sdf.format(created_at);
	}

	public void addAll(Collection<? extends User> collection, boolean paging) {
		if (!paging) {
			clear();
		}
		super.addAll(collection);
	}
}
