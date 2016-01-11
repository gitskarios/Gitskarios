package com.alorma.github.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.BranchesCallback;
import com.alorma.github.sdk.services.repo.DeleteRepoClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.ui.activity.ContentEditorActivity;

import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by a557114 on 01/08/2015.
 */
public class RepositoryManagerFragment extends PreferenceFragment {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String REQUEST_DTO = "REQUEST_DTO";
    private static final int DESCRIPTION_EDIT = 544;

    private RepoInfo repoInfo;
    private RepoRequestDTO repoRequestDTO;
    private ListPreference pref_repo_default_branch;
    private MaterialDialog deleteRepoDialog;

    public static RepositoryManagerFragment newInstance(RepoInfo repoInfo, RepoRequestDTO repoRequestDTO) {
        RepositoryManagerFragment fragment = new RepositoryManagerFragment();

        Bundle args = new Bundle();
        args.putParcelable(REPO_INFO, repoInfo);
        args.putParcelable(REQUEST_DTO, repoRequestDTO);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.repository_manager_prefs);

        repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
        repoRequestDTO = (RepoRequestDTO) getArguments().getParcelable(REQUEST_DTO);

        final EditTextPreference pref_repo_name = (EditTextPreference) findPreference("pref_repo_name");
        pref_repo_name.setTitle(repoRequestDTO.name);
        pref_repo_name.setText(repoRequestDTO.name);
        pref_repo_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                repoRequestDTO.name = (String) newValue;
                pref_repo_name.setTitle(repoRequestDTO.name);
                return true;
            }
        });

        Preference pref_repo_description = findPreference("pref_repo_description");
        pref_repo_description.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent =
                        ContentEditorActivity.createLauncherIntent(getActivity(), repoInfo, 0, null, repoRequestDTO.description, true, true);
                startActivityForResult(intent, DESCRIPTION_EDIT);
                return false;
            }
        });

        final Preference pref_repo_url = findPreference("pref_repo_url");
        pref_repo_url.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                repoRequestDTO.homepage = (String) newValue;
                return false;
            }
        });

        CheckBoxPreference pref_repo_has_issues = (CheckBoxPreference) findPreference("pref_repo_has_issues");
        pref_repo_has_issues.setChecked(repoRequestDTO.has_issues);
        pref_repo_has_issues.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                repoRequestDTO.has_issues = (Boolean) newValue;
                return true;
            }
        });

        CheckBoxPreference pref_repo_has_wiki = (CheckBoxPreference) findPreference("pref_repo_has_wiki");
        pref_repo_has_wiki.setChecked(repoRequestDTO.has_wiki);
        pref_repo_has_wiki.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                repoRequestDTO.has_wiki = (Boolean) newValue;
                return true;
            }
        });

        CheckBoxPreference pref_repo_has_downloads = (CheckBoxPreference) findPreference("pref_repo_has_downloads");
        pref_repo_has_downloads.setChecked(repoRequestDTO.has_downloads);
        pref_repo_has_downloads.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                repoRequestDTO.has_downloads = (Boolean) newValue;
                return true;
            }
        });

        //        CheckBoxPreference pref_repo_is_private = (CheckBoxPreference) findPreference("pref_repo_is_private");
        //        pref_repo_is_private.setChecked(repoRequestDTO.isPrivate);
        //        pref_repo_is_private.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        //            @Override
        //            public boolean onPreferenceChange(Preference preference, Object newValue) {
        //                repoRequestDTO.isPrivate = (Boolean) newValue;
        //                return true;
        //            }
        //        });

        pref_repo_default_branch = (ListPreference) findPreference("pref_repo_default_branch");
        pref_repo_default_branch.setTitle(repoRequestDTO.default_branch);
        pref_repo_default_branch.setEnabled(false);
        pref_repo_default_branch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                repoRequestDTO.default_branch = (String) newValue;
                pref_repo_default_branch.setTitle((String) newValue);
                return true;
            }
        });

        getBranches();

        Preference pref_repo_delete = findPreference("pref_repo_delete");
        pref_repo_delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkDeleteRepository();
                return false;
            }
        });
    }

    private void checkDeleteRepository() {
        Spanned content = Html.fromHtml(getString(R.string.delete_repository_content, repoInfo.owner, repoInfo.name));
        new MaterialDialog.Builder(getActivity()).title(R.string.delete_repository)
                .content(content)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.md_red_400)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        deleteRepository();
                    }
                })
                .show();
    }

    private void deleteRepository() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.progress(true, 0);
        builder.content(R.string.deleting_repository);
        deleteRepoDialog = builder.show();
        DeleteRepoClient deleteRepoClient = new DeleteRepoClient(repoInfo);
        deleteRepoClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "Repository delete failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Response response) {
                if (getActivity() != null) {
                    if (deleteRepoDialog != null) {
                        deleteRepoDialog.dismiss();
                    }
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();
                }
            }
        });
    }

    private void getBranches() {
        GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(repoInfo);
        repoBranchesClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BranchesCallback(repoInfo) {

            @Override
            protected void showBranches(String[] branches, int defaultBranchPosition) {
                if (branches != null && branches.length > 0) {
                    pref_repo_default_branch.setEntries(branches);
                    pref_repo_default_branch.setEntryValues(branches);
                    pref_repo_default_branch.setValueIndex(defaultBranchPosition);
                    pref_repo_default_branch.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DESCRIPTION_EDIT && data != null) {
                repoRequestDTO.description = data.getStringExtra(ContentEditorActivity.CONTENT);
            }
        }
    }

    public RepoRequestDTO getRepoRequestDTO() {
        return repoRequestDTO;
    }
}
