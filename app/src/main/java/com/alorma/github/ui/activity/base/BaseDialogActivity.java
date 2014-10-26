package com.alorma.github.ui.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.alorma.github.R;

/**
 * Created by Bernat on 06/09/2014.
 */
public class BaseDialogActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showAsDialog();

		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

	}

	private void showAsDialog() {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = getResources().getDimensionPixelOffset(R.dimen.floattingActivityWidth);
		params.height = getResources().getDimensionPixelOffset(R.dimen.floattingActivityHeight);
		params.dimAmount = 0.5f;
		params.alpha = 1.0f;
		getWindow().setAttributes(params);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == android.R.id.home) {
			finish();
		}

		return false;
	}
}
