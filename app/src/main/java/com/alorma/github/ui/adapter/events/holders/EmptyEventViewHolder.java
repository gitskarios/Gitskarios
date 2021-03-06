package com.alorma.github.ui.adapter.events.holders;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.UserAvatarView;
import core.User;

public class EmptyEventViewHolder extends EventViewHolder {

  @BindView(R.id.authorAvatar) UserAvatarView authorAvatar;
  @BindView(R.id.authorName) TextView authorName;
  @BindView(R.id.textDate) TextView textDate;

  public EmptyEventViewHolder(View itemView, RecyclerArrayAdapter.ItemCallback<GithubEvent> callback) {
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

  }

  @Override
  protected void populateDate(String date) {
    textDate.setText(date);
  }
}
