package com.alorma.github.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.bean.ClearNotification;
import com.alorma.github.bean.NotificationsCount;
import com.alorma.github.bean.UnsubscribeThreadNotification;
import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.UrlsManager;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.notifications.GetNotificationsClient;
import com.alorma.github.sdk.services.notifications.MarkNotificationAsRead;
import com.alorma.github.sdk.services.notifications.MarkRepoNotificationsRead;
import com.alorma.github.sdk.services.notifications.UnsubscribeThread;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.adapter.NotificationsAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.ui.view.DirectionalScrollListener;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Bernat on 19/02/2015.
 */
public class NotificationsFragment extends PaginatedListFragment<List<Notification>> {

    private StickyListHeadersListView listView;
    public NotificationsAdapter notificationsAdapter;

    @Inject
    Bus bus;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GitskariosApplication.get(getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.list_fragment_headers
                , null);
    }

    @Override
    protected void setupListView(View view) {
        listView = (StickyListHeadersListView) view.findViewById(android.R.id.list);
        if (listView != null) {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
            listView.setOnScrollListener(new DirectionalScrollListener(this, this, FAB_ANIM_DURATION));
            listView.setAreHeadersSticky(false);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.notifications);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();

        GetNotificationsClient client = new GetNotificationsClient(getActivity());
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {

    }

    @Override
    public void onResponseOk(final List<Notification> notifications, Response r) {
        stopRefresh();
        if (notifications != null) {
            bus.post(new NotificationsCount(notifications.size()));
        }
        super.onResponseOk(notifications, r);
        if (notifications != null) {
            if (notifications.size() == 0 && (notificationsAdapter == null || notificationsAdapter.getCount() == 0)) {
                setEmpty();
            }
        } else {
            setEmpty();
        }
    }

    @Override
    protected void onResponse(final List<Notification> notifications, boolean refreshing) {
        if (refreshing) {
            notificationsAdapter.clear();
        }
        if (notifications != null && notifications.size() > 0) {
            bus.post(new NotificationsCount(notifications.size()));
            if (notifications.size() > 0) {
                if (notificationsAdapter != null && notificationsAdapter.getCount() > 0) {
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

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        notificationsAdapter = new NotificationsAdapter(getActivity(), notifications);

                        GitskariosApplication.get(getActivity()).inject(notificationsAdapter);
                        bus.register(notificationsAdapter);

                        listView.setAdapter(notificationsAdapter);
                    }
                });

            } else {
                if (notificationsAdapter == null || notificationsAdapter.getCount() == 0) {
                    setEmpty();
                }

            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (notificationsAdapter == null || notificationsAdapter.getCount() == 0) {
            setEmpty();
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

    @Subscribe
    public void manageNotificationClick(Notification item) {
        String type = item.subject.type;

        Uri uri = Uri.parse(item.subject.url);
        if (type.equalsIgnoreCase("Issue") || type.equalsIgnoreCase("PullRequest")) {
            List<String> segments = uri.getPathSegments();
            String user = segments.get(1);
            String repo = segments.get(2);
            String number = segments.get(4);
            IssueInfo issueInfo = new IssueInfo();
            issueInfo.num = Integer.valueOf(number);
            issueInfo.repoInfo = new RepoInfo();
            issueInfo.repoInfo.owner = user;
            issueInfo.repoInfo.name = repo;
            Intent launcherIntent = IssueDetailActivity.createLauncherIntent(getActivity(), issueInfo);
            startActivity(launcherIntent);
        } else {
            startActivity(new UrlsManager(getActivity()).manageRepos(item.repository.html_url));
        }

    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (notificationsAdapter != null) {
            notificationsAdapter.clear();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        bus.register(this);
    }

    @Override
    public void onPause() {
        if (notificationsAdapter != null) {
            bus.unregister(notificationsAdapter);
        }
        bus.unregister(this);
        super.onPause();
    }

    @Subscribe
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
                        notificationsAdapter.remove(clearNotification.getNotification());
                        notificationsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFail(RetrofitError error) {

                }
            });
            notificationAsRead.execute();
        }
    }

    @Subscribe
    public void unsubscribeThreadNotification(final UnsubscribeThreadNotification unsubscribeThreadNotification) {
        final UnsubscribeThread unsubscribeThread = new UnsubscribeThread(getActivity(), unsubscribeThreadNotification.getNotification());
        unsubscribeThread.setOnResultCallback(new BaseClient.OnResultCallback<Response>() {
            @Override
            public void onResponseOk(Response response, Response r) {
                if (response != null && response.getStatus() == 204) {
                    notificationsAdapter.remove(unsubscribeThreadNotification.getNotification());
                    notificationsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        unsubscribeThread.execute();
    }
}
