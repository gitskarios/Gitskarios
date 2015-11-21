package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.PushEventPayload;
import com.alorma.github.utils.TextUtils;
import com.alorma.github.utils.TimeUtils;
import com.google.gson.Gson;
import java.io.IOException;

/**
 * Created by Bernat on 04/10/2014.
 */
public class PushEventView extends GithubEventView<PushEventPayload> {
  public PushEventView(Context context) {
    super(context);
  }

  public PushEventView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public PushEventView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void inflate() {
    inflate(getContext(), R.layout.payload_push, this);
  }

  @Override
  protected void populateView(GithubEvent event) {
    ImageView authorAvatar = (ImageView) findViewById(R.id.authorAvatar);

    //load the profile image from url with optimal settings
    handleImage(authorAvatar, event);

    TextView authorName = (TextView) findViewById(R.id.authorName);
    authorName.setText(event.actor.login);

    int textRes = R.string.event_pushed_comment_by;

    String ref = eventPayload.ref.replace("refs/heads/", "");

    String text = getContext().getString(textRes, event.actor.login, event.repo.name, ref);

    authorName.setText(Html.fromHtml(text));

    TextView textTitle = (TextView) findViewById(R.id.textTitle);
    if (eventPayload != null) {
      if (eventPayload.commits != null) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Math.min(eventPayload.commits.size(), 3); i++) {
          Commit commit = eventPayload.commits.get(i);
          try {
            builder.append("<b>");
            builder.append(commit.shortSha());
            builder.append("</b>");
            builder.append(" - ");
            builder.append(TextUtils.splitLines(commit.shortMessage(), 2));
            builder.append("<br />");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        if (eventPayload.size > 3) {
          builder.append(getContext().getString(R.string.n_more_commits, (eventPayload.size - 3)));
        }
        textTitle.setText(Html.fromHtml(builder.toString()));
      }
    }

    TextView textDate = (TextView) findViewById(R.id.textDate);

    String timeString = TimeUtils.getTimeAgoString(event.created_at);

    textDate.setText(timeString);
  }

  @Override
  protected PushEventPayload convert(Gson gson, String s) {
    return gson.fromJson(s, PushEventPayload.class);
  }
}
