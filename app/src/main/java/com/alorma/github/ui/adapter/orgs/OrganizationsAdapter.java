package com.alorma.github.ui.adapter.orgs;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListOrganizations;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.users.UsersHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 04/09/2014.
 */
public class OrganizationsAdapter extends ArrayAdapter<Organization> {
	private final LayoutInflater mInflater;

	public OrganizationsAdapter(Context context, List<Organization> organizations) {
		super(context, 0, organizations);
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = mInflater.inflate(R.layout.row_user, viewGroup, false);
		UsersHolder userHolder = new UsersHolder(v);

		Organization organization = getItem(position);

		if (organization.name != null) {
			userHolder.textView.setText(organization.name);
		} else if (organization.login != null) {
			userHolder.textView.setText(organization.login);
		}

		ImageLoader.getInstance().displayImage(organization.avatar_url, userHolder.imageView);

		return v;
	}
}
