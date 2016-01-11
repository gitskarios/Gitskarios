package com.alorma.github.ui.actions;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.ChangeIssueStateClient;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 12/10/2015.
 */
public class CloseAction extends Action<Issue> {

    private Context context;
    private IssueInfo issueInfo;
    private MaterialDialog dialog;
    private int dialogString;

    public CloseAction(Context context, IssueInfo issueInfo, int dialogString) {
        this.context = context;
        this.issueInfo = issueInfo;
        this.dialogString = dialogString;
    }

    @Override
    public Action<Issue> execute() {
        String title = context.getResources().getString(dialogString);
        String accept = context.getResources().getString(R.string.accept);
        String cancel = context.getResources().getString(R.string.cancel);

        MaterialDialog dialog = new MaterialDialog.Builder(context).title(title)
                .positiveText(accept)
                .negativeText(cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        super.onPositive(materialDialog);
                        changeIssueState(IssueState.closed);
                    }
                })
                .build();

        dialog.show();
        return this;
    }

    private void changeIssueState(IssueState state) {

        dialog = new MaterialDialog.Builder(context).content(dialogString).progress(true, 0).theme(Theme.DARK).show();

        ChangeIssueStateClient changeIssueStateClient = new ChangeIssueStateClient(issueInfo, state);
        changeIssueStateClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
    }

    @Override
    public void onNext(Issue issue) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (issue != null) {
            if (getCallback() != null) {
                getCallback().onResult(issue);
            }
        }
    }
}
