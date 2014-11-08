package com.alorma.github.ui.activity.base;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.alorma.github.R;

/**
 * Created by Bernat on 06/09/2014.
 */
public class BaseDialogActivity extends BackActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showAsDialog();
	}

	private void showAsDialog() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = getResources().getDimensionPixelOffset(R.dimen.floattingActivityWidth);
		params.height = getResources().getDimensionPixelOffset(R.dimen.floattingActivityHeight);
		params.dimAmount = 0.5f;
		params.alpha = 1.0f;
		getWindow().setAttributes(params);
	}
}
