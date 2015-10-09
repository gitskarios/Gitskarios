package com.alorma.github.ui.adapter.events;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.adapter.events.views.CommitCommentEventView;
import com.alorma.github.ui.adapter.events.views.CreatedEventView;
import com.alorma.github.ui.adapter.events.views.DeleteEventView;
import com.alorma.github.ui.adapter.events.views.ForkEventView;
import com.alorma.github.ui.adapter.events.views.GenericFeedEventView;
import com.alorma.github.ui.adapter.events.views.GithubEventView;
import com.alorma.github.ui.adapter.events.views.IssueCommentEventView;
import com.alorma.github.ui.adapter.events.views.IssueEventView;
import com.alorma.github.ui.adapter.events.views.PullRequestEventView;
import com.alorma.github.ui.adapter.events.views.PushEventView;
import com.alorma.github.ui.adapter.events.views.ReleaseEventView;
import com.alorma.github.ui.adapter.events.views.UnhandledEventView;
import com.alorma.github.ui.adapter.events.views.WatchEventView;
import com.alorma.github.utils.TimeUtils;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.Collection;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventAdapter extends RecyclerArrayAdapter<GithubEvent, EventAdapter.ViewHolder> {

    private final Resources resources;
    private EventAdapterListener eventAdapterListener;

    public EventAdapter(Context context, LayoutInflater inflater) {
        super(inflater);
        resources = context.getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.payload_watch, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, GithubEvent githubEvent) {
        handleImage(holder.authorAvatar, githubEvent);
        int textRes = R.string.event_generic_by;

        holder.authorName.setText(Html.fromHtml(resources.getString(textRes, githubEvent.actor.login, getTextForEvent(githubEvent))));

        String timeString = TimeUtils.getTimeAgoString(githubEvent.created_at);

        holder.textDate.setText(timeString);
    }

    public String getTextForEvent(GithubEvent event) {
        switch (event.type) {
            case WatchEvent:
                String textRes = "watched";
                if (event.payload.action.equalsIgnoreCase("started")) {
                    textRes = "starred";
                }
                return textRes + " " + "<b>" + event.repo.name + "</b>";
            case CreateEvent:
                return "created repository" + " " + "<b>" + event.repo.name + "</b>";
            case CommitCommentEvent:
                return "";
            case DownloadEvent:
                return "";
            case FollowEvent:
                return "";
            case ForkEvent:
                return "forked" + " <b>" + event.repo.name + "</b>" + " " + "to" + " " + "<b>" + event.payload.forkee.full_name + "</b>";
            case GistEvent:
                return "";
            case GollumEvent:
                return "";
            case IssueCommentEvent:
                String type = event.payload.issue.pullRequest == null ? "issue" : "pull request";
                return "commented on " + type + " " + "<b>" + event.repo.name + "#" + event.payload.issue.number + "</b>";
            case IssuesEvent:
                return event.payload.action + " " + "<b>" + event.repo.name + "#" + event.payload.issue.number + "</b>";
            case MemberEvent:
                return "";
            case PublicEvent:
                return "";
            case PullRequestEvent:
                String action = event.payload.action;

                if (event.payload.pull_request.merged) {
                    action = "merged";
                }

                return action + " pull request" + " " + "<b>" + event.repo.name + "#" + event.payload.pull_request.number + "</b>";
            case PullRequestReviewCommentEvent:
                return "";
            case PushEvent:
                String ref = event.payload.ref;
                String[] refs = ref.split("/");
                if (refs.length > 1) {
                    ref = refs[refs.length - 1];
                } else if (refs.length == 1) {
                    ref = refs[0];
                }
                return "pushed to" + " " + "<b>" + ref + "</b>" + " " + "at" + " " + "<b>" + event.repo.name + "</b>";
            case StatusEvent:
                return "";
            case TeamAddEvent:
                return "";
            case DeleteEvent:
                return "";
            case ReleaseEvent:
                return event.payload.action + " " + "<b>" + event.payload.release.tag_name + "</b>" + " " + "at" + " " + "<b>" + event.repo.name + "</b>";
        }

        return "";

    }

    public void handleImage(ImageView imageView, GithubEvent event) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ALPHA_8).build();
        ImageLoader.getInstance().displayImage(event.actor.avatar_url, imageView, displayImageOptions);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type.ordinal();
    }

    public void setEventAdapterListener(EventAdapterListener eventAdapterListener) {
        this.eventAdapterListener = eventAdapterListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView authorAvatar;
        private final TextView authorName;
        private final TextView textDate;

        private ViewHolder(View itemView) {
            super(itemView);

            authorAvatar = (ImageView) itemView.findViewById(R.id.authorAvatar);
            authorName = (TextView) itemView.findViewById(R.id.authorName);
            textDate = (TextView) itemView.findViewById(R.id.textDate);

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
