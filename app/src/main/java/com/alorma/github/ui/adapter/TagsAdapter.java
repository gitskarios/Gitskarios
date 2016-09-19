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
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.core.repositories.releases.Release;
import com.alorma.github.sdk.core.repositories.releases.tags.Tag;
import com.alorma.github.ui.activity.ReleaseDetailActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by a557114 on 29/07/2015.
 */
public class TagsAdapter extends RecyclerArrayAdapter<Tag, TagsAdapter.Holder> {

  private RepoInfo repoInfo;

  public TagsAdapter(LayoutInflater inflater, RepoInfo repoInfo) {
    super(inflater);
    this.repoInfo = repoInfo;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new Holder(getInflater().inflate(R.layout.row_repo_tag, parent, false));
  }

  @Override
  protected void onBindViewHolder(Holder holder, Tag tag) {
    Release release = tag.release;
    if (release != null) {
      fillRelease(holder, release);
    } else {
      fillTag(holder, tag);
    }
  }

  private void fillTag(Holder holder, Tag tag) {
    holder.name.setText(tag.getName());
    IconicsDrawable tagDrawable =
            new IconicsDrawable(holder.itemView.getContext())
                    .icon(Octicons.Icon.oct_tag)
                    .colorRes(R.color.md_grey_500)
                    .sizeDp(48);
    holder.imageState.setImageDrawable(tagDrawable);
    int size = 2;
    String numFilesString = holder.files.getContext()
            .getResources()
            .getQuantityString(R.plurals.repo_release_num_files, size, size);
    holder.files.setText(numFilesString);
  }

  private void fillRelease(Holder holder, Release release) {
    String name = release.getName();
    if (TextUtils.isEmpty(name)) {
      name = release.getTagName();
    }
    holder.name.setText(name);

    IconicsDrawable imageDrawable =
            new IconicsDrawable(holder.itemView.getContext())
                    .icon(Octicons.Icon.oct_bookmark)
                    .sizeDp(48);
    if (release.isPreRelease()) {
      imageDrawable.colorRes(R.color.release_prerelease);
    } else {
      imageDrawable.colorRes(R.color.release);
    }
    holder.imageState.setImageDrawable(imageDrawable);

    if (release.getAssets() != null) {
      int size = release.getAssets().size();
      if (!TextUtils.isEmpty(release.getZipballUrl())) {
        size = size + 1;
      }

      if (!TextUtils.isEmpty(release.getTarballUrl())) {
        size = size + 1;
      }
      String numFilesString = holder.files.getContext()
              .getResources()
              .getQuantityString(R.plurals.repo_release_num_files, size, size);
      holder.files.setText(numFilesString);
    }
  }

  public class Holder extends RecyclerView.ViewHolder {
    private final ImageView imageState;
    private final TextView name;
    private final TextView files;

    public Holder(View itemView) {
      super(itemView);
      imageState = (ImageView) itemView.findViewById(R.id.state);
      name = (TextView) itemView.findViewById(R.id.tagName);
      files = (TextView) itemView.findViewById(R.id.tagFiles);

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Tag tag = getItem(getAdapterPosition());
          Release release = tag.release;
          if (release != null) {
            com.alorma.github.sdk.bean.dto.response.Release dto =
                    new com.alorma.github.sdk.bean.dto.response.Release(release);
            Intent intent = ReleaseDetailActivity.launchIntent(v.getContext(), dto, repoInfo);
            v.getContext().startActivity(intent);
          }
        }
      });
    }
  }
}
