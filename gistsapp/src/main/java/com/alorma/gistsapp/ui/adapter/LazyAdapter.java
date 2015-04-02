package com.alorma.gistsapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Bernat on 03/10/2014.
 */
public abstract class LazyAdapter<T> extends ArrayAdapter<T> {

	private final LayoutInflater mInflater;
	private boolean lazyLoading;

	public LazyAdapter(Context context, int resource) {
		super(context, resource);
		mInflater = LayoutInflater.from(context);
	}

	public LazyAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		mInflater = LayoutInflater.from(context);
	}

	public LazyAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
		mInflater = LayoutInflater.from(context);
	}

	public LazyAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
		mInflater = LayoutInflater.from(context);
	}

	public LazyAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		mInflater = LayoutInflater.from(context);
	}

	public LazyAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
		mInflater = LayoutInflater.from(context);
	}

	public LazyAdapter(Context context, ArrayList<T> list) {
		super(context, 0, list);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	protected View inflate(int resource, ViewGroup parent, boolean attach) {
		return mInflater.inflate(resource, parent, attach);
	}

	public boolean isLazyLoading() {
		return lazyLoading;
	}

	public void setLazyLoading(boolean lazyLoading) {
		this.lazyLoading = lazyLoading;
	}

	public void addAll(Collection<? extends T> collection, boolean paging) {
		if (!paging) {
			clear();
		}
		super.addAll(collection);
	}
}
