package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.bean.NotificationsParent;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.presenter.notifications.NotificationsPresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.core.notifications.Notification;
import com.alorma.github.sdk.core.notifications.NotificationsRequest;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.notifications.MarkNotificationAsRead;
import com.alorma.github.sdk.services.notifications.MarkRepoNotificationsRead;
import com.alorma.github.sdk.services.notifications.UnsubscribeThread;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsFragment extends LoadingListFragment<NotificationsAdapter>
    implements NotificationsAdapter.NotificationsAdapterListener,
    Presenter.Callback<List<NotificationsParent>> {

  private boolean isShowingAllNotifications = false;

  public static NotificationsFragment newInstance() {
    return new NotificationsFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().setTitle(R.string.notifications);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.notifications_list_fragment, null, false);
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();

    String token =
        TokenProvider.getInstance() != null ? TokenProvider.getInstance().getToken() : null;
    if (getArguments() != null && getArguments().containsKey(BaseActivity.EXTRA_WITH_TOKEN)) {
      token = getArguments().getString(BaseActivity.EXTRA_WITH_TOKEN);
    }

    if (token != null) {
      NotificationsPresenter presenter = new NotificationsPresenter();

      NotificationsRequest request = new NotificationsRequest();
      request.setToken(token);
      request.setAllNotifications(isShowingAllNotifications);

      presenter.load(request, this);
    }
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.clear();
    if (isShowingAllNotifications) {
      getActivity().getMenuInflater().inflate(R.menu.action_notifications_new, menu);
    } else {
      getActivity().getMenuInflater().inflate(R.menu.action_notifications_all, menu);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    isShowingAllNotifications = item.getItemId() == R.id.action_notifications_all;
    getActivity().invalidateOptionsMenu();
    executeRequest();

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void loadArguments() {

  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_inbox;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_notifications;
  }

  @Override
  public void onRefresh() {
    super.onRefresh();
    if (getAdapter() != null) {
      getAdapter().clear();
    }
  }

  @Override
  public void onNotificationClick(Notification notification) {

    GitskariosSettings settings = new GitskariosSettings(getActivity());
    boolean markAsRead = settings.markAsRead();

    if (markAsRead) {
      clearNotifications(notification);
    }

    String type = notification.subject.type;

    Uri uri = null;
    if (type.equalsIgnoreCase("Issue")
        || type.equalsIgnoreCase("PullRequest")
        || type.equalsIgnoreCase("Release")) {
      uri = Uri.parse(notification.subject.url);
    } else {
      uri = Uri.parse(notification.repository.htmlUrl);
    }
    Intent intent = new IntentsManager(getActivity()).checkUri(uri);
    if (intent != null) {
      startActivity(intent);
    }
  }

  @Override
  protected void startRefresh() {
    fromRetry = true;
    super.startRefresh();
  }

  @Override
  public void clearNotifications(Notification notification) {
    startRefresh();
    if (getAdapter() != null) {
      getAdapter().clear();
    }
    setAction(new MarkNotificationAsRead(notification.id));
  }

  @Override
  public void requestRepo(NotificationsParent item) {
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = item.repo.getOwner().getLogin();
    repoInfo.name = item.repo.getName();

    Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), repoInfo);
    startActivity(intent);
  }

  @Override
  public void clearRepoNotifications(NotificationsParent item) {
    startRefresh();
    if (getAdapter() != null) {
      getAdapter().clear();
    }
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = item.repo.getOwner().getLogin();
    repoInfo.name = item.repo.getName();
    setAction(new MarkRepoNotificationsRead(repoInfo));
  }

  private void setAction(GithubClient<Boolean> booleanGithubClient) {
    booleanGithubClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Boolean>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(Boolean aBoolean) {
            executeRequest();
          }
        });
  }

  @Override
  public void unsubscribeThreadNotification(Notification notification) {
    startRefresh();
    setAction(new UnsubscribeThread(notification.id));
  }

  @Override
  public void showLoading() {
    startRefresh();
  }

  @Override
  public void onResponse(List<NotificationsParent> notifications) {
    if (notifications != null && notifications.size() > 0) {
      if (notifications.size() > 0) {
        if (getAdapter() != null && getAdapter().getItemCount() > 0) {
          hideEmpty();
        }

        NotificationsAdapter notificationsAdapter =
            new NotificationsAdapter(getActivity(), LayoutInflater.from(getActivity()));
        notificationsAdapter.addAll(notifications);
        notificationsAdapter.setNotificationsAdapterListener(this);

        setAdapter(notificationsAdapter);
      } else {
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
          setEmpty();
        }
      }
    }
  }

  @Override
  public void hideLoading() {
    stopRefresh();
  }
}
