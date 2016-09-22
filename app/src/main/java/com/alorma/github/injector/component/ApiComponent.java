package com.alorma.github.injector.component;

import com.alorma.github.injector.component.tags.RepositoryTagsComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.tags.RepositoryTagsModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.presenter.NavigationFragment;
import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.ui.actions.AssigneeAction;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.github.ui.fragment.NotificationsFragment;
import com.alorma.github.ui.fragment.issues.user.UserIssuesListFragment;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;
import com.alorma.github.ui.fragment.repos.CurrentAccountReposFragment;
import com.alorma.github.ui.fragment.repos.MembershipReposFragment;
import com.alorma.github.ui.fragment.repos.ReposFragmentFromOrgs;
import com.alorma.github.ui.fragment.repos.StarredReposFragment;
import com.alorma.github.ui.fragment.repos.UsernameReposFragment;
import com.alorma.github.ui.fragment.repos.WatchedReposFragment;

import dagger.Component;

@PerActivity @Component(dependencies = ApplicationComponent.class, modules = ApiModule.class)
public interface ApiComponent {

  ApiClient getApiClient();

  void inject(NotificationsFragment fragment);

  void inject(UsernameReposFragment usernameReposFragment);

  void inject(CurrentAccountReposFragment currentAccountReposFragment);

  void inject(WatchedReposFragment watchedReposFragment);

  void inject(StarredReposFragment starredReposFragment);

  void inject(ReposFragmentFromOrgs reposFragmentFromOrgs);

  void inject(OrgsReposFragment orgsReposFragment);

  void inject(MembershipReposFragment membershipReposFragment);

  void inject(IssueDetailActivity issueDetailActivity);

  void inject(AssigneeAction assigneeAction);

  void inject(NavigationFragment navigationFragment);

  void inject(CommitDetailActivity commitDetailActivity);

  void inject(UserIssuesListFragment userIssuesListFragment);

  // next components with its own tree
  RepositoryTagsComponent plus(RepositoryTagsModule module);
}
