package com.alorma.github.ui.fragment.issues;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.issues.UserIssuesClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.activity.PullRequestDetailActivity;
import com.alorma.github.ui.adapter.issues.IssuesAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GenericIssuesListFragment extends LoadingListFragment<IssuesAdapter>
    implements View.OnClickListener, IssuesAdapter.IssuesAdapterListener {

  private static final int ISSUE_REQUEST = 1234;

  public static GenericIssuesListFragment newInstance() {
    return new GenericIssuesListFragment();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.generic_issues_list_fragment, null, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.issue_list_filter, menu);
  }

  @Override
  protected void loadArguments() {

  }

  protected void executeRequest() {
    super.executeRequest();
    setAction(new UserIssuesClient());
  }

  private void setAction(GithubListClient<List<Issue>> client) {
    client.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(new Action1<Pair<List<Issue>, Integer>>() {
          @Override
          public void call(Pair<List<Issue>, Integer> listIntegerPair) {
            setPage(listIntegerPair.second);
          }
        })
        .flatMap(new Func1<Pair<List<Issue>, Integer>, Observable<Issue>>() {
          @Override
          public Observable<Issue> call(Pair<List<Issue>, Integer> listIntegerPair) {
            return Observable.from(listIntegerPair.first);
          }
        })
        .filter(new Func1<Issue, Boolean>() {
          @Override
          public Boolean call(Issue issue) {
            return issue.pullRequest == null;
          }
        })
        .toList()
        .subscribe(new Subscriber<List<Issue>>() {
          @Override
          public void onCompleted() {
            stopRefresh();
          }

          @Override
          public void onError(Throwable e) {
            if (getAdapter() == null || getAdapter().getItemCount() == 0) {
              setEmpty();
            }
          }

          @Override
          public void onNext(List<Issue> issues) {
            onResponse(issues);
          }
        });
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);
    setAction(new UserIssuesClient(page));
  }

  protected void onResponse(List<Issue> issues) {
    if (issues.size() > 0) {
      hideEmpty();
      if (refreshing || getAdapter() == null) {
        IssuesAdapter issuesAdapter = new IssuesAdapter(LayoutInflater.from(getActivity()));
        issuesAdapter.setIssuesAdapterListener(this);
        issuesAdapter.addAll(issues);
        setAdapter(issuesAdapter);
      } else {
        getAdapter().addAll(issues);
      }
    } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
      setEmpty();
    } else {
      getAdapter().clear();
      setEmpty();
    }
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_issue_opened;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_issues_found;
  }

  public void clear() {
    if (getAdapter() != null) {
      getAdapter().clear();
    }
  }

  @Override
  public void setRefreshing() {
    super.setRefreshing();
    startRefresh();
  }

  @Override
  public void onIssueOpenRequest(Issue issue) {
    if (issue != null) {
      IssueInfo info = new IssueInfo();
      RepoInfo repoInfo = new RepoInfo();
      repoInfo.owner = issue.repository.owner.login;
      repoInfo.name = issue.repository.name;
      info.repoInfo = repoInfo;
      info.num = issue.number;

      if (issue.pullRequest == null) {
        Intent intent = IssueDetailActivity.createLauncherIntent(getActivity(), info);
        startActivityForResult(intent, ISSUE_REQUEST);
      } else {
        Intent intent = PullRequestDetailActivity.createLauncherIntent(getActivity(), info);
        startActivity(intent);
      }
    }
  }
}
