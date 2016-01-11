package com.alorma.github.ui.actions;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.repo.GetRepoCollaboratorsClient;
import com.alorma.github.ui.adapter.users.UsersAdapterSpinner;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 12/10/2015.
 */
public class CollaboratorsPickerAction extends Action<User> {

    private Context context;
    private IssueInfo issueInfo;
    private MaterialDialog dialog;

    public CollaboratorsPickerAction(Context context, IssueInfo issueInfo) {
        this.context = context;
        this.issueInfo = issueInfo;
    }

    @Override
    public Action<User> execute() {
        dialog = new MaterialDialog.Builder(context).content(R.string.loading_collaborators).progress(true, 0).theme(Theme.DARK).show();

        GetRepoCollaboratorsClient contributorsClient = new GetRepoCollaboratorsClient(issueInfo.repoInfo);
        contributorsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<User>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<User> users) {
                showDialogSelect(users);
            }
        });
        return this;
    }

    private void showDialogSelect(final List<User> users) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (users != null) {
            Collections.reverse(users);
            UsersAdapterSpinner assigneesAdapter = new UsersAdapterSpinner(context, users);

            MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
            builder.adapter(assigneesAdapter, new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    User user = users.get(i);
                    Observable.just(user).subscribe(CollaboratorsPickerAction.this);
                    materialDialog.dismiss();
                }
            });
            builder.negativeText(R.string.no_assignee);
            builder.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onNegative(MaterialDialog dialog) {
                    super.onNegative(dialog);
                    Observable.<User>just(null).subscribe(CollaboratorsPickerAction.this);
                }
            });
            builder.show();
        }
    }

    @Override
    public void onNext(User user) {
        if (getCallback() != null) {
            getCallback().onResult(user);
        }
    }
}
