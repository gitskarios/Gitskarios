package com.alorma.github.injector.component.issues;

import com.alorma.github.injector.module.issues.UserIssuesModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.fragment.issues.user.UserIssuesListFragment;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = UserIssuesModule.class)
public interface UserIssuesComponent {
    void inject(UserIssuesListFragment userIssuesListFragment);
}
