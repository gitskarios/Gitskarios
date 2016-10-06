package com.alorma.github.presenter.repos.releases.tags;

import com.alorma.github.presenter.BaseRxPresenter;
import com.alorma.github.presenter.View;
import com.alorma.github.sdk.bean.info.RepoInfo;

import java.util.List;

import core.repositories.releases.tags.Tag;
import core.repository.GenericRepository;
import rx.Scheduler;

/**
 * Combines repository Tags with Commits and Releases. Callback receives list of tag with fetched
 * commits and releases. Release could be null in Tag object.
 */
public class RepositoryTagsPresenter
        extends BaseRxPresenter<RepoInfo, List<Tag>, View<List<Tag>>> {

  public RepositoryTagsPresenter(
          Scheduler mainScheduler, Scheduler ioScheduler,
          GenericRepository<RepoInfo, List<Tag>> repository) {
    super(mainScheduler, ioScheduler, repository);
  }
}
