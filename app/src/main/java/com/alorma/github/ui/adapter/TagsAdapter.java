package com.alorma.github.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.presenter.CommitInfoPresenter;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.ReleaseDetailActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.utils.DialogUtils;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.GitskariosDownloadManager;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import core.User;
import core.repositories.releases.Release;
import core.repositories.releases.tags.Tag;

public class TagsAdapter extends RecyclerArrayAdapter<Tag, TagsAdapter.Holder> {

  private RepoInfo repoInfo;
  private final CommitInfoPresenter comitPresenter;
  private GitskariosDownloadManager gitskariosDownloadManager;

  public TagsAdapter(LayoutInflater inflater, RepoInfo repoInfo, CommitInfoPresenter commitPresenter) {
    super(inflater);
    this.repoInfo = repoInfo;
    this.comitPresenter = commitPresenter;
    this.gitskariosDownloadManager = new GitskariosDownloadManager();
  }

  @Override
  protected int lazyLoadCount() {
    return 5;
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
        new IconicsDrawable(holder.itemView.getContext()).icon(Octicons.Icon.oct_tag).colorRes(R.color.md_grey_500).sizeDp(40);
    holder.imageState.setImageDrawable(tagDrawable);
    int size = 2;
    String numFilesString = holder.files.getContext().getResources().getQuantityString(R.plurals.repo_release_num_files, size, size);
    holder.files.setText(numFilesString);
  }

  private void fillRelease(Holder holder, Release release) {
    String name = release.getName();
    if (TextUtils.isEmpty(name)) {
      name = release.getTagName();
    }
    holder.name.setText(name);

    IconicsDrawable imageDrawable = new IconicsDrawable(holder.itemView.getContext()).icon(Octicons.Icon.oct_bookmark).sizeDp(40);
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
      String numFilesString = holder.files.getContext().getResources().getQuantityString(R.plurals.repo_release_num_files, size, size);
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

      itemView.setOnClickListener(ITEM_ON_CLICK_LISTENER);
    }

    private final View.OnClickListener ITEM_ON_CLICK_LISTENER = new View.OnClickListener() {
      private com.alorma.github.presenter.View<Commit> view;

      @Override
      public void onClick(View v) {
        Tag tag = getItem(getAdapterPosition());
        Release release = tag.release;
        Context context = itemView.getContext();
        if (release != null) {
          Intent intent = ReleaseDetailActivity.launchIntent(context, release, repoInfo);
          context.startActivity(intent);
        } else {
          LayoutInflater inflater = getInflater();
          View tagDetailsView = inflater.inflate(R.layout.dialog_tag_details, null, false);

          MaterialDialog.Builder builder = new DialogUtils().builder(context);
          builder.title(tag.getName())
              .negativeText(R.string.cancel)
              .onNegative(((dialog, which) -> dialog.dismiss()))
              .customView(tagDetailsView, true)
              .autoDismiss(false)
              .build()
              .show();

          CommitInfo commitInfo = new CommitInfo();
          commitInfo.repoInfo = repoInfo;
          commitInfo.sha = tag.getSha().getSha();
          view = new com.alorma.github.presenter.View<Commit>() {
            @Override
            public void showLoading() {
              tagDetailsView.findViewById(R.id.progressBarLayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void hideLoading() {
              tagDetailsView.findViewById(R.id.progressBarLayout).setVisibility(View.GONE);
            }

            @Override
            public void onDataReceived(Commit commit, boolean isFromPaginated) {
              fillTagDetailsView(tagDetailsView, tag, commit);
            }

            @Override
            public void showError(Throwable throwable) {

            }
          };
          comitPresenter.attachView(view);
          comitPresenter.execute(commitInfo);
        }
      }
    };

    private void fillTagDetailsView(View tagDetailsView, Tag tag, Commit commit) {
      Context context = tagDetailsView.getContext();
      UserAvatarView userAvatar = (UserAvatarView) tagDetailsView.findViewById(R.id.profileIcon);
      User owner = commit.author;
      userAvatar.setUser(owner);

      TextView authorName = (TextView) tagDetailsView.findViewById(R.id.authorName);
      authorName.setText(commit.author.getLogin());

      ImageView dateIcon = (ImageView) tagDetailsView.findViewById(R.id.createdIcon);
      IconicsDrawable icon =
          new IconicsDrawable(context, Octicons.Icon.oct_clock).sizeDp(34).color(AttributesUtils.getAccentColor(context));
      dateIcon.setImageDrawable(icon);

      TextView date = (TextView) tagDetailsView.findViewById(R.id.createdAt);
      DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
      DateTime dt = formatter.parseDateTime(commit.commit.committer.date);
      date.setText(TimeUtils.getDateToText(context, dt.toDate(), R.string.created_at));

      Button downloadZip = (Button) tagDetailsView.findViewById(R.id.downloadZip);
      downloadZip.setOnClickListener(view -> {
        gitskariosDownloadManager.download(context, tag.getZipballUrl(),
            context.getString(R.string.download_zip_archive) + " file for tag " + tag.getName(),
            text -> showSnackbar(tagDetailsView, context, text));
      });

      Button downloadTar = (Button) tagDetailsView.findViewById(R.id.downloadTar);
      downloadTar.setOnClickListener(view -> {
        gitskariosDownloadManager.download(context, tag.getZipballUrl(),
            context.getString(R.string.download_tar_archive) + " file for tag " + tag.getName(),
            text -> showSnackbar(tagDetailsView, context, text));
      });
    }

    private void showSnackbar(View tagDetailsView, Context context, int text) {
      Snackbar snackbar = Snackbar.make(tagDetailsView, context.getString(text), Snackbar.LENGTH_LONG);
      snackbar.setAction(context.getString(R.string.external_storage_permission_request_action),
          v -> gitskariosDownloadManager.openSettings(context));
      snackbar.show();
    }
  }
}
