package com.alorma.github.ui.fragment.preference;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.alorma.github.Interceptor;
import com.alorma.github.R;
import com.alorma.github.sdk.utils.GitskariosSettings;

public class GitskariosPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
	private static final String PREF_INTERCEPT = "pref_intercept";
	public static final String REPOS_SORT = "repos_sort";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_prefs);

		findPreference(PREF_INTERCEPT).setOnPreferenceChangeListener(this);

		findPreference(REPOS_SORT).setOnPreferenceChangeListener(this);

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
		}
		return true;
	}
}