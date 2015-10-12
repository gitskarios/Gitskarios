package com.alorma.github.ui.actions;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
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
public class AssigneeAction extends Action<RepoInfo, Boolean> {

    private Context context;
    private IssueInfo issueInfo;
    private User user;

    public AssigneeAction(Context context, IssueInfo issueInfo, User user) {
        this.context = context;
        this.issueInfo = issueInfo;
        this.user = user;
    }

    @Override
    public Action<RepoInfo, Boolean> execute() {
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
                if (getCallback() != null) {
                    getCallback().onResult(true);
                }
            }

            @Override
            public void onFail(RetrofitError error) {
                if (getCallback() != null) {
                    getCallback().onResult(false);
                }
            }
        });
        client.execute();
    }
}
