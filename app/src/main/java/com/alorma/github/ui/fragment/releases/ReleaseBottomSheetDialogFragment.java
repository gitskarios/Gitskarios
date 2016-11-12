package com.alorma.github.ui.fragment.releases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.GitskariosDownloadManager;
import com.alorma.github.utils.TimeUtils;
import core.repositories.releases.Asset;
import core.repositories.releases.Release;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ReleaseBottomSheetDialogFragment extends BottomSheetDialogFragment implements ReleaseAssetsAdapter.OnReleaseAssetClicked {

  private static final String RELEASE = "RELEASE";
  private static final String REPOINFO = "REPOINFO";

  @Inject GitskariosDownloadManager gitskariosDownloadManager;

  @BindView(R.id.profileIcon) UserAvatarView userAvatar;
  @BindView(R.id.authorName) TextView authorName;
  @BindView(R.id.createdAt) TextView date;
  @BindView(R.id.downloadZip) View downloadZip;
  @BindView(R.id.downloadTar) View downloadTar;
  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.recycler) RecyclerView recyclerView;

  public static ReleaseBottomSheetDialogFragment newInstance(Release release) {
    ReleaseBottomSheetDialogFragment fragment = new ReleaseBottomSheetDialogFragment();

    Bundle args = new Bundle();
    args.putParcelable(RELEASE, release);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    GitskariosApplication application = (GitskariosApplication) getContext().getApplicationContext();
    ApplicationComponent applicationComponent = application.getApplicationComponent();

    applicationComponent.inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.dialog_release_details, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    if (getArguments() != null) {
      Release release = getArguments().getParcelable(RELEASE);
      fillTagDetailsView(release);
    }
  }

  private void fillTagDetailsView(Release release) {
    authorName.setText(release.getAuthor().getLogin());
    userAvatar.setUser(release.getAuthor());

    date.setText(TimeUtils.getDateToText(getContext(), release.getCreatedAt(), R.string.created_at));

    fillAssets(release);
    configButton(downloadZip, R.string.download_zip_archive, release.getZipballUrl(), release);
    configButton(downloadTar, R.string.download_tar_archive, release.getTarballUrl(), release);
  }

  private void fillAssets(Release release) {
    if (release.getAssets() != null) {
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

      ReleaseAssetsAdapter assetsAdapter = new ReleaseAssetsAdapter(LayoutInflater.from(getContext()));
      assetsAdapter.setOnReleaseAssetClicked(this);

      List<Asset> assets = new ArrayList<>();

      for (Asset asset : release.getAssets()) {
        boolean isZip = release.getZipballUrl().equals(asset.getBrowserDownloadUrl());
        boolean isTar = release.getTarballUrl().equals(asset.getBrowserDownloadUrl());
        if (!isZip && !isTar) {
          assets.add(asset);
        }
      }

      assetsAdapter.addAll(assets);
      recyclerView.setAdapter(assetsAdapter);
    }
  }

  private void configButton(View button, int title, String url, Release tag) {
    button.setOnClickListener(view -> {
      String downloadText = getString(title) + " file for release " + tag.getName();
      gitskariosDownloadManager.download(getContext(), url, downloadText, this::showSnackbar);
    });
  }

  private void showSnackbar(int text) {
    if (getView() != null) {
      String title = getString(R.string.external_storage_permission_request_action);
      Snackbar snackbar = Snackbar.make(getView(), getContext().getString(text), Snackbar.LENGTH_LONG);
      snackbar.setAction(title, v -> gitskariosDownloadManager.openAppSettings(getContext()));
      snackbar.show();
    }
  }

  @Override
  public void onReleaseAssetCLicked(Asset asset) {
    if (getView() != null) {
      gitskariosDownloadManager.download(getContext(), asset.getBrowserDownloadUrl(), asset.getName(), this::showSnackbar);
    }
  }
}
