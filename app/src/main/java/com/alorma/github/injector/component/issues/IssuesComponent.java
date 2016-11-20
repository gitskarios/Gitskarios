package com.alorma.github.injector.component.issues;

import com.alorma.github.injector.module.issues.IssuesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;
import com.alorma.github.ui.fragment.issues.RepositoryIssuesListFragment;
import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = IssuesModule.class)
public interface IssuesComponent {
    void inject(IssuesListFragment userIssuesListFragment);
    void inject(RepositoryIssuesListFragment repositoryIssuesListFragment);
}
