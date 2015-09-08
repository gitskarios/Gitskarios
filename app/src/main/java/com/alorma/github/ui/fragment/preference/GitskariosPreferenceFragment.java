package com.alorma.github.ui.fragment.preference;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.alorma.github.Interceptor;
import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.gitskarios.core.client.StoreCredentials;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.fragment.ChangelogDialog;

public class GitskariosPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String PREF_INTERCEPT = "pref_intercept";
    private static final String PREF_MARK_AS_READ = "pref_mark_as_read";
    public static final String REPOS_SORT = "repos_sort";
    public static final String REPOS_FILE_TYPE = "repos_download_type";
    public static final String REAUTHORIZE = "reauthorize";
    public static final String GITSKARIOS = "gitskarios";
    public static final String CHANGELOG = "changelog";

    private StoreCredentials credentials;
    private ChangelogDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_prefs);

        GitskariosSettings settings = new GitskariosSettings(getActivity());

        CheckBoxPreference intercetor = (CheckBoxPreference) findPreference(PREF_INTERCEPT);

        ComponentName componentName = new ComponentName(getActivity(), Interceptor.class);
        int componentEnabledSetting = getActivity().getPackageManager().getComponentEnabledSetting(componentName);
        intercetor.setChecked(componentEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
        intercetor.setOnPreferenceChangeListener(this);

        findPreference(REPOS_SORT).setOnPreferenceChangeListener(this);
        findPreference(REPOS_FILE_TYPE).setOnPreferenceChangeListener(this);

        CheckBoxPreference pref_mark_as_read = (CheckBoxPreference) findPreference(PREF_MARK_AS_READ);
        pref_mark_as_read.setChecked(settings.markAsRead());
        pref_mark_as_read.setOnPreferenceChangeListener(this);


        Preference gitskarios = findPreference(GITSKARIOS);
        gitskarios.setOnPreferenceClickListener(this);
        Preference changelog = findPreference(CHANGELOG);
        changelog.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(GITSKARIOS)) {
            startActivity(new UrlsManager(getActivity()).manageRepos(Uri.parse("https://github.com/gitskarios/Gitskarios")));
        } else if (preference.getKey().equals(CHANGELOG)) {
            dialog = ChangelogDialog.create();
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
        }else if (preference.getKey().equals(PREF_MARK_AS_READ)) {
            GitskariosSettings settings = new GitskariosSettings(getActivity());
            Boolean value = (Boolean) newValue;
            settings.saveMarkAsRead(value);
        }
        return true;
    }


    @Override
    public void onStop() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}