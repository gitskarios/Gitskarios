package com.alorma.github.ui.adapter.users;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.UserAvatarView;

public class UsersAdapterSpinner extends RecyclerArrayAdapter<User, UsersAdapterSpinner.Holder> {

  public UsersAdapterSpinner(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  protected void onBindViewHolder(Holder holder, User user) {
    holder.fill(user);
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new Holder(getInflater().inflate(R.layout.item_profile, parent, false));
  }

  public class Holder extends RecyclerView.ViewHolder {
    @BindView(R.id.profileIcon) UserAvatarView authorAvatar;
    @BindView(R.id.name) TextView authorLogin;
    @BindView(R.id.email) TextView authorEmail;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(view -> {
        if (getCallback() != null) {
          getCallback().onItemSelected(getItem(getAdapterPosition()));
        }
      });
    }

    public void fill(User user) {
      if (authorAvatar != null) {
        this.authorAvatar.setVisibility(View.VISIBLE);
        authorAvatar.setUser(user);
      }

      if (authorLogin != null) {
        this.authorLogin.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(user.login)) {
          authorLogin.setText(user.login);
        }
      }

      if (authorEmail != null) {
        this.authorEmail.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(user.login)) {
          authorEmail.setText(user.email);
        }
      }
    }

    public void clear() {
      if (authorLogin != null) {
        authorLogin.setText("");
        this.authorLogin.setVisibility(View.INVISIBLE);
      }
      if (authorEmail != null) {
        authorEmail.setText("");
        this.authorEmail.setVisibility(View.INVISIBLE);
      }
      if (authorAvatar != null) {
        authorAvatar.setImageDrawable(null);
        this.authorAvatar.setVisibility(View.INVISIBLE);
      }
    }
  }
}
