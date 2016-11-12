package com.alorma.github.ui.fragment.commit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.github.ui.view.ItemSingleLineAvatar;
import core.User;
import core.repositories.Commit;

public class CommitDetailBottomSheetFragment extends BottomSheetDialogFragment {

  private static final String REPOINFO = "REPOINFO";
  private static final String COMMIT = "COMMIT";

  public static CommitDetailBottomSheetFragment newInstance(RepoInfo repoInfo, Commit commit) {
    CommitDetailBottomSheetFragment fragment = new CommitDetailBottomSheetFragment();

    Bundle args = new Bundle();
    args.putParcelable(REPOINFO, repoInfo);
    args.putParcelable(COMMIT, commit);
    fragment.setArguments(args);

    return fragment;
  }

  @BindView(R.id.commitToolbar) Toolbar toolbar;
  @BindView(R.id.author) ItemSingleLineAvatar author;
  @BindView(R.id.committer) ItemSingleLineAvatar committer;
  @BindView(R.id.commit_message) TextView commitMessage;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.commit_detail_bottom_sheet, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    if (getArguments() != null) {
      RepoInfo repoInfo = getArguments().getParcelable(REPOINFO);
      Commit commit = getArguments().getParcelable(COMMIT);
      if (commit != null) {
        fillToolbar(repoInfo, commit);

        fillUser(author, commit.author);
        fillUser(committer, commit.committer);

        commitMessage.setText(commit.commit.getMessage());
      }
    }
  }

  private void fillToolbar(RepoInfo repoInfo, Commit commit) {
    toolbar.setTitle(commit.shortSha());
    toolbar.inflateMenu(R.menu.commit_bottom_sheet);

    MenuItem expandItem = toolbar.getMenu().findItem(R.id.action_commit_expand);
    expandItem.setIcon(R.drawable.ic_expand_less_black_24dp);

    toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);

    toolbar.setOnMenuItemClickListener(item -> {
      if (item.getItemId() == R.id.action_commit_expand) {
        openCommit(repoInfo, commit);
      }
      return super.onOptionsItemSelected(item);
    });

    toolbar.setNavigationOnClickListener(view -> dismiss());
  }

  private void fillUser(ItemSingleLineAvatar singleLineAvatar, User user) {
    singleLineAvatar.getTextView().setText(user.getLogin());
    UniversalImageLoaderUtils.loadUserAvatar(singleLineAvatar.getImageView(), user.getLogin(), user.getAvatar());
    singleLineAvatar.setOnClickListener(view -> {
      Intent intent = ProfileActivity.createLauncherIntent(getActivity(), user);
      startActivity(intent);
      dismiss();
    });
  }

  private void openCommit(RepoInfo repoInfo, Commit commit) {
    CommitInfo info = new CommitInfo();
    info.repoInfo = repoInfo;
    info.sha = commit.sha;
    Intent intent = CommitDetailActivity.launchIntent(getActivity(), info);
    startActivity(intent);
  }
}
