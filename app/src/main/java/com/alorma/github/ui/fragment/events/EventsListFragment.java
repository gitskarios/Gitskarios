package com.alorma.github.ui.fragment.events;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.ListEvents;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.sdk.bean.dto.response.events.payload.ForkEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueCommentEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.PullRequestEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.PushEventPayload;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.user.events.GetUserEventsClient;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.adapter.events.EventAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.google.gson.Gson;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventsListFragment extends PaginatedListFragment<ListEvents> {

    private EventAdapter eventsAdapter;
    private String username;

    public static EventsListFragment newInstance(String usernºame) {
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);

        EventsListFragment f = new EventsListFragment();
        f.setArguments(bundle);

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(R.string.menu_events);
    }

    @Override
    protected void onResponse(ListEvents githubEvents, boolean refreshing) {
        if (githubEvents != null && githubEvents.size() > 0) {
            hideEmpty();
            if (getListAdapter() != null) {
                eventsAdapter.addAll(githubEvents, paging);
            } else if (eventsAdapter == null) {
                setUpList(githubEvents);
            } else {
                setAdapter(eventsAdapter);
            }
        } else if (eventsAdapter == null || eventsAdapter.getCount() == 0) {
            setEmpty();
        }

    }

    protected EventAdapter setUpList(ListEvents githubEvents) {
        eventsAdapter = new EventAdapter(getActivity(), githubEvents);
        setAdapter(eventsAdapter);
        return eventsAdapter;
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (eventsAdapter == null || eventsAdapter.getCount() == 0) {
            setEmpty();
        }
    }

    @Override
    protected void loadArguments() {
        username = getArguments().getString(USERNAME);
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        GetUserEventsClient eventsClient = new GetUserEventsClient(getActivity(), username);
        eventsClient.setOnResultCallback(this);
        eventsClient.execute();
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);

        eventsAdapter.setLazyLoading(true);

        GetUserEventsClient eventsClient = new GetUserEventsClient(getActivity(), username, page);
        eventsClient.setOnResultCallback(this);
        eventsClient.execute();
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_calendar;
    }

    @Override
    protected int getNoDataText() {
        return R.string.noevents;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        GithubEvent item = eventsAdapter.getItem(position);
        EventType type = item.getType();
        Gson gson = new Gson();
        if (type == EventType.IssueCommentEvent) {
            String s = gson.toJson(item.payload);
            IssueCommentEventPayload payload = gson.fromJson(s, IssueCommentEventPayload.class);
            Issue issue = payload.issue;
            if (issue != null) {
                startActivity(new UrlsManager(getActivity()).checkUri(Uri.parse(issue.html_url)));
            }
        } else if (type == EventType.PushEvent) {
            String payload = gson.toJson(item.payload);
            PushEventPayload pushEventPayload = gson.fromJson(payload, PushEventPayload.class);
            if (pushEventPayload != null && pushEventPayload.commits != null) {
                if (pushEventPayload.commits.size() == 1) {
                    Commit commit = pushEventPayload.commits.get(0);
                    startActivity(new UrlsManager(getActivity()).checkUri(Uri.parse(commit.url)));
                } else if (pushEventPayload.commits.size() > 1) {
                    showCommitsDialog(pushEventPayload.commits);
                }
            }
        } else if (type == EventType.IssuesEvent) {
            String payload = gson.toJson(item.payload);
            IssueEventPayload issueEventPayload = gson.fromJson(payload, IssueEventPayload.class);
            if (issueEventPayload != null) {
                startActivity(new UrlsManager(getActivity()).checkUri(Uri.parse(issueEventPayload.issue.html_url)));
            }
        } else if (type == EventType.PullRequestEvent) {
            String payload = gson.toJson(item.payload);
            PullRequestEventPayload pullRequestEventPayload = gson.fromJson(payload, PullRequestEventPayload.class);
            if (pullRequestEventPayload != null) {
                startActivity(new UrlsManager(getActivity()).checkUri(Uri.parse(pullRequestEventPayload.pull_request.html_url)));
            }
        } else if (type == EventType.ForkEvent) {
            String payload = gson.toJson(item.payload);
            ForkEventPayload forkEventPayload = gson.fromJson(payload, ForkEventPayload.class);
            if (forkEventPayload != null) {
                String parentRepo = item.repo.name;
                String forkeeRepo = forkEventPayload.forkee.full_name;

                showReposDialogDialog(parentRepo, forkeeRepo);
            }
        } else {
            // TODO manage TAGs
            if (item.repo.url != null) {
                startActivity(new UrlsManager(getActivity()).manageRepos(Uri.parse(item.repo.url)));
            }
        }
    }

    private void showCommitsDialog(List<Commit> commits) {
        final CommitsAdapter adapter = new CommitsAdapter(getActivity(), commits, true);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.event_select_commit);
        builder.adapter(adapter, new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                Commit item = adapter.getItem(i);

                startActivity(new UrlsManager(getActivity()).checkUri(Uri.parse(item.url)));
            }
        });
        builder.show();
    }

    private void showReposDialogDialog(final String... repos) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.event_select_repository);
        builder.items(repos);
        builder.alwaysCallSingleChoiceCallback();
        builder.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                String repoSelected = repos[i];
                String[] split = repoSelected.split("/");
                RepoInfo repoInfo = new RepoInfo();
                repoInfo.owner = split[0];
                repoInfo.name = split[1];

                Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), repoInfo);
                startActivity(intent);
                return true;
            }
        });

        builder.show();
    }
}
