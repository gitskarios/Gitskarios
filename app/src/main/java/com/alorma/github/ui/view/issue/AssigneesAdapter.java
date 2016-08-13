package com.alorma.github.ui.view.issue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.bumptech.glide.Glide;

public class AssigneesAdapter extends RecyclerArrayAdapter<User, AssigneesAdapter.AssigneeHolder> {
  public AssigneesAdapter(LayoutInflater layoutInflater) {
    super(layoutInflater);
  }

  @Override
  protected void onBindViewHolder(AssigneeHolder holder, User user) {
    Glide.with(holder.imageView.getContext()).load(user.avatar_url).into(holder.imageView);
    holder.textView.setText(user.login);
  }

  @Override
  public AssigneeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AssigneeHolder(getInflater().inflate(R.layout.assignee_row, parent, false));
  }

  public class AssigneeHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image) ImageView imageView;
    @BindView(R.id.text) TextView textView;

    public AssigneeHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(view -> {
        if (getCallback() != null) {
          getCallback().onItemSelected(getItem(getAdapterPosition()));
        }
      });
    }
  }
}
