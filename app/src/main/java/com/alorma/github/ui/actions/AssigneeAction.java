package com.alorma.github.ui.actions;

import android.content.Context;
import android.support.annotation.NonNull;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.sdk.bean.dto.request.EditIssueAssigneesRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.RetrofitWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.ui.utils.DialogUtils;
import com.alorma.gitskarios.core.client.TokenProvider;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AssigneeAction extends Action<Boolean> {

  private Context context;
  private ApiComponent apiComponent;
  private IssueInfo issueInfo;
  private List<User> users;
  private List<User> removedUsers;
  private MaterialDialog dialog;

  @Inject ApiClient apiClient;

  public AssigneeAction(Context context, ApiComponent apiComponent, IssueInfo issueInfo, List<User> users, List<User> removedUsers) {
    this.context = context;
    this.apiComponent = apiComponent;
    this.issueInfo = issueInfo;
    this.users = users;
    this.removedUsers = removedUsers;
    inject();
  }

  private void inject() {
    apiComponent.inject(this);
  }

  @Override
  public Action<Boolean> execute() {
    dialog = new DialogUtils().builder(context).content(R.string.changing_assignee).progress(true, 0).theme(Theme.DARK).show();

    create(users, removedUsers).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::returnResult, Throwable::printStackTrace);

    return this;
  }

  public Observable<Boolean> create(List<User> addUsers, List<User> removedUsers) {
    RetrofitWrapper wrapper = new RetrofitWrapper(apiClient, TokenProvider.getInstance().getToken()) {
      @Override
      protected AssigneesService get(Retrofit retrofit) {
        return retrofit.create(AssigneesService.class);
      }
    };

    return new CloudDataSource<Void, Boolean>(wrapper) {
      @Override
      protected Observable<SdkItem<Boolean>> execute(SdkItem<Void> request, RestWrapper service) {
        EditIssueAssigneesRequestDTO dtoAdd = createDto(addUsers);
        Observable<Issue> addAssignees = addAssignees(dtoAdd, service.get());
        EditIssueAssigneesRequestDTO dtoRemove = createDto(removedUsers);
        Observable<Issue> removeAssignees = removeAssignees(dtoRemove, service.get());
        return Observable.merge(addAssignees, removeAssignees).all(issue -> true).all(Boolean::booleanValue).map(SdkItem::new);
      }
    }.execute(null, wrapper).map(SdkItem::getK);
  }

  @NonNull
  private EditIssueAssigneesRequestDTO createDto(List<User> users) {
    EditIssueAssigneesRequestDTO dto = new EditIssueAssigneesRequestDTO();
    dto.assignees = new ArrayList<>();
    for (User us : users) {
      dto.assignees.add(us.login);
    }
    return dto;
  }

  public Observable<Issue> addAssignees(EditIssueAssigneesRequestDTO dto, AssigneesService service) {
    return Observable.defer(() -> {
      Call<Issue> call = service.changeAssignees(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num, dto);
      return executeCall(call);
    });
  }

  public Observable<Issue> removeAssignees(EditIssueAssigneesRequestDTO dto, AssigneesService service) {
    return Observable.defer(() -> {
      Call<Issue> call = service.removeAssignees(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num, dto);
      return executeCall(call);
    });
  }

  private Observable<Issue> executeCall(Call<Issue> call) {
    try {
      Response<Issue> response = call.execute();
      if (response.isSuccessful()) {
        return Observable.just(response.body());
      } else {
        return Observable.error(new Exception(response.errorBody().string()));
      }
    } catch (Exception e) {
      return Observable.error(e);
    }
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
