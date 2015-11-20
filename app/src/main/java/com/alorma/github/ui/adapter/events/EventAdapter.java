package com.alorma.github.ui.adapter.events;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.TimeUtils;
import com.musenkishi.atelier.Atelier;
import com.musenkishi.atelier.ColorType;
import com.musenkishi.atelier.swatch.DarkVibrantSwatch;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Bernat on 03/10/2014.
 */
public class EventAdapter extends RecyclerArrayAdapter<GithubEvent, EventAdapter.ViewHolder> {

    private final Resources resources;
    private final int defaultProfileColor;
    private EventAdapterListener eventAdapterListener;

    public EventAdapter(Context context, LayoutInflater inflater) {
        super(inflater);
        resources = context.getResources();
        defaultProfileColor = ContextCompat.getColor(context, R.color.primary);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.payload_watch, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, GithubEvent githubEvent) {
        handleImage(holder.authorAvatar, githubEvent);
        int textRes = R.string.event_generic_by;

        String textForEvent = getTextForEvent(githubEvent);

        holder.authorName.setText(
            Html.fromHtml(resources.getString(textRes, githubEvent.actor.login, textForEvent)));

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
                return "commented on commit "
                    + "<b>"
                    + event.repo.name
                    + "@"
                    + event.payload.comment.commit_id.substring(0, 10)
                    + "</b>";
            case DownloadEvent:
                return "";
            case FollowEvent:
                return "";
            case ForkEvent:
                return "forked"
                    + " <b>"
                    + event.repo.name
                    + "</b>"
                    + " "
                    + "to"
                    + " "
                    + "<b>"
                    + event.payload.forkee.full_name
                    + "</b>";
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

                return action
                    + " pull request"
                    + " "
                    + "<b>"
                    + event.repo.name
                    + "#"
                    + event.payload.pull_request.number
                    + "</b>";
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
                String deletedThing = "repository";
                if (event.payload.ref != null) {
                    deletedThing = "branch <b>" + event.payload.ref + "</b> at ";
                }
                return "deleted " + deletedThing + "<b>" + event.repo.name + "</b>";
            case ReleaseEvent:
                return event.payload.action
                    + " "
                    + "<b>"
                    + event.payload.release.tag_name
                    + "</b>"
                    + " "
                    + "at"
                    + " "
                    + "<b>"
                    + event.repo.name
                    + "</b>";
        }

        return "";
    }

    public void handleImage(final ImageView imageView, GithubEvent event) {
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.ALPHA_8)
            .build();
        ImageLoader.getInstance().displayImage(event.actor.avatar_url, imageView,
            displayImageOptions, new ImageLoadingListener() {
                @Override public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Context context = imageView.getContext();

                    //Set color tag to imageView
                    Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
                        .listener(new Atelier.OnPaletteRenderedListener() {
                            @Override
                            public void onRendered(Palette palette) {
                                imageView.setTag(palette.getVibrantColor(defaultProfileColor));
                            }
                        });
                }

                @Override public void onLoadingCancelled(String imageUri, View view) {

                }
            });
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
            authorAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    User user = getItem(getAdapterPosition()).actor ;
                    if (user.type == UserType.Organization) {
                        v.getContext().startActivity(
                            OrganizationActivity.launchIntent(v.getContext(), user.login));
                    } else {
                        final Intent
                            intent = ProfileActivity.createLauncherIntent(v.getContext(), user);
                        if (authorAvatar.getTag() != null) {
                            int color = (int) authorAvatar.getTag();
                            intent.putExtra(ProfileActivity.EXTRA_COLOR, color);
                        }
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    public interface EventAdapterListener {
        void onItem(GithubEvent event);
    }
}
