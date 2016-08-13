package com.alorma.github.ui.actions;

import android.content.Context;
import android.support.v4.util.Pair;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.repo.GetRepoCollaboratorsClient;
import com.alorma.github.ui.utils.DialogUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CollaboratorsPickerAction extends Action<Pair<List<User>, List<User>>> {

  private Context context;
  private IssueInfo issueInfo;
  private MaterialDialog dialog;

  public CollaboratorsPickerAction(Context context, IssueInfo issueInfo) {
    this.context = context;
    this.issueInfo = issueInfo;
  }

  @Override
  public Action<Pair<List<User>, List<User>>> execute() {
    dialog = new DialogUtils().builder(context).content(R.string.loading_collaborators).progress(true, 0).theme(Theme.DARK).show();

    GetRepoCollaboratorsClient contributorsClient = new GetRepoCollaboratorsClient(issueInfo.repoInfo);
    contributorsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<User>>() {
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

      List<String> names = new ArrayList<>(users.size());
      for (User user : users) {
        names.add(user.login);
      }

      MaterialDialog.Builder builder = new DialogUtils().builder(context);

      builder.items(names);
      builder.itemsCallbackMultiChoice(null, (dialog1, which, text) -> {
        Map<Integer, User> mapUser = new HashMap<>(users.size());
        for (int i = 0; i < users.size(); i++) {
          mapUser.put(i, users.get(i));
        }

        List<User> selectedUsers = new ArrayList<>();
        for (Integer integer : which) {
          selectedUsers.add(mapUser.get(integer));
          mapUser.remove(integer);
        }

        Observable<List<User>> added = Observable.just(selectedUsers);
        Observable<List<User>> removed = Observable.just(mapUser.values()).map(ArrayList::new);

        Observable.zip(added, removed, Pair::new).subscribe(CollaboratorsPickerAction.this);

        return true;
      });
      builder.positiveText(R.string.ok);
      builder.negativeText(R.string.no_assignee);
      builder.onNegative((dialog1, which) -> Observable.<Pair<List<User>, List<User>>>just(null).subscribe(CollaboratorsPickerAction.this));
      builder.show();
    }
  }

  @Override
  public void onNext(Pair<List<User>, List<User>> users) {
    if (getCallback() != null) {
      getCallback().onResult(users);
    }
  }
}
