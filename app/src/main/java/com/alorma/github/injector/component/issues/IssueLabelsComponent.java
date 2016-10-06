package com.alorma.github.injector.component.issues;

import com.alorma.github.injector.module.issues.IssueLabelsModule;
import com.alorma.github.injector.scope.PerActivity;
import com.alorma.github.ui.activity.issue.IssueLabelsActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = IssueLabelsModule.class)
public interface IssueLabelsComponent {
    void inject(IssueLabelsActivity activity);
}
