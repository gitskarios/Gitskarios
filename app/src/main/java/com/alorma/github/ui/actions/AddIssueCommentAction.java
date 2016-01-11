package com.alorma.github.ui.actions;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.NewIssueCommentClient;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddIssueCommentAction extends Action<GithubComment> {

    private Context context;
    private IssueInfo issueInfo;
    private String body;
    private View fab;
    private AddCommentCallback addCommentCallback;

    public AddIssueCommentAction(Context context, IssueInfo issueInfo, String body, View fab) {
        this.context = context;
        this.issueInfo = issueInfo;

        this.body = body;
        this.fab = fab;
    }

    @Override
    public Action<GithubComment> execute() {
        NewIssueCommentClient client = new NewIssueCommentClient(issueInfo, body);
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
        return this;
    }

    @Override
    public void onError(Throwable e) {
        if (addCommentCallback != null) {
            addCommentCallback.onCommentError();
        }
    }

    @Override
    public void onNext(GithubComment githubComment) {
        if (addCommentCallback != null) {
            addCommentCallback.onCommentAdded();
        }
        Snackbar.make(fab, R.string.add_comment_issue_ok, Snackbar.LENGTH_SHORT).show();
    }

    public void setAddCommentCallback(AddCommentCallback addCommentCallback) {
        this.addCommentCallback = addCommentCallback;
    }

    public interface AddCommentCallback {
        public void onCommentAdded();

        public void onCommentError();
    }
}
