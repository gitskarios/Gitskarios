package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.search.SearchReposFragment;

/**
 * Created by Bernat on 08/08/2014.
 */
public class SearchActivity extends BackActivity implements SearchReposFragment.OnSearchReposListener, View.OnClickListener, TextView.OnEditorActionListener, TextWatcher {

	private EditText searchEdit;
	private View searchIcon;
	private View cleanIcon;

	public static Intent createLauncherIntent(Context context) {
		return new Intent(context, SearchActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		searchEdit = (EditText) findViewById(R.id.edit);
		searchEdit.setOnEditorActionListener(this);
		searchEdit.addTextChangedListener(this);

		searchIcon = findViewById(R.id.searchIcon);
		cleanIcon = findViewById(R.id.cleanIcon);

		searchIcon.setOnClickListener(this);
	}

	private void onQuery(String query) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		SearchReposFragment fragment = SearchReposFragment.newInstance(query);
		fragment.setQuery(query);
		fragment.setOnSearchReposListener(this);
		ft.replace(R.id.content, fragment);
		ft.commit();
	}

	@Override
	public void onRepoItemSelected(Repo repo) {
		Intent intent = RepoDetailActivity.createLauncherActivity(this, repo.owner.login, repo.name, repo.description);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cleanIcon:
				searchEdit.getText().clear();
				break;
			case R.id.searchIcon:
				InputMethodManager imm = (InputMethodManager) getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
				onQuery(String.valueOf(searchEdit.getText()));
				break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			onQuery(String.valueOf(v.getText()));
		}

		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (searchEdit.getText().length() > 0) {
			cleanIcon.setVisibility(View.VISIBLE);
			cleanIcon.setOnClickListener(this);
		} else {
			cleanIcon.setVisibility(View.INVISIBLE);
			cleanIcon.setOnClickListener(null);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}