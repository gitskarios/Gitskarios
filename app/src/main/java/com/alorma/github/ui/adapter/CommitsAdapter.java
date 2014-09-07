package com.alorma.github.ui.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.alorma.github.sdk.bean.dto.response.Commit;

import java.util.List;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsAdapter extends ArrayAdapter<Commit>{
	private boolean lazyLoading;

	public CommitsAdapter(Context context, List<Commit> objects) {
		super(context, android.R.layout.simple_list_item_1, objects);
	}

	public void setLazyLoading(boolean lazyLoading) {
		this.lazyLoading = lazyLoading;
	}

	public boolean isLazyLoading() {
		return lazyLoading;
	}
}
