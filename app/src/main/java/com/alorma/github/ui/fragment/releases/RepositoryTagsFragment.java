package com.alorma.github.ui.fragment.releases;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.injector.component.ApiComponent;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.injector.component.DaggerApiComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.repository.tags.RepositoryTagsModule;
import com.alorma.github.presenter.CommitInfoPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.adapter.TagsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import javax.inject.Inject;

import core.repositories.releases.tags.Tag;

public class RepositoryTagsFragment extends LoadingListFragment<TagsAdapter> implements View<List<Tag>> {

  private static final String REPO_INFO = "REPO_INFO";
  private static final String REPO_PERMISSIONS = "REPO_PERMISSIONS";

  @Inject RepositoryTagsPresenter tagsPresenter;
  @Inject CommitInfoPresenter commitPresenter;
  private RepoInfo repoInfo;

  public static RepositoryTagsFragment newInstance(RepoInfo info) {
    RepositoryTagsFragment repositoryTagsFragment = new RepositoryTagsFragment();

    Bundle args = new Bundle();

    args.putParcelable(REPO_INFO, info);

    repositoryTagsFragment.setArguments(args);

    return repositoryTagsFragment;
  }

  @Override
  protected void injectComponents(ApplicationComponent applicationComponent) {
    ApiComponent apiComponent =
            DaggerApiComponent.builder()
                    .applicationComponent(applicationComponent)
                    .apiModule(new ApiModule())
                    .build();
    apiComponent
            .plus(new RepositoryTagsModule())
            .inject(this);
    tagsPresenter.attachView(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    tagsPresenter.detachView();
    commitPresenter.detachView();
  }

  @Override
  protected void loadArguments() {
    repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();

    if (getAdapter() != null) {
      getAdapter().clear();
    }

    tagsPresenter.execute(repoInfo.toCoreRepoInfo());
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);
    tagsPresenter.executePaginated(repoInfo.toCoreRepoInfo());
  }

  @Override
  public void showLoading() {
    //do nothing
  }

  @Override
  public void onDataReceived(List<Tag> tags, boolean isFromPaginated) {
    if(getActivity() == null) return;

    if (tags.size() > 0) {
      hideEmpty();
      if (refreshing || getAdapter() == null) {
        TagsAdapter adapter = new TagsAdapter(LayoutInflater.from(getActivity()), repoInfo, commitPresenter);
        adapter.addAll(tags);
        setAdapter(adapter);
      } else {
        getAdapter().addAll(tags);
      }
      setPage(tagsPresenter.getPage());
    } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
      setEmpty();
    } else {
      getAdapter().clear();
      setEmpty();
    }
  }

  @Override
  public void hideLoading() {
    stopRefresh();
  }

  @Override
  public void showError(Throwable throwable) {
    stopRefresh();
    if (getAdapter() == null || getAdapter().getItemCount() == 0) {
      setEmpty();
    }
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return null;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_tags;
  }
}
