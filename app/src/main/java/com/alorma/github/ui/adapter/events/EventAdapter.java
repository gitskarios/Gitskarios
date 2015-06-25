package com.alorma.github.ui.adapter.events;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.adapter.events.views.CommitCommentEventView;
import com.alorma.github.ui.adapter.events.views.CreatedEventView;
import com.alorma.github.ui.adapter.events.views.DeleteEventView;
import com.alorma.github.ui.adapter.events.views.ForkEventView;
import com.alorma.github.ui.adapter.events.views.GithubEventView;
import com.alorma.github.ui.adapter.events.views.IssueCommentEventView;
import com.alorma.github.ui.adapter.events.views.IssueEventView;
import com.alorma.github.ui.adapter.events.views.PullRequestEventView;
import com.alorma.github.ui.adapter.events.views.PushEventView;
import com.alorma.github.ui.adapter.events.views.ReleaseEventView;
import com.alorma.github.ui.adapter.events.views.UnhandledEventView;
import com.alorma.github.ui.adapter.events.views.WatchEventView;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Collection;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventAdapter extends LazyAdapter<GithubEvent> {

    public EventAdapter(Context context, Collection<GithubEvent> collection) {
        super(context, new ArrayList<GithubEvent>());
        addAll(collection);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GithubEvent githubEvent = getItem(position);

        //get the viewHolder
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflate(githubEvent);
            viewHolder = new ViewHolder((GithubEventView) convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //here it will get populated :O
        viewHolder.view.setEvent(githubEvent);
        return convertView;
    }

    public View inflate(GithubEvent event) {
        GithubEventView v;

        if (event.getType() == EventType.PushEvent) {
            v = new PushEventView(getContext());
        } else if (event.getType() == EventType.WatchEvent) {
            v = new WatchEventView(getContext());
        } else if (event.getType() == EventType.CreateEvent) {
            v = new CreatedEventView(getContext());
        } else if (event.getType() == EventType.IssueCommentEvent) {
            v = new IssueCommentEventView(getContext());
        } else if (event.getType() == EventType.CommitCommentEvent) {
            v = new CommitCommentEventView(getContext());
        } else if (event.getType() == EventType.IssuesEvent) {
            v = new IssueEventView(getContext());
        } else if (event.getType() == EventType.ForkEvent) {
            v = new ForkEventView(getContext());
        } else if (event.getType() == EventType.DeleteEvent) {
            v = new DeleteEventView(getContext());
        } else if (event.getType() == EventType.ReleaseEvent) {
            v = new ReleaseEventView(getContext());
        } else if (event.getType() == EventType.PullRequestEvent) {
            v = new PullRequestEventView(getContext());
        } else {
            v = new UnhandledEventView(getContext());
        }

        return v;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return EventType.values().length;
    }

    @Override
    public void addAll(Collection<? extends GithubEvent> collection) {
        for (GithubEvent githubEvent : collection) {
            if (checkEventHandled(githubEvent)) {
                add(githubEvent);
            } else if (Fabric.isInitialized()) {
                Crashlytics.log(githubEvent.type + " not handled");
            }
        }
    }

    @Override
    public void addAll(GithubEvent... items) {
        super.addAll(items);
        for (GithubEvent githubEvent : items) {
            if (checkEventHandled(githubEvent)) {
                add(githubEvent);
            } else if (Fabric.isInitialized()) {
                Crashlytics.log(githubEvent.type + " not handled");
            }
        }
    }

    @Override
    public void addAll(Collection<? extends  GithubEvent> collection, boolean paging) {
        if (!paging) {
            clear();
        }

        addAll(collection);
    }

    private boolean checkEventHandled(GithubEvent event) {
        return event.getType() != null && (event.getType() == EventType.PushEvent)
                || (event.getType() == EventType.WatchEvent)
                || (event.getType() == EventType.CreateEvent)
                || (event.getType() == EventType.IssueCommentEvent)
                || (event.getType() == EventType.CommitCommentEvent)
                || (event.getType() == EventType.IssuesEvent)
                || (event.getType() == EventType.ForkEvent)
                || (event.getType() == EventType.ReleaseEvent)
                || (event.getType() == EventType.PullRequestEvent)
                || (event.getType() == EventType.DeleteEvent);
    }

    private static class ViewHolder {
        private GithubEventView view;

        private ViewHolder(GithubEventView view) {
            this.view = view;
        }
    }
}
