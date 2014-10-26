package com.alorma.github.ui.activity.base;

import android.os.Bundle;
import android.view.MenuItem;

import com.alorma.github.R;

/**
 * Created by Bernat on 17/07/2014.
 */
public class BackActivity extends BaseActivity {

	@Override
	protected void onStart() {
		super.onStart();
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
