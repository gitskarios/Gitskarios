package com.alorma.github.ui.adapter.detail.repo;

import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ContentType;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */
public class RepoSourceAdapter extends ArrayAdapter<Content> {

	private final LayoutInflater inflater;
	private Context context;
	private int style;

	public RepoSourceAdapter(Context context, List<Content> objects, @StyleRes int style) {
		super(context, 0, objects);
		this.context = context;
		this.style = style;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = inflater.inflate(R.layout.row_content, parent, false);

		TextView textName = (TextView) v.findViewById(R.id.name);
		ImageView image = (ImageView) v.findViewById(R.id.icon);

		Content item = getItem(position);

		textName.setText(item.name);

		GithubIconDrawable iconDrawable = null;
		if (ContentType.dir.equals(item.type)) {
			iconDrawable = new GithubIconDrawable(context, GithubIconify.IconValue.octicon_file_directory);
		} else if (ContentType.submodule.equals(item.type)) {
			iconDrawable = new GithubIconDrawable(context, GithubIconify.IconValue.octicon_file_submodule);
		} else if (ContentType.file.equals(item.type)) {
			iconDrawable = new GithubIconDrawable(context, GithubIconify.IconValue.octicon_file_text);
		}

		if (iconDrawable != null) {
			iconDrawable.sizeDp(GithubIconDrawable.ANDROID_ACTIONBAR_ICON_SIZE_DP);
			iconDrawable.color(AttributesUtils.getPrimaryLightColor(getContext(), style));

			image.setImageDrawable(iconDrawable);
		}

		return v;
	}
}
