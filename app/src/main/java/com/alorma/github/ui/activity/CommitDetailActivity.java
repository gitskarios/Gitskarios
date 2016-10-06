package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.CommitDetailModule;
import com.alorma.github.presenter.CommitInfoPresenter;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.dto.response.GithubStatus;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.adapter.commit.GithubStatusAdapter;
import com.alorma.github.ui.fragment.commit.CommitFilesFragment;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.github.ui.view.ItemSingleLineAvatar;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import core.User;

public class CommitDetailActivity extends RepositoryThemeActivity
    implements CommitFilesAdapter.OnFileRequestListener, com.alorma.github.presenter.View<Commit> {

  @Inject CommitInfoPresenter commitInfoPresenter;

  @BindView(R.id.author) ItemSingleLineAvatar authorView;
  @BindView(R.id.committer) ItemSingleLineAvatar committerView;
  @BindView(R.id.recycler) RecyclerView recyclerView;
  @BindView(R.id.statusesTitle) TextView statusesTextView;
  @BindView(R.id.statusesRecycler) RecyclerView statusesRecyclerView;
  @BindView(R.id.commit_message) TextView commitMessageTextView;
  @BindView(R.id.commit_id) TextView commitIdTextView;
  @BindView(R.id.commit_parent) TextView commitParentTextView;

  private MaterialDialog dialog;
  private CommitInfo info;

  public static Intent launchIntent(Context context, CommitInfo commitInfo) {
    Bundle b = new Bundle();
    b.putParcelable(CommitFilesFragment.INFO, commitInfo);

    Intent intent = new Intent(context, CommitDetailActivity.class);
    intent.putExtras(b);

    return intent;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.commit_detail);
    commitInfoPresenter.attachView(this);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      info = getIntent().getExtras().getParcelable(CommitFilesFragment.INFO);

      if (info != null && info.repoInfo != null) {

        setTitle(String.valueOf(info.repoInfo));

        commitInfoPresenter.execute(info);
      } else {
        finish();
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    commitInfoPresenter.detachView();
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    super.injectComponents(applicationComponent);

    ApiComponent apiComponent =
            DaggerApiComponent.builder()
                    .applicationComponent(applicationComponent)
                    .apiModule(new ApiModule())
                    .build();
    apiComponent
            .plus(new CommitDetailModule())
            .inject(this);
  }

  @Override
  public void onFileRequest(CommitFile file) {
    FileInfo info = new FileInfo();
    info.content = file.patch;
    info.name = file.getFileName();
    Intent launcherIntent = FileActivity.createLauncherIntent(this, info);
    startActivity(launcherIntent);
  }

  @Override
  public void showLoading() {
    dialog = new MaterialDialog.Builder(this).progress(true, 10).content(R.string.commits_detail_loading).show();
  }

  @Override
  public void onDataReceived(Commit commit, boolean isFromPaginated) {

    if (commit.commit != null && commit.commit.message != null) {
      commitMessageTextView.setText(Html.fromHtml(commit.commit.message));
    }

    User author = commit.author;

    if (author != null) {
      showUser(author, authorView);
    }

    User committer = commit.committer;

    if (committer != null) {
      showUser(committer, committerView);
    }

    if (commit.files != null) {
      showFiles(commit.files);
    }

    if (commit.combinedStatus != null && commit.combinedStatus.statuses != null && !commit.combinedStatus.statuses.isEmpty()) {
      showStatus(commit.combinedStatus.statuses);
    } else {
      statusesTextView.setVisibility(View.GONE);
      statusesRecyclerView.setVisibility(View.GONE);
    }

    // TODO Multiple parents
    if (commit.parents != null && !commit.parents.isEmpty() && commit.parents.get(0) != null && commit.parents.get(0).sha != null) {
      commitParentTextView.setText(getString(R.string.parent_commit, commit.parents.get(0).sha));
      commitParentTextView.setOnClickListener(v -> {
        CommitInfo request = new CommitInfo();
        request.repoInfo = info.repoInfo;
        request.sha = commit.parents.get(0).sha;
        Intent intent = CommitDetailActivity.launchIntent(v.getContext(), request);
        startActivity(intent);
      });
    }

    if (commit.sha != null) {
      commitIdTextView.setText(commit.sha);
    }
  }

  private void showUser(User user, ItemSingleLineAvatar userView) {
    ImageView imageView = userView.getImageView();
    if (imageView != null) {
      UniversalImageLoaderUtils.loadUserAvatar(imageView, user);
    }

    TextView textView = userView.getTextView();
    if (textView != null) {
      textView.setText(user.getLogin());
    }

    userView.setOnClickListener(v -> {
      Intent intent = ProfileActivity.createLauncherIntent(v.getContext(), user);
      startActivity(intent);
    });
  }

  private void showFiles(List<CommitFile> files) {
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setNestedScrollingEnabled(false);

    CommitFilesAdapter adapter = new CommitFilesAdapter(getLayoutInflater());
    recyclerView.setAdapter(adapter);

    adapter.addAll(files);

    adapter.setOnFileRequestListener(this);
  }

  private void showStatus(List<GithubStatus> statuses) {
    statusesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    statusesRecyclerView.setNestedScrollingEnabled(false);

    GithubStatusAdapter adapter = new GithubStatusAdapter(getLayoutInflater());
    statusesRecyclerView.setAdapter(adapter);
    adapter.setCallback(item -> {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(item.target_url));
      startActivity(intent);
    });
    adapter.addAll(statuses);
  }

  @Override
  public void hideLoading() {
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  @Override
  public void showError(Throwable throwable) {

  }
}
