package com.alorma.github.ui.adapter.gists;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;

import java.util.List;

/**
 * Created by Bernat on 12/07/2014.
 */
public class GistsAdapter extends ArrayAdapter<Gist> {

	private final LayoutInflater mInflater;
	private Context context;

	public GistsAdapter(Context context, List<Gist> gists) {
		super(context, 0, 0, gists);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View v = mInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
		GistsHolder gistsHolder = new GistsHolder(v);

		Gist gist = getItem(position);

		String description = gist.description;
		if (TextUtils.isEmpty(description)) {
			description = context.getResources().getString(R.string.no_gist_description);
		}
		gistsHolder.text1.setText(description);
		gistsHolder.text2.setText("Num files: " + gist.files.size());

		return v;
	}
}
