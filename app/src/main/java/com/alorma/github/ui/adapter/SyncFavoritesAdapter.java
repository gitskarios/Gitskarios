package com.alorma.github.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alorma.github.bean.sync.SyncFavorite;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

public class SyncFavoritesAdapter extends RecyclerArrayAdapter<SyncFavorite, SyncFavoritesAdapter.Holder> {

  private static final int DEFAULT_VIEW = 0;

  public SyncFavoritesAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new Holder(getInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
  }

  @Override
  protected void onBindViewHolder(Holder holder, SyncFavorite syncFavorite) {
    String type = syncFavorite.getType().name();
    holder.text.setText(type);
  }

  @Override
  protected int getItemViewType(SyncFavorite syncFavorite) {
    return DEFAULT_VIEW;
  }

  public class Holder extends RecyclerView.ViewHolder {
    private final TextView text;

    public Holder(View itemView) {
      super(itemView);
      text = (TextView) itemView.findViewById(android.R.id.text1);
    }
  }
}
