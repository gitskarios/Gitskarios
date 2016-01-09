package com.alorma.github.ui.callbacks;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.BranchesCallback;

public abstract class DialogBranchesCallback extends BranchesCallback implements MaterialDialog.ListCallbackSingleChoice {

    private Context context;

    public DialogBranchesCallback(Context context, RepoInfo repoInfo) {
        super(repoInfo);
        this.context = context;
    }

    @Override
    protected void showBranches(String[] branches, int defaultBranchPosition) {
        if (branches != null) {
            if (branches.length > 1) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                builder.autoDismiss(true);
                builder.items(branches);
                builder.itemsCallbackSingleChoice(defaultBranchPosition, this);
                builder.build().show();
            } else if (branches.length == 1) {
                onBranchSelected(branches[0]);
            } else {
                onNoBranches();
            }
        }
    }

    protected abstract void onNoBranches();

    @Override
    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
        materialDialog.dismiss();
        onBranchSelected(charSequence.toString());
        return true;
    }

    protected abstract void onBranchSelected(String branch);

    public Context getContext() {
        return context;
    }
}