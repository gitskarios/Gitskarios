package com.alorma.github.ui.fragment.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.sdk.bean.dto.response.events.payload.ForkEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueCommentEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.IssueEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.PullRequestEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.PushEventPayload;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.user.events.GetUserEventsClient;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.adapter.events.EventAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.alorma.github.utils.AttributesUtils;
import com.google.gson.Gson;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventsListFragment extends PaginatedListFragment<List<GithubEvent>, EventAdapter> implements EventAdapter.EventAdapterListener {

    private String username;

    public static EventsListFragment newInstance(String username) {
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
    protected void onResponse(List<GithubEvent> githubEvents, boolean refreshing) {
        if (githubEvents != null && githubEvents.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(githubEvents);
            } else {
                EventAdapter eventAdapter = new EventAdapter(LayoutInflater.from(getActivity()));
                eventAdapter.setEventAdapterListener(this);
                eventAdapter.addAll(githubEvents);
                setAdapter(eventAdapter);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty(false);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        super.onFail(error);
        if (error != null && error.getResponse() != null) {
            setEmpty(true, error.getResponse().getStatus());
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
    public void onItem(GithubEvent item) {
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
        final CommitsAdapter adapter = new CommitsAdapter(getActivity(), commits);
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

    private class CommitsAdapter extends ArrayAdapter<Commit> {

        private final LayoutInflater mInflater;

        public CommitsAdapter(Context context, List<Commit> objects) {
            super(context, 0, objects);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.commit_row, parent, false);

            ViewHolder holder = new ViewHolder(view);

            Commit commit = getItem(position);

            User author = commit.author;

            if (author == null) {
                author = commit.commit.author;
            }

            if (author == null) {
                author = commit.commit.committer;
            }

            if (author != null) {
                if (author.avatar_url != null) {
                    ImageLoader.getInstance().displayImage(author.avatar_url, holder.avatar);
                } else if (author.email != null) {
                    try {
                        MessageDigest digest = MessageDigest.getInstance("MD5");
                        digest.update(author.email.getBytes());
                        byte messageDigest[] = digest.digest();
                        StringBuffer hexString = new StringBuffer();
                        for (int i = 0; i < messageDigest.length; i++)
                            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                        String hash = hexString.toString();
                        ImageLoader.getInstance().displayImage("http://www.gravatar.com/avatar/" + hash, holder.avatar);
                    } catch (NoSuchAlgorithmException e) {
                        IconicsDrawable iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_octoface);
                        iconDrawable.color(AttributesUtils.getSecondaryTextColor(holder.itemView.getContext()));
                        iconDrawable.sizeDp(36);
                        iconDrawable.setAlpha(128);
                        holder.avatar.setImageDrawable(iconDrawable);
                    }

                } else {
                    IconicsDrawable iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_octoface);
                    iconDrawable.color(AttributesUtils.getSecondaryTextColor(holder.itemView.getContext()));
                    iconDrawable.sizeDp(36);
                    iconDrawable.setAlpha(128);
                    holder.avatar.setImageDrawable(iconDrawable);
                }

                if (author.login != null) {
                    holder.user.setText(author.login);
                } else if (author.name != null) {
                    holder.user.setText(author.name);
                } else if (author.email != null) {
                    holder.user.setText(author.email);
                }
            }

            String message = commit.shortMessage();
            if (commit.commit != null && commit.commit.shortMessage() != null) {
                message = commit.commit.shortMessage();
            }


            holder.title.setText(message);

            if (commit.sha != null) {
                holder.sha.setText(commit.shortSha());
            }

            holder.textNums.setText("");

            if (commit.stats != null) {
                String textCommitsStr = null;
                if (commit.stats.additions > 0 && commit.stats.deletions > 0) {
                    textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_add_del, commit.stats.additions, commit.stats.deletions);
                    holder.textNums.setVisibility(View.VISIBLE);
                } else if (commit.stats.additions > 0) {
                    textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_add, commit.stats.additions);
                    holder.textNums.setVisibility(View.VISIBLE);
                } else if (commit.stats.deletions > 0) {
                    textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_del, commit.stats.deletions);
                    holder.textNums.setVisibility(View.VISIBLE);
                } else {
                    holder.textNums.setVisibility(View.GONE);
                }

                if (textCommitsStr != null) {
                    holder.textNums.setText(Html.fromHtml(textCommitsStr));
                }
            } else {
                holder.textNums.setVisibility(View.GONE);
            }

            if (commit.files != null && commit.files.size() > 0) {
                holder.numFiles.setVisibility(View.VISIBLE);
                holder.numFiles.setText(holder.itemView.getContext().getString(R.string.num_of_files, commit.files.size()));
            } else {
                holder.numFiles.setVisibility(View.GONE);
            }

            return view;
        }

        public class ViewHolder {

            private final TextView title;
            private final TextView user;
            private final TextView sha;
            private final TextView textNums;
            private final TextView numFiles;
            private final ImageView avatar;
            private View itemView;

            public ViewHolder(final View itemView) {
                this.itemView = itemView;
                title = (TextView) itemView.findViewById(R.id.title);
                user = (TextView) itemView.findViewById(R.id.user);
                sha = (TextView) itemView.findViewById(R.id.sha);
                textNums = (TextView) itemView.findViewById(R.id.textNums);
                numFiles = (TextView) itemView.findViewById(R.id.numFiles);
                avatar = (ImageView) itemView.findViewById(R.id.avatarAuthor);
            }
        }
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
