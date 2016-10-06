package com.alorma.github.injector.component.issues;

import com.alorma.github.injector.module.issues.IssueDetailModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.IssueDetailActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = IssueDetailModule.class)
public interface IssueDetailComponent {
    void inject(IssueDetailActivity issueDetailActivity);
}
