package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;

import de.psdev.licensesdialog.LicensesDialogFragment;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

/**
 * Created by Bernat on 01/01/2015.
 */
public class AboutActivity extends BackActivity {

	public static Intent launchIntent(Context context) {
		return new Intent(context, AboutActivity.class);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generic_toolbar_no_drawer);

		setTitle(R.string.about_title);

		Notices notices = new Notices();

		
		
		
		notices.addNotice(new Notice());
		
		LicensesDialogFragment fragment = LicensesDialogFragment.newInstance(notices, false, true);
		
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.content, fragment);
		ft.commit();
	}
}
