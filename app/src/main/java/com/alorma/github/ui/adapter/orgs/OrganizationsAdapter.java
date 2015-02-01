package com.alorma.github.ui.adapter.orgs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.sdk.bean.dto.response.Organization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
		return null;
	}

	private String getDate(Date created_at) {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return sdf.format(created_at);
	}

	public void addAll(Collection<? extends Organization> collection, boolean paging) {
		if (!paging) {
			clear();
		}
		super.addAll(collection);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getView(position, convertView, parent);
	}
}
