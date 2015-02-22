package com.alorma.github.ui.fragment.preference;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.alorma.github.Interceptor;
import com.alorma.github.R;
import com.alorma.github.inapp.IabConstants;
import com.alorma.github.inapp.IabHelper;
import com.alorma.github.inapp.IabResult;
import com.alorma.github.inapp.Inventory;
import com.alorma.github.inapp.Purchase;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.LoginActivity;

public class GitskariosPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
		/*, IabHelper.OnIabSetupFinishedListener,
		IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener*/ {

	private static final String PREF_INTERCEPT = "pref_intercept";
	public static final String REPOS_SORT = "repos_sort";
	public static final String REPOS_FILE_TYPE = "repos_download_type";
	public static final String REAUTHORIZE = "reauthorize";
//	public static final String DONATE = "donate";

	private StoreCredentials credentials;
	/*private IabHelper iabHelper;
	private boolean iabEnabled;*/
	private Preference donate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_prefs);

		findPreference(PREF_INTERCEPT).setOnPreferenceChangeListener(this);

		findPreference(REPOS_SORT).setOnPreferenceChangeListener(this);

		findPreference(REPOS_FILE_TYPE).setOnPreferenceChangeListener(this);

		Preference reauthorize = findPreference(REAUTHORIZE);

		credentials = new StoreCredentials(getActivity());
		if (credentials.scopes() != null && credentials.scopes().contains("repo")) {
			reauthorize.setEnabled(false);
		} else {
			reauthorize.setEnabled(true);
			reauthorize.setOnPreferenceClickListener(this);
		}

		//donate = findPreference(DONATE);

		//checkIab();

	}
/*
	private void checkIab() {
		iabHelper = new IabHelper(getActivity(), IabConstants.KEY);
		iabHelper.startSetup(this);
	}*/

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(REAUTHORIZE)) {
			credentials.clear();
			Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(loginIntent);
			getActivity().finish();
		} /*else if (preference.getKey().equals(DONATE)) {

		}*/
		return true;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(PREF_INTERCEPT)) {
			Boolean value = (Boolean) newValue;

			int flag = value ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

			ComponentName componentName = new ComponentName(getActivity(), Interceptor.class);
			getActivity().getPackageManager().setComponentEnabledSetting(componentName, flag, PackageManager.DONT_KILL_APP);

		} else if (preference.getKey().equals(REPOS_SORT)) {
			GitskariosSettings settings = new GitskariosSettings(getActivity());
			settings.saveRepoSort(String.valueOf(newValue));
		} else if (preference.getKey().equals(REPOS_FILE_TYPE)) {
			GitskariosSettings settings = new GitskariosSettings(getActivity());
			settings.saveDownloadFileType(String.valueOf(newValue));
		}
		return true;
	}

	/*@Override
	public void onIabSetupFinished(IabResult result) {
		iabEnabled = result.isSuccess();
		if (iabEnabled) {
			try {
				iabHelper.queryInventoryAsync(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (iabHelper != null) {
			iabHelper.dispose();
		}
		iabHelper = null;
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if (result.isFailure()) {
			invalidateDonateOption();
		} else if (info.getSku().equals(IabConstants.SKU_DONATE)) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(IabConstants.SKU_DONATE, true);
			editor.apply();
			invalidateDonateOption();
		}
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) {
		if (result.isFailure()) {
			if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(IabConstants.SKU_DONATE, true);
				editor.apply();
			}
			invalidateDonateOption();
		} else {
			boolean iabDonatePurchased = inv.hasPurchase(IabConstants.SKU_DONATE);
			if (iabDonatePurchased) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean(IabConstants.SKU_DONATE, true);
				editor.apply();
			}
			invalidateDonateOption();
		}
	}

	private void invalidateDonateOption() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		donate.setEnabled(!preferences.getBoolean(IabConstants.SKU_DONATE, false));
	}
*/
}