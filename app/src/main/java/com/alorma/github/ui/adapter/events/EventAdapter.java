package com.alorma.github.ui.adapter.events;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.adapter.events.views.CreatedEventView;
import com.alorma.github.ui.adapter.events.views.ForkEventView;
import com.alorma.github.ui.adapter.events.views.GithubEventView;
import com.alorma.github.ui.adapter.events.views.IssueCommentEventView;
import com.alorma.github.ui.adapter.events.views.IssueEventView;
import com.alorma.github.ui.adapter.events.views.PushEventView;
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

        View v = inflate(githubEvent);

        return v;
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
        }  else if (event.getType() == EventType.IssuesEvent) {
            v = new IssueEventView(getContext());
        } else if (event.getType() == EventType.ForkEvent) {
            v = new ForkEventView(getContext());
        } else {
            v = new UnhandledEventView(getContext());
        }

        v.setEvent(event);

        return v;
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

    private boolean checkEventHandled(GithubEvent event) {
        return event.getType() != null && (event.getType() == EventType.PushEvent)
                || (event.getType() == EventType.WatchEvent)
                || (event.getType() == EventType.CreateEvent)
                || (event.getType() == EventType.IssueCommentEvent)
				|| (event.getType() == EventType.IssuesEvent)
                || (event.getType() == EventType.ForkEvent);
    }
}
