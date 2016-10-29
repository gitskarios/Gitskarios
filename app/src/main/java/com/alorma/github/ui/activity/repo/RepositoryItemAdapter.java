package com.alorma.github.ui.activity.repo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;

public class RepositoryItemAdapter extends RecyclerArrayAdapter<RepoItem, RepositoryItemAdapter.Holder> {

  private static final int DEFAULT_ITEM = 0;
  private static final int EXPANDABLE_ITEM = 1;

  public RepositoryItemAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case EXPANDABLE_ITEM:
        return new ExpandableViewHolder(getInflater().inflate(R.layout.repo_about_item_expandable, parent, false));
      case DEFAULT_ITEM:
      default:
        return new SimpleViewHolder(getInflater().inflate(R.layout.repo_about_item_simple, parent, false));
    }
  }

  @Override
  protected void onBindViewHolder(Holder holder, RepoItem repoItem) {
    holder.populate(repoItem);
  }

  @Override
  protected int getItemViewType(RepoItem repoItem) {
    if (repoItem.isExpandable()) {
      return EXPANDABLE_ITEM;
    }
    return DEFAULT_ITEM;
  }

  public class ExpandableViewHolder extends Holder {
    @BindView(R.id.image) ImageView image;
    @BindView(R.id.text) TextView text;
    @BindView(R.id.expandable_icon) ImageView expandableIcon;

    public ExpandableViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(RepoItem repoItem) {
      if (repoItem.getAvatar() == null) {
        image.setImageResource(repoItem.getIcon());
      } else {
        UniversalImageLoaderUtils.loadUserAvatar(image, repoItem.getContent(), repoItem.getAvatar());
      }
      text.setText(repoItem.getContent());
    }
  }

  public class SimpleViewHolder extends Holder {
    @BindView(R.id.image) ImageView image;
    @BindView(R.id.text) TextView text;

    public SimpleViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(RepoItem repoItem) {
      if (repoItem.getAvatar() == null) {
        image.setImageResource(repoItem.getIcon());
      } else {
        UniversalImageLoaderUtils.loadUserAvatar(image, repoItem.getContent(), repoItem.getAvatar());
      }
      text.setText(repoItem.getContent());
    }
  }

  public abstract class Holder extends RecyclerView.ViewHolder {
    public Holder(View itemView) {
      super(itemView);

      itemView.setOnClickListener(v -> {
        RepoItem item = getItem(getAdapterPosition());

        if (item != null && getCallback() != null) {
          getCallback().onItemSelected(item);
        }
      });
    }

    public abstract void populate(RepoItem repoItem);
  }
}
