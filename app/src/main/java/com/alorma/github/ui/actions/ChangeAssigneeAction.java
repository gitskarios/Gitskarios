package com.alorma.github.ui.actions;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;

/**
 * Created by Bernat on 12/10/2015.
 */
public class ChangeAssigneeAction extends Action<Boolean> implements ActionCallback<User> {

    private final Context context;
    private final IssueInfo issueInfo;

    public ChangeAssigneeAction(Context context, IssueInfo issueInfo) {
        this.context = context;
        this.issueInfo = issueInfo;
    }

    @Override
    public Action<Boolean> execute() {
        new CollaboratorsPickerAction(context, issueInfo).setCallback(this).execute();
        return this;
    }

    @Override
    public void onResult(User user) {
        new AssigneeAction(context, issueInfo, user).setCallback(new ActionCallback<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                if (getCallback() != null) {
                    getCallback().onResult(aBoolean);
                }
            }
        }).execute();
    }
}
