package com.alorma.github.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseAssetsAdapter extends RecyclerArrayAdapter<ReleaseAsset, ReleaseAssetsAdapter.Holder> {

    public ReleaseAssetsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(getInflater().inflate(R.layout.row_repo_release_asset, parent, false));
    }

    @Override
    protected void onBindViewHolder(Holder holder, ReleaseAsset releaseAsset) {
        holder.releaseAssetName.setText(releaseAsset.name);
        if (releaseAsset.size > 0) {
            String size = Formatter.formatShortFileSize(holder.releaseAssetSize.getContext(), releaseAsset.size);
            holder.releaseAssetSize.setText(size);
            holder.releaseAssetSize.setVisibility(View.VISIBLE);
        } else {
            holder.releaseAssetSize.setVisibility(View.GONE);
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView releaseAssetName;
        private final TextView releaseAssetSize;

        public Holder(View itemView) {
            super(itemView);
            releaseAssetName = (TextView) itemView.findViewById(R.id.releaseAssetName);
            releaseAssetSize = (TextView) itemView.findViewById(R.id.releaseAssetSize);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
