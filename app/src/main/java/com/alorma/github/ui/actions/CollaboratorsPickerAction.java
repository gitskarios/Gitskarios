package com.alorma.github.ui.actions;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoCollaboratorsClient;
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
public class CollaboratorsPickerAction extends Action<RepoInfo, User> implements BaseClient.OnResultCallback<List<User>> {

    private Context context;
    private IssueInfo issueInfo;

    public CollaboratorsPickerAction(Context context, IssueInfo issueInfo) {
        this.context = context;
        this.issueInfo = issueInfo;
    }

    @Override
    public Action<RepoInfo, User> execute() {
        GetRepoCollaboratorsClient contributorsClient = new GetRepoCollaboratorsClient(context, item);
        contributorsClient.setOnResultCallback(this);
        contributorsClient.execute();
        return this;
    }

    @Override
    public void onResponseOk(final List<User> users, Response r) {
        if (users != null) {
            Collections.reverse(users);
            UsersAdapterSpinner assigneesAdapter = new UsersAdapterSpinner(context, users);

            MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
            builder.adapter(assigneesAdapter, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    User user = users.get(i);
                    setAssigneeUser(user);
                    materialDialog.dismiss();
                }
            });
            builder.negativeText(R.string.no_assignee);
            builder.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);
                    setAssigneeUser(null);
                }
            });
            builder.show();
        }
    }

    private void setAssigneeUser(User user) {
        if (getCallback() != null) {
            getCallback().onResult(user);
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }
}
