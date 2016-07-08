package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.response.Milestone;
import com.alorma.github.sdk.bean.dto.response.MilestoneState;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.BaseInfiniteCallback;
import com.alorma.github.sdk.services.client.GithubClient;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Bernat on 14/04/2015.
 */
public class GetMilestonesClient extends GithubClient<List<Milestone>> {

  private RepoInfo repoInfo;
  private MilestoneState state;
  private boolean sortAlphabetic = false;
  private Comparator<? super Milestone> NAME_SORT = new Comparator<Milestone>() {
    @Override
    public int compare(Milestone lhs, Milestone rhs) {
      return lhs.title.compareTo(rhs.title);
    }
  };

  public GetMilestonesClient(RepoInfo repoInfo, MilestoneState state) {
    super();
    this.repoInfo = repoInfo;
    this.state = state;
  }

  public GetMilestonesClient(RepoInfo repoInfo, MilestoneState state, boolean sortAlphabetic) {
    super();
    this.repoInfo = repoInfo;
    this.state = state;
    this.sortAlphabetic = sortAlphabetic;
  }

  @Override
  protected Observable<List<Milestone>> getApiObservable(final RestAdapter restAdapter) {
    Observable<List<Milestone>> listObservable =
        Observable.create(new BaseInfiniteCallback<List<Milestone>>() {
          @Override
          public void execute() {
            IssuesService issuesService = restAdapter.create(IssuesService.class);
            issuesService.milestones(repoInfo.owner, repoInfo.name, state.name(), this);
          }

          @Override
          protected void executePaginated(int nextPage) {
            IssuesService issuesService = restAdapter.create(IssuesService.class);
            issuesService.milestones(repoInfo.owner, repoInfo.name, state.name(), nextPage, this);
          }
        });
    if (!sortAlphabetic) {
      return listObservable;
    } else {
      return listObservable.map(new Func1<List<Milestone>, List<Milestone>>() {
        @Override
        public List<Milestone> call(List<Milestone> milestones) {
          Collections.sort(milestones, NAME_SORT);
          return milestones;
        }
      });
    }
  }
}
