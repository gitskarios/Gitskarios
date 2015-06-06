package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.DeleteEventPayload;
import com.alorma.github.sdk.bean.dto.response.events.payload.ReleaseEventPayload;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 30/05/2015.
 */
public class ReleaseEventView extends GithubEventView<ReleaseEventPayload> {
    public ReleaseEventView(Context context) {
        super(context);
    }

    public ReleaseEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReleaseEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.payload_deleted, this);
    }

    @Override
    protected void populateView(GithubEvent event) {
        ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);

        TextView authorName = (TextView) findViewById(R.id.authorName);

        TextView textTitle = (TextView) findViewById(R.id.textTitle);

        if (eventPayload.action.equalsIgnoreCase("published")) {
            int textRes = eventPayload.release.prerelease ? R.string.event_prereleased_by : R.string.event_released_by;
            String text = getContext().getResources().getString(textRes,
                    event.actor.login, eventPayload.release.tag_name, event.repo.name);

            authorName.setText(Html.fromHtml(text));

        }

        TextView textDate = (TextView) findViewById(R.id.textDate);

        String timeString = TimeUtils.getTimeString(textDate.getContext(), event.created_at);

        textDate.setText(timeString);
    }

    @Override
    protected ReleaseEventPayload convert(Gson gson, String s) {
        return gson.fromJson(s, ReleaseEventPayload.class);
    }
}
