package com.alorma.github.ui.adapter.events;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.track.Tracker;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.adapter.events.holders.EventViewHolder;

public class EventsAdapter extends RecyclerArrayAdapter<GithubEvent, EventViewHolder> {

  private final EventViewHolderFactory factory;
  private final Tracker tracker;

  public EventsAdapter(LayoutInflater inflater, EventViewHolderFactory factory, Tracker tracker) {
    super(inflater);
    this.factory = factory;
    this.tracker = tracker;
  }

  @Override
  public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    EventViewHolder viewHolder = factory.getViewHolder(getInflater(), parent, viewType);
    viewHolder.setCallback(getCallback());
    viewHolder.setTracker(tracker);
    return viewHolder;
  }

  @Override
  protected void onBindViewHolder(EventViewHolder holder, GithubEvent githubEvent) {
    holder.populate(githubEvent);
  }

  @Override
  public int getItemViewType(int position) {
    return factory.viewType(getItem(position));
  }
}
