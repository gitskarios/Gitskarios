package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.WatchedEventPayload;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 04/10/2014.
 */
public class WatchEventView extends GithubEventView<WatchedEventPayload> {
    public WatchEventView(Context context) {
        super(context);
    }

    public WatchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WatchEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.payload_watch, this);
    }

    @Override
    protected void populateView(GithubEvent event) {

        int textRes = R.string.event_watched_by;
        if (eventPayload.action.equalsIgnoreCase("started")) {
            textRes = R.string.event_starred_by;
        }

        ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        //load the profile image from url with optimal settings
        handleImage(authorAvatar, event);

        TextView authorName = (TextView) findViewById(R.id.authorName);
        authorName.setText(Html.fromHtml(getContext().getResources().getString(textRes, event.actor.login, event.repo.name)));

        TextView textDate = (TextView) findViewById(R.id.textDate);

        String timeString = TimeUtils.getTimeString(textDate.getContext(), event.created_at);

        textDate.setText(timeString);
    }

    @Override
    protected WatchedEventPayload convert(Gson gson, String s) {
        return gson.fromJson(s, WatchedEventPayload.class);
    }
}
