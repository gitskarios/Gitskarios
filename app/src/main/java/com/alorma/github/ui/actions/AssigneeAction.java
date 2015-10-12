package com.alorma.github.ui.actions;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.EditIssueAssigneeRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.sdk.services.repo.GetRepoCollaboratorsClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.adapter.users.UsersAdapterSpinner;
import com.alorma.gitskarios.core.client.BaseClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

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
        dialog = new MaterialDialog.Builder(context)
                .content(R.string.changing_assignee)
                .progress(true, 0)
                .theme(Theme.DARK)
                .show();
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
        EditIssueClient client = new EditIssueClient(context, issueInfo, editIssueRequestDTO);
        client.setOnResultCallback(new BaseClient.OnResultCallback<Issue>() {
            @Override
            public void onResponseOk(Issue issue, Response r) {
                returnResult(true);
            }

            @Override
            public void onFail(RetrofitError error) {
                returnResult(false);
            }
        });
        client.execute();
    }

    private void returnResult(boolean t) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (getCallback() != null) {
            getCallback().onResult(t);
        }
    }
}
