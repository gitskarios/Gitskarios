package com.alorma.github.ui.activity.issue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.repository.RepositoryMilestonesModule;
import com.alorma.github.presenter.View;
import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.NaturalTimeFormatter;
import java.util.List;

import javax.inject.Inject;

public class RepositoryMilestonesActivity extends RepositoryThemeActivity
    implements View<List<Milestone>>, RecyclerArrayAdapter.ItemCallback<Milestone> {

  private static final String REPO_INFO = "REPO_INFO";
  private static final String RETURN = "RETURN";
  private static final String STATE = "STATE";

  @BindView(R.id.recycler) RecyclerView recyclerView;
  private MilestonesAdapter adapter;
  private boolean returnResult;
  @Inject IssueMilestonePresenter presenter;

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
  protected void injectComponents(ApplicationComponent applicationComponent) {
    ApiComponent apiComponent =
            DaggerApiComponent.builder()
                    .applicationComponent(applicationComponent)
                    .apiModule(new ApiModule())
                    .build();
    apiComponent
            .plus(new RepositoryMilestonesModule(buildMilestoneState()))
            .inject(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_recyclerview);
    ButterKnife.bind(this);
    presenter.attachView(this);
    presenter.execute(buildRepoInfo());

    if (getToolbar() != null) {
      getToolbar().setBackgroundColor(AttributesUtils.getPrimaryColor(this));
    }

    setTitle(R.string.milestones);

    returnResult = getIntent().getExtras().getBoolean(RETURN, false);

    adapter = new MilestonesAdapter(getLayoutInflater());
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter.setTimeFormatter(new NaturalTimeFormatter(this));
    adapter.setCallback(this);
    recyclerView.setAdapter(adapter);
  }

  private MilestoneState buildMilestoneState() {
    return (MilestoneState) getIntent().getExtras().getSerializable(STATE);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    presenter.detachView();
  }

  private RepoInfo buildRepoInfo() {
    RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
    return repoInfo;
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void onDataReceived(List<Milestone> milestones, boolean isFromPaginated) {
    if (!isFromPaginated) {
      adapter.clear();
    }
    adapter.addAll(milestones);
  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void showError(Throwable throwable) {

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
