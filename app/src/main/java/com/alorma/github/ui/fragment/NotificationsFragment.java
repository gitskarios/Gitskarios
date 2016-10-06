package com.alorma.github.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.bean.NotificationsParent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.UserNotificationsModule;
import com.alorma.github.presenter.notifications.NotificationsPresenter;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.notifications.MarkNotificationAsRead;
import com.alorma.github.sdk.services.notifications.MarkRepoNotificationsRead;
import com.alorma.github.sdk.services.notifications.UnsubscribeThread;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.activity.repo.RepoDetailActivity;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.client.TokenProvider;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.notifications.Notification;
import core.notifications.NotificationsRequest;
import java.util.List;
import javax.inject.Inject;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NotificationsFragment extends LoadingListFragment<NotificationsAdapter>
    implements NotificationsAdapter.NotificationsAdapterListener,
        com.alorma.github.presenter.View<List<NotificationsParent>> {

  @Inject NotificationsPresenter presenter;
  private boolean isShowingAllNotifications = false;

  public NotificationsFragment() {

  }

  public static NotificationsFragment newInstance() {
    return new NotificationsFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    initializeInjector();
  }

  private void initializeInjector() {
    GitskariosApplication application = (GitskariosApplication) getActivity().getApplication();
    ApplicationComponent component = application.getApplicationComponent();

    DaggerApiComponent.builder().applicationComponent(component)
            .apiModule(new ApiModule())
            .build()
            .plus(new UserNotificationsModule())
            .inject(this);
    presenter.attachView(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().setTitle(R.string.notifications);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.notifications_list_fragment, null, false);
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Notifications;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Notifications;
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();
    if (getAdapter() != null) {
      getAdapter().clear();
    }

    String token = TokenProvider.getInstance() != null ? TokenProvider.getInstance().getToken() : null;
    if (getArguments() != null && getArguments().containsKey(BaseActivity.EXTRA_WITH_TOKEN)) {
      token = getArguments().getString(BaseActivity.EXTRA_WITH_TOKEN);
    }

    if (token != null) {

      NotificationsRequest request = new NotificationsRequest();
      request.setToken(token);
      request.setAllNotifications(isShowingAllNotifications);

      presenter.execute(request);
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

    if (type.equalsIgnoreCase("Issue") || type.equalsIgnoreCase("PullRequest") || type.equalsIgnoreCase("Release")) {
      Uri uri = Uri.parse(notification.subject.url);
      launchUri(uri);
    } else if (type.equalsIgnoreCase("Commit")) {
      Uri uri = Uri.parse(notification.subject.url);
      String commit = uri.getLastPathSegment();
      CommitInfo info = new CommitInfo();
      info.repoInfo = new RepoInfo();
      info.repoInfo.owner = notification.repository.owner.getLogin();
      info.repoInfo.name = notification.repository.name;
      info.sha = commit;
      Intent intent = CommitDetailActivity.launchIntent(getActivity(), info);
      startActivity(intent);
    } else {
      Uri uri = Uri.parse(notification.repository.htmlUrl);
      launchUri(uri);
    }
  }

  private void launchUri(Uri uri) {
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
  public void onDataReceived(List<NotificationsParent> notifications, boolean isFromPaginated) {
    if (notifications != null && notifications.size() > 0) {
      if (notifications.size() > 0) {
        if (getAdapter() != null && getAdapter().getItemCount() > 0) {
          hideEmpty();
        }

        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getActivity(), LayoutInflater.from(getActivity()));
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

  @Override
  public void showError(Throwable throwable) {

  }
}
