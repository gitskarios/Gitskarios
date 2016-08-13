package com.alorma.github.ui.actions;

import android.content.Context;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.EditIssueClearAssigneesRequestDTO;
import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.EditIssueClient;
import com.alorma.github.ui.utils.DialogUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ClearAssigneesAction extends Action<Boolean> {

  private Context context;
  private IssueInfo issueInfo;
  private MaterialDialog dialog;

  public ClearAssigneesAction(Context context, IssueInfo issueInfo) {
    this.context = context;
    this.issueInfo = issueInfo;
  }

  @Override
  public Action<Boolean> execute() {
    dialog = new DialogUtils().builder(context).content(R.string.changing_assignee).progress(true, 0).theme(Theme.DARK).show();

    EditIssueClearAssigneesRequestDTO editIssueRequestDTO = new EditIssueClearAssigneesRequestDTO();
    executeChangeEditIssue(editIssueRequestDTO).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(issue -> returnResult(true), Throwable::printStackTrace);

    return this;
  }

  private Observable<Issue> executeChangeEditIssue(final EditIssueRequestDTO assigneesRequestDTO) {
    EditIssueClient client = new EditIssueClient(issueInfo, assigneesRequestDTO);
    return client.observable();
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
