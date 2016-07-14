package com.alorma.github.ui.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.ChangeIssueStateClient;
import com.alorma.github.ui.utils.DialogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReopenAction extends Action<Issue> implements MaterialDialog.SingleButtonCallback {

  private Context context;
  private IssueInfo issueInfo;
  private MaterialDialog dialog;
  private int dialogString;

  public ReopenAction(Context context, IssueInfo issueInfo, int dialogString) {
    this.context = context;
    this.issueInfo = issueInfo;
    this.dialogString = dialogString;
  }

  @Override
  public Action<Issue> execute() {
    String title = context.getResources().getString(dialogString);
    String accept = context.getResources().getString(R.string.accept);
    String cancel = context.getResources().getString(R.string.cancel);

    MaterialDialog dialog =
        new DialogUtils().builder(context).title(title).positiveText(accept).negativeText(cancel).onPositive(this).build();

    dialog.show();
    return this;
  }

  private void changeIssueState(IssueState state) {
    dialog = new DialogUtils().builder(context).content(dialogString).progress(true, 0).theme(Theme.DARK).show();

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

  @Override
  public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
    changeIssueState(IssueState.open);
  }
}
