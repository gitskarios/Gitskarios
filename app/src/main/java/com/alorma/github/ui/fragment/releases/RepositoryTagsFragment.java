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
import com.alorma.github.presenter.View;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.adapter.TagsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.repositories.releases.Release;
import core.repositories.releases.tags.Tag;
import java.util.List;
import javax.inject.Inject;

public class RepositoryTagsFragment extends LoadingListFragment<TagsAdapter> implements View<List<Tag>>, TagsAdapter.TagsCallback {

  private static final String REPO_INFO = "REPO_INFO";

  @Inject RepositoryTagsPresenter tagsPresenter;

  private ReleasesCallback releasesCallback;

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
    ApiComponent apiComponent = DaggerApiComponent.builder().applicationComponent(applicationComponent).apiModule(new ApiModule()).build();
    apiComponent.plus(new RepositoryTagsModule()).inject(this);
    tagsPresenter.attachView(this);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof ReleasesCallback) {
      releasesCallback = (ReleasesCallback) context;
    }
  }

  @Override
  public void onDetach() {
    releasesCallback = new ReleasesCallback() {
      @Override
      public void showTagDialog(Tag tag) {

      }

      @Override
      public void showReleaseDialog(Release release) {

      }
    };
    super.onDetach();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    tagsPresenter.detachView();
  }

  @Override
  protected void loadArguments() {
    repoInfo = getArguments().getParcelable(REPO_INFO);
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
    if (getActivity() == null) return;

    if (tags.size() > 0) {
      hideEmpty();
      if (refreshing || getAdapter() == null) {
        TagsAdapter adapter = new TagsAdapter(LayoutInflater.from(getActivity()), repoInfo);
        adapter.setTagsCallback(this);
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

  @Override
  public void onTagSelected(Tag tag) {
    releasesCallback.showTagDialog(tag);
  }

  @Override
  public void onReleaseSelected(Release release) {
    releasesCallback.showReleaseDialog(release);
  }

  public interface ReleasesCallback {
    void showTagDialog(Tag tag);
    void showReleaseDialog(Release release);
  }
}
