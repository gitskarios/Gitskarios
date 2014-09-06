package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;

/**
 * Created by Bernat on 09/08/2014.
 */
public class TextDescriptionFragment extends BaseFragment {

	private TextView tv;

	public static TextDescriptionFragment newInstance(String text) {
		Bundle args = new Bundle();
		args.putString("TEXT", text);

		TextDescriptionFragment fragment = new TextDescriptionFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.small_pager_text, null, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tv = (TextView) view.findViewById(R.id.text);

		String text = getArguments().getString("TEXT");
		tv.setText(text);
	}

	public void setText(String text) {
		if (tv != null) {
			tv.setText(text);
		}
	}
}
