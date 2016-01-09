package com.alorma.github.ui.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ReleaseDetailActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

/**
 * Created by a557114 on 29/07/2015.
 */
public class ReleasesAdapter extends RecyclerArrayAdapter<Release, ReleasesAdapter.Holder> {

    private RepoInfo repoInfo;

    public ReleasesAdapter(LayoutInflater inflater, RepoInfo repoInfo) {
        super(inflater);
        this.repoInfo = repoInfo;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(getInflater().inflate(R.layout.row_repo_release, parent, false));
    }

    @Override
    protected void onBindViewHolder(Holder holder, Release release) {
        String name = release.name;
        if (TextUtils.isEmpty(name)) {
            name = release.tag_name;
        }
        holder.releaseName.setText(name);

        if (release.prerelease) {
            holder.releaseState.setImageResource(R.drawable.repo_release_prerelease);
        }

        if (release.assets != null) {
            int size = release.assets.size();
            if (!TextUtils.isEmpty(release.zipball_url)) {
                size = size + 1;
            }

            if (!TextUtils.isEmpty(release.tarball_url)) {
                size = size + 1;
            }

            holder.filesRelease.setText(
                    holder.filesRelease.getContext().getResources().getQuantityString(R.plurals.repo_release_num_files, size, size));
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView releaseState;
        private final TextView releaseName;
        private final TextView filesRelease;

        public Holder(View itemView) {
            super(itemView);
            releaseState = (ImageView) itemView.findViewById(R.id.releaseState);
            releaseName = (TextView) itemView.findViewById(R.id.releaseName);
            filesRelease = (TextView) itemView.findViewById(R.id.filesRelease);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ReleaseDetailActivity.launchIntent(v.getContext(), getItem(getAdapterPosition()), repoInfo);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
