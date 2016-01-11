package com.alorma.github.ui.actions;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.EditIssueAssigneeRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.EditIssueClient;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 12/10/2015.
 */
public class AssigneeAction extends Action<Boolean> {

    private Context context;
    private IssueInfo issueInfo;
    private User user;
    private MaterialDialog dialog;

    public AssigneeAction(Context context, IssueInfo issueInfo, User user) {
        this.context = context;
        this.issueInfo = issueInfo;
        this.user = user;
    }

    @Override
    public Action<Boolean> execute() {
        dialog = new MaterialDialog.Builder(context).content(R.string.changing_assignee).progress(true, 0).theme(Theme.DARK).show();
        EditIssueAssigneeRequestDTO editIssueRequestDTO = new EditIssueAssigneeRequestDTO();
        if (user != null) {
            editIssueRequestDTO.assignee = user.login;
        } else {
            editIssueRequestDTO.assignee = null;
        }
        executeEditIssue(editIssueRequestDTO);
        return this;
    }

    private void executeEditIssue(final EditIssueRequestDTO editIssueRequestDTO) {
        EditIssueClient client = new EditIssueClient(issueInfo, editIssueRequestDTO);
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Issue>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                returnResult(false);
            }

            @Override
            public void onNext(Issue issue) {
                returnResult(true);
            }
        });
    }

    private void returnResult(boolean t) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (getCallback() != null) {
            getCallback().onResult(t);
        }
    }

    @Override
    public void onNext(Boolean aBoolean) {

    }
}
