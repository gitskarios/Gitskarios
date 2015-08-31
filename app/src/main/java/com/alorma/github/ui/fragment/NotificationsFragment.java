package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.bean.ClearNotification;
import com.alorma.github.bean.UnsubscribeThreadNotification;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.sdk.services.notifications.MarkNotificationAsRead;
import com.alorma.github.sdk.services.notifications.MarkRepoNotificationsRead;
import com.alorma.github.sdk.services.notifications.UnsubscribeThread;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsFragment extends PaginatedListFragment<List<Notification>, NotificationsAdapter> implements NotificationsAdapter.NotificationsAdapterListener {

    private StickyRecyclerHeadersDecoration headersDecoration;

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
    protected void onResponse(final List<Notification> notifications, boolean refreshing) {
        if (notifications != null && notifications.size() > 0) {
            if (notifications.size() > 0) {
                if (getAdapter() != null && getAdapter().getItemCount() > 0) {
                    hideEmpty();
                }

                Map<String, Integer> ids = new HashMap<>();

                int id = 0;
                for (Notification notification : notifications) {
                    if (ids.get(notification.repository.name) == null) {
                        ids.put(notification.repository.name, id++);
                    }
                    notification.adapter_repo_parent_id = ids.get(notification.repository.name);
                }

                Collections.sort(notifications, Notification.Comparators.REPO_ID);
                NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getActivity(), LayoutInflater.from(getActivity()));
                notificationsAdapter.addAll(notifications);
                notificationsAdapter.setNotificationsAdapterListener(this);

                setAdapter(notificationsAdapter);


                if (headersDecoration == null) {
                    headersDecoration = new StickyRecyclerHeadersDecoration(getAdapter());
                    addItemDecoration(headersDecoration);
                }

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
    public void onNotificationClick(Notification item) {
        String type = item.subject.type;

        Uri uri = null;
        if (type.equalsIgnoreCase("Issue") || type.equalsIgnoreCase("PullRequest")) {
            uri = Uri.parse(item.subject.url);
        } else {
            uri = Uri.parse(item.repository.html_url);
        }

        Intent intent = new UrlsManager(getActivity()).checkUri(uri);
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (getAdapter() != null) {
            getAdapter().clear();
        }
    }

    @Override
    public void clearRepoNotifications(final ClearNotification clearNotification) {
        if (clearNotification.isAllRepository()) {
            RepoInfo repoInfo = new RepoInfo();
            repoInfo.owner = clearNotification.getNotification().repository.owner.login;
            repoInfo.name = clearNotification.getNotification().repository.name;
            MarkRepoNotificationsRead client = new MarkRepoNotificationsRead(getActivity(), repoInfo);
            client.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
                @Override
                public void onResponseOk(Response response, Response r) {
                    executeRequest();
                }

                @Override
                public void onFail(RetrofitError error) {

                }
            });
            client.execute();
        } else {
            final MarkNotificationAsRead notificationAsRead = new MarkNotificationAsRead(getActivity(), clearNotification.getNotification());
            notificationAsRead.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
                @Override
                public void onResponseOk(Response response, Response r) {
                    if (response != null && response.getStatus() == 205) {
                        getAdapter().remove(clearNotification.getNotification());
                    }
                }

                @Override
                public void onFail(RetrofitError error) {

                }
            });
            notificationAsRead.execute();
        }
    }

    @Override
        public void unsubscribeThreadNotification(final UnsubscribeThreadNotification unsubscribeThreadNotification) {
        final UnsubscribeThread unsubscribeThread = new UnsubscribeThread(getActivity(), unsubscribeThreadNotification.getNotification());
        unsubscribeThread.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
            @Override
            public void onResponseOk(Response response, Response r) {
                if (response != null && response.getStatus() == 204) {
                    getAdapter().remove(unsubscribeThreadNotification.getNotification());
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        unsubscribeThread.execute();
    }
}
