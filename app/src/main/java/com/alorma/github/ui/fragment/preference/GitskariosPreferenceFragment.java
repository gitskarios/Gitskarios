package com.alorma.github.ui.fragment.preference;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.alorma.github.Interceptor;
import com.alorma.github.R;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.LoginActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.fragment.ChangelogDialog;
import com.alorma.github.ui.fragment.ChangelogDialogSupport;

public class GitskariosPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

	private static final String PREF_INTERCEPT = "pref_intercept";
	public static final String REPOS_SORT = "repos_sort";
	public static final String REPOS_FILE_TYPE = "repos_download_type";
	public static final String REAUTHORIZE = "reauthorize";
	public static final String GITSKARIOS = "gitskarios";
	public static final String CHANGELOG = "changelog";

	private StoreCredentials credentials;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_prefs);

		CheckBoxPreference intercetor = (CheckBoxPreference) findPreference(PREF_INTERCEPT);
		intercetor.setOnPreferenceChangeListener(this);

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

		Preference gitskarios = findPreference(GITSKARIOS);
		gitskarios.setOnPreferenceClickListener(this);

		Preference changelog = findPreference(CHANGELOG);
		changelog.setOnPreferenceClickListener(this);
	}
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(REAUTHORIZE)) {
			credentials.clear();
			Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(loginIntent);
			getActivity().finish();
		} else if (preference.getKey().equals(GITSKARIOS)) {
			Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), "alorma", "gitskarios");
			startActivity(intent);
		}else if (preference.getKey().equals(CHANGELOG)) {
			ChangelogDialog dialog = ChangelogDialog.create(false, getResources().getColor(R.color.accent));
			dialog.show(getFragmentManager(), "changelog");
		}
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
}