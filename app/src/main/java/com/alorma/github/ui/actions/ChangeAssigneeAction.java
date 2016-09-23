package com.alorma.github.ui.actions;

import android.content.Context;
import android.support.v4.util.Pair;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.sdk.bean.info.IssueInfo;
import core.User;
import java.util.List;

public class ChangeAssigneeAction extends Action<Boolean> implements ActionCallback<Pair<List<User>, List<User>>> {

  private final Context context;
  private List<User> currentAssignees;
  private final IssueInfo issueInfo;
  private ApiComponent apiComponent;

  public ChangeAssigneeAction(Context context, ApiComponent apiComponent, List<User> currentAssignees, IssueInfo issueInfo) {
    this.context = context;
    this.apiComponent = apiComponent;
    this.currentAssignees = currentAssignees;
    this.issueInfo = issueInfo;
  }

  @Override
  public Action<Boolean> execute() {
    new CollaboratorsPickerAction(context, currentAssignees, issueInfo).setCallback(this).execute();
    return this;
  }

  @Override
  public void onResult(Pair<List<User>, List<User>> users) {
    Action<Boolean> action;
    if (users == null) {
      action = new ClearAssigneesAction(context, issueInfo);
    } else {
      action = new AssigneeAction(context, apiComponent, issueInfo, users.first, users.second);
    }
    action.setCallback(aBoolean -> {
      if (getCallback() != null) {
        getCallback().onResult(aBoolean);
      }
    }).execute();
  }

  @Override
  public void onNext(Boolean aBoolean) {

  }
}
