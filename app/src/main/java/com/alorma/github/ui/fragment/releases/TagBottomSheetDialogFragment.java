package com.alorma.github.ui.fragment.releases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.GitskariosApplication;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.CommitDetailModule;
import com.alorma.github.presenter.CommitInfoPresenter;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.fragment.BaseBottomSheetDialogFragment;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.GitskariosDownloadManager;
import com.alorma.github.utils.TimeUtils;
import core.User;
import core.repositories.Commit;
import core.repositories.releases.tags.Tag;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TagBottomSheetDialogFragment extends BaseBottomSheetDialogFragment implements com.alorma.github.presenter.View<Commit> {

  private static final String TAG = "TAG";
  private static final String REPOINFO = "REPOINFO";

  @Inject CommitInfoPresenter commitPresenter;
  @Inject GitskariosDownloadManager gitskariosDownloadManager;

  @BindView(R.id.profileIcon) UserAvatarView userAvatar;
  @BindView(R.id.authorName) TextView authorName;
  @BindView(R.id.createdAt) TextView date;
  @BindView(R.id.downloadZip) View downloadZip;
  @BindView(R.id.downloadTar) View downloadTar;
  @BindView(R.id.loading) View loadingView;
  @BindView(R.id.toolbar) Toolbar toolbar;

  private Tag tag;
  private RepoInfo repoInfo;

  public static TagBottomSheetDialogFragment newInstance(RepoInfo repoInfo, Tag tag) {
    TagBottomSheetDialogFragment fragment = new TagBottomSheetDialogFragment();

    Bundle args = new Bundle();
    args.putParcelable(REPOINFO, repoInfo);
    args.putParcelable(TAG, tag);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    GitskariosApplication application = (GitskariosApplication) getContext().getApplicationContext();
    ApplicationComponent applicationComponent = application.getApplicationComponent();

    //applicationComponent.inject(this);

    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();
    apiComponent.plus(new CommitDetailModule()).inject(this);

    commitPresenter.attachView(this);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.dialog_tag_details, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    if (getArguments() != null) {
      tag = getArguments().getParcelable(TAG);
      fillToolbar(tag);

      repoInfo = getArguments().getParcelable(REPOINFO);
      CommitInfo commitInfo = new CommitInfo();
      commitInfo.repoInfo = repoInfo;
      commitInfo.sha = tag.getSha().getSha();
      commitPresenter.execute(commitInfo);
    }
  }

  @Override
  public void onDetach() {
    commitPresenter.detachView();
    super.onDetach();
  }

  @Override
  public void showLoading() {
    loadingView.setVisibility(View.VISIBLE);
  }

  @Override
  public void hideLoading() {
    loadingView.setVisibility(View.GONE);
  }

  @Override
  public void onDataReceived(Commit commit, boolean isFromPaginated) {
    if (tag != null && commit != null) {
      fillTagDetailsView(tag, commit);
    }
  }

  @Override
  public void showError(Throwable throwable) {

  }

  private void fillTagDetailsView(Tag tag, Commit commit) {
    User owner = commit.author;

    if (owner == null) {
      owner = commit.commit.author;
    }

    if (owner != null) {
      authorName.setText(owner.getLogin() != null ? owner.getLogin() : owner.getEmail());
      userAvatar.setUser(owner);
    }

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    DateTime dt = formatter.parseDateTime(commit.commit.committer.date);
    date.setText(TimeUtils.getDateToText(getContext(), dt.toDate(), R.string.created_at));

    configButton(downloadZip, R.string.download_zip_archive, tag.getZipballUrl(), tag);
    configButton(downloadTar, R.string.download_tar_archive, tag.getTarballUrl(), tag);
  }

  private void fillToolbar(Tag tag) {
      toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
      toolbar.setNavigationOnClickListener(view -> dismiss());

      toolbar.setTitle(tag.getName());
    }

  private void configButton(View button, int title, String url, Tag tag) {
    button.setOnClickListener(view -> {
      String downloadText = getString(title) + " file for tag " + tag.getName();
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
}
