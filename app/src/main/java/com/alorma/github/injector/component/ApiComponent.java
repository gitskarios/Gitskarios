package com.alorma.github.injector.component;

import com.alorma.github.injector.component.issues.IssueDetailComponent;
import com.alorma.github.injector.component.issues.IssueLabelsComponent;
import com.alorma.github.injector.component.issues.UserIssuesComponent;
import com.alorma.github.injector.component.repository.AuthOrgsRepositoriesComponent;
import com.alorma.github.injector.component.repository.CurrentAccountRepositoriesComponent;
import com.alorma.github.injector.component.repository.MembershipRepositoriesComponent;
import com.alorma.github.injector.component.repository.OrganizationRepositoriesComponent;
import com.alorma.github.injector.component.repository.RepoDetailComponent;
import com.alorma.github.injector.component.repository.RepositoryMilestonesComponent;
import com.alorma.github.injector.component.repository.StarredRepositoriesComponent;
import com.alorma.github.injector.component.repository.UsernameRepositoriesComponent;
import com.alorma.github.injector.component.repository.WatchedRepositoriesComponent;
import com.alorma.github.injector.component.repository.tags.RepositoryTagsComponent;
import com.alorma.github.injector.module.ApiModule;
import com.alorma.github.injector.module.CommitDetailModule;
import com.alorma.github.injector.module.NavigationModule;
import com.alorma.github.injector.module.UserNotificationsModule;
import com.alorma.github.injector.module.issues.IssueDetailModule;
import com.alorma.github.injector.module.issues.IssueLabelsModule;
import com.alorma.github.injector.module.issues.UserIssuesModule;
import com.alorma.github.injector.module.repository.AuthOrgsRepositoriesModule;
import com.alorma.github.injector.module.repository.CurrentAccountRepositoriesModule;
import com.alorma.github.injector.module.repository.MembershipRepositoriesModule;
import com.alorma.github.injector.module.repository.OrganizationRepositoriesModule;
import com.alorma.github.injector.module.repository.RepoDetailModule;
import com.alorma.github.injector.module.repository.RepositoryMilestonesModule;
import com.alorma.github.injector.module.repository.StarredRepositoriesModule;
import com.alorma.github.injector.module.repository.UsernameRepositoriesModule;
import com.alorma.github.injector.module.repository.WatchedRepositoriesModule;
import com.alorma.github.injector.module.repository.tags.RepositoryTagsModule;
import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.actions.AssigneeAction;

import core.ApiClient;
import dagger.Component;
import rx.Scheduler;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ApiModule.class)
public interface ApiComponent {

  ApiClient getApiClient();

  @MainScheduler
  Scheduler provideMainScheduler();

  @IOScheduler
  Scheduler provideIOScheduler();

  void inject(AssigneeAction assigneeAction);

  // next components with its own tree
  UserIssuesComponent plus(UserIssuesModule module);

  RepositoryTagsComponent plus(RepositoryTagsModule module);

  MembershipRepositoriesComponent plus(MembershipRepositoriesModule module);

  OrganizationRepositoriesComponent plus(OrganizationRepositoriesModule module);

  CurrentAccountRepositoriesComponent plus(CurrentAccountRepositoriesModule module);

  AuthOrgsRepositoriesComponent plus(AuthOrgsRepositoriesModule module);

  StarredRepositoriesComponent plus(StarredRepositoriesModule module);

  UsernameRepositoriesComponent plus(UsernameRepositoriesModule module);

  WatchedRepositoriesComponent plus(WatchedRepositoriesModule module);

  IssueDetailComponent plus(IssueDetailModule module);

  UserNotificationsComponent plus(UserNotificationsModule module);

  CommitDetailComponent plus(CommitDetailModule module);

  NavigationComponent plus(NavigationModule module);

  RepoDetailComponent plus(RepoDetailModule module);

  IssueLabelsComponent plus(IssueLabelsModule module);

  RepositoryMilestonesComponent plus(RepositoryMilestonesModule module);

}
