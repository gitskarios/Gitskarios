package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.IssueState;
import com.alorma.github.sdk.bean.dto.response.events.payload.CreatedEventPayload;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 04/10/2014.
 */
public class CreatedEventView extends GithubEventView<CreatedEventPayload> {
    public CreatedEventView(Context context) {
        super(context);
    }

    public CreatedEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreatedEventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void inflate() {
        inflate(getContext(), R.layout.payload_create, this);
    }

    @Override
    protected void populateView(GithubEvent event) {

        ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

        ImageLoader.getInstance().displayImage(event.actor.avatar_url, authorAvatar);

        TextView authorName = (TextView) findViewById(R.id.authorName);

        TextView textTitle = (TextView) findViewById(R.id.textTitle);

        int textRes = 0;
        if (eventPayload.ref_type.equalsIgnoreCase("branch")) {
            textRes = R.string.event_created_branch_by;
        } else if (eventPayload.ref_type.equalsIgnoreCase("tag")) {
            textRes = R.string.event_created_tag_by;
        } else if (eventPayload.ref_type.equalsIgnoreCase("repository")) {
            String text = getContext().getResources().getString(R.string.event_created_repository_by,
                    event.actor.login, event.repo.name);

            authorName.setText(Html.fromHtml(text));
        }

        String title = eventPayload.description;
        if (title != null) {
            textTitle.setText(title);
            textTitle.setVisibility(View.VISIBLE);
        } else {
            textTitle.setVisibility(View.GONE);
        }

        if (textRes != 0) {
            String text = getContext().getResources().getString(textRes,
                    event.actor.login, eventPayload.ref, event.repo.name);

            authorName.setText(Html.fromHtml(text));
        }


        TextView textDate = (TextView) findViewById(R.id.textDate);

        String timeString = TimeUtils.getTimeString(textDate.getContext(), event.created_at);

        textDate.setText(timeString);
    }

    @Override
    protected CreatedEventPayload convert(Gson gson, String s) {
        return gson.fromJson(s, CreatedEventPayload.class);
    }
}
