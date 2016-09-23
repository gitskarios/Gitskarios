package com.alorma.github.ui.adapter.events.holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.alorma.github.BuildConfig;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.track.Tracker;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.TimeUtils;
import core.User;

public abstract class EventViewHolder extends RecyclerView.ViewHolder {
  private RecyclerArrayAdapter.ItemCallback<GithubEvent> callback;
  private Tracker tracker;

  public EventViewHolder(View itemView) {
    super(itemView);
    inflateViews(itemView);
  }

  protected abstract void inflateViews(View itemView);

  public void populate(GithubEvent event) {
    long millis = System.currentTimeMillis();
    populateAvatar(event.actor);
    populateContent(event);
    populateDate(TimeUtils.getTimeAgoString(event.created_at));

    if (BuildConfig.DEBUG) {
      Log.i("Event: " + event.type, (System.currentTimeMillis() - millis) + "ms");
    }

    itemView.setOnClickListener(view -> {
      if (tracker != null) {
        tracker.trackEvent("EventClick", "type", event.type.toString());
      }
      if (callback != null) {
        callback.onItemSelected(event);
      }
    });
  }

  protected abstract void populateAvatar(User actor);

  protected abstract void populateContent(GithubEvent event);

  protected abstract void populateDate(String date);

  public void setCallback(RecyclerArrayAdapter.ItemCallback<GithubEvent> callback) {
    this.callback = callback;
  }

  public void setTracker(Tracker tracker) {
    this.tracker = tracker;
  }
}
