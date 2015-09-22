package com.alorma.github.ui.adapter.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
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

import java.util.Collection;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventAdapter extends RecyclerArrayAdapter<GithubEvent, EventAdapter.ViewHolder> {

    private EventAdapterListener eventAdapterListener;

    public EventAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GithubEventView v;

        if (viewType == EventType.PushEvent.ordinal()) {
            v = new PushEventView(parent.getContext());
        } else if (viewType == EventType.WatchEvent.ordinal()) {
            v = new WatchEventView(parent.getContext());
        } else if (viewType == EventType.CreateEvent.ordinal()) {
            v = new CreatedEventView(parent.getContext());
        } else if (viewType == EventType.IssueCommentEvent.ordinal()) {
            v = new IssueCommentEventView(parent.getContext());
        } else if (viewType == EventType.CommitCommentEvent.ordinal()) {
            v = new CommitCommentEventView(parent.getContext());
        } else if (viewType == EventType.IssuesEvent.ordinal()) {
            v = new IssueEventView(parent.getContext());
        } else if (viewType == EventType.ForkEvent.ordinal()) {
            v = new ForkEventView(parent.getContext());
        } else if (viewType == EventType.DeleteEvent.ordinal()) {
            v = new DeleteEventView(parent.getContext());
        } else if (viewType == EventType.ReleaseEvent.ordinal()) {
            v = new ReleaseEventView(parent.getContext());
        } else if (viewType == EventType.PullRequestEvent.ordinal()) {
            v = new PullRequestEventView(parent.getContext());
        } else {
            v = new UnhandledEventView(parent.getContext());
        }

        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, GithubEvent githubEvent) {
        holder.view.setEvent(githubEvent);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type.ordinal();
    }

    @Override
    public void addAll(Collection<GithubEvent> items) {
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
                || (event.getType() == EventType.CommitCommentEvent)
                || (event.getType() == EventType.IssuesEvent)
                || (event.getType() == EventType.ForkEvent)
                || (event.getType() == EventType.ReleaseEvent)
                || (event.getType() == EventType.PullRequestEvent)
                || (event.getType() == EventType.DeleteEvent);
    }

    public void setEventAdapterListener(EventAdapterListener eventAdapterListener) {
        this.eventAdapterListener = eventAdapterListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private GithubEventView view;

        private ViewHolder(GithubEventView itemView) {
            super(itemView);
            this.view = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eventAdapterListener != null) {
                        eventAdapterListener.onItem(getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface EventAdapterListener {
        void onItem(GithubEvent event);
    }
}
