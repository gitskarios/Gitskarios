package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.DeleteEventPayload;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;

/**
 * Created by Bernat on 30/05/2015.
 */
public class DeleteEventView extends GithubEventView<DeleteEventPayload> {
    public DeleteEventView(Context context) {
        super(context);
    }

    public DeleteEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DeleteEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.payload_deleted, this);
    }

    @Override
    protected void populateView(GithubEvent event) {
        ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        //load the profile image from url with optimal settings
        handleImage(authorAvatar, event);

        TextView authorName = (TextView) findViewById(R.id.authorName);

        int textRes = 0;
        if (eventPayload.ref_type.equalsIgnoreCase("branch")) {
            textRes = R.string.event_delete_branch_by;
        } else if (eventPayload.ref_type.equalsIgnoreCase("tag")) {
            textRes = R.string.event_delete_tag_by;
        }

        if (textRes != 0) {
            String text = getContext().getResources().getString(textRes,
                    event.actor.login, eventPayload.ref, event.repo.name);

            authorName.setText(Html.fromHtml(text));
        }

        TextView textDate = (TextView) findViewById(R.id.textDate);

        String timeString = TimeUtils.getTimeAgoString(textDate.getContext(), event.created_at);

        textDate.setText(timeString);
    }

    @Override
    protected DeleteEventPayload convert(Gson gson, String s) {
        return gson.fromJson(s, DeleteEventPayload.class);
    }
}
