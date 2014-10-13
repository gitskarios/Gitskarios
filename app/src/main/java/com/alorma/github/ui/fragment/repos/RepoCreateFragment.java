package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 28/09/2014.
 */
public class RepoCreateFragment extends Fragment implements View.OnClickListener {


	private CheckedTextView repoPrivateCheck;
	private CheckedTextView repoIssuesCheck;
	private CheckedTextView repoWikiCheck;
	private CheckedTextView repoDownloadsCheck;
	private EditText editTitle;
	private EditText editBody;
	private Spinner spinnerBranch;

	public static RepoCreateFragment newInstance() {
		return new RepoCreateFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.repo_create_fragment, null, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		findViews(view);

	}

	private void findViews(View view) {
		editTitle = (EditText) view.findViewById(R.id.editTitle);
		editBody = (EditText) view.findViewById(R.id.editBody);
		spinnerBranch = (Spinner) view.findViewById(R.id.spinnerBranch);
		findChecks(view);
	}

	private void findChecks(View view) {
		repoPrivateCheck = (CheckedTextView) view.findViewById(R.id.repoPrivateCheck);
		repoIssuesCheck = (CheckedTextView) view.findViewById(R.id.repoIssuesCheck);
		repoWikiCheck = (CheckedTextView) view.findViewById(R.id.repoWikiCheck);
		repoDownloadsCheck = (CheckedTextView) view.findViewById(R.id.repoDownloadsCheck);

		repoPrivateCheck.setCheckMarkDrawable(R.drawable.btn_check_material);
		repoIssuesCheck.setCheckMarkDrawable(R.drawable.btn_check_material);
		repoWikiCheck.setCheckMarkDrawable(R.drawable.btn_check_material);
		repoDownloadsCheck.setCheckMarkDrawable(R.drawable.btn_check_material);

		repoPrivateCheck.setOnClickListener(this);
		repoIssuesCheck.setOnClickListener(this);
		repoWikiCheck.setOnClickListener(this);
		repoDownloadsCheck.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.repoPrivateCheck:
				changeCheckedState(v);
				break;
			case R.id.repoIssuesCheck:
				changeCheckedState(v);
				break;
			case R.id.repoWikiCheck:
				changeCheckedState(v);
				break;
			case R.id.repoDownloadsCheck:
				changeCheckedState(v);
				break;
		}
	}

	private void changeCheckedState(View v) {
		CheckedTextView ctv = (CheckedTextView) v;
		changeCheckedState(ctv, !ctv.isChecked());
	}

	private void changeCheckedState(CheckedTextView ctv, boolean checked) {
		ctv.setChecked(checked);
	}

}
