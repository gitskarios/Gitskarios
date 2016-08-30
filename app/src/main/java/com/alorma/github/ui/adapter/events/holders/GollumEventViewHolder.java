package com.alorma.github.ui.adapter.events.holders;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.GithubPage;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.view.UserAvatarView;
import java.util.List;

public class GollumEventViewHolder extends EventViewHolder {

  @BindView(R.id.authorAvatar) UserAvatarView authorAvatar;
  @BindView(R.id.authorName) TextView authorName;
  @BindView(R.id.textDate) TextView textDate;

  public GollumEventViewHolder(View itemView) {
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
    if (event.payload.pages != null) {
      List<GithubPage> pages = event.payload.pages;
      if (pages.size() == 1) {
        String text = "<b>" + event.actor.login + "</b>" + " " + pages.get(0).action + " the <b>" + event.repo.name + "</b> wiki";
        authorName.setText(Html.fromHtml(text));
      } else {
        String text = "<b>" + event.actor.login + "</b>" + " updated the <b>" + event.repo.name + "</b> wiki";
        authorName.setText(Html.fromHtml(text));
      }
    }
  }

  @Override
  protected void populateDate(String date) {
    textDate.setText(date);
  }
}
