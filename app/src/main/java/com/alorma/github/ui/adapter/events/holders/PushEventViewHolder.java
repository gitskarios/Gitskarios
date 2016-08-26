package com.alorma.github.ui.adapter.events.holders;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.view.UserAvatarView;

public class PushEventViewHolder extends EventViewHolder {

  @BindView(R.id.authorAvatar) UserAvatarView authorAvatar;
  @BindView(R.id.authorName) TextView authorName;
  @BindView(R.id.textDate) TextView textDate;

  public PushEventViewHolder(View itemView) {
    super(itemView);
  }

  @Override
  protected void inflateViews(View itemView) {
    ButterKnife.bind(this, itemView);
  }

  @Override
  protected void populateAvatar(User actor) {
    authorAvatar.setUser(actor);
  }

  @Override
  protected void populateContent(GithubEvent event) {

    String ref = event.payload.ref;
    String[] refs = ref.split("/");
    if (refs.length > 1) {
      ref = refs[refs.length - 1];
    } else if (refs.length == 1) {
      ref = refs[0];
    }

    String text = "<b>"
        + event.actor.login
        + "</b>"
        + " "
        + "pushed to"
        + " "
        + "<b>"
        + ref
        + "</b>"
        + " "
        + "at"
        + " "
        + "<b>"
        + event.repo.name
        + "</b>";
    authorName.setText(Html.fromHtml(text));
  }

  @Override
  protected void populateDate(String date) {
    textDate.setText(date);
  }
}
