package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.WatchedEventPayload;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;

/**
 * Created by Bernat on 04/10/2014.
 */
public class GenericFeedEventView extends GithubEventView<WatchedEventPayload> {
    public GenericFeedEventView(Context context) {
        super(context);
    }

    public GenericFeedEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GenericFeedEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.payload_watch, this);
    }

    @Override
    protected void populateView(GithubEvent event) {

//        ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);
//        //load the profile image from url with optimal settings
//        handleImage(authorAvatar, event);
//
//        TextView authorName = (TextView) findViewById(R.id.authorName);
//        int textRes = R.string.event_generic_by;
//        authorName.setText(Html.fromHtml(getContext().getResources().getString(textRes, event.actor.login, event.payload.action, event.repo.name)));
//
//        TextView textDate = (TextView) findViewById(R.id.textDate);
//
//        String timeString = TimeUtils.getTimeAgoString(event.created_at);
//
//        textDate.setText(timeString);
    }

    @Override
    protected WatchedEventPayload convert(Gson gson, String s) {
        return gson.fromJson(s, WatchedEventPayload.class);
    }
}
