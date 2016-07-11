package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.response.Label;
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
 * Created by Bernat on 10/05/2015.
 */
public class GithubIssueLabelsClient extends GithubClient<List<Label>> {
  private RepoInfo repoInfo;
  private boolean sortAlphabetic = false;
  private Comparator<? super Label> NAME_SORT = new Comparator<Label>() {
    @Override
    public int compare(Label lhs, Label rhs) {
      return lhs.name.compareTo(rhs.name);
    }
  };

  public GithubIssueLabelsClient(RepoInfo repoInfo) {
    super();
    this.repoInfo = repoInfo;
  }

  public GithubIssueLabelsClient(RepoInfo repoInfo, boolean sortAlphabetic) {
    super();
    this.repoInfo = repoInfo;
    this.sortAlphabetic = sortAlphabetic;
  }

  @Override
  protected Observable<List<Label>> getApiObservable(final RestAdapter restAdapter) {
    Observable<List<Label>> listObservable =
        Observable.create(new BaseInfiniteCallback<List<Label>>() {
          @Override
          public void execute() {
            IssuesService issueService = restAdapter.create(IssuesService.class);
            issueService.labels(repoInfo.owner, repoInfo.name, this);
          }

          @Override
          protected void executePaginated(int nextPage) {
            IssuesService issueService = restAdapter.create(IssuesService.class);
            issueService.labels(repoInfo.owner, repoInfo.name, nextPage, this);
          }
        });

    if (!sortAlphabetic) {
      return listObservable;
    } else {
      return listObservable.map(new Func1<List<Label>, List<Label>>() {
        @Override
        public List<Label> call(List<Label> labels) {
          Collections.sort(labels, NAME_SORT);
          return labels;
        }
      });
    }
  }
}
