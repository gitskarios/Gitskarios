package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.gitskarios.core.client.BaseClient;
import com.alorma.github.bean.NotificationsParent;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.sdk.services.notifications.MarkNotificationAsRead;
import com.alorma.github.sdk.services.notifications.MarkRepoNotificationsRead;
import com.alorma.github.sdk.services.notifications.UnsubscribeThread;
import com.alorma.github.sdk.utils.GitskariosSettings;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsFragment extends PaginatedListFragment<List<Notification>, NotificationsAdapter> implements NotificationsAdapter.NotificationsAdapterListener {

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.notifications);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        String token = null;
        if (getArguments() != null && getArguments().containsKey(BaseActivity.EXTRA_WITH_TOKEN)) {
            token = getArguments().getString(BaseActivity.EXTRA_WITH_TOKEN);
        }

        GetNotificationsClient client = new GetNotificationsClient(getActivity(), token);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {

    }

    @Override
    public void onResponseOk(final List<Notification> notifications, Response r) {
        stopRefresh();
        super.onResponseOk(notifications, r);
        if (notifications != null) {
            if (notifications.size() == 0 && (getAdapter() == null || getAdapter().getItemCount() == 0)) {
                setEmpty(false);
            }
        } else {
            setEmpty(false);
        }
    }

    @Override
    protected void onResponse(final List<Notification> notifications, final boolean refreshing) {
        if (notifications != null && notifications.size() > 0) {
            if (notifications.size() > 0) {
                if (getAdapter() != null && getAdapter().getItemCount() > 0) {
                    hideEmpty();
                }

                Map<Long, NotificationsParent> parents = new HashMap<>();

                for (Notification notification : notifications) {
                    if (parents.get(notification.repository.id) == null) {
                        NotificationsParent notificationsParent = new NotificationsParent();
                        parents.put(notification.repository.id, notificationsParent);
                        notificationsParent.repo = notification.repository;
                        notificationsParent.notifications = new ArrayList<>();
                    }
                    parents.get(notification.repository.id).notifications.add(notification);
                }

                NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getActivity(), LayoutInflater.from(getActivity()));
                notificationsAdapter.addAll(parents.values());
                notificationsAdapter.setNotificationsAdapterListener(this);

                setAdapter(notificationsAdapter);
            } else {
                if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                    setEmpty(false);
                }
            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty(true);
        }
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
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return null;
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
        if (type.equalsIgnoreCase("Issue") || type.equalsIgnoreCase("PullRequest") || type.equalsIgnoreCase("Release")) {
            uri = Uri.parse(notification.subject.url);
        } else {
            uri = Uri.parse(notification.repository.html_url);
        }
        Intent intent = new UrlsManager(getActivity()).checkUri(uri);
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
        MarkNotificationAsRead notificationAsRead = new MarkNotificationAsRead(getActivity(), notification);
        notificationAsRead.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
            @Override
            public void onResponseOk(Response response, Response r) {
                if (response != null && response.getStatus() == 205) {
                    executeRequest();
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        notificationAsRead.execute();
    }

    @Override
    public void requestRepo(NotificationsParent item) {
        RepoInfo repoInfo = new RepoInfo();
        repoInfo.owner = item.repo.owner.login;
        repoInfo.name = item.repo.name;

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
        repoInfo.owner = item.repo.owner.login;
        repoInfo.name = item.repo.name;
        MarkRepoNotificationsRead client = new MarkRepoNotificationsRead(getActivity(), repoInfo);
        client.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
            @Override
            public void onResponseOk(Response response, Response r) {
                if (response != null && response.getStatus() == 205) {
                    executeRequest();
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        client.execute();
    }

    @Override
    public void unsubscribeThreadNotification(Notification notification) {
        startRefresh();
        final UnsubscribeThread unsubscribeThread = new UnsubscribeThread(getActivity(), notification);
        unsubscribeThread.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
            @Override
            public void onResponseOk(Response response, Response r) {
                if (response != null && response.getStatus() == 204) {
                    executeRequest();
                }
            }

            @Override
            public void onFail(RetrofitError error) {
                stopRefresh();
            }
        });
        unsubscribeThread.execute();
    }
}
