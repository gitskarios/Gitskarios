package com.alorma.github.ui.callbacks;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.GetArchiveLinkService;
import com.alorma.github.sdk.services.repo.BranchesCallback;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.ErrorHandler;

import retrofit.RetrofitError;

public abstract class DialogBranchesCallback extends BranchesCallback implements MaterialDialog.ListCallback {


	private Context context;
	private String[] branches;

	public DialogBranchesCallback(Context context, RepoInfo repoInfo) {
		super(repoInfo);
		this.context = context;
	}

	@Override
	protected void showBranches(String[] branches, int defaultBranchPosition) {
		this.branches = branches;
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
		builder.autoDismiss(true);
		builder.items(branches);
		builder.itemsCallbackSingleChoice(defaultBranchPosition, this);
		builder.build().show();
	}

	@Override
	public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
		materialDialog.dismiss();

		onBranchSelected(branches[i]);
	}

	protected abstract void onBranchSelected(String branch);
	
	@Override
	public void onFail(RetrofitError error) {
		ErrorHandler.onRetrofitError(context, "Branches callback", error);
	}

	public Context getContext() {
		return context;
	}
}