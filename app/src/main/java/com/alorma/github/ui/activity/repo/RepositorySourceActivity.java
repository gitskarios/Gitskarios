package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.RepositorySourceModule;
import com.alorma.github.presenter.RepositorySourcePresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.detail.repo.RepositorySourceListFragment;
import com.alorma.gitskarios.core.Pair;
import core.repositories.Branch;
import core.repositories.Repo;
import java.util.List;
import javax.inject.Inject;

public class RepositorySourceActivity extends RepositoryThemeActivity
    implements com.alorma.github.presenter.View<Pair<Branch, List<Branch>>> {

  private static final String REPO = "REPO";
  private static final String BRANCH = "BRANCH";
  private Repo repo;
  private Branch selectedBranch;

  public static Intent launcherIntent(Context context, Repo currentRepo, Branch branch) {
    Intent intent = new Intent(context, RepositorySourceActivity.class);
    intent.putExtra(REPO, currentRepo);
    intent.putExtra(BRANCH, branch);
    return intent;
  }

  @Inject RepositorySourcePresenter repositorySourcePresenter;

  @BindView(R.id.branchesTitle) Spinner branchesTitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.source_activity);
    repositorySourcePresenter.attachView(this);

    ButterKnife.bind(this);

    if (getIntent() != null) {
      repo = getIntent().getParcelableExtra(REPO);
      Branch branch = getIntent().getParcelableExtra(BRANCH);
      if (repo != null & branch != null) {
        repositorySourcePresenter.execute(new Pair<>(repo, branch));
      }
    }
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();
    apiComponent.plus(new RepositorySourceModule()).inject(this);
  }

  private void openSourceFragment(Repo repo, Branch branch) {
    selectedBranch = branch;
    RepoInfo repoInfo = new RepoInfo();
    repoInfo.owner = repo.getOwner().getLogin();
    repoInfo.name = repo.name;
    repoInfo.branch = branch.getCommit().getSha();
    RepositorySourceListFragment sourceListFragment = RepositorySourceListFragment.newInstance(repoInfo);

    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.content, sourceListFragment);
    ft.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.source_list_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.action_repo_commits);
    if (item != null) {
      Drawable drawable = AppCompatDrawableManager.get().getDrawable(this, R.drawable.ic_git_commit);
      drawable = DrawableCompat.wrap(drawable);
      DrawableCompat.setTint(drawable, Color.WHITE);
      item.setIcon(drawable);

      item.setEnabled(selectedBranch != null);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_repo_commits:
        openCommits();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private void openCommits() {
    if (selectedBranch != null) {
      RepoInfo repoInfo = new RepoInfo();
      repoInfo.owner = repo.getOwner().getLogin();
      repoInfo.name = repo.name;
      repoInfo.branch = selectedBranch.getCommit().getSha();
      Intent intent = BranchCommitsActivity.createLauncherIntent(this, repoInfo);
      startActivity(intent);
    }
  }

  @Override
  protected void onDestroy() {
    repositorySourcePresenter.detachView();
    super.onDestroy();
  }

  @Override
  public void showLoading() {

  }

  @Override
  public void hideLoading() {

  }

  @Override
  public void onDataReceived(Pair<Branch, List<Branch>> data, boolean isFromPaginated) {
    openSourceFragment(repo, data.first);

    ArrayAdapter<Branch> adapter = new ArrayAdapter<>(this, R.layout.branch_title, android.R.id.text1, data.second);
    branchesTitle.setAdapter(adapter);
    branchesTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Branch branch1 = data.second.get(position);
        openSourceFragment(repo, branch1);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  @Override
  public void showError(Throwable throwable) {

  }
}
