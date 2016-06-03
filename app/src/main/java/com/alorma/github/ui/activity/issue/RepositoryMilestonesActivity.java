package com.alorma.github.ui.activity.issue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.NaturalTimeFormatter;
import java.util.List;

public class RepositoryMilestonesActivity extends BackActivity
    implements Presenter.Callback<List<Milestone>>, RecyclerArrayAdapter.ItemCallback<Milestone> {

  private static final String REPO_INFO = "REPO_INFO";
  private static final String RETURN = "RETURN";
  private static final String STATE = "STATE";

  @Bind(R.id.recycler) RecyclerView recyclerView;
  private MilestonesAdapter adapter;
  private boolean returnResult;

  public static Intent createLauncher(Context context, RepoInfo repoInfo, boolean returnResult) {
    Intent intent = new Intent(context, RepositoryMilestonesActivity.class);
    intent.putExtra(REPO_INFO, repoInfo);
    intent.putExtra(RETURN, returnResult);
    intent.putExtra(STATE, MilestoneState.open);
    return intent;
  }

  public static Intent createLauncher(Context context, RepoInfo issrepoInfoeInfo, MilestoneState state, boolean returnResult) {
    Intent intent = new Intent(context, RepositoryMilestonesActivity.class);
    intent.putExtra(REPO_INFO, issrepoInfoeInfo);
    intent.putExtra(RETURN, returnResult);
    intent.putExtra(STATE, state);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_recyclerview);
    ButterKnife.bind(this);

    setTitle(R.string.milestones);

    RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
    MilestoneState state = (MilestoneState) getIntent().getExtras().getSerializable(STATE);

    returnResult = getIntent().getExtras().getBoolean(RETURN, false);

    adapter = new MilestonesAdapter(getLayoutInflater());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter.setTimeFormatter(new NaturalTimeFormatter(this));
    adapter.setCallback(this);
    recyclerView.setAdapter(adapter);

    IssueMilestonePresenter presenter = new IssueMilestonePresenter(state);
    presenter.load(repoInfo, this);
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onResponse(List<Milestone> milestones, boolean firstTime) {
    if (firstTime) {
      adapter.clear();
    }
    adapter.addAll(milestones);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onResponseEmpty() {

  }

  @Override
  public void onItemSelected(Milestone item) {
    if (returnResult) {
      Intent intent = new Intent();
      intent.putExtra(Milestone.class.getSimpleName(), item);
      setResult(RESULT_OK, intent);
      finish();
    }
  }
}
