package com.alorma.github.ui.fragment.releases;

import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import core.repositories.releases.Asset;

public class ReleaseAssetsAdapter extends RecyclerArrayAdapter<Asset, ReleaseAssetsAdapter.Holder> {

  private OnReleaseAssetClicked onReleaseAssetClicked;

  public ReleaseAssetsAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  protected void onBindViewHolder(Holder holder, Asset releaseAsset) {
    holder.releaseAssetName.setText(releaseAsset.getName());
    if (releaseAsset.getSize() != null && releaseAsset.getSize() > 0) {
      String size = Formatter.formatShortFileSize(holder.releaseAssetSize.getContext(), releaseAsset.getSize());
      holder.releaseAssetSize.setText(size);
      holder.releaseAssetSize.setVisibility(View.VISIBLE);
    } else {
      holder.releaseAssetSize.setVisibility(View.GONE);
    }
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new Holder(getInflater().inflate(R.layout.row_repo_release_asset, parent, false));
  }

  public void setOnReleaseAssetClicked(OnReleaseAssetClicked onReleaseAssetClicked) {
    this.onReleaseAssetClicked = onReleaseAssetClicked;
  }

  public class Holder extends RecyclerView.ViewHolder {

    @BindView(R.id.releaseAssetName) TextView releaseAssetName;
    @BindView(R.id.releaseAssetSize) TextView releaseAssetSize;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(v -> {
        if (onReleaseAssetClicked != null) {
          onReleaseAssetClicked.onReleaseAssetCLicked(getItem(getAdapterPosition()));
        }
      });
    }
  }

  public interface OnReleaseAssetClicked {
    void onReleaseAssetCLicked(Asset asset);
  }
}
